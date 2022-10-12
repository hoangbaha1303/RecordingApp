package com.example.recordingapp;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
    public String getTimeAgo(long duration){
        Date now= new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - duration);
        long minutes = TimeUnit.MICROSECONDS.toMinutes(now.getTime() -duration);
        long hours   = TimeUnit.MICROSECONDS.toHours(now.getTime() -duration);
        long days    = TimeUnit.MICROSECONDS.toDays(now.getTime() -duration);

        if (seconds < 60){
            return "Just now";
        }
        else if (minutes == 1){
            return "A minute ago";
        }
        else if (minutes > 1 && minutes < 60){
            return minutes + "minutes ago";
        }
        else if (hours == 1){
            return "A hour ago";
        }
        else if (hours > 1 && hours < 24){
            return hours + "hours ago";
        }
        else if (days == 1){
            return "A day ago";
        }
        else {
            return days + "days ago";
        }
    }
}
