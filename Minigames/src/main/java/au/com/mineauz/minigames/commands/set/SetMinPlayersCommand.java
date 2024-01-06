package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetMinPlayersCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "minplayers";
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
        return "Sets the minimum players for a multiplayer Minigame. (Default: 2)";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> minplayers <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the minimum players for a Minigame!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.minplayers";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int min = Integer.parseInt(args[0]);
                minigame.setMinPlayers(min);
                sender.sendMessage(ChatColor.GRAY + "Minimum players has been set to " + min + " for " + minigame);
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
