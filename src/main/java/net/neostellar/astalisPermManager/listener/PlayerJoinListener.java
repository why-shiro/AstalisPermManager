package net.neostellar.astalisPermManager.listener;

import net.neostellar.astalisPermManager.AstalisPermManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AstalisPermManager.getPermissionService().refreshAsync(event.getPlayer());
    }


}
