package net.neostellar.astalisPermManager.listener.gui;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.guis.RankDetailGui;
import net.neostellar.astalisPermManager.guis.RankListGui;
import net.neostellar.astalisPermManager.rank.Rank;
import net.neostellar.astalisPermManager.rank.RankManager;
import net.neostellar.astalisPermManager.utils.ChatInputManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiClickListener implements Listener {

    private final RankManager rankManager = AstalisPermManager.getRankManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("§6Rank Listesi")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String rankId = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            Rank rank = rankManager.getRank(rankId);
            if (rank == null) {
                player.sendMessage("§cRank bulunamadı.");
                return;
            }

            new RankDetailGui(rank).open(player);
        }else if (event.getView().getTitle().startsWith("§eRank: ")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            String rankId = ChatColor.stripColor(event.getView().getTitle().replace("Rank: ", ""));

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            String clickedName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

            switch (clickedName.toLowerCase()) {
                case "prefix":
                    player.closeInventory();
                    ChatInputManager.requestInput(player, "prefix", rankId);
                    break;
                case "suffix":
                    player.closeInventory();
                    ChatInputManager.requestInput(player, "suffix", rankId);
                    break;
                case "weight":
                    player.closeInventory();
                    ChatInputManager.requestInput(player, "weight", rankId);
                    break;
                case "geri":
                    player.closeInventory();
                    new RankListGui().open(player);
                    break;
            }
        }

    }
}

