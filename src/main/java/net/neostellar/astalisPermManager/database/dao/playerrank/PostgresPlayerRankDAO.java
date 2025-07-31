package net.neostellar.astalisPermManager.database.dao.playerrank;

import net.neostellar.astalisPermManager.database.DatabaseManager;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresPlayerRankDAO implements PlayerRankDAO {

    @Override
    public void createTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS player_ranks (
            player_uuid UUID PRIMARY KEY,
            rank_id TEXT NOT NULL,
            expires_at TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String indexSQL = "CREATE INDEX IF NOT EXISTS idx_rank_expiry ON player_ranks(expires_at);";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(indexSQL)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayerRank(UUID uuid, String rankId, Instant expiresAt) {
        String sql = """
            INSERT INTO player_ranks (player_uuid, rank_id, expires_at, updated_at)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT (player_uuid)
            DO UPDATE SET
                rank_id = EXCLUDED.rank_id,
                expires_at = EXCLUDED.expires_at,
                updated_at = CURRENT_TIMESTAMP;
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, uuid);
            ps.setString(2, rankId);
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

    @Override
    public List<UUID> getExpiredRanks() {
        String sql = "SELECT player_uuid FROM player_ranks WHERE expires_at IS NOT NULL AND expires_at <= CURRENT_TIMESTAMP";
        List<UUID> expired = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                expired.add(rs.getObject("player_uuid", UUID.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expired;
    }

    @Override
    public void resetToDefaultRank(UUID uuid, String defaultRankId) {
        String sql = """
            UPDATE player_ranks
            SET rank_id = ?, expires_at = NULL, updated_at = CURRENT_TIMESTAMP
            WHERE player_uuid = ?;
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, defaultRankId);
            ps.setObject(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Instant getRankExpiry(UUID uuid) {
        return getRankExpireTime(uuid);
    }
}
