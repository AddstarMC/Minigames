package au.com.mineauz.minigames.config;

import au.com.mineauz.minigames.Minigames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class MinigameSave {
    private final @NotNull String name;
    private final @Nullable String minigame;
    private FileConfiguration minigameSave = null;
    private File minigameSaveFile = null;

    public MinigameSave(@NotNull String name) {
        this.name = name;
        this.minigame = null;
        reloadFile();
        saveConfig();
    }

    public MinigameSave(@NotNull String minigame, @NotNull String name) {
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
        } else {
            if (minigameSaveFile == null) {
                minigameSaveFile = new File(Minigames.getPlugin().getDataFolder() + "/", name + ".yml");
            }
        }
        minigameSave = YamlConfiguration.loadConfiguration(minigameSaveFile);
    }

    public FileConfiguration getConfig() {
        if (minigameSave == null) {
            reloadFile();
        }
        return minigameSave;
    }

    public void saveConfig() {
        if (minigameSave == null || minigameSaveFile == null) {
            if (minigame != null) {
                Minigames.getCmpnntLogger().info("Could not save " + minigame + File.separator + name + " config file!");
            } else {
                Minigames.getCmpnntLogger().info("Could not save " + name + " config file!");
            }
            return;
        }
        try {
            minigameSave.save(minigameSaveFile);
        } catch (IOException ex) {
            if (minigame != null) {
                Minigames.getCmpnntLogger().error("Could not save " + minigame + File.separator + name + " config file!");
            } else {
                Minigames.getCmpnntLogger().error("Could not save " + name + " config file!");
            }
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
