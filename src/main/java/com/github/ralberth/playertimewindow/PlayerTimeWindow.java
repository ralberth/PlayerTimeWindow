package com.github.ralberth.playertimewindow;

import com.github.ralberth.playertimewindow.logic.CommandLine;
import com.github.ralberth.playertimewindow.logic.LoginEventHandler;
import com.github.ralberth.playertimewindow.logic.PlayerEjector;
import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import com.github.ralberth.playertimewindow.util.PeriodicExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PlayerTimeWindow extends JavaPlugin {

    public static final String TIMEWINDOWS_COMMAND = "timewindows";

    public static final int MINUTES_BETWEEN_KICK_CHECKS = 5;

    private PeriodicExecutor playerEjectorScheduler;
    private CommandLine cmdline;


    @Override
    public void onEnable() {
        saveDefaultConfig(); // Creates config.yml from src/main/resources if not present
        AllPlayerSchedules schedules = loadConfig();

        if (playerEjectorScheduler != null)
            playerEjectorScheduler.stop();

        PlayerEjector ejector = new PlayerEjector(this, schedules);
        playerEjectorScheduler = new PeriodicExecutor(this, ejector, MINUTES_BETWEEN_KICK_CHECKS);
        playerEjectorScheduler.start();

        LoginEventHandler logins = new LoginEventHandler(getServer(), schedules);
        getServer().getPluginManager().registerEvents(logins, this);

        cmdline = new CommandLine(schedules);
        this.getCommand(TIMEWINDOWS_COMMAND).setExecutor(cmdline);
    }


    @Override
    public void onDisable() {
        if (playerEjectorScheduler != null) {
            playerEjectorScheduler.stop();
            playerEjectorScheduler = null;
        }

        HandlerList.unregisterAll(this);
        cmdline.setActive(false);
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
}
