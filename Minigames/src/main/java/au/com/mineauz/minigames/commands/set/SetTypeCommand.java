package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetTypeCommand implements ICommand {

    @Override
    public String getName() {
        return "type";
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
        return "Sets a Minigames game type. All types can be seen in the parameter section. (also can be used as an alias of preset).";
    }

    @Override
    public String[] getParameters() {
        String[] mgtypes = new String[plugin.getMinigameManager().getMinigameTypes().size() + 1];
        int inc = 0;
        for (MinigameType type : MinigameType.values()) {
            mgtypes[inc] = type.toString();
            inc++;
        }
        return mgtypes;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> type <Type>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set a Minigames type!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.type";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (MinigameType.hasValue(args[0])) {
                minigame.setType(MinigameType.valueOf(args[0].toUpperCase()));
                sender.sendMessage(ChatColor.GRAY + "Minigame type has been set to " + args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "Error: Invalid minigame type!");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> types = new ArrayList<>();
            for (MinigameType t : MinigameType.values()) {
                types.add(t.toString());
            }
            return MinigameUtils.tabCompleteMatch(types, args[0]);
        }
        return null;
    }

}
