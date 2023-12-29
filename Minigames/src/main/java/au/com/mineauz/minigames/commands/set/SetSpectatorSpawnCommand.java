package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetSpectatorSpawnCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "spectatorstart";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"specstart"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Sets the start position for spectators";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> spectatorstart"};
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to set the spectator start point!";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.spectatorstart";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        Player ply = (Player) sender;
        minigame.setSpectatorLocation(ply.getLocation());
        ply.sendMessage(ChatColor.GRAY + "Set the spectator start point to where you are standing");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
