package net.neostellar.astalisPermManager.listener;

import net.neostellar.astalisPermManager.AstalisPermManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        AstalisPermManager.getPermissionService().remove(event.getPlayer());
    }
}
