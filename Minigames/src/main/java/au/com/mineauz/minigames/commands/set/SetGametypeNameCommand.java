package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetGametypeNameCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "gametypename";
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
        return "Sets the name of the game type that displays when a player joins (Replacing \"Singleplayer\" and \"Free For All\"). " +
                "Typing \"null\" will remove the name.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> gametypename <Name>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to set the gametype name!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.gametypename";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            if (!args[0].equals("null")) {
                StringBuilder gtn = new StringBuilder();
                int count = 0;
                for (String arg : args) {
                    gtn.append(arg);
                    count++;
                    if (count != args.length)
                        gtn.append(" ");
                }
                minigame.setGameTypeName(gtn.toString());
                sender.sendMessage(ChatColor.GRAY + "Gametype name for " + minigame + " has been set to " + gtn + ".");
            } else {
                minigame.setGameTypeName(null);
                sender.sendMessage(ChatColor.GRAY + "Gametype name for " + minigame + " has been removed.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
