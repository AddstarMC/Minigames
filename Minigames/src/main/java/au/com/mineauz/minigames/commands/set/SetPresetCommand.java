package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.presets.PresetLoader;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetPresetCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "preset";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_PRESET_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_PRESET_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.preset";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args.length == 1) {
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.INFO, PresetLoader.loadPreset(args[0], minigame));
                return true;
            } else if (args.length >= 2) {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_SET_PRESET_HEADER);
                MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE, PresetLoader.getPresetInfo(args[0]));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull @Nullable [] args) {
        if (args.length == 2) {
            return MinigameUtils.tabCompleteMatch(List.of("info"), args[1]);
        }
        return null;
    }

}
