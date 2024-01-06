package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetRestartDelayCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "restartdelay";
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
        return "Sets how long it will take for a Treasure Hunt Minigame to respawn its treasure.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> restartdelay <time>[m|h]"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the restart delay for a treasure hunt game!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.restartdelay";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+([mh])?")) {
                int time = Integer.parseInt(args[0].replaceAll("[mh]", ""));
                String mod = args[0].replaceAll("[0-9]", "");
                if (mod.equals("m"))
                    time *= 60;
                else if (mod.equals("h"))
                    time = time * 60 * 60;

                TreasureHuntModule.getMinigameModule(minigame).setTreasureWaitTime(time);
                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) +
                        "'s restart delay has been set to " + MinigameUtils.convertTime(time));
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
