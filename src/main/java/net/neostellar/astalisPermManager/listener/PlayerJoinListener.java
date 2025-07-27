package net.neostellar.astalisPermManager.listener;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        player.sendMessage("Player with UUID: " + uuid);

        // RANK ATAMA (eğer yoksa)
        String rankId = DAOProvider.getPlayerRankDAO().getPlayerRank(uuid);
        if (rankId == null) {
            String defaultRankId = AstalisPermManager.getRankManager().getDefaultRank().getId();
            player.sendMessage("Default rank ID" + defaultRankId + " [ÇÜNKÜ NULL DÖNDÜRDÜM]");
            DAOProvider.getPlayerRankDAO().setPlayerRank(uuid, defaultRankId, null); // kalıcı
            rankId = defaultRankId;
        }

        // ÖZEL PERMISSION'LAR
        List<String> permissions = DAOProvider.getPlayerPermissionDAO().getActivePermissions(uuid);
        if (permissions != null && !permissions.isEmpty()) {
            PermissionAttachment attachment = player.addAttachment(AstalisPermManager.getInstance());
            for (String perm : permissions) {
                attachment.setPermission(perm, true);
            }

        }
    }

}
