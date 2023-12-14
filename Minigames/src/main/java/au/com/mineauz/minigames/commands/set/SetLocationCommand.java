package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetLocationCommand implements ICommand {

    @Override
    public String getName() {
        return "location";
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
        return "Sets the location name for a treasure hunt Minigame.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> location <Location Name Here>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set a Minigames location!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.location";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            StringBuilder location = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                location.append(args[i]);
                if (i != args.length - 1) {
                    location.append(" ");
                }
            }
            TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
            thm.setLocation(location.toString());
            sender.sendMessage(ChatColor.GRAY + "The location name for " + minigame + " has been set to " + location);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
