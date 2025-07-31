package net.neostellar.astalisPermManager.commands;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import net.neostellar.astalisPermManager.guis.RankListGui;
import net.neostellar.astalisPermManager.rank.Rank;
import net.neostellar.astalisPermManager.rank.RankManager;
import net.neostellar.astalisPermManager.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ApmCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 0) {
            commandSender.sendMessage("§c/apm <rank|player> ...");
            return true;
        }

        switch (strings[0].toLowerCase()) {
            case "rank":
                handleRankCommand(commandSender, strings);
                break;
            case "player":
                handlePlayerCommand(commandSender, strings);
                break;
            default:
                commandSender.sendMessage("§cBilinmeyen komut: " + strings[0]);
        }

        return true;
    }

    private void handleRankCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /apm rank <give|create|delete|setdefault|addperm|removeperm|addinherit|removeinherit|list|show>");
            return;
        }

        String sub = args[1].toLowerCase();
        RankManager manager = AstalisPermManager.getRankManager();

        switch (sub) {
            case "give":
                if (!hasPermissionOrWarn(sender, "give")) return;
                if (args.length != 5) {
                    sender.sendMessage("§cKullanım: /apm rank give <player> <rank_id> <duration>");
                    return;
                }
                String playerName = args[2];
                String rankId = args[3];
                String durationStr = args[4];

                try {
                    if (durationStr.equalsIgnoreCase("permanent")){
                        sender.sendMessage("§a" + playerName + " adlı oyuncuya " + rankId + " rütbesi verildi. Süre: §cSınırsız");
                        DAOProvider.getPlayerRankDAO().setPlayerRank(Bukkit.getPlayer(playerName).getUniqueId(), rankId, null);

                        Player target = Bukkit.getPlayer(playerName);
                        if (target != null && target.isOnline()) {
                            AstalisPermManager.getPermissionService().refreshAsync(target);
                        }
                    }else {
                        long duration = TimeUtils.parseDuration(durationStr);
                        Instant expiry = Instant.now().plusMillis(duration);
                        sender.sendMessage("§a" + playerName + " adlı oyuncuya " + rankId + " rütbesi verildi. Süre: " + duration + "ms");
                        DAOProvider.getPlayerRankDAO().setPlayerRank(Bukkit.getPlayer(playerName).getUniqueId(), rankId, expiry);

                        Player target = Bukkit.getPlayer(playerName);
                        if (target != null && target.isOnline()) {
                            AstalisPermManager.getPermissionService().refreshAsync(target);
                        }

                    }
                } catch (Exception e) {
                    sender.sendMessage("§cGeçersiz süre formatı.");
                }
                break;

            case "create":
                if (!hasPermissionOrWarn(sender, "create")) return;
                if (args.length < 3) {
                    sender.sendMessage("§cKullanım: /apm rank create <id> [prefix] [suffix] [weight]");
                    return;
                }
                String id = args[2];
                String prefix = args.length > 3 ? args[3] : "";
                String suffix = args.length > 4 ? args[4] : "";
                int weight = 0;
                if (args.length > 5) {
                    try {
                        weight = Integer.parseInt(args[5]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§cWeight sayısal olmalı.");
                        return;
                    }
                }
                boolean success = manager.createRank(id, prefix, suffix, weight);
                sender.sendMessage(success ? "§aRank oluşturuldu: " + id : "§cBu ID zaten var.");
                AstalisPermManager.getPermissionService().refreshAll();
                break;

            case "delete":
                if (!hasPermissionOrWarn(sender, "delete")) return;
                if (args.length < 3) {
                    sender.sendMessage("§cKullanım: /apm rank delete <id>");
                    return;
                }
                if (manager.deleteRank(args[2])) {
                    sender.sendMessage("§aRank silindi: " + args[2]);
                    AstalisPermManager.getPermissionService().refreshAll();
                } else {
                    sender.sendMessage("§cRank silinemedi.");
                }
                break;

            case "setdefault":
                if (!hasPermissionOrWarn(sender, "setdefault")) return;
                if (args.length < 3) {
                    sender.sendMessage("§cKullanım: /apm rank setdefault <id>");
                    return;
                }
                if (manager.setDefaultRank(args[2])) {
                    sender.sendMessage("§aVarsayılan rank ayarlandı: " + args[2]);
                    AstalisPermManager.getPermissionService().refreshAll();
                } else {
                    sender.sendMessage("§cRank bulunamadı.");
                }
                break;

            case "rankgui":
                if (!hasPermissionOrWarn(sender, "rankgui")) return;
                Player player = (Player) sender;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                new RankListGui().open(player);
                break;

            case "addperm":
                if (!hasPermissionOrWarn(sender, "addperm")) return;
                if (args.length < 4) {
                    sender.sendMessage("§cKullanım: /apm rank addperm <id> <permission>");
                    return;
                }
                if (manager.addPermission(args[2], args[3])) {
                    sender.sendMessage("§aİzin eklendi.");
                    AstalisPermManager.getPermissionService().refreshAll();
                } else {
                    sender.sendMessage("§cEklenemedi.");
                }
                break;

            case "removeperm":
                if (!hasPermissionOrWarn(sender, "removeperm")) return;
                if (args.length < 4) {
                    sender.sendMessage("§cKullanım: /apm rank removeperm <id> <permission>");
                    return;
                }
                if (manager.removePermission(args[2], args[3])) {
                    sender.sendMessage("§aİzin kaldırıldı.");
                    AstalisPermManager.getPermissionService().refreshAll();
                } else {
                    sender.sendMessage("§cKaldırılamadı.");
                }
                break;

            case "addinherit":
                if (!hasPermissionOrWarn(sender, "addinherit")) return;
                if (args.length < 4) {
                    sender.sendMessage("§cKullanım: /apm rank addinherit <id> <parent_id>");
                    return;
                }
                if (manager.addInheritance(args[2], args[3])) {
                    sender.sendMessage("§aMiras eklendi.");
                    AstalisPermManager.getPermissionService().refreshAll();
                } else {
                    sender.sendMessage("§cEklenemedi.");
                }
                break;

            case "removeinherit":
                if (!hasPermissionOrWarn(sender, "removeinherit")) return;
                if (args.length < 4) {
                    sender.sendMessage("§cKullanım: /apm rank removeinherit <id> <parent_id>");
                    return;
                }
                if (manager.removeInheritance(args[2], args[3])) {
                    sender.sendMessage("§aMiras kaldırıldı.");
                    AstalisPermManager.getPermissionService().refreshAll();
                } else {
                    sender.sendMessage("§cKaldırılamadı.");
                }
                break;

            case "list":
                if (!hasPermissionOrWarn(sender, "list")) return;
                sender.sendMessage("§6[Ranks]");
                for (Rank rank : manager.getRanks().values()) {
                    String line = " §7- §e" + rank.getId();
                    if (rank == manager.getDefaultRank()) {
                        line += " §7(§aVarsayılan§7)";
                    }
                    sender.sendMessage(line);
                }
                break;

            case "show":
                if (!hasPermissionOrWarn(sender, "show")) return;
                if (args.length < 3) {
                    sender.sendMessage("§cKullanım: /apm rank show <id>");
                    return;
                }

                Rank rankToShow = manager.getRank(args[2]);
                if (rankToShow == null) {
                    sender.sendMessage("§cRank bulunamadı.");
                    return;
                }

                sender.sendMessage("§6[Rank: " + rankToShow.getId() + "]");
                sender.sendMessage("§7Prefix: §r" + rankToShow.getPrefix());
                sender.sendMessage("§7Suffix: §r" + rankToShow.getSuffix());
                sender.sendMessage("§7Weight: §e" + rankToShow.getWeight());
                sender.sendMessage("§7Inheritance:");
                if (rankToShow.getInheritance().isEmpty()) {
                    sender.sendMessage(" §8(yok)");
                } else {
                    for (String inh : rankToShow.getInheritance()) {
                        sender.sendMessage(" §7- §e" + inh);
                    }
                }
                sender.sendMessage("§7Permissions:");
                if (rankToShow.getPermissions().isEmpty()) {
                    sender.sendMessage(" §8(yok)");
                } else {
                    for (String perm : rankToShow.getPermissions()) {
                        sender.sendMessage(" §7- §f" + perm);
                    }
                }
                break;

            default:
                sender.sendMessage("§cBilinmeyen rank alt komutu.");
        }
    }


    private void handlePlayerCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /apm player <oyuncu_ismi|setperm> [...]");
            return;
        }

        if (args[1].equalsIgnoreCase("setperm")) {
            if (!hasPermissionOrWarn(sender, "setperm")) return;
            if (args.length != 5) {
                sender.sendMessage("§cKullanım: /apm player setperm <oyuncu_ismi> <permission> <süre>");
                sender.sendMessage("§7Süre örnekleri: 10m, 2h, 1d, permanent");
                return;
            }

            String playerName = args[2];
            UUID uuid = Bukkit.getPlayer(playerName).getUniqueId();
            if (uuid == null) {
                sender.sendMessage("§cOyuncu bulunamadı: " + playerName);
                return;
            }

            String permission = args[3];
            String durationStr = args[4];

            try {
                Instant expiry = null;
                if (!durationStr.equalsIgnoreCase("permanent")) {
                    long duration = TimeUtils.parseDuration(durationStr);
                    expiry = Instant.now().plusMillis(duration);
                }

                DAOProvider.getPlayerPermissionDAO().setPermission(uuid, permission, expiry);
                sender.sendMessage("§a" + playerName + " adlı oyuncuya §e" + permission + " §aizni verildi.");
                if (expiry != null) {
                    sender.sendMessage("§7Sona erme: §e" + expiry.toString());
                } else {
                    sender.sendMessage("§7Sona erme: §aSınırsız");
                }

                AstalisPermManager.getPermissionService().refreshAsync(Bukkit.getPlayer(uuid));

            } catch (Exception e) {
                sender.sendMessage("§cGeçersiz süre formatı.");
                e.printStackTrace();
            }
            return;
        } else if (args[1].equalsIgnoreCase("removeperm")) {
            if (!hasPermissionOrWarn(sender, "removeperm")) return;
            if (args.length != 4) {
                sender.sendMessage("§cKullanım: /apm player removeperm <oyuncu_ismi> <permission>");
                return;
            }

            String playerName = args[2];
            UUID uuid = Bukkit.getPlayer(playerName).getUniqueId();
            if (uuid == null) {
                sender.sendMessage("§cOyuncu bulunamadı: " + playerName);
                return;
            }

            String permission = args[3];

            DAOProvider.getPlayerPermissionDAO().removePermission(uuid, permission);
            sender.sendMessage("§e" + permission + " §cizni " + playerName + " adlı oyuncudan kaldırıldı.");

            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                AstalisPermManager.getPermissionService().refreshAsync(player);
            }

            return;
        }

        String playerName = args[1];
        UUID uuid = Bukkit.getPlayer(playerName).getUniqueId();
        if (uuid == null) {
            sender.sendMessage("§cOyuncu bulunamadı: " + playerName);
            return;
        }

        String rankId = DAOProvider.getPlayerRankDAO().getPlayerRank(uuid);
        Instant expiry = DAOProvider.getPlayerRankDAO().getRankExpiry(uuid);
        List<String> permissions = DAOProvider.getPlayerPermissionDAO().getActivePermissions(uuid);

        sender.sendMessage("§6[" + playerName + "] §7Kullanıcı Bilgileri");
        sender.sendMessage("§7UUID: §f" + uuid.toString());

        if (rankId != null) {
            sender.sendMessage("§7Rank: §a" + rankId);
            if (expiry != null) {
                long remaining = expiry.getEpochSecond() - Instant.now().getEpochSecond();
                String humanReadable = TimeUtils.formatDuration(remaining * 1000);
                sender.sendMessage("§7Sona erme: §e" + expiry.toString() + " §8(" + humanReadable + ")");
            } else {
                sender.sendMessage("§7Sona erme: §aSınırsız");
            }
        } else {
            sender.sendMessage("§7Rank: §cYok");
        }

        sender.sendMessage("§7Aktif Yetkiler:");
        if (permissions.isEmpty()) {
            sender.sendMessage(" §8(yok)");
        } else {
            for (String perm : permissions) {
                sender.sendMessage(" §7- §f" + perm);
            }
        }
    }


    private boolean hasPermissionOrWarn(CommandSender sender, String node) {
        if (sender.hasPermission("apm.admin." + node)) return true;
        sender.sendMessage("§cBu komutu kullanmak için §eapm.admin." + node + " §ciznine sahip olmalısın.");
        return false;
    }


}
