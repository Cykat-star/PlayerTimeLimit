package com.cykatstar.PlayerTimeLimit.listeners;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.model.TimeLimitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final PlayerTimeLimit plugin;

    public PlayerListener(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        
        plugin.getPlayerManager().loadOrCreateByInfo(e.getUniqueId().toString(), e.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        
        TimeLimitPlayer p = plugin.getPlayerManager().getPlayerByUUID(uuid);
        if (p != null) {
            p.setPlayer(player);
        } else {
            plugin.getPlayerManager().loadOrCreate(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getPlayerManager().unload(player);
        });
    }
}