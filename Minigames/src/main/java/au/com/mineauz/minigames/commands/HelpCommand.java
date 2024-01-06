package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HelpCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "help";
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
        return MinigameMessageManager.getMgMessage("command.help.description");
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame help"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.help";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        sender.sendMessage(ChatColor.GREEN + MinigameMessageManager.getUnformattedMgMessage("command.info.header"));
        sender.sendMessage(ChatColor.BLUE + "/minigame");
        sender.sendMessage(ChatColor.GRAY + MinigameMessageManager.getUnformattedMgMessage("command.info.mgm"));
        if (player == null || player.hasPermission("minigame.join")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame join <Minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.join"));
        }
        if (player == null || player.hasPermission("minigame.quit")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame quit");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.quit"));
            if (player == null || player.hasPermission("minigame.quit.other")) {
                sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.quitOther"));
            }
        }
        if (player == null || player.hasPermission("minigame.end")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame end [Player]");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.end"));
        }
        if (player == null || player.hasPermission("minigame.revert")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame revert");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.revert"));
        }
        if (player == null || player.hasPermission("minigame.delete")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame delete <Minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.delete"));
        }
        if (player == null || player.hasPermission("minigame.hint")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame hint <minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.hint"));
        }
        if (player == null || player.hasPermission("minigame.toggletimer")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame toggletimer <Minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.timer"));
        }
        if (player == null || player.hasPermission("minigame.list")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame list");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.list"));
        }
        if (player == null || player.hasPermission("minigame.reload")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame reload");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.reload"));
        }

        sender.sendMessage(ChatColor.BLUE + "/minigame set <Minigame> <parameter>...");
        sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.set"));
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
