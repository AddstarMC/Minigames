package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class RevertCommand implements ICommand{

	@Override
	public String getName() {
		return "revert";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"r"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return null;
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to revert to your checkpoint!";
	}

	@Override
	public String getPermission() {
		return "minigame.revert";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		Player player = (Player)sender;
		
		if(pdata.playerInMinigame(player)){
			pdata.revertToCheckpoint(player);
		}
		else {
			player.sendMessage(ChatColor.RED + "Error: You are not in a minigame!");
		}
		return true;
	}

}
