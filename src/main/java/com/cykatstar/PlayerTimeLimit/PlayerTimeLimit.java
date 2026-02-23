package com.cykatstar.PlayerTimeLimit;

import com.cykatstar.PlayerTimeLimit.api.ExpansionPlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.configs.ConfigsManager;
import com.cykatstar.PlayerTimeLimit.listeners.PlayerListener;
import com.cykatstar.PlayerTimeLimit.managers.MessageManager;
import com.cykatstar.PlayerTimeLimit.managers.PlayerManager;
import com.cykatstar.PlayerTimeLimit.managers.ServerManager;
import com.cykatstar.PlayerTimeLimit.sql.SQLConnection;
import com.cykatstar.PlayerTimeLimit.sql.PlayerSQLRepository;
import com.cykatstar.PlayerTimeLimit.tasks.DataSaveTask;
import com.cykatstar.PlayerTimeLimit.tasks.PlayerTimeTask;
import com.cykatstar.PlayerTimeLimit.tasks.ServerTimeResetTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerTimeLimit extends JavaPlugin {

    private final String version = getDescription().getVersion();
    private ConfigsManager configsManager;
    private MessageManager messageManager;
    private PlayerManager playerManager;
    private ServerManager serverManager;
    private SQLConnection sqlConnection;
    private PlayerSQLRepository sqlRepo;
    private DataSaveTask dataSaveTask;

    public static String pluginPrefix = ChatColor.translateAlternateColorCodes('&', "&8[&bPlayerTime&cLimit&8] ");

    @Override
    public void onEnable() {
        registerConfig();
        reloadConfig();
        
        this.configsManager = new ConfigsManager(this);
        this.configsManager.onEnable();

        this.sqlConnection = new SQLConnection(this);
        this.sqlRepo = new PlayerSQLRepository(sqlConnection);

        this.playerManager = new PlayerManager(this, sqlRepo);
        this.serverManager = new ServerManager(this);

        registerCommands();
        registerEvents();

        serverManager.executeDataTime();
        
        new PlayerTimeTask(this).start();
        new ServerTimeResetTask(this).start();
        reloadDataSaveTask();

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerManager.loadOrCreate(player);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ExpansionPlayerTimeLimit(this).register();
        }

        Bukkit.getConsoleSender().sendMessage(pluginPrefix + ChatColor.GREEN + "Enabled successfully! v" + version);
    }

    @Override
    public void onDisable() {
        if (dataSaveTask != null) {
            dataSaveTask.end(); 
        }

        if (serverManager != null) {
            serverManager.saveDataTime();
        }

        if (sqlConnection != null) {
            sqlConnection.disconnect();
        }

        Bukkit.getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED + "Disabled successfully.");
    }

    public void registerCommands() {
        getCommand("playertimelimit").setExecutor(new com.cykatstar.PlayerTimeLimit.Comando(this));
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
    }

    public void registerConfig() {
        saveDefaultConfig();
    }

    public void reloadPluginConfigs() {
        reloadConfig();
        if (configsManager != null) {
            configsManager.getMessageConfigManager().reloadMessages();
            configsManager.getMainConfigManager().onEnable(); 
        }
        reloadDataSaveTask();
    }

    public void reloadDataSaveTask() {
        if (dataSaveTask != null) {
            dataSaveTask.end();
        }
        dataSaveTask = new DataSaveTask(this);
        
        int interval = getConfig().getInt("data_save_time", 300);
        dataSaveTask.start(interval);
    }

    public PlayerManager getPlayerManager() { return playerManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public void setMessageManager(MessageManager messageManager) { this.messageManager = messageManager; }
    public ConfigsManager getConfigsManager() { return configsManager; }
    public ServerManager getServerManager() { return serverManager; }
    public FileConfiguration getMessages() { 
        return configsManager.getMessageConfigManager().getMessages(); 
    }
}