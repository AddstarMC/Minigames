package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 3/06/2020.
 */
public class ListPlaceholder implements ICommand {
    @Override
    public String getName() {
        return "placeholders";
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
        return "List all registered placeholders";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame placeholders"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to list all placeholders!";
    }

    @Override
    public String getPermission() {
        return "minigame.placeholders";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
        Set<String> placeholders =  plugin.getPlaceHolderManager().getRegisteredPlaceHolders();
        StringBuilder result = new StringBuilder();
        for (String pHolder:placeholders) {
            result.append("%").append(plugin.getName()).append("_").append(pHolder).append("%, ");
        }
        result.delete(result.length()-1,result.length());
        sender.sendMessage("PlaceHolder List");
        sender.sendMessage(result.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame, String alias, String[] args) {
        return null;
    }
}
