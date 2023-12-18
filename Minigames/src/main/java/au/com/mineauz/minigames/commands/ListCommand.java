package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListCommand implements ICommand {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Lists all the Minigames.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame list"};
    }

    @Override
    public String getPermission() {
        return "minigame.list";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        List<String> mglist = plugin.getConfig().getStringList("minigames");
        StringBuilder minigames = new StringBuilder();

        for (int i = 0; i < mglist.size(); i++) {
            minigames.append(mglist.get(i));
            if (i != mglist.size() - 1) {
                minigames.append(", ");
            }
        }

        sender.sendMessage(ChatColor.GRAY + minigames.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
