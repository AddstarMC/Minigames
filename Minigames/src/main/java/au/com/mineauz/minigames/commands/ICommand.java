package au.com.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

public interface ICommand {
//	public PlayerData pdata = Minigames.plugin.getPlayerData();
//	public MinigameData mdata = Minigames.plugin.getMinigameData();
Minigames plugin = Minigames.plugin;
	
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
