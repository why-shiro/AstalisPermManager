package net.neostellar.astalisPermManager.service;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.rank.Rank;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;
import java.util.logging.Level;

public class PermissionService {

    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public void refresh(Player player) {
        UUID uuid = player.getUniqueId();

        // Eskiyi kaldır
        if (attachments.containsKey(uuid)) {
            player.removeAttachment(attachments.get(uuid));
        }

        PermissionAttachment attachment = player.addAttachment(AstalisPermManager.getInstance());
        attachments.put(uuid, attachment);

        String rankId = DAOProvider.getPlayerRankDAO().getPlayerRank(uuid);
        if (rankId == null) {
            rankId = AstalisPermManager.getRankManager().getDefaultRankId();
            DAOProvider.getPlayerRankDAO().setPlayerRank(uuid, rankId, null);
        }

        Rank rank = AstalisPermManager.getRankManager().getRank(rankId);
        for (String perm : rank.getResolvedPermissions()) {
            AstalisPermManager.getInstance().getLogger().log(Level.INFO, "[Perm] " + player.getName() + " verilen Yetki: " + perm );
            attachment.setPermission(perm, true);
        }

        List<String> extra = DAOProvider.getPlayerPermissionDAO().getActivePermissions(uuid);
        for (String perm : extra) {
            attachment.setPermission(perm, true);
        }

        AstalisPermManager.getInstance().getLogger().log(Level.INFO, "§aİzinler güncellendi. Rank: §e" + rank.getId());
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refresh(player);
        }
    }

    public void remove(Player player) {
        UUID uuid = player.getUniqueId();
        if (attachments.containsKey(uuid)) {
            player.removeAttachment(attachments.get(uuid));
            attachments.remove(uuid);
        }
    }
}

