package com.github.ralberth.playertimewindow;

import com.github.ralberth.playertimewindow.logic.LoginEventHandler;
import com.github.ralberth.playertimewindow.logic.Scheduler;
import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PlayerTimeWindow extends JavaPlugin {

    public static final String TIMEWINDOWS_COMMAND = "timewindows";

    public static final long TICKS_PER_SECOND = 20;
    public static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * 60; // 5 TPS * 60 sec/min

    private AllPlayerSchedules schedules;
    private Scheduler scheduler;


    @Override
    public void onEnable() {
        saveDefaultConfig(); // Creates config.yml from src/main/resources if not present
        schedules = loadConfig();

        if (scheduler != null)
            scheduler.cancel();
        scheduler = new Scheduler(this, schedules);
        scheduler.runTaskTimer(this, 0, 5 * TICKS_PER_MINUTE); // every 5 minutes

        LoginEventHandler logins = new LoginEventHandler(getServer(), schedules);
        getServer().getPluginManager().registerEvents(logins, this);
    }


    @Override
    public void onDisable() {
        if (scheduler != null) {
            scheduler.cancel();
            scheduler = null;
        }
    }


    private AllPlayerSchedules loadConfig() {
        AllPlayerSchedules aps = new AllPlayerSchedules();

        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) {
            try {
                YamlConfiguration config = new YamlConfiguration();
                config.load(configFile);
                aps.load(config);
            } catch(Exception e) {
                getLogger().info("Couldn't load config.yml file: " + e);
            }
        } else {
            getLogger().info("No " + configFile + " present, PlayerTimeWindow disabled");
        }

        return aps;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase(TIMEWINDOWS_COMMAND)) { // If the player typed /basic then do the following, note: If you only registered this executor for one command, you don't need this
            getLogger().info("Player Time Windows from plugins/PlayerTimeWindow/config.yml:");
            schedules.dump(getLogger());
            return true;
        }
        return false;
    }
}
