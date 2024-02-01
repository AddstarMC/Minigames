package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SaveCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "save";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"s"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Saves a Minigame to disk.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame save <Minigame>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.save";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args != null) {
            if (PLUGIN.getMinigameManager().hasMinigame(args[0])) {
                Minigame mg = PLUGIN.getMinigameManager().getMinigame(args[0]);
                mg.saveMinigame();
                sender.sendMessage(ChatColor.GRAY + mg.getName(false) + " has been saved.");
            } else {
                sender.sendMessage(ChatColor.RED + "There is no Minigame by the name: " + args[0]);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        List<String> mgs = new ArrayList<>(PLUGIN.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
