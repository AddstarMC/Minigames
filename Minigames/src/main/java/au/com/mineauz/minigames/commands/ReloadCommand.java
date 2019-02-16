package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReloadCommand implements ICommand {

    @Override
    public String getName() {
        return "reload";
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
        return "Reloads the Minigames config files.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame reload"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to reload the plugin!";
    }

    @Override
    public String getPermission() {
        return "minigame.reload";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getPlayerManager().getMinigamePlayer(p).isInMinigame()) {
                plugin.getPlayerManager().quitMinigame(plugin.getPlayerManager().getMinigamePlayer(p), true);
            }
        }

        Minigames.getPlugin().getMinigameManager().getAllMinigames().clear();

        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (FileNotFoundException ex) {
            plugin.getLogger().info("Failed to load config, creating one.");
            try {
                plugin.getConfig().save(plugin.getDataFolder() + "/config.yml");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config.yml!");
                e.printStackTrace();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config!");
            e.printStackTrace();
        }

        List<String> mgs = new ArrayList<>();
        if (Minigames.getPlugin().getConfig().contains("minigames")) {
            mgs = Minigames.getPlugin().getConfig().getStringList("minigames");
        }
        final List<String> allMGS = new ArrayList<>(mgs);

        if (!mgs.isEmpty()) {
            for (String mgm : allMGS) {
                Minigame game = new Minigame(mgm);
                game.loadMinigame();
                Minigames.getPlugin().getMinigameManager().addMinigame(game);
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Reloaded Minigame configs");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

}
