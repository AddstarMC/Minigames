package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class StartCommand implements ICommand{

	@Override
	public String getName() {
		return "start";
	}
	
	@Override
	public String[] getAliases(){
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Starts a Treasure Hunt Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame start <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to start a Treasure Hunt Minigame";
	}

	@Override
	public String getPermission() {
		return "minigame.start";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
			
			if(mgm != null && mgm.getThTimer() == null && mgm.getType() == MinigameType.TREASURE_HUNT){
				plugin.mdata.startGlobalMinigame(mgm.getName());
				mgm.setEnabled(true);
				mgm.saveMinigame();
			}
			else if(mgm == null || mgm.getType() != MinigameType.TREASURE_HUNT){
				sender.sendMessage(ChatColor.RED + "There is no TreasureHunt Minigame by the name \"" + args[0] + "\"");
			}
			else if(mgm.getThTimer() != null){
				sender.sendMessage(ChatColor.RED + mgm.getName() + " is already running!");
			}
			return true;
		}
		return false;
	}

}
