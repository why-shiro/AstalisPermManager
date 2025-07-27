package net.neostellar.astalisPermManager.database.dao.playerrank;

import net.neostellar.astalisPermManager.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class SQLitePlayerRankDAO implements PlayerRankDAO {
    @Override
    public void createTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS player_ranks (
            player_uuid TEXT PRIMARY KEY,
            rank_id TEXT NOT NULL,
            expires_at TEXT,
            updated_at TEXT DEFAULT (datetime('now'))
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
    public void setPlayerRank(@NotNull UUID uuid, String rankId, Instant expiresAt) {
        String sql = """
        INSERT INTO player_ranks (player_uuid, rank_id, expires_at, updated_at)
        VALUES (?, ?, ?, datetime('now'))
        ON CONFLICT(player_uuid) DO UPDATE SET
            rank_id = excluded.rank_id,
            expires_at = excluded.expires_at,
            updated_at = datetime('now');
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, rankId);
            if (expiresAt != null)
                ps.setString(3, expiresAt.toString()); // ISO-8601
            else
                ps.setNull(3, java.sql.Types.VARCHAR);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPlayerRank(@NotNull UUID uuid) {
        String sql = "SELECT rank_id FROM player_ranks WHERE player_uuid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("rank_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Instant getRankExpireTime(@NotNull UUID uuid) {
        String sql = "SELECT expires_at FROM player_ranks WHERE player_uuid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String expiresStr = rs.getString("expires_at");
                return (expiresStr != null) ? Instant.parse(expiresStr) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}

