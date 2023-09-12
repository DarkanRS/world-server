package com.rs.utils;

import com.rs.lib.util.Utils;

import java.util.concurrent.TimeUnit;

public class Timer {
    private long time = 0l;
    private long timeMs = 0l;
    public Timer() {

    }

    public Timer start() {
        time = System.nanoTime();
        return this;
    }

    public String stop() {
        time = System.nanoTime() - time;
        timeMs = TimeUnit.NANOSECONDS.toMillis(time);
        return getFormattedTime();
    }

    public String getFormattedTime() {
        if (timeMs > 5)
            return Utils.formatLong(timeMs) + "ms";
        return Utils.formatDouble(time / 1000000.0) + "ms";
    }

    public long getTimeMs() {
        return timeMs;
    }
}
