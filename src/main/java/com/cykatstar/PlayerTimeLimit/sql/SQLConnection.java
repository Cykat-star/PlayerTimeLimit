package com.cykatstar.PlayerTimeLimit.sql;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnection {

    private final PlayerTimeLimit plugin;
    private HikariDataSource dataSource;

    public SQLConnection(PlayerTimeLimit plugin) {
        this.plugin = plugin;
        initPool();
    }

    private void initPool() {
        String rawType = plugin.getConfig().getString("database.type", "SQLite");
        String type = rawType.toUpperCase();
        
        HikariConfig config = new HikariConfig();

        if (type.equals("MYSQL")) {
            String host = plugin.getConfig().getString("database.mysql.host", "localhost");
            int port = plugin.getConfig().getInt("database.mysql.port", 3306);
            String database = plugin.getConfig().getString("database.mysql.database", "minecraft");
            String username = plugin.getConfig().getString("database.mysql.username", "root");
            String password = plugin.getConfig().getString("database.mysql.password", "");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + 
                         "?useSSL=false&autoReconnect=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§aInitializing MySQL Connection...");
        } else {
            if (!type.equals("SQLITE")) {
                Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§c'" + rawType + "' is not a valid database type. Valid options: MySQL, SQLite.");
                Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§eFalling back to SQLite for this session. Config file remains unchanged.");
            }

            File dataFolder = new File(plugin.getDataFolder(), "playerdata");
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            String path = dataFolder.getAbsolutePath() + File.separator + "database";
            config.setJdbcUrl("jdbc:sqlite:" + path + ".db");
            config.setDriverClassName("org.sqlite.JDBC");
            
            config.setMaximumPoolSize(2); 
            
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§aUsing SQLite Local Database.");
        }

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        config.setMinimumIdle(1);
        config.setPoolName("PTL-Pool");

        try {
            this.dataSource = new HikariDataSource(config);
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§aDatabase connection established successfully.");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§c§lCRITICAL DATABASE ERROR:");
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§cCould not connect to the database. Verify your credentials/drivers.");
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.pluginPrefix + "§4§lPLUGIN IS NOW DISABLED TO PREVENT DATA LOSS.");
            
            e.printStackTrace();
            
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource is null or closed.");
        }
        return dataSource.getConnection();
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}