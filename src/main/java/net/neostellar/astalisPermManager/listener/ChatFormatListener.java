package net.neostellar.astalisPermManager.listener;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.rank.Rank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFormatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Rank rank = AstalisPermManager.getRankManager().getPlayerRank(event.getPlayer());

        String prefix = rank.getPrefix();
        String suffix = rank.getSuffix();

        String format = prefix + " %s" + suffix + " : %s";
        event.setFormat(format);
    }
}
