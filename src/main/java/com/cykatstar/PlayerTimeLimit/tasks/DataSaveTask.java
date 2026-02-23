package com.cykatstar.PlayerTimeLimit.tasks;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DataSaveTask {

    private final PlayerTimeLimit plugin;
    private BukkitRunnable task;

    public DataSaveTask(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    public void start(int intervalSeconds) {
        if (intervalSeconds <= 0) {
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix 
                    + "§eDataSaveTask disabled (interval <= 0).");
            return;
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getPlayerManager().saveAllPlayers();
            }
        };

        task.runTaskTimerAsynchronously(plugin, intervalSeconds * 20L, intervalSeconds * 20L);

        Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix 
                + "§aDataSaveTask started. Interval: " + intervalSeconds + " seconds.");
    }

    public void end() {
        if (task != null) {
            task.cancel();
        }
        
        plugin.getPlayerManager().saveAllPlayers();
        
        Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix 
                + "§eDataSaveTask stopped and final save completed.");
    }
}