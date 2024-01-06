package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JoinCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "join";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameUtils.getLang("command.join.description");
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame join <Minigame>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.join";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame, @NotNull String label, @NotNull String @Nullable [] args) {
        Player player = (Player) sender;
        if (args != null) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);
            if (mgm != null && (!mgm.getUsePermissions() || player.hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))) {
                if (!plugin.getPlayerManager().getMinigamePlayer(player).isInMinigame()) {
                    sender.sendMessage(ChatColor.GREEN + MinigameMessageManager.getMinigamesMessage("command.join.joining", mgm.getName(false)));
                    plugin.getPlayerManager().joinMinigame(plugin.getPlayerManager().getMinigamePlayer(player), mgm, false, 0.0);
                } else {
                    player.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.join.alreadyPlaying"));
                }
            } else if (mgm != null && mgm.getUsePermissions()) {
                player.sendMessage(ChatColor.RED + MinigameMessageManager.getMinigamesMessage("command.join.noMinigamePermission", "minigame.join." + mgm.getName(false).toLowerCase()));
            } else {
                player.sendMessage(ChatColor.RED + MinigameUtils.getLang("minigame.error.noMinigame"));
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>(plugin.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
        }
        return null;
    }

}
