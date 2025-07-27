package net.neostellar.astalisPermManager.rank;

import net.neostellar.astalisPermManager.AstalisPermManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


import java.util.*;

public class RankManager {

    private final AstalisPermManager plugin;
    private final RankFile rankFile;

    private final Map<String, Rank> ranks = new HashMap<>();

    private Rank defaultRank;

    private final Map<UUID, String> playerRanks = new HashMap<>();

    public RankManager(AstalisPermManager plugin) {
        this.plugin = plugin;
        this.rankFile = new RankFile(plugin);
        loadRanks();
    }

    public void loadRanks() {
        ranks.clear();
        FileConfiguration config = rankFile.getConfig();

        ConfigurationSection section = config.getConfigurationSection("ranks");
        if (section == null) {
            plugin.getLogger().severe("[Severe] Could not load ranks. 'ranks' section missing from config!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        for (String id : section.getKeys(false)) {
            String path = "ranks." + id;
            String prefix = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".prefix", ""));
            String suffix = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".suffix", ""));
            List<String> inheritance = config.getStringList(path + ".inheritance");
            int weight = config.getInt(path + ".weight", 0);
            List<String> perms = config.getStringList(path + ".permissions");
            boolean isDefault = config.getBoolean(path + ".default", false);

            net.neostellar.astalisPermManager.rank.Rank rank = new net.neostellar.astalisPermManager.rank.Rank(id, prefix, suffix, weight, inheritance, perms);
            ranks.put(id.toLowerCase(), rank);

            if (isDefault) {
                defaultRank = rank;
            }
        }



        if (defaultRank == null) {
            plugin.getLogger().severe("[Severe] No default rank set! Please mark one rank as default: true");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public Rank getRank(String id) {
        return ranks.getOrDefault(id.toLowerCase(), defaultRank);
    }

    public void reloadRanks() {
        rankFile.reloadConfig();
        loadRanks();
    }


    public Rank getPlayerRank(Player player) {
        String id = playerRanks.getOrDefault(player.getUniqueId(), defaultRank.getId());
        return getRank(id);
    }

    public void setPlayerRank(Player player, String rankId) {
        playerRanks.put(player.getUniqueId(), rankId.toLowerCase());
    }

    public Map<String, Rank> getRanks(){
        return  ranks;
    }

    public Rank getDefaultRank() {
        return defaultRank;
    }

    public String getDefaultRankId() {
        return defaultRank.getId();
    }

    public boolean createRank(String id, String prefix, String suffix, int weight) {
        id = id.toLowerCase();

        if (ranks.containsKey(id)) {
            return false; // Zaten varsa oluşturma
        }

        FileConfiguration config = rankFile.getConfig();
        String path = "ranks." + id;

        config.set(path + ".default", false);
        config.set(path + ".prefix", prefix);
        config.set(path + ".suffix", suffix);
        config.set(path + ".weight", weight);
        config.set(path + ".inheritance", new ArrayList<String>());
        config.set(path + ".permissions", new ArrayList<String>());

        rankFile.saveConfig();
        reloadRanks();

        return true;
    }

    public boolean deleteRank(String id) {
        id = id.toLowerCase();
        if (!ranks.containsKey(id)) return false;

        if (defaultRank != null && defaultRank.getId().equalsIgnoreCase(id)) {
            return false; // Default rank silinemez
        }

        FileConfiguration config = rankFile.getConfig();
        config.set("ranks." + id, null);
        rankFile.saveConfig();
        reloadRanks();
        return true;
    }

    public boolean setDefaultRank(String id) {
        id = id.toLowerCase();
        if (!ranks.containsKey(id)) return false;

        FileConfiguration config = rankFile.getConfig();

        // Eski default'u kaldır
        for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
            config.set("ranks." + key + ".default", false);
        }

        // Yeni default'u ayarla
        config.set("ranks." + id + ".default", true);
        rankFile.saveConfig();
        reloadRanks();
        return true;
    }

    public boolean addPermission(String rankId, String permission) {
        rankId = rankId.toLowerCase();
        if (!ranks.containsKey(rankId)) return false;

        FileConfiguration config = rankFile.getConfig();
        String path = "ranks." + rankId + ".permissions";
        List<String> perms = config.getStringList(path);

        if (perms.contains(permission)) return false; // Zaten varsa ekleme

        perms.add(permission);
        config.set(path, perms);
        rankFile.saveConfig();
        reloadRanks();
        return true;
    }

    public boolean addInheritance(String rankId, String parentId) {
        rankId = rankId.toLowerCase();
        parentId = parentId.toLowerCase();

        if (!ranks.containsKey(rankId) || !ranks.containsKey(parentId)) return false;
        if (rankId.equals(parentId)) return false;

        FileConfiguration config = rankFile.getConfig();
        String path = "ranks." + rankId + ".inheritance";
        List<String> list = config.getStringList(path);

        if (list.contains(parentId)) return false; // Zaten var

        list.add(parentId);
        config.set(path, list);
        rankFile.saveConfig();
        reloadRanks();
        return true;
    }

    public boolean removePermission(String rankId, String permission) {
        rankId = rankId.toLowerCase();
        if (!ranks.containsKey(rankId)) return false;

        FileConfiguration config = rankFile.getConfig();
        String path = "ranks." + rankId + ".permissions";
        List<String> perms = config.getStringList(path);

        if (!perms.contains(permission)) return false;

        perms.remove(permission);
        config.set(path, perms);
        rankFile.saveConfig();
        reloadRanks();
        return true;
    }

    public void setPrefix(String id, String newPrefix) {
        FileConfiguration config = rankFile.getConfig();
        config.set("ranks." + id.toLowerCase() + ".prefix", newPrefix);
        rankFile.saveConfig();
        reloadRanks();
    }

    public void setSuffix(String id, String newSuffix) {
        FileConfiguration config = rankFile.getConfig();
        config.set("ranks." + id.toLowerCase() + ".suffix", newSuffix);
        rankFile.saveConfig();
        reloadRanks();
    }

    public void setWeight(String id, int weight) {
        FileConfiguration config = rankFile.getConfig();
        config.set("ranks." + id.toLowerCase() + ".weight", weight);
        rankFile.saveConfig();
        reloadRanks();
    }


    public boolean removeInheritance(String rankId, String parentId) {
        rankId = rankId.toLowerCase();
        parentId = parentId.toLowerCase();

        if (!ranks.containsKey(rankId)) return false;

        FileConfiguration config = rankFile.getConfig();
        String path = "ranks." + rankId + ".inheritance";
        List<String> list = config.getStringList(path);

        if (!list.contains(parentId)) return false;

        list.remove(parentId);
        config.set(path, list);
        rankFile.saveConfig();
        reloadRanks();
        return true;
    }


}
