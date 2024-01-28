package au.com.mineauz.minigames.presets;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PresetLoader {

    public static Component loadPreset(@NotNull String preset, @NotNull Minigame minigame) {
        preset = preset.toLowerCase();
        File file = new File(Minigames.getPlugin().getDataFolder() + "/presets/" + preset + ".yml");
        if (file.exists()) {
            MinigameSave save = new MinigameSave("presets/" + preset);
            FileConfiguration config = save.getConfig();

            for (String opt : config.getConfigurationSection(preset).getKeys(false)) {
                Flag<?> flag = minigame.getConfigFlag(opt);
                if (flag != null) {
                    flag.loadValue(preset, config);
                }
            }

            return ChatColor.GRAY + "Loaded the " +
                    WordUtils.capitalizeFully(preset) + " preset to " + minigame.getName(false);
        } else {
            return ChatColor.RED + "Failed to load preset: " +
                    ChatColor.GRAY + preset + ".yml was not found in the presets folder!";
        }
    }

    public static Component getPresetInfo(String preset) {
        preset = preset.toLowerCase();
        File file = new File(Minigames.getPlugin().getDataFolder() + "/presets/" + preset + ".yml");
        if (file.exists()) {
            MinigameSave save = new MinigameSave("presets/" + preset);
            FileConfiguration config = save.getConfig();

            if (config.contains(preset + ".info")) {
                return MiniMessage.miniMessage().deserialize(config.getString(preset + ".info"));
            } else {
                return "No information given on this preset.";
            }
        }
        return ChatColor.RED + "Failed to load preset: " +
                ChatColor.GRAY + preset + ".yml was not found in the presets folder!";
    }

}
