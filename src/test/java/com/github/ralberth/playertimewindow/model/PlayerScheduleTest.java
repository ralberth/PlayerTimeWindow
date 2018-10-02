package com.github.ralberth.playertimewindow.model;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(DataProviderRunner.class)
public class PlayerScheduleTest {

    private PlayerSchedule sched;


    @Before
    public void setup() {
        sched = new PlayerSchedule();
    }


    @Test
    public void daySchedule() {
        sched.setDayOfWeekSched("Sun", "9-10 11-12");
        List<TimeRange> ranges = sched.timeRanges.get(DayOfWeek.SUN);
        assertEquals(2, ranges.size());
        assertEquals(2, sched.timeRanges.size());
        assertEquals("9:00-9:59", ranges.get(0).toString());
        assertEquals("11:00-11:59", ranges.get(1).toString());
    }


    @DataProvider
    public static Object[][] isInRange() {
        return new Object[][] {
                { null,  null,    17, false, "empty timeRanges doesn't allow logins" },
                { "Thu", "21-23", 17, false, "Below low-bound" },
                { "Thu", "17-19", 17, true,  "At low-bound" },
                { "Thu", "16-18", 17, true,  "within bounds" },
                { "Thu", "12-17", 17, false, "At upper-bound" },
                { "Thu", "19-21", 17, false, "above upper-bound" },
                { "Thu",  "0-23", 17, true,  "0-23 is all-inclusive" },
                { "Thu",  "0-23",  0, true,  "midnight vs. 0-23" },
                { "Thu",  "0-24", 23, true,  "11PM vs. 0-23" },
                { "Wed", "15-19", 17, false, "wrong day of week" },
        };
    }


    @Test
    @UseDataProvider
    public void isInRange(String dow, String ranges, int hour, boolean shouldBeInRange, String errorMsg) {
        Calendar cal = mock(Calendar.class);
        when(cal.get(Calendar.DAY_OF_WEEK)).thenReturn(Calendar.THURSDAY);
        when(cal.get(Calendar.HOUR_OF_DAY)).thenReturn(hour);
        if (dow != null)
            sched.setDayOfWeekSched(dow, ranges);
        assertTrue(errorMsg, sched.isInRange(cal) == shouldBeInRange);
    }


    @Test
    public void dumpEmptySchedule() {
        assertEquals("", sched.dumpSchedule());
    }


    @Test
    public void dumpSchedules() {
        sched.setDayOfWeekSched("Mon", "3-5 7-9 15-21");
        sched.setDayOfWeekSched("Tue", "12-13");
        assertEquals("Mon: 3:00-4:59 7:00-8:59 15:00-20:59, Tue: 12:00-12:59", sched.dumpSchedule());
    }
}
