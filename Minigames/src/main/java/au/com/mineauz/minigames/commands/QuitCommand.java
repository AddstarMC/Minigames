package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
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

public class QuitCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "quit";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"q"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameUtils.getLang("command.quit.description");
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame quit [Player]",
                "/minigame quit ALL [MinigameName]"
        };
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.quit";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length == 0 && sender instanceof Player player) {
            MinigamePlayer mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(player);
            if (mgPlayer.isInMinigame()) {
                PLUGIN.getPlayerManager().quitMinigame(mgPlayer, false);
            } else {
                sender.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.quit.notPlaying"));
            }
            return true;
        } else if (args.length > 0) {
            if (sender.hasPermission("minigame.quit.other")) {
                if (args[0].equals("ALL")) {
                    if (args.length > 1) {
                        if (PLUGIN.getMinigameManager().hasMinigame(args[1])) {
                            Minigame mg = PLUGIN.getMinigameManager().getMinigame(args[1]);
                            List<MinigamePlayer> pls = new ArrayList<>(mg.getPlayers());
                            for (MinigamePlayer pl : pls) {
                                PLUGIN.getPlayerManager().quitMinigame(pl, true);
                            }
                            sender.sendMessage(ChatColor.GRAY + MinigameMessageManager.getMinigamesMessage("command.quit.quitAllMinigame", mg.getName(true)));
                        } else {
                            sender.sendMessage(ChatColor.RED + MinigameMessageManager.getMinigamesMessage("minigame.error.noMinigameName", args[1]));
                        }
                    } else {
                        for (MinigamePlayer pl : PLUGIN.getPlayerManager().getAllMinigamePlayers()) {
                            if (pl.isInMinigame()) {
                                PLUGIN.getPlayerManager().quitMinigame(pl, true);
                            }
                        }
                        sender.sendMessage(ChatColor.GRAY + MinigameUtils.getLang("command.quit.quitAll"));
                    }
                    return true;
                } else {
                    MinigamePlayer ply;
                    List<Player> players = plugin.getServer().matchPlayer(args[0]);

                    if (players.isEmpty()) {
                        sender.sendMessage(ChatColor.RED + MinigameMessageManager.getMinigamesMessage("command.quit.invalidPlayer", args[0]));
                        return true;
                    } else {
                        ply = PLUGIN.getPlayerManager().getMinigamePlayer(players.get(0));
                    }

                    if (ply.isInMinigame()) {
                        PLUGIN.getPlayerManager().quitMinigame(ply, false);
                        sender.sendMessage(ChatColor.GRAY + MinigameMessageManager.getMinigamesMessage("command.quit.quitOther", ply.getName()));
                    } else {
                        sender.sendMessage(ChatColor.RED + MinigameMessageManager.getMinigamesMessage("command.quit.invalidPlayer", args[0]));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.quit.noPermissionOther"));
                sender.sendMessage(ChatColor.RED + "minigame.quit.other");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> plys = new ArrayList<>(PLUGIN.getServer().getOnlinePlayers().size() + 1);
            for (Player ply : PLUGIN.getServer().getOnlinePlayers()) {
                plys.add(ply.getName());
            }
            plys.add("ALL");
            return MinigameUtils.tabCompleteMatch(plys, args[0]);
        } else if (args.length == 2) {
            List<String> mgs = new ArrayList<>(PLUGIN.getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[1]);
        }
        return null;
    }

}
