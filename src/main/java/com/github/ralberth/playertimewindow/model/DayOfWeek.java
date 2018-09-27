package com.github.ralberth.playertimewindow.model;


import java.util.Calendar;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * Enum where each value represents a day of the week, starting from Sunday.
 *
 * Yea, JodaTime and many other time/date libraries do a great job of doing
 * exactly this.  However, we're going for simple and small here and don't
 * want to depend on another library we'd have to package along with our
 * code in this Bukkit plugin: simpler to implement the basics here.
 */
public enum DayOfWeek {

    SUN (Calendar.SUNDAY,    "Sun"),
    MON (Calendar.MONDAY,    "Mon"),
    TUE (Calendar.TUESDAY,   "Tue"),
    WED (Calendar.WEDNESDAY, "Wed"),
    THU (Calendar.THURSDAY,  "Thu"),
    FRI (Calendar.FRIDAY,    "Fri"),
    SAT (Calendar.SATURDAY,  "Sat");

    private final int calendarDowValue;
    private final String label;


    private DayOfWeek(int calendarDowValue, String label) {
        this.calendarDowValue = calendarDowValue;
        this.label = label;
    }


    public static DayOfWeek fromCalendar(Calendar cal) {
        notNull(cal, "Calendar arg to fromCalendar cannot be null");

        int calDow = cal.get(Calendar.DAY_OF_WEEK);
        for(DayOfWeek enumDow : DayOfWeek.values()) {
            if (enumDow.calendarDowValue == calDow) {
                return enumDow;
            }
        }

        throw new IllegalArgumentException("Calendar's DAY_OF_WEEK had bad value");
    }


    public static DayOfWeek fromLabel(String label) {
        notEmpty(label, "Day of week string arg to DayOfWeek.fromLabel() cannot be null or empty");

        for(DayOfWeek dow : DayOfWeek.values()) {
            if (dow.label.equalsIgnoreCase(label)) {
                return dow;
            }
        }

        throw new IllegalArgumentException("Day of week \"" + label +
                "\" wasn't valid.  Use Sun, Mon, Tue, Wed, Thu, Fri, or Sat.");
    }


    public String toString() {
        return label;
    }
}
