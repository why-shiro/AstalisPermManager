package net.neostellar.astalisPermManager.database.rank.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.neostellar.astalisPermManager.database.DatabaseProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresProvider implements DatabaseProvider {
    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public PostgresProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        FileConfiguration cfg = plugin.getConfig();
        String host = cfg.getString("database.postgresql.host");
        int port = cfg.getInt("database.postgresql.port");
        String db = cfg.getString("database.postgresql.name");
        String user = cfg.getString("database.postgresql.user");
        String pass = cfg.getString("database.postgresql.password");
        int poolSize = cfg.getInt("database.postgresql.pool-size");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + db);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(poolSize);
        config.setPoolName("SkyRealm-PostgreSQL");

        dataSource = new HikariDataSource(config);
        plugin.getLogger().info("✅ PostgreSQL bağlantısı kuruldu.");
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

