package net.neostellar.astalisPermManager.database.dao.playerrank;

import net.neostellar.astalisPermManager.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

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

public class SQLitePlayerRankDAO implements PlayerRankDAO {

    @Override
    public void createTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS player_ranks (
            player_uuid TEXT PRIMARY KEY,
            rank_id TEXT NOT NULL,
            expires_at TEXT,
            updated_at TEXT DEFAULT (datetime('now', 'localtime'))
        );
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // İndeksi ayrı olarak oluşturuyoruz
        String indexSQL = "CREATE INDEX IF NOT EXISTS idx_rank_expiry ON player_ranks(expires_at);";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(indexSQL)) {
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
            if (expiresAt != null) {
                String formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneOffset.UTC)
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
    public Instant getRankExpiry(UUID uuid) {
        String sql = "SELECT expires_at FROM player_ranks WHERE player_uuid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String expiresAt = rs.getString("expires_at");
                if (expiresAt != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneOffset.UTC);
                    LocalDateTime ldt = LocalDateTime.parse(expiresAt, formatter);
                    return ldt.toInstant(ZoneOffset.UTC);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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
                if (expiresStr != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ldt = LocalDateTime.parse(expiresStr, formatter);
                    return ldt.toInstant(ZoneOffset.UTC);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<UUID> getExpiredRanks() {
        String sql = "SELECT player_uuid FROM player_ranks WHERE expires_at IS NOT NULL AND expires_at <= datetime('now', 'utc')";
        List<UUID> expired = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                expired.add(UUID.fromString(rs.getString("player_uuid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expired;
    }

    public void resetToDefaultRank(@NotNull UUID uuid, @NotNull String defaultRankId) {
        String sql = "UPDATE player_ranks SET rank_id = ?, expires_at = NULL, updated_at = datetime('now') WHERE player_uuid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, defaultRankId);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}

