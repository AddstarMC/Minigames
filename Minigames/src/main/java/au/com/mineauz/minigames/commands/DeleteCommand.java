package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeleteCommand implements ICommand {

    @Override
    public String getName() {
        return "delete";
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
        return "Deletes a Minigame from existance. It will be gone forever! (A very long time)";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame delete <Minigame>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to delete Minigames!";
    }

    @Override
    public String getPermission() {
        return "minigame.delete";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            Minigame mgm = plugin.getMinigameManager().getMinigame(args[0]);

            if (mgm != null) {
                File save = new File(plugin.getDataFolder() + "/minigames/" + mgm.getName(false));
                if (save.exists() && save.isDirectory()) {
                    if (save.list().length == 0) {
                        save.delete();
                    } else {
                        for (String file : save.list()) {
                            File nfile = new File(save, file);
                            nfile.delete();
                        }
                        save.delete();
                    }
                    List<String> ls = plugin.getConfig().getStringList("minigames");
                    ls.remove(mgm.getName(false));
                    plugin.getConfig().set("minigames", ls);
                    plugin.getMinigameManager().removeMinigame(mgm.getName(false));
                    plugin.saveConfig();
                    sender.sendMessage(ChatColor.RED + "The minigame " + mgm.getName(false) + " has been removed");
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>(Minigames.getPlugin().getMinigameManager().getAllMinigames().keySet());
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        }
        return null;
    }

}
