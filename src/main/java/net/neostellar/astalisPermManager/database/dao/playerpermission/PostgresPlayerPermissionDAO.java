package net.neostellar.astalisPermManager.database.dao.playerpermission;

import net.neostellar.astalisPermManager.database.DatabaseManager;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresPlayerPermissionDAO implements PlayerPermissionDAO {

    @Override
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_permissions (
                player_uuid UUID,
                permission TEXT,
                expires_at TIMESTAMP, -- NULL = kalıcı
                PRIMARY KEY (player_uuid, permission)
            );
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPermission(UUID uuid, String permission, Instant expiresAt) {
        String sql = """
            INSERT INTO player_permissions (player_uuid, permission, expires_at)
            VALUES (?, ?, ?)
            ON CONFLICT (player_uuid, permission) DO UPDATE SET
                expires_at = EXCLUDED.expires_at;
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ps.setString(2, permission);
            if (expiresAt != null) {
                ps.setTimestamp(3, Timestamp.from(expiresAt));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getActivePermissions(UUID uuid) {
        String sql = """
            SELECT permission FROM player_permissions
            WHERE player_uuid = ?
              AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP);
        """;

        List<String> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("permission"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<PlayerPermissionEntry> getAllPermissions(UUID uuid) {
        String sql = "SELECT permission, expires_at FROM player_permissions WHERE player_uuid = ?";
        List<PlayerPermissionEntry> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String perm = rs.getString("permission");
                Timestamp expires = rs.getTimestamp("expires_at");

                Instant expiry = null;
                if (expires != null) {
                    expiry = expires.toInstant();
                }

                result.add(new PlayerPermissionEntry(perm, expiry));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Instant getPermissionExpiry(UUID uuid, String permission) {
        String sql = "SELECT expires_at FROM player_permissions WHERE player_uuid = ? AND permission = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ps.setString(2, permission);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp expires = rs.getTimestamp("expires_at");
                if (expires != null) {
                    return expires.toInstant();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<UUID> clearExpiredPermissions() {
        List<UUID> affectedPlayers = new ArrayList<>();

        String selectSql = "SELECT DISTINCT player_uuid FROM player_permissions WHERE expires_at IS NOT NULL AND expires_at < CURRENT_TIMESTAMP";
        String deleteSql = "DELETE FROM player_permissions WHERE expires_at IS NOT NULL AND expires_at < CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {
                while (rs.next()) {
                    affectedPlayers.add(rs.getObject("player_uuid", UUID.class));
                }
            }


            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return affectedPlayers;
    }

    @Override
    public void removePermission(UUID uuid, String permission) {
        String sql = "DELETE FROM player_permissions WHERE player_uuid = ? AND permission = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ps.setString(2, permission);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
