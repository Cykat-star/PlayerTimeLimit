package com.cykatstar.PlayerTimeLimit.managers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import net.md_5.bungee.api.ChatColor;
import com.cykatstar.PlayerTimeLimit.libs.centeredmessages.DefaultFontInfo;

public class MessageManager {
    
    private String prefix;
    private String actionBarMessage;
    private String bossBarMessage;
    private String timeSeconds;
    private String timeMinutes;
    private String timeHours;
    private String timeDays;
    private String timeInfinite;
    
    public MessageManager(String prefix) {
        this.prefix = prefix;
    }

    public void sendListMessage(CommandSender sender, List<String> messages) {
        if (messages == null || messages.isEmpty()) return;
        for (String line : messages) {
            sender.sendMessage(getColoredMessage(line));
        }
    }

    public String getJoinedMessages(List<String> messages) {
        if (messages == null || messages.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            sb.append(getColoredMessage(messages.get(i)));
            if (i < messages.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    public void sendMessage(CommandSender sender, String message, boolean usePrefix) {
        if (message == null || message.isEmpty()) return;
        
        String finalMsg = usePrefix ? this.prefix + message : message;
        sender.sendMessage(getColoredMessage(finalMsg));
    }
    
    public static String getColoredMessage(String text) {
        if (text == null) return "";

        if (isModernVersion()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher match = pattern.matcher(text);
            while (match.find()) {
                String color = text.substring(match.start(), match.end());
                text = text.replace(color, ChatColor.of(color) + "");
                match = pattern.matcher(text);
            }
        }
        
        text = ChatColor.translateAlternateColorCodes('&', text);
        
        if (text.contains("{centered}")) {
            text = text.replace("{centered}", "");
            text = getCenteredMessage(text);
        }
        return text;
    }

    private static boolean isModernVersion() {
        String version = Bukkit.getBukkitVersion();
        return version.matches("1\\.(1[6-9]|[2-9][0-9]).*");
    }
    
    public static String getCenteredMessage(String message) {
        if (message == null || message.isEmpty()) return "";
        
        int CENTER_PX = 154;
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
       
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
       
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString() + message;       
    }

    public String getActionBarMessage() { return actionBarMessage; }
    public void setActionBarMessage(String actionBarMessage) { this.actionBarMessage = actionBarMessage; }
    public String getBossBarMessage() { return bossBarMessage; }
    public void setBossBarMessage(String bossBarMessage) { this.bossBarMessage = bossBarMessage; }
    public String getTimeSeconds() { return timeSeconds; }
    public void setTimeSeconds(String timeSeconds) { this.timeSeconds = timeSeconds; }
    public String getTimeMinutes() { return timeMinutes; }
    public void setTimeMinutes(String timeMinutes) { this.timeMinutes = timeMinutes; }
    public String getTimeHours() { return timeHours; }
    public void setTimeHours(String timeHours) { this.timeHours = timeHours; }
    public String getTimeDays() { return timeDays; }
    public void setTimeDays(String timeDays) { this.timeDays = timeDays; }
    public String getTimeInfinite() { return timeInfinite; }
    public void setTimeInfinite(String timeInfinite) { this.timeInfinite = timeInfinite; }
}