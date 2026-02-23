package com.cykatstar.PlayerTimeLimit.sql;

import com.cykatstar.PlayerTimeLimit.model.TimeLimitPlayer;
import java.sql.*;
import java.util.Collection;

public class PlayerSQLRepository {

    private final SQLConnection connection;
    private final String tableName = "playertimelimit_players";

    public PlayerSQLRepository(SQLConnection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "name VARCHAR(16), " +
                "`current_time` INT NOT NULL DEFAULT 0, " +
                "`total_time` INT NOT NULL DEFAULT 0, " +
                "message_enabled BOOLEAN DEFAULT TRUE" +
                ");";

        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(TimeLimitPlayer player) {
        String sql = "REPLACE INTO " + tableName + " (uuid, name, `current_time`, `total_time`, message_enabled) " +
                "VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUuid());
            stmt.setString(2, player.getName());
            stmt.setInt(3, player.getCurrentTime());
            stmt.setInt(4, player.getTotalTime());
            stmt.setBoolean(5, player.isMessageEnabled());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TimeLimitPlayer loadPlayer(String uuid) {
        String sql = "SELECT * FROM " + tableName + " WHERE uuid = ?;";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TimeLimitPlayer p = new TimeLimitPlayer(uuid, rs.getString("name"));
                    p.setCurrentTime(rs.getInt("current_time"));
                    p.setTotalTime(rs.getInt("total_time"));
                    p.setMessageEnabled(rs.getBoolean("message_enabled"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void resetAllPlayers() {
        String sql = "UPDATE " + tableName + " SET `current_time` = 0;";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAllPlayers(Collection<TimeLimitPlayer> players) {
        for (TimeLimitPlayer player : players) {
            savePlayer(player);
        }
    }
}