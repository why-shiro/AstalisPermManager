package net.neostellar.astalisPermManager.guis;

import net.neostellar.astalisPermManager.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RankDetailGui {

    private final Rank rank;

    public RankDetailGui(Rank rank) {
        this.rank = rank;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§eRank: " + rank.getId());

        inv.setItem(10, createItem(Material.NAME_TAG, "§bPrefix", rank.getPrefix()));
        inv.setItem(11, createItem(Material.NAME_TAG, "§bSuffix", rank.getSuffix()));
        inv.setItem(12, createItem(Material.GOLD_INGOT, "§bWeight", String.valueOf(rank.getWeight())));
        inv.setItem(14, createItem(Material.BOOK, "§bPermissions", String.valueOf(rank.getPermissions().size())));
        inv.setItem(15, createItem(Material.CHAINMAIL_HELMET, "§bInheritance", String.valueOf(rank.getInheritance().size())));
        inv.setItem(22, createItem(Material.BARRIER, "§cGeri", "Tıklayarak geri dön."));

        player.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, String loreLine) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of("§7" + loreLine));
        item.setItemMeta(meta);
        return item;
    }
}
