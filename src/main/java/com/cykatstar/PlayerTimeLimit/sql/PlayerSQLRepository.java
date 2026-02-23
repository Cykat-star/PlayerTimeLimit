package com.cykatstar.PlayerTimeLimit.sql;

import com.cykatstar.PlayerTimeLimit.PlayerTimeLimit;
import com.cykatstar.PlayerTimeLimit.model.TimeLimitPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class PlayerSQLRepository {

    private final PlayerTimeLimit plugin;
    private final SQLConnection sql;

    public PlayerSQLRepository(PlayerTimeLimit plugin, SQLConnection sql) {
        this.plugin = plugin;
        this.sql = sql;
        createTable();
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS playertimelimit_players ("
                + "uuid VARCHAR(36) PRIMARY KEY,"
                + "name VARCHAR(16) NOT NULL,"
                + "current_time INT NOT NULL DEFAULT 0,"
                + "total_time INT NOT NULL DEFAULT 0,"
                + "message_enabled BOOLEAN NOT NULL DEFAULT 0"
                + ");";

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(TimeLimitPlayer p) {
        if (!p.isModified()) return;

        String query = "REPLACE INTO playertimelimit_players (uuid, name, current_time, total_time, message_enabled) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, p.getUuid());
            ps.setString(2, p.getName());
            ps.setInt(3, p.getCurrentTime());
            ps.setInt(4, p.getTotalTime());
            ps.setBoolean(5, p.isMessageEnabled());

            ps.executeUpdate();
            p.setModified(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAllPlayers(Collection<TimeLimitPlayer> players) {
        String query = "REPLACE INTO playertimelimit_players (uuid, name, current_time, total_time, message_enabled) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = sql.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                int count = 0;
                for (TimeLimitPlayer p : players) {
                    if (!p.isModified()) continue;

                    ps.setString(1, p.getUuid());
                    ps.setString(2, p.getName());
                    ps.setInt(3, p.getCurrentTime());
                    ps.setInt(4, p.getTotalTime());
                    ps.setBoolean(5, p.isMessageEnabled());
                    ps.addBatch();
                    
                    p.setModified(false);
                    count++;
                }
                if (count > 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TimeLimitPlayer loadPlayer(String uuid) {
        String query = "SELECT * FROM playertimelimit_players WHERE uuid = ?";
        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TimeLimitPlayer p = new TimeLimitPlayer(rs.getString("uuid"), rs.getString("name"));
                    p.setCurrentTime(rs.getInt("current_time"));
                    p.setTotalTime(rs.getInt("total_time"));
                    p.setMessageEnabled(rs.getBoolean("message_enabled"));
                    p.setModified(false);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void resetAllPlayers() {
        String query = "UPDATE playertimelimit_players SET current_time = 0";
        try (Connection conn = sql.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}