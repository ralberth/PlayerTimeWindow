package com.github.ralberth.playertimewindow.util;

import java.util.Calendar;

/**
 * Utility class that acts as a predicate answering "is the plugin enabled and disallowing users from playing?".
 *
 * There are two concepts to get confused: enable/disable at the plugin level and enable/disable from the user's
 * point of view.  This plugin has an internal concept of enable/disable implemented as callbacks in class
 * PlayerTimeWindow.  This is part of the normal plugin lifecycle, and the callbacks are invoked by Minecraft
 * as it starts up.
 *
 * Enable/disable from a user's point of view mean whether or not this plugin is actively watching and denying
 * users from connecting based on their timewindow, or if the plugin is just sitting idly by letting all users
 * login regardless of their timewindow config.
 *
 * Sure, this class could have been "ActiveStatus", but the name is driven by the best description a human has
 * for how this works.  "Non-active" isn't as clear a concept as "the time window plugin is disabled, so you
 * can login whenever you like."  Customer-centric here, we deal with the name clash internally.
 *
 * This class holds a boolean state anyone can query via a public method.  Anyone can set it true (plugin is
 * preventing certain players from playing), but it can't be set false without an expiration time.  This prevents
 * a person from turning it off and forgetting that it's off for days/weeks :-)
 *
 * Two Use Cases:
 *      1. Disable: You must pass how long it can be disabled.  When this expires, this
 *                  class automatically flips back to Enabled.
 *      2. Enable:  Flip to enabled immediately, even if it is currently disabled and
 *                  waiting for an expire time to arrive.
 *
 * This is fully thread-safe because it doesn't use any background threading to sleep until a specified time.
 * Once the duration has past, there are no actions to be taken in here until someone else asks for the
 * enable/disabled status.  So, we simply delay detecting an expire time until someone asks for a status.
 */
public class EnabledStatus {

    boolean enabled = true;
    Calendar disabledExpireTime = null;


    public void enable() {
        enabled = true;
        disabledExpireTime = null;
    }


    public void disable(int hours) {
        if (hours > 0) {
            enabled = false;
            disabledExpireTime = Calendar.getInstance();
            disabledExpireTime.add(Calendar.HOUR, hours);
        }
    }


    public boolean isEnabled() {
        if (!enabled && disabledExpireTime.before(now()))
            enable();
        return enabled;
    }


    Calendar now() { // design for testability
        return Calendar.getInstance();
    }
}
