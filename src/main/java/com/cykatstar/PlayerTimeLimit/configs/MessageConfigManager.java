package com.cykatstar.PlayerTimeLimit.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.managers.MessageManager;

public class MessageConfigManager {

    private final PlayerTimeLimit plugin;
    private FileConfiguration messages = null;
    private File messagesFile = null;
    private String messagesPath;

    public MessageConfigManager(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        registerMessages();
        setMessages();
    }

    public void setMessages() {
        FileConfiguration config = getMessages();
        
        MessageManager msgManager = new MessageManager(config.getString("prefix", "&8[&bPlayerTime&cLimit&8] "));
        
        msgManager.setActionBarMessage(config.getString("actionBarMessage"));
        msgManager.setBossBarMessage(config.getString("bossBarMessage"));
        
        msgManager.setTimeSeconds(config.getString("timeSeconds"));
        msgManager.setTimeMinutes(config.getString("timeMinutes"));
        msgManager.setTimeHours(config.getString("timeHours"));
        msgManager.setTimeDays(config.getString("timeDays"));
        msgManager.setTimeInfinite(config.getString("timeInfinite"));

        this.plugin.setMessageManager(msgManager); 
    }

    public void registerMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messagesPath = messagesFile.getPath();
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public void saveMessages() {
        try {
            getMessages().save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getMessages() {
        if (messages == null) {
            reloadMessages();
        }
        return messages;
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        try {
            var resource = plugin.getResource("messages.yml");
            if (resource != null) {
                Reader defConfigStream = new InputStreamReader(resource, "UTF-8");
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                messages.setDefaults(defConfig);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        setMessages();
    }

    public String getPath() {
        return messagesPath;
    }
}