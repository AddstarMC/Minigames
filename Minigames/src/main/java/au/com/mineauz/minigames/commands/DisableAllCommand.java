package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisableAllCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "disableall";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"disall"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Disables all Minigames, unless it's added to the exclude list.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame disableall [ExcludedMinigame]..."};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.disableall";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String @Nullable [] args) {
        MinigameManager mdata = Minigames.getPlugin().getMinigameManager();
        List<Minigame> minigames = new ArrayList<>(mdata.getAllMinigames().values());
        if (args != null) {
            for (String arg : args) {
                if (mdata.hasMinigame(arg))
                    minigames.remove(mdata.getMinigame(arg));
                else
                    sender.sendMessage(ChatColor.RED + "No Minigame found by the name \"" + arg + "\"; Ignoring...");
            }
        }
        for (Minigame mg : minigames) {
            mg.setEnabled(false);
        }
        sender.sendMessage(ChatColor.GRAY + String.valueOf(minigames.size()) + " Minigames disabled!");
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
        return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
    }

}
