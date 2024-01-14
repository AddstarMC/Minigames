package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EditCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "edit";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Lets you edit a Minigame using a neat menu. Clicking on the menu items will allow"
                + " you to change the settings of the Minigame.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame edit <Minigame>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.edit";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable Minigame minigame,
                             @NotNull String @Nullable [] args) {

        if (args != null) {
            if (plugin.getMinigameManager().hasMinigame(args[0])) {
                Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);
                if (mgm == null) {
                    plugin.getLogger().warning("The Minigame requested has a configuration"
                            + " problem and is returning nulls");
                    return false;
                }
                MinigamePlayer player = plugin.getPlayerManager()
                        .getMinigamePlayer((Player) sender);
                mgm.displayMenu(player);
            } else {
                sender.sendMessage(ChatColor.RED
                        + "There is no Minigame by the name " + args[0]);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args != null && args.length == 1) {
            List<String> mgs
                    = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        }
        return null;
    }

}
