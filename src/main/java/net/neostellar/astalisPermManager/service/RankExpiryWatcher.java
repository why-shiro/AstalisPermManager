package net.neostellar.astalisPermManager.service;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class RankExpiryWatcher {

    public static void start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(AstalisPermManager.getInstance(), () -> {
            AstalisPermManager.getInstance().getLogger().log(Level.INFO, "Süresi dolan ranklar ve yetkiler kontrol ediliyor...");

            // 1. Süresi dolmuş rankları tespit et
            List<UUID> expiredRanks = DAOProvider.getPlayerRankDAO().getExpiredRanks();

            for (UUID uuid : expiredRanks) {
                String defaultRankId = AstalisPermManager.getRankManager().getDefaultRankId();
                DAOProvider.getPlayerRankDAO().resetToDefaultRank(uuid, defaultRankId);

                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    Bukkit.getScheduler().runTask(AstalisPermManager.getInstance(), () -> {
                        AstalisPermManager.getPermissionService().refreshAsync(player);
                        player.sendMessage("§cRank süren dolduğu için varsayılan rank'a döndün.");
                    });
                }
            }

            // 2. Süresi dolmuş player izinlerini sil
            List<UUID> deleted = DAOProvider.getPlayerPermissionDAO().clearExpiredPermissions();

            if (!deleted.isEmpty()){
                // 3. Online oyunculardan etkilenen varsa güncelle
                for (UUID playerUUID: deleted){
                    AstalisPermManager.getPermissionService().refreshAsync(Objects.requireNonNull(Bukkit.getPlayer(playerUUID)));
                }
                AstalisPermManager.getInstance().getLogger().log(Level.INFO, "§7Süresi dolduğu için silinen izin sayısı: " + deleted.size());
            }



        }, 20L * 30, 20L * 30);
    }
}
