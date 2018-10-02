package com.github.ralberth.playertimewindow.model;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;


/**
 * Essentially, a Java model that holds everything from plugins/PlayerTimeWindow/config.yml
 * section "schedules".
 */
public class AllPlayerSchedules {

    final Map<String, PlayerSchedule> schedules;


    public AllPlayerSchedules() {
        schedules = new HashMap<>();
    }


    public void load(Configuration config) {
        schedules.clear();
        ConfigurationSection entries = config.getConfigurationSection("schedules");

        if (entries != null) {
            for (String player : entries.getKeys(false)) {
                ConfigurationSection dowSection = entries.getConfigurationSection(player);
                if (dowSection != null) {
                    Set<String> daysOfWeek = dowSection.getKeys(false);
                    if (!daysOfWeek.isEmpty()) {
                        PlayerSchedule ps = new PlayerSchedule();
                        schedules.put(player, ps);
                        for (String dow : daysOfWeek) {
                            String rawSched = dowSection.getString(dow);
                            ps.setDayOfWeekSched(dow, rawSched);
                        }
                    }
                }
            }
        }
    }


    public boolean isPlayerAllowed(String playerName, Calendar now) {
        PlayerSchedule sched = schedules.get(playerName);
        return sched == null || sched.isInRange(now);
    }


    // Returns a list of strings, intended to be printed one string per line by caller
    public List<String> dump() {
        List<String> ret = new ArrayList<String>();
        if (!schedules.isEmpty()) {
            SortedSet<String> players = new TreeSet<>(schedules.keySet());
            if (!players.isEmpty()) {
                for (String player : players) {
                    String playerSched = schedules.get(player).dumpSchedule();
                    if (!playerSched.isEmpty())
                        ret.add(player + ": " + playerSched);
                }
            }
        }
        return ret;
    }
}
