package com.cykatstar.PlayerTimeLimit.configs;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;

public class ConfigsManager {

    private final MessageConfigManager messageConfigManager;
    private final MainConfigManager mainConfigManager;

    public ConfigsManager(PlayerTimeLimit plugin) {
        this.mainConfigManager = new MainConfigManager(plugin);
        this.messageConfigManager = new MessageConfigManager(plugin);
    }

    public void onEnable() {
        this.mainConfigManager.onEnable();
        this.messageConfigManager.onEnable();
    }

    public MessageConfigManager getMessageConfigManager() {
        return messageConfigManager;
    }

    public MainConfigManager getMainConfigManager() {
        return mainConfigManager;
    }
}