package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetObjectiveCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "objective";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"obj"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Sets the objective description for the player to see when they join a Minigame. Typing \"null\" will remove the objective.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> objective <Objective Here>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the objective!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.objective";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (!args[0].equals("null")) {
                StringBuilder obj = new StringBuilder();
                int count = 0;
                for (String arg : args) {
                    obj.append(arg);
                    count++;
                    if (count != args.length)
                        obj.append(" ");
                }
                minigame.setObjective(obj.toString());
                sender.sendMessage(ChatColor.GRAY + "The objective for " + minigame + " has been set.");
            } else {
                minigame.setObjective(null);
                sender.sendMessage(ChatColor.GRAY + "The objective for " + minigame + " has been removed.");
            }

            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
