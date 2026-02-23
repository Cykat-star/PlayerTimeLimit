package com.cykatstar.PlayerTimeLimit.managers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.configs.MainConfigManager;
import com.cykatstar.PlayerTimeLimit.utils.UtilsTime;

public class ServerManager {

    private final PlayerTimeLimit plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public ServerManager(PlayerTimeLimit plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        loadDataFile();
    }

    private void loadDataFile() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveDataTime() {
        String resetTimeHour = plugin.getConfigsManager().getMainConfigManager().getResetTime();
        long finalMillis = UtilsTime.getNextResetMillis(resetTimeHour);
        
        // Save to data.yml ONLY
        dataConfig.set("next_millis_reset", finalMillis);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.getPlayerManager().saveAllPlayers();
    }

    public void executeDataTime() {
        if (dataConfig.contains("next_millis_reset")) {
            long millisReset = dataConfig.getLong("next_millis_reset");
            
            if (System.currentTimeMillis() > millisReset) {
                plugin.getPlayerManager().resetPlayers();
                saveDataTime();
            }
        } else {
            saveDataTime();
        }
    }

    public String getRemainingTimeForTimeReset() {
        String resetTimeHour = plugin.getConfigsManager().getMainConfigManager().getResetTime();
        long finalMillis = UtilsTime.getNextResetMillis(resetTimeHour);
        long remainingMillis = finalMillis - System.currentTimeMillis();
        
        long seconds = Math.max(0, remainingMillis / 1000);
        
        return UtilsTime.getTime(seconds, plugin.getMessageManager());
    }

    public boolean isValidWorld(World world) {
        MainConfigManager mainConfig = plugin.getConfigsManager().getMainConfigManager();
        if (!mainConfig.isWorldWhitelistEnabled()) {
            return true;
        }
        
        List<String> worlds = mainConfig.getWorldWhitelistWorlds();
        return worlds.contains(world.getName());
    }
}