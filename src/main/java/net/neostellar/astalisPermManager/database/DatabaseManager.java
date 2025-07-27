package net.neostellar.astalisPermManager.database;

import net.neostellar.astalisPermManager.database.rank.postgres.PostgresProvider;
import net.neostellar.astalisPermManager.database.rank.sqlite.SQLiteProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseProvider provider;

    public static void init(JavaPlugin plugin) {
        String mode = plugin.getConfig().getString("database.mode", "sqlite").toUpperCase();

        switch (mode) {
            case "POSTGRESQL" -> provider = new PostgresProvider(plugin);
            case "SQLITE" -> provider = new SQLiteProvider(plugin);
            default -> throw new IllegalArgumentException("Desteklenmeyen veritabanı modu: " + mode);
        }

        provider.init();
    }

    public static Connection getConnection() throws SQLException {
        return provider.getConnection();
    }

    public static void close() {
        if (provider != null) provider.close();
    }
}


