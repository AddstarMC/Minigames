package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetBlocksDropCommand implements ICommand {

    @Override
    public String getName() {
        return "blocksdrop";
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
        return "Sets whether blocks drop item when broken within a Minigame. (Default: true)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> blocksdrop <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set whether blocks can drop!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.blocksdrop";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable @NotNull [] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setBlocksDrop(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Blocks can now drop when broken in " + minigame);
            } else {
                sender.sendMessage(ChatColor.GRAY + "Blocks will no longer drop when broken in " + minigame);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
        return null;
    }

}
