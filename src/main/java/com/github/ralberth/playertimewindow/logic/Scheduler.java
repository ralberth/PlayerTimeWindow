package com.github.ralberth.playertimewindow.logic;

import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.logging.Logger;


/**
 * Runnable scheduled and executed by Bukkit every 5 minutes by PlayerTimeWindow.java.
 *
 * This looks at all currently-logged in users and kicks anyone off that is on the server
 * but shouldn't based on their scheduled hours.
 */
public class Scheduler extends BukkitRunnable {

    public static final String PLAYER_KICK_MESSAGE = "It is now past when you are allowed on the server";
    public static final String BROADCAST_MESSAGE = "%s left the game because it's past their allowed time";

    private JavaPlugin plugin;
    private AllPlayerSchedules schedules;



    public Scheduler(JavaPlugin plugin, AllPlayerSchedules schedules) {
        this.plugin = plugin;
        this.schedules = schedules;
    }


    @Override
    public void run() {
        Logger log = plugin.getLogger();
        log.info("Checking if any players need to be kicked-off");
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            String playerName = player.getName();
            if (!schedules.isPlayerAllowed(playerName, Calendar.getInstance())) {
                player.kickPlayer(PLAYER_KICK_MESSAGE);
                plugin.getServer().broadcastMessage(String.format(BROADCAST_MESSAGE, playerName));
                log.info("   " + playerName + ": kicked-off, outside their hours");
            } else {
                log.info("   " + playerName + ": OK");
            }
        }
    }
}
