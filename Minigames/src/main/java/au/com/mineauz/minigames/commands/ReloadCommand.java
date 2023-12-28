package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
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
                Minigames.getCmpnntLogger().error("Could not save config.yml!", e);
            }
        } catch (Exception e) {
            Minigames.getCmpnntLogger().error("Failed to load config!", e);
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
