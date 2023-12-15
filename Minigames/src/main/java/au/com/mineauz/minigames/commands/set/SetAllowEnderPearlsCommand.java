package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetAllowEnderPearlsCommand implements ICommand {

    @Override
    public String getName() {
        return "allowenderpearls";
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
    public Component getDescription() {
        return "Sets whether players can use ender pearls in a Minigame.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> allowenderpearls <true / false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to change allow ender pearl usage!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.allowenderpearls";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable @NotNull [] args) {
        if (args != null) {
            if (Boolean.parseBoolean(args[0])) {
                minigame.setAllowEnderPearls(true);
                sender.sendMessage(ChatColor.GRAY + "Allowed ender pearl usage in " + minigame);
            } else {
                minigame.setAllowEnderPearls(false);
                sender.sendMessage(ChatColor.GRAY + "Disallowed ender pearl usage in " + minigame);
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
