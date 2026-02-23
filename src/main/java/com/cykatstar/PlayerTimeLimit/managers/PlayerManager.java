package com.cykatstar.PlayerTimeLimit.managers;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.model.TimeLimitPlayer;
import com.cykatstar.PlayerTimeLimit.sql.PlayerSQLRepository;
import com.cykatstar.PlayerTimeLimit.utils.UtilsTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final PlayerTimeLimit plugin;
    private final PlayerSQLRepository sqlRepo;
    private final Map<String, TimeLimitPlayer> players = new ConcurrentHashMap<>();

    public PlayerManager(PlayerTimeLimit plugin, PlayerSQLRepository sqlRepo) {
        this.plugin = plugin;
        this.sqlRepo = sqlRepo;
    }

    public Collection<TimeLimitPlayer> getPlayers() {
        return players.values();
    }

    public TimeLimitPlayer getPlayerByUUID(String uuid) {
        return players.get(uuid);
    }

    public void loadOrCreateByInfo(String uuid, String name) {
        if (players.containsKey(uuid)) return;

        TimeLimitPlayer p = sqlRepo.loadPlayer(uuid);

        if (p == null) {
            p = new TimeLimitPlayer(uuid, name);
            boolean defaultMsg = plugin.getConfig().getBoolean("information_message_enabled_by_default", false);
            p.setMessageEnabled(defaultMsg);
            
            p.setModified(true);
            sqlRepo.savePlayer(p); 
        }

        players.put(uuid, p);
    }

    public TimeLimitPlayer loadOrCreate(Player player) {
        String uuid = player.getUniqueId().toString();

        if (players.containsKey(uuid)) {
            TimeLimitPlayer p = players.get(uuid);
            p.setPlayer(player);
            return p;
        }

        TimeLimitPlayer p = sqlRepo.loadPlayer(uuid);

        if (p == null) {
            p = new TimeLimitPlayer(uuid, player.getName());
            boolean defaultMsg = plugin.getConfig().getBoolean("information_message_enabled_by_default", false);
            p.setMessageEnabled(defaultMsg);
            
            p.setModified(true);
            sqlRepo.savePlayer(p); 
        }

        p.setPlayer(player);
        players.put(uuid, p);

        return p;
    }

    public void unload(Player player) {
        String uuid = player.getUniqueId().toString();
        TimeLimitPlayer p = players.remove(uuid);
        if (p != null) {
            sqlRepo.savePlayer(p);
        }
    }

    public void saveAllPlayers() {
        sqlRepo.saveAllPlayers(players.values());
    }

    public void resetPlayers() {
        sqlRepo.resetAllPlayers();
        for (TimeLimitPlayer p : players.values()) {
            p.resetTime();
        }
    }

    public boolean hasTimeLeft(TimeLimitPlayer p) {
        if (p.getPlayer() == null) return true;
        int currentTime = p.getCurrentTime();
        int timeLimit = getTimeLimitPlayer(p.getPlayer());
        
        return timeLimit == 0 || currentTime < timeLimit;
    }

    public void checkUserTime(Player player, TimeLimitPlayer p) {
        if (!hasTimeLeft(p)) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                String kickMsg = plugin.getConfigsManager().getMainConfigManager().getKickMessage();
                player.kickPlayer(plugin.getMessageManager().getColoredMessage(kickMsg));
            });
        }
    }

    public String getTimeLeft(TimeLimitPlayer p, int timeLimit) {
        if (timeLimit == 0) {
            return plugin.getMessageManager().getTimeInfinite();
        }
        int secondsLeft = timeLimit - p.getCurrentTime();
        if (secondsLeft < 0) secondsLeft = 0;
        
        return UtilsTime.getTime(secondsLeft, plugin.getMessageManager());
    }

    public int getTimeLimitPlayer(Player player) {
    if (player == null) return 0;
    
    var configManager = plugin.getConfigsManager().getMainConfigManager();
    if (configManager == null) return 0;

    var timeLimits = configManager.getTimeLimits();
    
    int timeReal = 0; 
    int defaultTime = 0;

    for (var timeLimit : timeLimits) {
        String name = timeLimit.getName();
        int time = timeLimit.getTime();

        if (name.equalsIgnoreCase("default")) {
            defaultTime = time;
            continue;
        }

        if (name.equalsIgnoreCase("op") && player.isOp()) {
            return time;
        }

        if (player.hasPermission("playertimelimit.limit." + name)) {
            timeReal = time; 
        }
    }

    return (timeReal > 0) ? timeReal : defaultTime;
}

    public void takeTime(TimeLimitPlayer p, int time) {
        if (p.getPlayer() == null) return;
        int timeLimit = getTimeLimitPlayer(p.getPlayer());
        if (timeLimit == 0) return;

        p.takeTime(time);
        if (p.getCurrentTime() > timeLimit) {
            p.setCurrentTime(timeLimit);
        }
    }

    public void addTime(TimeLimitPlayer p, int time) {
        p.addTime(time);
        if (p.getCurrentTime() < 0) {
            p.setCurrentTime(0);
        }
    }
}