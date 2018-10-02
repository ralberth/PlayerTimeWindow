package com.github.ralberth.playertimewindow.model;

import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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


    public String dumpSchedule() {
        List<String> scheds = new ArrayList<String>(); // each entry is one weekday's collection of time ranges

        for(DayOfWeek dow : DayOfWeek.values()) {
            List<TimeRange> ranges = timeRanges.get(dow);
            if (ranges != null) {
                List<String> timeRangeStrings = new ArrayList<String>();
                for (TimeRange range : ranges)
                    timeRangeStrings.add(range.toString());
                if (!timeRangeStrings.isEmpty())
                    scheds.add(dow + ": " + StringUtils.join(timeRangeStrings, " "));
            }
        }

        return StringUtils.join(scheds, ", ");
    }
}
