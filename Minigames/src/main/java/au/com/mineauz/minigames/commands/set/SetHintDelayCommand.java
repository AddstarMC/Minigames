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

public class SetHintDelayCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "hintdelay";
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
        return "Sets the amount of time a player must wait before they can use the hint command again (On this Minigame)";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> hintdelay <time>[m|h]"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the hint delay time!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.hintdelay";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+([mh])?")) {
                int time = Integer.parseInt(args[0].replaceAll("[mh]", ""));
                String mod = args[0].replaceAll("[0-9]", "");
                if (mod.equals("m"))
                    time *= 60;
                else if (mod.equals("h"))
                    time = time * 60 * 60;

                TreasureHuntModule.getMinigameModule(minigame).setHintDelay(time);
                sender.sendMessage(ChatColor.GRAY + minigame.getName(false) +
                        "'s hint delay has been set to " + MinigameUtils.convertTime(time));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
