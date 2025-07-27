package net.neostellar.astalisPermManager.utils;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.guis.RankDetailGui;
import net.neostellar.astalisPermManager.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatInputManager {
    private static final Map<UUID, ChatInputSession> waiting = new HashMap<>();

    public static void requestInput(Player player, String type, String rankId) {
        waiting.put(player.getUniqueId(), new ChatInputSession(type, rankId));
        player.sendMessage("§eLütfen yeni " + type + " değerini girin. İptal etmek için 'iptal' yaz.");
    }

    public static boolean handleInput(Player player, String message) {
        ChatInputSession session = waiting.remove(player.getUniqueId());
        if (session == null) return false;

        RankManager rankManager = AstalisPermManager.getRankManager();
        String id = session.rankId;

        if (message.equalsIgnoreCase("iptal")) {
            player.sendMessage("§cİşlem iptal edildi.");
            Bukkit.getScheduler().runTask(AstalisPermManager.getInstance(), () ->
                    new RankDetailGui(rankManager.getRank(id)).open(player));
            return true;
        }

        switch (session.type) {
            case "prefix":
                rankManager.setPrefix(id, message);
                player.sendMessage("§aYeni prefix ayarlandı: §r" + message);
                break;
            case "suffix":
                rankManager.setSuffix(id, message);
                player.sendMessage("§aYeni suffix ayarlandı: §r" + message);
                break;
            case "weight":
                try {
                    int weight = Integer.parseInt(message);
                    rankManager.setWeight(id, weight);
                    player.sendMessage("§aYeni weight ayarlandı: §e" + weight);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cWeight sayısal olmalı.");
                }
                break;
        }

        Bukkit.getScheduler().runTask(AstalisPermManager.getInstance(), () ->
                new RankDetailGui(rankManager.getRank(id)).open(player));

        return true;
    }


    private static class ChatInputSession {
        String type;
        String rankId;

        ChatInputSession(String type, String rankId) {
            this.type = type;
            this.rankId = rankId;
        }
    }
}

