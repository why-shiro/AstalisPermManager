package net.neostellar.astalisPermManager;

import net.neostellar.astalisPermManager.commands.ApmCommandExecutor;
import net.neostellar.astalisPermManager.completer.ApmTabCompleter;
import net.neostellar.astalisPermManager.database.DatabaseManager;
import net.neostellar.astalisPermManager.database.DatabaseType;
import net.neostellar.astalisPermManager.database.dao.DAOProvider;
import net.neostellar.astalisPermManager.listener.ChatFormatListener;
import net.neostellar.astalisPermManager.listener.PlayerJoinListener;
import net.neostellar.astalisPermManager.listener.gui.GuiClickListener;
import net.neostellar.astalisPermManager.listener.gui.GuiPlayerInputListener;
import net.neostellar.astalisPermManager.rank.Rank;
import net.neostellar.astalisPermManager.rank.RankManager;
import net.neostellar.astalisPermManager.service.PermissionService;
import net.neostellar.astalisPermManager.service.RankExpiryWatcher;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class AstalisPermManager extends JavaPlugin {


    private static AstalisPermManager instance;
    private static PermissionService permissionService;
    private static RankManager rankManager;
    private static String version;
    private static boolean debug;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        DatabaseManager.init(this);

        String modeStr = getConfig().getString("database.mode", "sqlite").toUpperCase();
        version = getConfig().getString("config-version", "unkn");
        debug = getConfig().getBoolean("debug", false);
        DatabaseType type = DatabaseType.valueOf(modeStr);
        DAOProvider.init(type);

        rankManager = new RankManager(this);
        permissionService = new PermissionService();

        getLogger().log(Level.INFO, "RankExpiryWatcher is booting...");
        RankExpiryWatcher.start();

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new GuiClickListener(), this);
        getServer().getPluginManager().registerEvents(new GuiPlayerInputListener(), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(), this);

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

    public static RankManager getRankManager() {return rankManager;}

    public static PermissionService getPermissionService() {return permissionService;}

    public static String getVersion(){return version;}

    public static boolean getDebug(){return debug;}
}
