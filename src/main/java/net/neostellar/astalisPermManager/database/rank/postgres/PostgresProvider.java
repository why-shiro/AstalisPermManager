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

        String host = cfg.getString("database.postgresql.host", "localhost");
        int port = cfg.getInt("database.postgresql.port", 5432);
        String db = cfg.getString("database.postgresql.name", "skyrealm");
        String user = cfg.getString("database.postgresql.user", "postgres");
        String pass = cfg.getString("database.postgresql.password", "");
        int poolSize = cfg.getInt("database.postgresql.pool-size", 10);

        // JDBC bağlantı adresi
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db;

        try {
            // 🔧 DRIVER'ı manuel yükle
            Class.forName("org.postgresql.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);
            config.setMaximumPoolSize(poolSize);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(10000); // 10 saniye
            config.setPoolName("SkyRealm-PostgreSQL");
            config.addDataSourceProperty("ssl", "false"); // SSL kapalı, opsiyonel

            dataSource = new HikariDataSource(config);
            plugin.getLogger().info("✅ PostgreSQL bağlantısı kuruldu: " + jdbcUrl);
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("❌ PostgreSQL JDBC Driver sınıfı bulunamadı: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().severe("❌ PostgreSQL bağlantısı kurulamadı: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("PostgreSQL veri kaynağı kapalı veya başlatılmamış.");
        }
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("🔒 PostgreSQL bağlantısı kapatıldı.");
        }
    }
}
