package com.github.ralberth.playertimewindow.model;

import java.util.Calendar;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;


/**
 * Capture a range of time irrespective of date or day of week.
 *
 * "9 AM through 4 PM" for example.
 */
public class TimeRange {

    public static final String FORMAT_ERROR     = "Each time range should be two numbers separated by '-'";
    public static final String HOUR_RANGE_ERROR = "Hours must be between 0 and 24";

    private final int hourFrom; // INclusive
    private final int hourTo;   // EXclusive


    /**
     * Create a TimeRange from an input string.
     *
     * "0-23" means "all day, no restriction" because it encompasses all hours of the day.
     * Numbers outside the range [0,23] are not allowed.
     * @param input number + "-" + number, lower and upper hour bound in 24-hour notation
     */
    public TimeRange(String input) {
        notEmpty(input, "Time range cannot be empty");
        String[] parts = input.split("-");
        isTrue(parts.length == 2, FORMAT_ERROR);
        try {
            hourFrom = Integer.parseInt(parts[0].trim());
            hourTo = Integer.parseInt(parts[1].trim());
            isTrue(hourFrom >= 0 && hourFrom <= 24, HOUR_RANGE_ERROR);
            isTrue(hourTo >= 0 && hourTo <= 24, HOUR_RANGE_ERROR);
            isTrue(hourFrom < hourTo, "Low-bound has to be less than high-bound");
        } catch(NumberFormatException n) {
            throw new IllegalArgumentException(FORMAT_ERROR);
        }
    }


    /**
     * Return true iff the hour in the calendar passed in is within the hourFrom,hourTo
     * range, inclusive.
     *
     * @param c Valid Calendar, the hour of the day is inspected, the rest is ignored
     * @return true iff c's hour of the day is within [hourFrom,hourTo] range
     */
    public boolean isInRange(Calendar c) {
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        return hourOfDay >= hourFrom && hourOfDay < hourTo;
    }


    @Override
    public String toString() {
        return hourFrom + ":00 - " + (hourTo - 1) + ":59";
    }
}
