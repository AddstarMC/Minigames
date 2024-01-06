package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PartyModeCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "partymode";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"pm", "party"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Changes party mode state between on and off.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame partymode <true/false>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.partymode";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        if (args != null) {
            boolean bool = Boolean.parseBoolean(args[0]);
            plugin.getPlayerManager().setPartyMode(bool);
            if (bool) {
                sender.sendMessage(ChatColor.GREEN + "Party mode has been enabled! WooHoo!");
            } else {
                sender.sendMessage(ChatColor.RED + "Party mode has been disabled. :(");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         String alias, @NotNull String @NotNull [] args) {
        return MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[0]);
    }

}
