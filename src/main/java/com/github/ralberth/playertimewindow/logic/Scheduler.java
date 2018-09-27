package com.github.ralberth.playertimewindow.logic;

import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Scheduler extends BukkitRunnable {

    private JavaPlugin plugin;
    private AllPlayerSchedules schedules;



    public Scheduler(JavaPlugin plugin, AllPlayerSchedules schedules) {
        this.plugin = plugin;
        this.schedules = schedules;
    }


    @Override
    public void run() {
        //plugin.getServer().getOnlinePlayers()
    }
}
