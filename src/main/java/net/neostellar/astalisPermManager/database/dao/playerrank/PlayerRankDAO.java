package net.neostellar.astalisPermManager.database.dao.playerrank;

import java.time.Instant;
import java.util.UUID;

public interface PlayerRankDAO {
    void createTable();
    void setPlayerRank(UUID uuid, String rankId, Instant expiresAt);
    String getPlayerRank(UUID uuid);
    Instant getRankExpireTime(UUID uuid);
}

