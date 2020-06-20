package au.com.mineauz.minigames.commands.set;

import java.security.MessageDigest;
import java.util.List;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.managers.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetStoreCheckpointsCommand implements ICommand {

    @Override
    public String getName() {
        return "storecheckpoints";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"storecp", "spc"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return MessageManager.getMinigamesMessage("command.checkpoint.saving.description");
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> storecheckpoints <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return MessageManager.getMinigamesMessage("command.checkpoint.saving.nopermission");
    }

    @Override
    public String getPermission() {
        return "minigame.set.storecheckpoints";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            Boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setSaveCheckpoint(bool);
            MessageManager.sendMessage(sender,MinigameMessageType.INFO,null,"command.checkpoint.saving.toggle",bool.toString(),minigame.getName(true));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("true;false"), args[0]);
        return null;
    }

}
