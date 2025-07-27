package net.neostellar.astalisPermManager.database.dao.playerpermission;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.neostellar.astalisPermManager.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresPlayerPermissionDAO implements PlayerPermissionDAO {
    private final ObjectMapper mapper = new ObjectMapper(); // Jackson JSON parser

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
        ON CONFLICT(player_uuid, permission) DO UPDATE SET
            expires_at = excluded.expires_at;
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, permission);
            if (expiresAt != null) {
                ps.setString(3, expiresAt.toString()); // ISO format
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
          AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP)
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
    public List<PlayerPermissionEntry> getAllPermissions(UUID uuid) {
        return List.of();
    }

    @Override
    public Instant getPermissionExpiry(UUID uuid, String permission) {
        return null;
    }

    @Override
    public List<UUID> clearExpiredPermissions() {
        return List.of();
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




}

