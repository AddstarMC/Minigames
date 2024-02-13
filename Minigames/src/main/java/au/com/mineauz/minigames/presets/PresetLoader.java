package au.com.mineauz.minigames.presets;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.text.WordUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PresetLoader {

    public static void loadPreset(@NotNull String preset, @NotNull Minigame minigame, Audience audience) {
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

            MinigameMessageManager.sendMessage(audience, MinigameMessageType.SUCCESS,
                    MinigameMessageManager.getMgMessage(MinigameLangKey.PRESET_LOAD_SUCCESS,
                            Placeholder.unparsed(MinigamePlaceHolderKey.PRESET.getKey(), WordUtils.capitalizeFully(preset)),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName())));
        } else {
            MinigameMessageManager.sendMessage(audience, MinigameMessageType.ERROR,
                    MinigameMessageManager.getMgMessage(MinigameLangKey.PRESET_LOAD_ERROR_NOTFOUND,
                            Placeholder.unparsed(MinigamePlaceHolderKey.PRESET.getKey(), WordUtils.capitalize(preset))));
        }
    }

    public static void getPresetInfo(@NotNull String preset, @NotNull Audience audience) {
        preset = preset.toLowerCase();
        File file = new File(Minigames.getPlugin().getDataFolder() + "/presets/" + preset + ".yml");
        if (file.exists()) {
            MinigameSave save = new MinigameSave("presets/" + preset);
            FileConfiguration config = save.getConfig();

            String info = config.getString(preset + ".info");
            if (info != null) {
                MinigameMessageManager.sendMessage(audience, MinigameMessageType.INFO,
                        MiniMessage.miniMessage().deserialize(info));
            } else {
                MinigameMessageManager.sendMgMessage(audience, MinigameMessageType.WARNING,
                        MinigameLangKey.PRESET_INFO_NOINFO);
            }
        }
        MinigameMessageManager.sendMessage(audience, MinigameMessageType.ERROR,
                MinigameMessageManager.getMgMessage(MinigameLangKey.PRESET_LOAD_ERROR_NOTFOUND,
                        Placeholder.unparsed(MinigamePlaceHolderKey.PRESET.getKey(), WordUtils.capitalize(preset))));
    }
}
