package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {
    //    public MinigamePlayerManager playerManager = Minigames.plugin.getPlayerData();
//    public MinigameManager minigameManager = Minigames.plugin.getMinigameData();
    Minigames plugin = Minigames.getPlugin();

    String getName();

    String[] getAliases();

    boolean canBeConsole();

    String getDescription();

    String[] getParameters();

    String[] getUsage();

    String getPermissionMessage();

    String getPermission();

    boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args);

    List<String> onTabComplete(CommandSender sender, Minigame minigame, String alias, String[] args);
}
