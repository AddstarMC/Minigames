package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.Minigames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MinigameSave {
    String minigame = null;
    private FileConfiguration minigameSave = null;
    private File minigameSaveFile = null;
    private String name;

    public MinigameSave(String name) {
        this.name = name;
        reloadFile();
        saveConfig();
    }

    public MinigameSave(String minigame, String name) {
        this.minigame = minigame;
        this.name = name;
        reloadFile();
        saveConfig();
    }

    public void reloadFile() {
        if (minigame != null) {
            if (minigameSaveFile == null) {
                minigameSaveFile = new File(Minigames.getPlugin().getDataFolder() + "/minigames/" + minigame + "/", name + ".yml");
            }
            minigameSave = YamlConfiguration.loadConfiguration(minigameSaveFile);
        } else {
            if (minigameSaveFile == null) {
                minigameSaveFile = new File(Minigames.getPlugin().getDataFolder() + "/", name + ".yml");
            }
            minigameSave = YamlConfiguration.loadConfiguration(minigameSaveFile);
        }
    }

    public FileConfiguration getConfig() {
        if (minigameSave == null) {
            reloadFile();
        }
        return minigameSave;
    }

    public void saveConfig() {
        if (minigameSave == null || minigameSaveFile == null) {
            Minigames.getPlugin().getLogger().log(Level.INFO, "Could not save " + minigame + File.separator + name + " config file!");
            return;
        }
        try {
            minigameSave.save(minigameSaveFile);
        } catch (IOException ex) {
            Minigames.getPlugin().getLogger().log(Level.SEVERE, "Could not save " + minigame + File.separator + name + " config file!");
        }
    }

    public void deleteFile() {
        if (minigameSave == null) {
            reloadFile();
        }
        File delfile = new File(minigameSaveFile.getPath());
        delfile.delete();
        minigameSaveFile = null;
    }
}
