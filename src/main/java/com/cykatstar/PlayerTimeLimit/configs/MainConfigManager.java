package com.cykatstar.PlayerTimeLimit.configs;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.configs.others.Notification;
import com.cykatstar.PlayerTimeLimit.configs.others.TimeLimit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainConfigManager {

    private final PlayerTimeLimit plugin;
    private ArrayList<TimeLimit> timeLimits;
    private boolean actionBar;
    private boolean bossBar;
    private String bossBarColor;
    private String bossBarStyle;
    private String resetTime;
    private boolean worldWhitelistEnabled;
    private List<String> worldWhitelistWorlds;
    private String worldWhitelistTeleportCoordinates;
    private ArrayList<Notification> notifications;

    public MainConfigManager(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        FileConfiguration config = plugin.getConfig();

        timeLimits = new ArrayList<>();
        ConfigurationSection limitsSection = config.getConfigurationSection("time_limits");
        if (limitsSection != null) {
            for (String key : limitsSection.getKeys(false)) {
                int time = config.getInt("time_limits." + key);
                timeLimits.add(new TimeLimit(key, time));
            }
        }

        actionBar = config.getBoolean("action_bar", false);
        bossBar = config.getBoolean("boss_bar.enabled", false);
        bossBarColor = config.getString("boss_bar.color", "BLUE");
        bossBarStyle = config.getString("boss_bar.style", "SOLID");
        resetTime = config.getString("reset_time", "00:00:00");
        
        worldWhitelistEnabled = config.getBoolean("world_whitelist_system.enabled", false);
        worldWhitelistWorlds = config.getStringList("world_whitelist_system.worlds");
        worldWhitelistTeleportCoordinates = config.getString("world_whitelist_system.teleport_coordinates_on_kick");

        notifications = new ArrayList<>();
        ConfigurationSection notificationSection = config.getConfigurationSection("notification");
        if (notificationSection != null) {
            for (String key : notificationSection.getKeys(false)) {
                try {
                    int seconds = Integer.parseInt(key);
                    List<String> message = config.getStringList("notification." + key + ".message");
                    notifications.add(new Notification(seconds, message));
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid notification time key: " + key);
                }
            }
        }
    }

    public boolean isActionBar() { return actionBar; }
    public boolean isBossBar() { return bossBar; }
    public String getBossBarColor() { return bossBarColor; }
    public String getBossBarStyle() { return bossBarStyle; }
    public String getResetTime() { return resetTime; }
    public ArrayList<TimeLimit> getTimeLimits() { return timeLimits; }
    public boolean isWorldWhitelistEnabled() { return worldWhitelistEnabled; }
    public List<String> getWorldWhitelistWorlds() { return worldWhitelistWorlds; }
    public String getWorldWhitelistTeleportCoordinates() { return worldWhitelistTeleportCoordinates; }
    public ArrayList<Notification> getNotifications() { return notifications; }

    public Notification getNotificationAtTime(int seconds) {
        for (Notification n : notifications) {
            if (n.getSeconds() == seconds) {
                return n;
            }
        }
        return null;
    }
    public String getKickMessage() {
        return plugin.getConfig().getString("kick_message", "&cYou have reached your daily time limit!");
    }
}