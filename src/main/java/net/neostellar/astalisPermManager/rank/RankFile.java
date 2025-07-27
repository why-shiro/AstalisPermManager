package net.neostellar.astalisPermManager.rank;

import net.neostellar.astalisPermManager.AstalisPermManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class RankFile {

    private final AstalisPermManager astalisPermManager;
    private final File file;
    private FileConfiguration config;

    public RankFile(AstalisPermManager astalisPermManager) {
        this.astalisPermManager = astalisPermManager;
        this.file = new File(astalisPermManager.getDataFolder(), "ranks.yml");

        if (!this.file.exists()) {
            astalisPermManager.saveResource("ranks.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void saveConfig() {
        try {
            config.save(file);
        }catch (IOException e){
            astalisPermManager.getLogger().severe("[KRITIK] ranks.yml kaydedilemedi!");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

     public FileConfiguration getConfig() {
        return this.config;
    }

}
