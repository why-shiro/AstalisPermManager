package net.neostellar.astalisPermManager.database.dao.playerrank;

import net.neostellar.astalisPermManager.database.DatabaseManager;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class PostgresPlayerRankDAO implements PlayerRankDAO {
    @Override
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_ranks (
                player_uuid UUID PRIMARY KEY,
                rank_id TEXT NOT NULL,
                expires_at TIMESTAMP,
                updated_at TIMESTAMP DEFAULT now()
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
    public void setPlayerRank(UUID uuid, String rankId, Instant expiresAt) {
        String sql = """
            INSERT INTO player_ranks (player_uuid, rank_id, expires_at)
            VALUES (?, ?, ?)
            ON CONFLICT (player_uuid)
            DO UPDATE SET rank_id = EXCLUDED.rank_id, expires_at = EXCLUDED.expires_at, updated_at = now();
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ps.setString(2, rankId);
            if (expiresAt != null)
                ps.setTimestamp(3, Timestamp.from(expiresAt));
            else
                ps.setNull(3, Types.TIMESTAMP);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPlayerRank(UUID uuid) {
        String sql = "SELECT rank_id FROM player_ranks WHERE player_uuid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("rank_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Instant getRankExpireTime(UUID uuid) {
        String sql = "SELECT expires_at FROM player_ranks WHERE player_uuid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("expires_at");
                return ts != null ? ts.toInstant() : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}


