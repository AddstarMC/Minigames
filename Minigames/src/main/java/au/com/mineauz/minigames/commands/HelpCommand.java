package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HelpCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "help";
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
    public String[] getUsage() {
        return new String[]{"/minigame help"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.help";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        sender.sendMessage(ChatColor.GREEN + MinigameMessageManager.getUnformattedMgMessage("command.info.header"));
        sender.sendMessage(ChatColor.BLUE + "/minigame");
        sender.sendMessage(ChatColor.GRAY + MinigameMessageManager.getUnformattedMgMessage("command.info.mgm"));
        if (sender.hasPermission("minigame.join")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame join <Minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.join"));
        }
        if (sender.hasPermission("minigame.quit")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame quit");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.quit"));
            if (sender.hasPermission("minigame.quit.other")) {
                sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.quitOther"));
            }
        }
        if (sender.hasPermission("minigame.end")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame end [Player]");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.end"));
        }
        if (sender.hasPermission("minigame.revert")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame revert");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.revert"));
        }
        if (sender.hasPermission("minigame.delete")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame delete <Minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.delete"));
        }
        if (sender.hasPermission("minigame.hint")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame hint <minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.hint"));
        }
        if (sender.hasPermission("minigame.toggletimer")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame toggletimer <Minigame>");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.timer"));
        }
        if (sender.hasPermission("minigame.list")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame list");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.list"));
        }
        if (sender.hasPermission("minigame.reload")) {
            sender.sendMessage(ChatColor.BLUE + "/minigame reload");
            sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.reload"));
        }

        sender.sendMessage(ChatColor.BLUE + "/minigame set <Minigame> <parameter>...");
        sender.sendMessage(MinigameMessageManager.getMessage(null, "command.info.set"));
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
