package com.github.ralberth.playertimewindow.model;

import com.google.common.collect.ArrayListMultimap;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static org.apache.commons.lang.Validate.notEmpty;


public class PlayerSchedule {

    ArrayListMultimap<DayOfWeek, TimeRange> timeRanges = ArrayListMultimap.create();


    public void setDayOfWeekSched(String dayOfWeek, String rawValue) {
        notEmpty(dayOfWeek, "Day of week cannot be empty");
        notEmpty(rawValue, "Schedule for day of week cannot be empty");

        DayOfWeek dow = DayOfWeek.fromLabel(dayOfWeek);

        String[] rawRanges = rawValue.split("[, \t]+");
        for(String rawRange : rawRanges)
            timeRanges.put(dow, new TimeRange(rawRange));
    }


    public boolean isInRange(Calendar c) {
        DayOfWeek enumDow = DayOfWeek.fromCalendar(c);
        List<TimeRange> ranges = timeRanges.get(enumDow);
        if (ranges != null) {
            for (TimeRange r : ranges) {
                if (r.isInRange(c)) {
                    return true;
                }
            }
        }
        return false;
    }


    public void dumpSchedule(Logger log) {
        for(DayOfWeek dow : DayOfWeek.values()) {
            List<TimeRange> ranges = timeRanges.get(dow);
            if (ranges != null) {
                StringBuilder b = new StringBuilder();
                for (TimeRange range : ranges)
                    b.append(range.toString() + " ");
                log.info("      " + dow + ": " + b);
            }
        }
    }
}
