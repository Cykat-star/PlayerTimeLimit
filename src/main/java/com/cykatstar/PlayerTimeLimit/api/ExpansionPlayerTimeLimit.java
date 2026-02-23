package com.cykatstar.PlayerTimeLimit.api;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExpansionPlayerTimeLimit extends PlaceholderExpansion {

    private final PlayerTimeLimit plugin;

    public ExpansionPlayerTimeLimit(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ajneb97 & Cykat-star";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "playertimelimit";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equalsIgnoreCase("timeleft")) {
            String time = PlayerTimeLimitAPI.getTimeLeft(player);
            return time != null ? time : "";
        }

        if (identifier.equalsIgnoreCase("totaltime")) {
            String time = PlayerTimeLimitAPI.getTotalTime(player);
            return time != null ? time : "";
        }

        return null;
    }
}