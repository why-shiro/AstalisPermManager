package net.neostellar.astalisPermManager.service;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class RankExpiryWatcher {

    public static void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(AstalisPermManager.getInstance(), () -> {
            AstalisPermManager.getInstance().getLogger().log(Level.INFO, "Biten ranklar araniyor...");
            List<UUID> expired = DAOProvider.getPlayerRankDAO().getExpiredRanks();

            for (UUID uuid : expired) {
                String defaultRankId = AstalisPermManager.getRankManager().getDefaultRankId();
                DAOProvider.getPlayerRankDAO().resetToDefaultRank(uuid, defaultRankId);

                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    Bukkit.getScheduler().runTask(AstalisPermManager.getInstance(), () -> {
                        AstalisPermManager.getPermissionService().refresh(player); // tüm perms’i tekrar yükle
                        player.sendMessage("§cRank süren dolduğu için varsayılan rank'a döndün.");
                    });
                }
            }
        }, 20L * 30, 20L * 30); // 30 saniyede bir
    }
}

