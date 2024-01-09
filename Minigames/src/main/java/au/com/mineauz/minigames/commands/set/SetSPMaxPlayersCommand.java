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

public class SetSPMaxPlayersCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "spmaxplayers";
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
        return "Sets whether a singleplayer game should have max players or not. (Default: false)";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> spmaxplayers <true/false>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to change singleplayer max players!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.spmaxplayers";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             String @NotNull [] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            minigame.setSpMaxPlayers(bool);
            if (bool)
                sender.sendMessage(ChatColor.GRAY + "Enabled singleplayer max players.");
            else
                sender.sendMessage(ChatColor.RED + "Disabled singleplayer max players.");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
        return null;
    }

}
