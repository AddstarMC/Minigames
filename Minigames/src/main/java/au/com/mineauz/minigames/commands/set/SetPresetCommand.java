package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.presets.PresetLoader;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetPresetCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "preset";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Automatically sets up a Minigame using a preset provided. " +
                "You can add your own presets to the Minigames/presets folder.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> preset <Preset>",
                "/minigame set <Minigame> preset <Preset> info"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to use a preset on a Minigame!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.preset";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args.length == 1) {
                sender.sendMessage(PresetLoader.loadPreset(args[0], minigame));
                return true;
            } else if (args.length >= 2) {
                sender.sendMessage(ChatColor.AQUA + "------------------Preset Info------------------");
                sender.sendMessage(PresetLoader.getPresetInfo(args[0]));
                return true;
            } else {
                sender.sendMessage(ChatColor.GRAY + "Please specify the name of the preset!");
            }

        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        if (args.length == 2)
            return MinigameUtils.tabCompleteMatch(List.of("info"), args[1]);
        return null;
    }

}
