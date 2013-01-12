package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.Minigames;

public interface ICommand {
//	public PlayerData pdata = Minigames.plugin.getPlayerData();
//	public MinigameData mdata = Minigames.plugin.getMinigameData();
	public Minigames plugin = Minigames.plugin;
	
	public String getName();
	
	public String[] getAliases();
	
	public boolean canBeConsole();
	
	public String getDescription();
	
	public String[] getParameters();
	
	public String[] getUsage();
	
	public String getPermissionMessage();
	
	public String getPermission();
	
	public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args);
}
