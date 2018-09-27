package com.github.ralberth.playertimewindow.logic;

import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Calendar;


/**
 * Respond to Bukkit LOGIN events and decide if the player should be let in based
 * on their schedule from config.yml.
 */
public class LoginEventHandler implements Listener {

    public static final PlayerLoginEvent.Result DISALLOW_REASON = PlayerLoginEvent.Result.KICK_OTHER;
    public static final String REASON_STRING = "It's outside the hours you're allowed to play";

    private Server server;
    private AllPlayerSchedules schedules;


    public LoginEventHandler(Server server, AllPlayerSchedules schedules) {
        this.server = server;
        this.schedules = schedules;
    }


    @EventHandler(ignoreCancelled = true)
    public void onLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        Calendar c = Calendar.getInstance();
        if (!schedules.isPlayerAllowed(playerName, Calendar.getInstance())) {
            event.disallow(DISALLOW_REASON, REASON_STRING);
            server.broadcastMessage(playerName + " tried to login outside of their allowed hours");
        }
    }
}
