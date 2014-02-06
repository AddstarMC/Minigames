package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
//import com.pauldavdesign.mineauz.minigames.StoredPlayerCheckpoints;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

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
		return MinigameUtils.getLang("command.revert.description");
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
		return MinigameUtils.getLang("command.revert.noPermission");
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
//		else if(plugin.pdata.hasStoredPlayerCheckpoint(player)){
//			StoredPlayerCheckpoints spc = plugin.pdata.getPlayersStoredCheckpoints(player);
//			if(spc.hasGlobalCheckpoint()){
//				player.getPlayer().teleport(spc.getGlobalCheckpoint());
//			}
//		}
		else {
			player.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.revert.noGlobal"));
		}
		return true;
	}

}
