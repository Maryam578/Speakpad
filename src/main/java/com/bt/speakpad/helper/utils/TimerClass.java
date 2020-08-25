package com.bt.speakpad.helper.utils;

import java.util.Timer;

public class TimerClass {
    private static Timer timer;

    public static synchronized Timer getInstance() {
        if (timer == null) {
            timer = new Timer();
            return timer;
        }
        return timer;
    }
}
