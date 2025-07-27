package net.neostellar.astalisPermManager.database.playerperm.sqlite;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.neostellar.astalisPermManager.database.DatabaseProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLitePlayerPermProvider implements DatabaseProvider {
    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public SQLitePlayerPermProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        String fileName = plugin.getConfig().getString("database.sqlite.file", "data.db"); // 👈 değiştirildi
        File dbFile = new File(plugin.getDataFolder(), fileName);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        config.setPoolName("SkyRealm-SQLite");
        config.setMaximumPoolSize(1);

        dataSource = new HikariDataSource(config);
        plugin.getLogger().info("✅ SQLite bağlantısı kuruldu: " + dbFile.getName());
    }


    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        if (dataSource != null) dataSource.close();
    }
}
