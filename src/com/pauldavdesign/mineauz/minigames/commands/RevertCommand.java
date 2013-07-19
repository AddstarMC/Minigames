package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.StoredPlayerCheckpoints;

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
		return "Reverts the player that issues the command to their last checkpoint or to the start position if none is set.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame revert"};
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
		MinigamePlayer player = plugin.pdata.getMinigamePlayer((Player)sender);
		
		if(player.isInMinigame()){
			plugin.pdata.revertToCheckpoint(player);
		}
		else if(plugin.pdata.hasStoredPlayerCheckpoint(player)){
			StoredPlayerCheckpoints spc = plugin.pdata.getPlayersStoredCheckpoints(player);
			if(spc.hasGlobalCheckpoint()){
				player.getPlayer().teleport(spc.getGlobalCheckpoint());
			}
		}
		else {
			player.sendMessage(ChatColor.RED + "Error: You do not have any global checkpoints!");
		}
		return true;
	}

}
