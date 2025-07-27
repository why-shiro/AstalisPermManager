package net.neostellar.astalisPermManager.completer;

import net.neostellar.astalisPermManager.AstalisPermManager;
import net.neostellar.astalisPermManager.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApmTabCompleter implements TabCompleter {

    private static final List<String> ROOT_COMMANDS = Arrays.asList("rank", "player");
    private static final List<String> RANK_SUBCOMMANDS = Arrays.asList(
            "give", "create", "delete", "setdefault", "list", "show",
            "addperm", "removeperm", "addinherit", "removeinherit", "rankgui"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!(sender instanceof Player)) return List.of();

        if (!sender.isOp() && !sender.hasPermission("apm.admin.tab")) {
            return List.of();
        }

        RankManager rankManager = AstalisPermManager.getRankManager();

        if (args.length == 1) {
            return partial(ROOT_COMMANDS, args[0]);
        }

        if (args[0].equalsIgnoreCase("rank")) {
            if (args.length == 2) {
                return partial(RANK_SUBCOMMANDS, args[1]);
            }

            String sub = args[1].toLowerCase();

            switch (sub) {
                case "give":
                    if (args.length == 3) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(p -> p.getName())
                                .filter(p -> p.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    } else if (args.length == 4) {
                        return partial(rankManager.getRanks().keySet(), args[3]);
                    } else if (args.length == 5) {
                        return List.of("1d", "5h", "30m", "permanent");
                    }
                    break;

                case "create":
                    if (args.length == 4) return List.of("&a[PREFIX]");
                    if (args.length == 5) return List.of("&7[SUFFIX]");
                    if (args.length == 6) return List.of("10", "50", "100");
                    break;

                case "delete":
                case "setdefault":
                case "rankgui":
                case "show":
                    if (args.length == 3) {
                        return partial(rankManager.getRanks().keySet(), args[2]);
                    }
                    break;

                case "addperm":
                case "removeperm":
                case "addinherit":
                case "removeinherit":
                    if (args.length == 3) {
                        return partial(rankManager.getRanks().keySet(), args[2]);
                    }
                    if (args.length == 4 && sub.contains("perm")) {
                        return List.of("fly.use", "test.test", "*");
                    }
                    if (args.length == 4 && sub.contains("inherit")) {
                        return partial(rankManager.getRanks().keySet(), args[3]);
                    }
                    break;
            }
        }

        if (args[0].equalsIgnoreCase("player") && args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(p -> p.getName())
                    .filter(p -> p.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    private List<String> partial(Iterable<String> options, String prefix) {
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(prefix.toLowerCase())) {
                matches.add(option);
            }
        }
        return matches;
    }
}
