package com.cykatstar.PlayerTimeLimit.utils;

import java.util.Calendar;
import com.cykatstar.PlayerTimeLimit.managers.MessageManager;

public class UtilsTime {

    public static String getTime(long seconds, MessageManager msgManager) {
        if (seconds <= 0) {
            return "0" + msgManager.getTimeSeconds();
        }

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder timeStr = new StringBuilder();

        if (days > 0) {
            timeStr.append(days).append(msgManager.getTimeDays()).append(" ");
        }
        if (hours > 0) {
            timeStr.append(hours).append(msgManager.getTimeHours()).append(" ");
        }
        if (minutes > 0) {
            timeStr.append(minutes).append(msgManager.getTimeMinutes()).append(" ");
        }
        if (secs > 0 || timeStr.length() == 0) {
            timeStr.append(secs).append(msgManager.getTimeSeconds());
        }

        return timeStr.toString().trim();
    }

    public static long getNextResetMillis(String resetTimeHour) {
        String[] sep = resetTimeHour.split(":");
        int hour = Integer.parseInt(sep[0]);
        int minute = Integer.parseInt(sep[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        return calendar.getTimeInMillis();
    }
}