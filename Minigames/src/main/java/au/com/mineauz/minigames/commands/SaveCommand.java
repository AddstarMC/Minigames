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

public class SaveCommand implements ICommand {

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
    public @NotNull String @Nullable [] getParameters() {
        return null;
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
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (plugin.getMinigameManager().hasMinigame(args[0])) {
                Minigame mg = plugin.getMinigameManager().getMinigame(args[0]);
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
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
