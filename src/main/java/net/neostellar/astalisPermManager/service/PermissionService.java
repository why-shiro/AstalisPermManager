package net.neostellar.astalisPermManager.service;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.database.dao.playerpermission.PlayerPermissionEntry;
import net.neostellar.astalisPermManager.rank.Rank;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;
import java.util.logging.Level;

public class PermissionService {

    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public void refreshAsync(Player player) {
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(AstalisPermManager.getInstance(), () -> {
            String rankId = DAOProvider.getPlayerRankDAO().getPlayerRank(uuid);
            if (rankId == null) {
                rankId = AstalisPermManager.getRankManager().getDefaultRankId();
                DAOProvider.getPlayerRankDAO().setPlayerRank(uuid, rankId, null);
            }

            List<PlayerPermissionEntry> perms = DAOProvider.getPlayerPermissionDAO().getAllPermissions(uuid);
            String finalRankId = rankId;

            Bukkit.getScheduler().runTask(AstalisPermManager.getInstance(), () -> {
                refreshSync(player, finalRankId, perms);
            });
        });
    }

    private void refreshSync(Player player, String rankId, List<PlayerPermissionEntry> perms) {
        UUID uuid = player.getUniqueId();

        if (attachments.containsKey(uuid)) {
            player.removeAttachment(attachments.get(uuid));
        }

        PermissionAttachment attachment = player.addAttachment(AstalisPermManager.getInstance());
        attachments.put(uuid, attachment);

        AstalisPermManager.getRankManager().setPlayerRank(player, rankId);

        Rank rank = AstalisPermManager.getRankManager().getRank(rankId);
        for (String perm : rank.getResolvedPermissions()) {
            attachment.setPermission(perm, true);
            AstalisPermManager.getInstance().getLogger().log(Level.INFO, "[Perm] " + player.getName() + " [RANK] " + perm);
        }

        for (PlayerPermissionEntry entry : perms) {
            if (entry.isActive()) {
                attachment.setPermission(entry.getPermission(), true);
                if (entry.getExpiresAt() != null) {
                    AstalisPermManager.getInstance().getLogger().log(Level.INFO, "[Perm] " + player.getName() + " [TEMP] " + entry.getPermission() + " (expires at: " + entry.getExpiresAt() + ")");
                } else {
                    AstalisPermManager.getInstance().getLogger().log(Level.INFO, "[Perm] " + player.getName() + " [PERM] " + entry.getPermission());
                }
            }
        }

        AstalisPermManager.getInstance().getLogger().log(Level.INFO, "§aİzinler güncellendi. Rank: §e" + rank.getId());
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshAsync(player);
        }
    }

    public void remove(Player player) {
        UUID uuid = player.getUniqueId();
        if (attachments.containsKey(uuid)) {
            player.removeAttachment(attachments.get(uuid));
            attachments.remove(uuid);
        }
    }

    public String getPrefixByUUID(UUID uuid) {
        String rankId = DAOProvider.getPlayerRankDAO().getPlayerRank(uuid);
        if (rankId == null) {
            rankId = AstalisPermManager.getRankManager().getDefaultRankId();
        }

        Rank rank = AstalisPermManager.getRankManager().getRank(rankId);
        return rank != null ? rank.getPrefix() : "";
    }

    public String getSuffixByUUID(UUID uuid) {
        String rankId = DAOProvider.getPlayerRankDAO().getPlayerRank(uuid);
        if (rankId == null) {
            rankId = AstalisPermManager.getRankManager().getDefaultRankId();
        }

        Rank rank = AstalisPermManager.getRankManager().getRank(rankId);
        return rank != null ? rank.getSuffix() : "";
    }


}
