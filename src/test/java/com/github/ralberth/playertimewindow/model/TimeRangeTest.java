package com.github.ralberth.playertimewindow.model;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class TimeRangeTest {

    @DataProvider
    public static Object[][] tests() {
        return new Object[][] {
                { null,      null, "Time range cannot be empty" },
                { "",        null, "Time range cannot be empty" },
                { "  ",      null, "Each time range should be two numbers separated by '-'" },
                { "12",      null, "Each time range should be two numbers separated by '-'" },
                { "6-",      null, "Each time range should be two numbers separated by '-'" },
                { "-7",      null, "Each time range should be two numbers separated by '-'" },
                { "4-4",     null, "Low-bound has to be less than high-bound"               },
                { "9-12-13", null, "Each time range should be two numbers separated by '-'" },
                { "21-25",   null, "Hours must be between 0 and 24"                         },
                { "10-15",   "10:00 - 14:59", null }
        };
    }


    @Test
    @UseDataProvider("tests")
    public void tests(String input, String expectedToString, String expectedExceptionMessage) {
        try {
            TimeRange tr = new TimeRange(input);
            assertEquals(expectedToString, tr.toString());
        } catch(IllegalArgumentException e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }


    @DataProvider
    public static Object[][] isInRange() {
        return new Object[][] {
                { "4-6", 3, false },
                { "4-6", 4, true  },
                { "4-6", 5, true  },
                { "4-6", 6, false },
                { "4-6", 7, false }
        };
    }


    @Test
    @UseDataProvider
    public void isInRange(String input, int hour, boolean expected) {
        TimeRange range = new TimeRange(input);
        Calendar cal = mock(Calendar.class);
        when(cal.get(Calendar.HOUR_OF_DAY)).thenReturn(hour);
        assertEquals(expected, range.isInRange(cal));
    }
}
