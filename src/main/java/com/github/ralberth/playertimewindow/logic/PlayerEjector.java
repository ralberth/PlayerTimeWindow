package com.github.ralberth.playertimewindow.logic;

import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import com.github.ralberth.playertimewindow.util.EnabledStatus;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.collect.Sets.difference;


/**
 * Runnable scheduled and executed by Bukkit every 5 minutes by PlayerTimeWindow.java.
 *
 * This looks at all currently-logged in users and kicks anyone off that is on the server
 * but shouldn't based on their scheduled hours.
 *
 * If we keep state on a player and the player logs out on their own, the state left here will be stale:
 * consider this scenario:
 *      1. 13:00, tell player it's time to logout
 *      2. 13:05, tell them again, last warning
 *      3. 13:06, player logs out
 *      => if we're keeping any state at this point and the player leaves, the state will still be around
 *         the next time they connect!  They may be booted too soon.
 */
public class PlayerEjector implements Runnable {

    public static final String WARNING_MESSAGE     = "%s, it's almost time to logout.  Finish up what you're doing and quit, please.";
    public static final String PLAYER_KICK_MESSAGE = "It is now past when you are allowed on the server";
    public static final String BROADCAST_MESSAGE   = "%s left the game because it's past their allowed time";

    Server server;
    AllPlayerSchedules schedules;
    EnabledStatus status;
    Set<Player> warnedPlayersToKickNextCycle;


    public PlayerEjector(Server server, AllPlayerSchedules schedules, EnabledStatus status) {
        this.server = server;
        this.schedules = schedules;
        this.status = status;
        warnedPlayersToKickNextCycle = new HashSet<>();
    }


    @Override
    public void run() {
        if (status.isEnabled()) {
            Set<Player> onlinePlayers = new HashSet<>(server.getOnlinePlayers());
            notifyOrKickPlayers(onlinePlayers);
            clearWarningsForNonactivePlayers(onlinePlayers);
        }
    }


    void notifyOrKickPlayers(Set<Player> onlinePlayers) {
        for (Player player : onlinePlayers) {
            String playerName = player.getName();
            if (!schedules.isPlayerAllowed(playerName, Calendar.getInstance()))
                handlePlayerAfterHours(player);
        }
    }


    void clearWarningsForNonactivePlayers(Set<Player> onlinePlayers) {
        // The loop above only visited Players that are logged-in.  If anyone just logged out, clear their warning.
        // (avoiding streaming and removeIf() to be backwards compatible with earlier JVMs)
        Set<Player> toBeRemoved = new HashSet<>();
        for(Player p : warnedPlayersToKickNextCycle)
            if (!onlinePlayers.contains(p))
                toBeRemoved.add(p);

        warnedPlayersToKickNextCycle.removeAll(toBeRemoved);
    }


    /**
     * It's unfair to just kick players off the server the moment it's past their time.  Better to give them
     * some notice and a count-down so they can finish what they're doing and logout themselves.
     *
     */
    void handlePlayerAfterHours(Player player) {
        String playerName = player.getName();

        if (warnedPlayersToKickNextCycle.contains(player)) {
            // Already warned them, kick them off
            player.kickPlayer(PLAYER_KICK_MESSAGE);
            server.broadcastMessage(String.format(BROADCAST_MESSAGE, playerName));
            warnedPlayersToKickNextCycle.remove(player);
        } else {
            server.broadcastMessage(String.format(WARNING_MESSAGE, playerName));
            warnedPlayersToKickNextCycle.add(player);
        }
    }
}
