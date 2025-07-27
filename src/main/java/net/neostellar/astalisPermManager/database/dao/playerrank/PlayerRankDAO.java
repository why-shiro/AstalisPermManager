package net.neostellar.astalisPermManager.database.dao.playerrank;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PlayerRankDAO {
    void createTable();
    void setPlayerRank(UUID uuid, String rankId, Instant expiresAt);
    String getPlayerRank(UUID uuid);
    Instant getRankExpireTime(UUID uuid);
    List<UUID> getExpiredRanks();
    void resetToDefaultRank(UUID uuid, String defaultRankId);
    Instant getRankExpiry(UUID uuid);
}

