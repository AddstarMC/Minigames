package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetMinScoreCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "minscore";
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
        return "Sets the minimum score amount for deathmatch and team deathmatch minigames. (Default: 5)";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> minscore <Number>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the minimum score for a Minigame!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.minscore";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int minscore = Integer.parseInt(args[0]);
                minigame.setMinScore(minscore);
                sender.sendMessage(ChatColor.GRAY + "Minimum score has been set to " + minscore + " for " + minigame.getName(false));
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
