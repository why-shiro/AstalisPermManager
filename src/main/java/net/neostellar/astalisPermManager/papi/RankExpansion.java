package net.neostellar.astalisPermManager.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.neostellar.astalisPermManager.AstalisPermManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RankExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "apm";
    }

    @Override
    public @NotNull String getAuthor() {
        return "why_shiro";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // reload sonrası da kayıtlı kal
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("prefix")) {
            return AstalisPermManager.getPermissionService().getPrefixByUUID(player.getUniqueId());

        }

        if (params.equalsIgnoreCase("suffix")) {
            return AstalisPermManager.getPermissionService().getSuffixByUUID(player.getUniqueId());
        }

        if (params.equalsIgnoreCase("full_tag")) {
            return (AstalisPermManager.getPermissionService().getPrefixByUUID(player.getUniqueId()) + " " + player.getName() + AstalisPermManager.getPermissionService().getSuffixByUUID(player.getUniqueId()));
        }

        return null;
    }


}
