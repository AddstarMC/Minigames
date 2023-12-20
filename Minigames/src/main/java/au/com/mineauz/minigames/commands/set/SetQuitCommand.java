package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetQuitCommand implements ICommand {

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Sets the quitting position of a Minigame to where you are standing.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> quit"};
    }

    @Override
    public String getPermission() {
        return "minigame.set.quit";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, @NotNull String @Nullable [] args) {
        Player player = (Player) sender;
        minigame.setQuitLocation(player.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Quit position has been set for " + minigame);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
