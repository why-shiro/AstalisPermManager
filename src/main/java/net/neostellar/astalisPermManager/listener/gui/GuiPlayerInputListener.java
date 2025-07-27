package net.neostellar.astalisPermManager.listener.gui;

import net.neostellar.astalisPermManager.utils.ChatInputManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GuiPlayerInputListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (ChatInputManager.handleInput(player, message)) {
            event.setCancelled(true); // Mesaj herkese gitmesin
        }
    }

}
