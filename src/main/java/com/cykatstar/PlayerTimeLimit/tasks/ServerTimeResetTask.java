package com.cykatstar.PlayerTimeLimit.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.scheduler.BukkitRunnable;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.configs.MainConfigManager;

public class ServerTimeResetTask {

    private final PlayerTimeLimit plugin;
    private final DateTimeFormatter dtf;
    private String lastResetDate = "";

    public ServerTimeResetTask(PlayerTimeLimit plugin) {
        this.plugin = plugin;
        this.dtf = DateTimeFormatter.ofPattern("HH:mm");
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                execute();
            }
        }.runTaskTimer(plugin, 0L, 1200L); 
    }

    public void execute() {
        MainConfigManager mainConfig = plugin.getConfigsManager().getMainConfigManager();
        String resetTime = mainConfig.getResetTime();

        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        String currentDate = now.toLocalDate().toString();

        if (resetTime.equals(currentTime) && !lastResetDate.equals(currentDate)) {
            
            lastResetDate = currentDate;

            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getPlayerManager().resetPlayers();
                    
                    plugin.getServerManager().saveDataTime();
                    
                    plugin.getLogger().info("Global time reset executed successfully.");
                }
            }.runTaskAsynchronously(plugin);
        }
    }
}