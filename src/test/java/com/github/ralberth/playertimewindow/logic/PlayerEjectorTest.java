package com.github.ralberth.playertimewindow.logic;


import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import com.github.ralberth.playertimewindow.util.EnabledStatus;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(DataProviderRunner.class)
public class PlayerEjectorTest {

    Server server;
    AllPlayerSchedules schedules;
    EnabledStatus status;
    PlayerEjector pe;


    @Before
    public void setup() {
        server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getGlobal());
        schedules = mock(AllPlayerSchedules.class);
        status = new EnabledStatus();
        pe = new PlayerEjector(server, schedules, status);
    }


    @DataProvider
    public static Object[][] clearWarningsForNonactivePlayers() {
        Player joe = mkPlayer("joe");
        Player sue = mkPlayer("sue");
        Player ron = mkPlayer("ron");
        Player amy = mkPlayer("amy");;

        return new Object[][] {
                // ONLINE PLAYERS,                 WARNED PLAYERS,                  REMAINING WARNED PLAYERS
                { new Player[] { },                new Player[] { },                new Player[] { } },
                { new Player[] { joe, sue },       new Player[] { },                new Player[] { } },
                { new Player[] { },                new Player[] { joe, sue },       new Player[] { } },
                { new Player[] { joe, sue, amy },  new Player[] { amy, sue, ron },  new Player[] { amy, sue } },
                { new Player[] { joe, amy },       new Player[] { joe, amy },       new Player[] { joe, amy } }
        };
    }


    @Test
    @UseDataProvider
    public void clearWarningsForNonactivePlayers(Player[] onlinePlayersAry, Player[] warnedPlayersAry, Player[] expectedRemaining) {
        Set<Player> onlinePlayers = toSet(onlinePlayersAry);
        Set<Player> warnedPlayers = toSet(warnedPlayersAry);
        Set<Player> expectedSet   = toSet(expectedRemaining);

        pe.warnedPlayersToKickNextCycle = warnedPlayers;
        pe.clearWarningsForNonactivePlayers(onlinePlayers);

        assertEquals(expectedSet, pe.warnedPlayersToKickNextCycle);
    }


    @Test
    public void warnThenKick() {
        Player tom = mkPlayer("tom");
        when(pe.schedules.isPlayerAllowed(anyString(), any(Calendar.class))).thenReturn(false);
        Set<Player> online = new HashSet<>();
        online.add(tom);

        pe.notifyOrKickPlayers(online);                                // should tell tom he's warned
        assertTrue(pe.warnedPlayersToKickNextCycle.contains(tom));
        verify(tom, never()).kickPlayer(anyString());                  // tom wasn't kicked

        pe.notifyOrKickPlayers(online);                                // tom still online!
        verify(tom).kickPlayer(anyString());                           // tom verified was kicked off
        assertTrue(pe.warnedPlayersToKickNextCycle.isEmpty());         // and warning state cleared
    }


    @Test
    public void warnThenLeft() {
        Player tom = mkPlayer("tom");
        when(pe.schedules.isPlayerAllowed(anyString(), any(Calendar.class))).thenReturn(false);
        pe.warnedPlayersToKickNextCycle.clear();

        final Collection<Player> online = new HashSet<>();
        online.add(tom);

        setOnlinePlayers(online);                                          // tom is online
        pe.run();
        assertTrue(pe.warnedPlayersToKickNextCycle.contains(tom));         // tom was warned

        setOnlinePlayers(new HashSet<Player>());                           // tom logged out already
        pe.run();
        assertTrue(pe.warnedPlayersToKickNextCycle.isEmpty());             // and warning state cleared

        verify(tom, never()).kickPlayer(anyString());                      // didn't try to kick her off
    }


    /*
     * See https://stackoverflow.com/questions/17343759/having-an-issue-with-mocking-a-method-which-has-a-generic-extends-collection
     */
    private void setOnlinePlayers(final Collection<Player> players) {
        when(server.getOnlinePlayers()).thenAnswer(new Answer<Collection<Player>>() {

            @Override
            public Collection<Player> answer(InvocationOnMock invocation) throws Throwable {
                return players;
            }
        });
    }


    private static Player mkPlayer(String name) {
        Player p = mock(Player.class);
        when(p.getName()).thenReturn(name);
        return p;
    }


    private static <T> Set<T> toSet(T[] things) {
        Set<T> ret = new HashSet<>();
        for(T thing : things)
            ret.add(thing);
        return ret;
    }
}
