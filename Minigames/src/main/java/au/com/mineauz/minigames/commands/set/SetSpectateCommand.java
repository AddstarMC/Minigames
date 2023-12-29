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

public class SetSpectateCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "spectatefly";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"specfly"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Enables or disabled spectator fly mode for a Minigame. (Default: false)";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> spectatefly <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to enable or disable spectator fly mode in a Minigame!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.spectatefly";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setCanSpectateFly(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GRAY + "Enabled spectator flying in " + minigame);
            } else
                sender.sendMessage(ChatColor.GRAY + "Disabled spectator flying in " + minigame);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
        return null;
    }

}
