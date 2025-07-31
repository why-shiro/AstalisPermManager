package net.neostellar.astalisPermManager.database.dao.playerpermission;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PlayerPermissionDAO {
    void createTable();
    void setPermission(UUID uuid, String permission, Instant expiresAt);
    void removePermission(UUID uuid, String permission);
    List<String> getActivePermissions(UUID uuid);

    List<PlayerPermissionEntry> getAllPermissions(UUID uuid);

    Instant getPermissionExpiry(UUID uuid, String permission);

    List<UUID> clearExpiredPermissions();
}



