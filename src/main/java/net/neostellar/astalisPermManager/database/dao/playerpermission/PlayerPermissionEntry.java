package net.neostellar.astalisPermManager.database.dao.playerpermission;

import java.time.Instant;

public class PlayerPermissionEntry {
    private final String permission;
    private final Instant expiresAt;

    public PlayerPermissionEntry(String permission, Instant expiresAt) {
        this.permission = permission;
        this.expiresAt = expiresAt;
    }

    public String getPermission() {
        return permission;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return expiresAt == null || Instant.now().isBefore(expiresAt);
    }
}
