package com.cykatstar.PlayerTimeLimit.managers;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataManager {
    private final PlayerTimeLimit plugin;
    private File file;
    private FileConfiguration config;

    public DataManager(PlayerTimeLimit plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        this.file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}