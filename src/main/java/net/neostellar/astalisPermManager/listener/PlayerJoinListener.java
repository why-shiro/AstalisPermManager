package net.neostellar.astalisPermManager.listener;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import net.neostellar.astalisPermManager.rank.Rank;
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
        AstalisPermManager.getPermissionService().refreshAsync(event.getPlayer());
    }


}
