package com.github.ralberth.playertimewindow.util;


import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.logging.Level;

/**
 * Class that invokes a callback you supply on a regular schedule every N minutes (approx).
 *
 * On the surface, this seems like a useless wrapper around Bukkit's BukkitRunnable class,
 * but there's a fundamental difference between what we need in this PlayerTimeWindow plugin
 * and what BukkitRunnable can provide: recurring execution based on wall-clock time.
 *
 * BukkitRunnable will execute jobs on a recurring cycle, but it's based on clock ticks, which
 * can vary from server to server, and even change ticks-per-second on the same server as
 * load changes.  So, PeriodicExecutor will guarantee that it won't call the supplied callback
 * faster than N minutes bewteen calls.  This allows a cadence based on human-time, not
 * game-time.
 *
 * Under the covers, this just uses BukkitRunnable on a faster cycle time.  It keeps track of
 * the last time the user-supplied callback was invoked, and waits for enough time to pass
 * before re-executing it.
 *
 * The resolution and accuracy of this is dependent on the value in CHECK_FREQUENCY.  This is
 * the number of ticks between times when we check to see if the user-supplied delay has
 * occurred.  For example, if CHECK_FREQUENCY is 100, and the ticks per second on this server
 * is currently 20, then CHECK_FREQUENCY *right* *now* is roughly 100 / 20 = 5 seconds.
 * This will wake-up every 5 seconds and see if it's time yet to invoke the user's callback.
 */
public class PeriodicExecutor {

    private static final long TICKS_PER_SECOND = 20;                      // This is *approximate* at best
    private static final long CHECK_FREQUENCY  = 15 * TICKS_PER_SECOND;   // *roughly* 15 seconds
    private static final long MS_PER_MIN = 1000 * 60;                    // milliseconds per minute

    private final Plugin plugin;
    private final int minutesBetweenCallbacks;
    private final Runnable callback;
    private Date nextExecution;
    private boolean running;


    /*
     * Why not inherit from BukkitRunnable?  Because it would expose methods we deliberately
     * want to hide in here.  We don't want to confuse a user of PeriodicExecutor by having
     * multiple methods that start and stop things, and exposing two different schedules.
     */
    BukkitRunnable scheduler;


    public PeriodicExecutor(Plugin plugin, Runnable callback, int minutesBetweenCallbacks) {
        this.plugin = plugin;
        this.callback = callback;
        this.minutesBetweenCallbacks = minutesBetweenCallbacks;
        nextExecution = new Date(); // simpler than lots of null-checks below

        scheduler = new BukkitRunnable() {
            @Override
            public void run() {
                checkForCallbackTime();
            }
        };

        running = false;
    }


    public void start() {
        if (!running) {
            scheduler.runTaskTimer(plugin, 0L, CHECK_FREQUENCY);
            running = true;
        }
    }


    public void stop() {
        if (running && !scheduler.isCancelled()) {
            scheduler.cancel();
            running = false;
        }
    }


    private void checkForCallbackTime() {
        Date now = new Date();
        if (now.after(nextExecution)) {
            nextExecution.setTime(now.getTime() + (minutesBetweenCallbacks * MS_PER_MIN));
            try {
                callback.run();
            } catch(Exception e) {
                plugin.getLogger().log(Level.WARNING, "Exception running callback in PeriodicExecutor:", e);
            }
        }
    }
}
