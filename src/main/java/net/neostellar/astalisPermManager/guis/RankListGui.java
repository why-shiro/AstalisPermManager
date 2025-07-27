package net.neostellar.astalisPermManager.guis;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.rank.Rank;
import net.neostellar.astalisPermManager.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RankListGui {

    private final RankManager rankManager = AstalisPermManager.getRankManager();

    public void open(Player player) {
        int size = ((rankManager.getRanks().size() - 1) / 9 + 1) * 9;
        Inventory inv = Bukkit.createInventory(null, size, "§6Rank Listesi");

        for (Rank rank : rankManager.getRanks().values()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + rank.getId());
            List<String> lore = new ArrayList<>();
            lore.add("§7Prefix: §r" + rank.getPrefix());
            lore.add("§7Suffix: §r" + rank.getSuffix());
            lore.add("§7Weight: §a" + rank.getWeight());
            lore.add("§7Tıklayarak detayları aç.");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.addItem(item);
        }

        player.openInventory(inv);
    }
}

