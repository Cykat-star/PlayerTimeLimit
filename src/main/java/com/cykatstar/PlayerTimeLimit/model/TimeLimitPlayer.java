package com.cykatstar.PlayerTimeLimit.model;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TimeLimitPlayer {

    private final String uuid;
    private final String name;
    private Player player;
    private int currentTime;
    private int totalTime;
    private boolean messageEnabled;
    private BossBar bossBar;

    private boolean modified;

    public TimeLimitPlayer(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.currentTime = 0;
        this.totalTime = 0;
        this.messageEnabled = false;
        this.modified = false; 
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void increaseTime() {
        this.currentTime++;
        this.totalTime++;
        this.modified = true;
    }

    public void resetTime() {
        this.currentTime = 0;
        this.modified = true;
    }

    public void takeTime(int time) {
        this.currentTime += time;
        this.modified = true;
    }

    public void addTime(int time) {
        this.currentTime -= time;
        this.modified = true;
    }

    public String getUuid() { return uuid; }
    public String getName() { return name; }
    
    public int getCurrentTime() { return currentTime; }
    public void setCurrentTime(int currentTime) { 
        this.currentTime = currentTime; 
        this.modified = true;
    }

    public int getTotalTime() { return totalTime; }
    public void setTotalTime(int totalTime) { 
        this.totalTime = totalTime; 
        this.modified = true;
    }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public boolean isMessageEnabled() { return messageEnabled; }
    public void setMessageEnabled(boolean messageEnabled) { this.messageEnabled = messageEnabled; }

    public BossBar getBossBar() { return bossBar; }
    public void setBossBar(BossBar bossBar) { this.bossBar = bossBar; }

    public void removeBossBar() {
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            bossBar = null;
        }
    }
}