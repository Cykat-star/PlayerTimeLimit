package com.cykatstar.PlayerTimeLimit.api;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.managers.PlayerManager;
import com.cykatstar.PlayerTimeLimit.model.TimeLimitPlayer;
import com.cykatstar.PlayerTimeLimit.utils.UtilsTime;
import org.bukkit.entity.Player;

public class PlayerTimeLimitAPI {

    private static PlayerTimeLimit plugin;

    public PlayerTimeLimitAPI(PlayerTimeLimit plugin) {
        PlayerTimeLimitAPI.plugin = plugin;
    }

    public static String getTimeLeft(Player player) {
        if (plugin == null || player == null) {
            return "0";
        }

        PlayerManager pm = plugin.getPlayerManager();
        TimeLimitPlayer p = pm.getPlayerByUUID(player.getUniqueId().toString());

        if (p == null) {
            return "N/A";
        }

        int timeLimit = pm.getTimeLimitPlayer(player);
        return pm.getTimeLeft(p, timeLimit);
    }

    public static String getTotalTime(Player player) {
        if (plugin == null || player == null) {
            return "0";
        }

        PlayerManager pm = plugin.getPlayerManager();
        TimeLimitPlayer p = pm.getPlayerByUUID(player.getUniqueId().toString());

        if (p == null) {
            return "N/A";
        }

        return UtilsTime.getTime(p.getTotalTime(), plugin.getMessageManager());
    }
}