package net.neostellar.astalisPermManager.database.dao.playerpermission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.neostellar.astalisPermManager.database.DatabaseManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLitePlayerPermissionDAO implements PlayerPermissionDAO {

    @Override
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_permissions (
                player_uuid TEXT,
                permission TEXT,
                expires_at TEXT, -- ISO 8601 (null = kalıcı)
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
        ON CONFLICT(player_uuid, permission) DO UPDATE SET
            expires_at = excluded.expires_at;
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, permission);
            if (expiresAt != null) {
                String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneOffset.UTC)  // veya systemDefault(), fark etmez
                        .format(expiresAt);
                ps.setString(3, formatted);
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
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
          AND (expires_at IS NULL OR expires_at > datetime('now'))
    """;

        List<String> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
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
    public void removePermission(UUID uuid, String permission) {
        String sql = "DELETE FROM player_permissions WHERE player_uuid = ? AND permission = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, permission);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UUID> clearExpiredPermissions() {
        List<UUID> affectedPlayers = new ArrayList<>();

        String selectSql = "SELECT DISTINCT player_uuid FROM player_permissions WHERE expires_at IS NOT NULL AND expires_at < datetime('now')";
        String deleteSql = "DELETE FROM player_permissions WHERE expires_at IS NOT NULL AND expires_at < datetime('now')";

        try (Connection conn = DatabaseManager.getConnection()) {
            // Önce etkilenen oyuncuları al
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery()) {
                while (rs.next()) {
                    affectedPlayers.add(UUID.fromString(rs.getString("player_uuid")));
                }
            }

            // Sonra kayıtları sil
            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return affectedPlayers;
    }



    @Override
    public List<PlayerPermissionEntry> getAllPermissions(UUID uuid) {
        String sql = "SELECT permission, expires_at FROM player_permissions WHERE player_uuid = ?";
        List<PlayerPermissionEntry> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                String perm = rs.getString("permission");
                String expires = rs.getString("expires_at");

                Instant expiry = null;
                if (expires != null) {
                    LocalDateTime ldt = LocalDateTime.parse(expires, formatter);
                    expiry = ldt.toInstant(ZoneOffset.UTC);
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
            ps.setString(1, uuid.toString());
            ps.setString(2, permission);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String expires = rs.getString("expires_at");

                if (expires != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ldt = LocalDateTime.parse(expires, formatter);
                    return ldt.toInstant(ZoneOffset.UTC);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }





}

