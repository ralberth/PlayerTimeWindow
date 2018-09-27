package com.github.ralberth.playertimewindow.model;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;


@RunWith(DataProviderRunner.class)
public class AllPlayerSchedulesTest {

    @Test
    public void empty() throws Exception {
        AllPlayerSchedules aps = createAPS("config_file_tests/empty.yml");
        assertEquals(0, aps.schedules.size());
    }

    @Test
    public void noPlayers() throws Exception {
        AllPlayerSchedules aps = createAPS("config_file_tests/noPlayers.yml");
        assertEquals(0, aps.schedules.size());
    }

    @Test
    public void players() throws Exception {
        AllPlayerSchedules aps = createAPS("config_file_tests/players.yml");
        assertEquals(2, aps.schedules.size());
        PlayerSchedule player2 = aps.schedules.get("Player2");
        assertEquals("9:00 - 10:59",  player2.timeRanges.get(DayOfWeek.MON).get(0).toString());
        assertEquals("13:00 - 15:59", player2.timeRanges.get(DayOfWeek.MON).get(1).toString());
        assertEquals("9:00 - 11:59",  player2.timeRanges.get(DayOfWeek.THU).get(0).toString());
    }

    private static AllPlayerSchedules createAPS(String resourceName) throws Exception {
        URL url = Resources.getResource(resourceName);
        String yaml = Resources.toString(url, Charsets.UTF_8);
        YamlConfiguration mc = new YamlConfiguration();
        mc.loadFromString(yaml);

        AllPlayerSchedules aps = new AllPlayerSchedules();
        aps.load(mc);

        return aps;
    }
}
