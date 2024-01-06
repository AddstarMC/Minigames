package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetLocationCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "location";
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
        return "Sets the location name for a treasure hunt Minigame.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> location <Location Name Here>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set a Minigames location!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.location";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            StringBuilder location = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                location.append(args[i]);
                if (i != args.length - 1) {
                    location.append(" ");
                }
            }
            TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(minigame);
            thm.setLocation(location.toString());
            sender.sendMessage(ChatColor.GRAY + "The location name for " + minigame + " has been set to " + location);
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
