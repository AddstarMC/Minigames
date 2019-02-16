package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.presets.PresetLoader;

public class SetPresetCommand implements ICommand {

    @Override
    public String getName() {
        return "preset";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Automatically sets up a Minigame using a preset provided. " +
                "You can add your own presets to the Minigames/presets folder.";
    }

    @Override
    public String[] getParameters() {
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
    public String getPermission() {
        return "minigame.set.preset";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args.length == 1) {
                sender.sendMessage(PresetLoader.loadPreset(args[0], minigame));
                return true;
            } else if (args.length >= 2) {
                sender.sendMessage(ChatColor.AQUA + "------------------Preset Info------------------");
                sender.sendMessage(PresetLoader.getPresetInfo(args[0]));
                return true;
            } else {
                sender.sendMessage(ChatColor.GRAY + "There is no preset by the name " + args[0]);
            }

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 2)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("info"), args[1]);
        return null;
    }

}
