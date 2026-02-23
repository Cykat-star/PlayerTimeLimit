package com.cykatstar.PlayerTimeLimit.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.configs.MainConfigManager;
import com.cykatstar.PlayerTimeLimit.configs.others.Notification;
import com.cykatstar.PlayerTimeLimit.libs.actionbar.ActionBarAPI;
import com.cykatstar.PlayerTimeLimit.libs.bossbar.BossBarAPI;
import com.cykatstar.PlayerTimeLimit.managers.MessageManager;
import com.cykatstar.PlayerTimeLimit.managers.PlayerManager;
import com.cykatstar.PlayerTimeLimit.managers.ServerManager;
import com.cykatstar.PlayerTimeLimit.model.TimeLimitPlayer;
import com.cykatstar.PlayerTimeLimit.utils.UtilsTime;

import java.util.List;

public class PlayerTimeTask {

    private final PlayerTimeLimit plugin;

    public PlayerTimeTask(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                execute();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void execute() {
        MainConfigManager mainConfig = plugin.getConfigsManager().getMainConfigManager();
        PlayerManager playerManager = plugin.getPlayerManager();
        ServerManager serverManager = plugin.getServerManager();

        boolean actionBarEnabled = mainConfig.isActionBar();
        boolean bossBarEnabled = mainConfig.isBossBar();
        String bossBarColor = mainConfig.getBossBarColor();
        String bossBarStyle = mainConfig.getBossBarStyle();

        for (TimeLimitPlayer p : playerManager.getPlayers()) {
            Player player = p.getPlayer();
            
            if (player != null && player.isOnline()) {
                World world = player.getWorld();
                
                if (!serverManager.isValidWorld(world)) {
                    p.removeBossBar();
                    continue;
                }

                p.increaseTime();

                sendActionBar(player, p, actionBarEnabled);
                sendBossBar(player, p, bossBarEnabled, bossBarColor, bossBarStyle);
                
                sendNotification(player, p, mainConfig);
                
                playerManager.checkUserTime(player, p);
            }
        }
    }

    public void sendNotification(Player player, TimeLimitPlayer p, MainConfigManager mainConfig) {
        PlayerManager playerManager = plugin.getPlayerManager();
        int timeLimit = playerManager.getTimeLimitPlayer(player);
        
        if (timeLimit <= 0) return;

        int remainingTime = timeLimit - p.getCurrentTime();

        Notification notification = mainConfig.getNotificationAtTime(remainingTime);
        if (notification == null) return;

        List<String> message = notification.getMessage();
        if (message != null) {
            for (String line : message) {
                player.sendMessage(MessageManager.getColoredMessage(line));
            }
        }
    }

    public void sendActionBar(Player player, TimeLimitPlayer p, boolean enabled) {
        if (!p.isMessageEnabled() || !enabled) return;

        PlayerManager playerManager = plugin.getPlayerManager();
        int timeLimit = playerManager.getTimeLimitPlayer(player);
        
        if (timeLimit <= 0) return;

        MessageManager msgManager = plugin.getMessageManager();
        
        String actionBarMessage = msgManager.getActionBarMessage();
        int remaining = timeLimit - p.getCurrentTime();
        String timeString = UtilsTime.getTime(remaining, msgManager);

        actionBarMessage = actionBarMessage.replace("%time%", timeString);
        ActionBarAPI.sendActionBar(player, actionBarMessage);
    }

    public void sendBossBar(Player player, TimeLimitPlayer p, boolean enabled, String bossBarColor, String bossBarStyle) {
        if (!p.isMessageEnabled() || !enabled) {
            p.removeBossBar();
            return;
        }

        if (Bukkit.getVersion().contains("1.8")) return;

        PlayerManager playerManager = plugin.getPlayerManager();
        int timeLimit = playerManager.getTimeLimitPlayer(player);

        if (timeLimit <= 0) {
            p.removeBossBar();
            return;
        }

        MessageManager msgManager = plugin.getMessageManager();
        
        int remainingTime = timeLimit - p.getCurrentTime();
        String timeString = UtilsTime.getTime(remainingTime, msgManager);
        
        String title = "\u00A78[ \u00A7b\u00A7l" + timeString + " \u00A7f\u00A7lRemaining \u00A78]";

        BossBar bossBar = p.getBossBar();
        if (bossBar == null) {
            try {
                BarColor color = BarColor.valueOf(bossBarColor.toUpperCase());
                BarStyle style = BarStyle.valueOf(bossBarStyle.toUpperCase());
                bossBar = BossBarAPI.create(player, title, color, style);
                p.setBossBar(bossBar);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid BossBar color or style in config!");
                return;
            }
        }

        bossBar.setTitle(MessageManager.getColoredMessage(title));
        
        double ratio = (double) remainingTime / timeLimit;
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, ratio)));
    }
}