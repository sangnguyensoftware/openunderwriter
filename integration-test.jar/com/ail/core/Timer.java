/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.core;

import java.util.HashMap;

/**
 * Timer to help make crude performance measurements from unit tests. Think of this class as a stop watch; each method (start,
 * stop, split, delta) takes a name - that's the name of the watch. You can have an many different watches running at the same
 * time as you like.
 */
public class Timer {
    static HashMap<String,Long> timers=new HashMap<String,Long>();
    
    /**
     * Start a new watch running
     * @param name Name of watch
     */
    public static void start(String name) {
        timers.put(name, System.currentTimeMillis());
    }
            
    /**
     * Report the split time for a running watch. This reports the time since the stop watch started, and if there was a previous
     * split time the time since that too (in brackets).
     * @param name name of watch
     * @return time since 'start'
     */
    public static long split(String name) {
        long timeNow=System.currentTimeMillis();
        long ret=(timeNow-timers.get(name));
        if (timers.containsKey(name+".split")) {
            System.out.printf("%s split: %dms (%dms)\n", name, ret, timeNow-timers.get(name+".split"));
        }
        else {
            System.out.printf("%s split: %dms\n", name, ret);
        }
        timers.put(name+".split", timeNow);

        return ret;
    }
        
    /**
     * Similar to split(), but this returns the time since the last split rather than the time since the watch started.
     * @param name name of watch
     * @return time since last split, of since start if there have been no splits
     */
    public static long delta(String name) {
        long timeNow=System.currentTimeMillis();
        long ret=(timeNow-timers.get(name));
        if (timers.containsKey(name+".split")) {
            System.out.printf("%s split: %dms (%dms)\n", name, ret, timeNow-timers.get(name+".split"));
            timers.put(name+".split", timeNow);
            return timeNow-timers.get(name+".split");
        }
        else {
            System.out.printf("%s split: %dms\n", name, ret);
            timers.put(name+".split", timeNow);
            return ret;
        }
    }

    /**
     * Stop the watch, report it's results and delete it
     * @param name Name of watch
     * @return total time since the watch started.
     */
    public static long stop(String name) {
        long timeNow=System.currentTimeMillis();
        long ret=(timeNow-timers.get(name));
        if (timers.containsKey(name+".split")) {
            System.out.printf("%s stop: %dms (%dms)\n", name, ret, timeNow-timers.get(name+".split"));
        }
        else {
            System.out.printf("%s stop: %dms\n", name, ret);
        }
        return ret;
    }
}
