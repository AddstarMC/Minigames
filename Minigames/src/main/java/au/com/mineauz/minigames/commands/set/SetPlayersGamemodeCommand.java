package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SetPlayersGamemodeCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "gamemode";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"gm"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Sets the players gamemode when they join a Minigame. (Default: adventure)";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return new String[]{"survival", "adventure", "creative", "0", "1", "2"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> gamemode <Parameter>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the players gamemode for a Minigame!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.gamemode";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0")) {
                minigame.setDefaultGamemode(GameMode.SURVIVAL);
                sender.sendMessage(minigame.getName(false) + "'s gamemode has been set to Survival.");
                return true;
            } else if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("2")) {
                minigame.setDefaultGamemode(GameMode.ADVENTURE);
                sender.sendMessage(minigame.getName(false) + "'s gamemode has been set to Adventure.");
                return true;
            } else if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1")) {
                minigame.setDefaultGamemode(GameMode.CREATIVE);
                sender.sendMessage(minigame.getName(false) + "'s gamemode has been set to Creative.");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1)
            return MinigameUtils.tabCompleteMatch(Arrays.stream(GameMode.values()).map(gm -> gm.name().toLowerCase()).toList(), args[0]);
        return null;
    }

}
