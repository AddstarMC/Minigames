package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeniedCommandCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "deniedcommand";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"deniedcomd", "deniedcom"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Sets commands to be disabled when playing a Minigame. (eg: home or spawn)";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame deniedcommand add <Command>", "/minigame deniedcommand remove <Command>", "/minigame deniedcommand list"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.deniedcommands";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            if (args[0].equalsIgnoreCase("add") && args.length >= 2) {
                plugin.getPlayerManager().addDeniedCommand(args[1]);
                sender.sendMessage(ChatColor.GRAY + "Added \"" + args[1] + "\" to the denied command list.");
                return true;
            } else if (args[0].equalsIgnoreCase("remove") && args.length >= 2) {
                plugin.getPlayerManager().removeDeniedCommand(args[1]);
                sender.sendMessage(ChatColor.GRAY + "Removed \"" + args[1] + "\" from the denied command list.");
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                String coms = String.join(", ", plugin.getPlayerManager().getDeniedCommands());
                sender.sendMessage(ChatColor.GRAY + "Disabled Commands: " + coms);
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return MinigameUtils.tabCompleteMatch(List.of("add", "remove", "list"), args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return MinigameUtils.tabCompleteMatch(plugin.getPlayerManager().getDeniedCommands(), args[1]);
        }
        return null;
    }

}
