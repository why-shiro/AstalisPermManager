package net.neostellar.astalisPermManager;

import net.neostellar.astalisPermManager.commands.ApmCommandExecutor;
import net.neostellar.astalisPermManager.completer.ApmTabCompleter;
import net.neostellar.astalisPermManager.database.DatabaseManager;
import net.neostellar.astalisPermManager.database.DatabaseType;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import net.neostellar.astalisPermManager.listener.PlayerJoinListener;
import net.neostellar.astalisPermManager.listener.gui.GuiClickListener;
import net.neostellar.astalisPermManager.listener.gui.GuiPlayerInputListener;
import net.neostellar.astalisPermManager.rank.RankManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AstalisPermManager extends JavaPlugin {


    private static AstalisPermManager instance;

    private static RankManager rankManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        DatabaseManager.init(this);

        String modeStr = getConfig().getString("database.mode", "sqlite").toUpperCase();
        DatabaseType type = DatabaseType.valueOf(modeStr);
        DAOProvider.init(type);

        rankManager = new RankManager(this);

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new GuiClickListener(), this);
        getServer().getPluginManager().registerEvents(new GuiPlayerInputListener(), this);

        //Commands
        Objects.requireNonNull(getCommand("apm")).setExecutor(new ApmCommandExecutor());

        //Tab Completer
        Objects.requireNonNull(getCommand("apm")).setTabCompleter(new ApmTabCompleter());

    }

    @Override
    public void onDisable() {
        DatabaseManager.close(); // Bağlantıları düzgün kapat
        getLogger().info("❌ AstalisPermManager devre dışı bırakıldı.");
    }

    public static AstalisPermManager getInstance() {
        return instance;
    }

    public static RankManager getRankManager() {
        return rankManager;
    }
}
