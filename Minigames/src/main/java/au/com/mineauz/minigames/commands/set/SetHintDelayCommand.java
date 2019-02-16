package au.com.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;

public class SetHintDelayCommand implements ICommand {

    @Override
    public String getName() {
        return "hintdelay";
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
        return "Sets the amount of time a player must wait before they can use the hint command again (On this Minigame)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> hintdelay <time>[m|h]"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the hint delay time!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.hintdelay";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+(m|h)?")) {
                int time = Integer.parseInt(args[0].replaceAll("[mh]", ""));
                String mod = args[0].replaceAll("[0-9]", "");
                if (mod.equals("m"))
                    time *= 60;
                else if (mod.equals("h"))
                    time = time * 60 * 60;

                TreasureHuntModule.getMinigameModule(minigame).setHintDelay(time);
                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) +
                        "'s hint delay has been set to " + MinigameUtils.convertTime(time));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
