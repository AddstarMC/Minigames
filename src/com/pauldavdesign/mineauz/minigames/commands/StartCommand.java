package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.MultiplayerTimer;
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
		return "Starts a Treasure Hunt Minigame. If the game isn't treasure hunt, it will force start a game begin countdown without waiting for players.";
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
		return "You do not have permission to start a Minigame";
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
			
			if(mgm != null){
				if(mgm.getThTimer() == null && mgm.getType() == MinigameType.TREASURE_HUNT){
					plugin.mdata.startGlobalMinigame(mgm.getName());
					mgm.setEnabled(true);
					mgm.saveMinigame();
				}
				else if(mgm.getType() != MinigameType.TREASURE_HUNT && mgm.getType() != MinigameType.SINGLEPLAYER && mgm.hasPlayers()){
					if(mgm.getMpTimer() == null){
						mgm.setMpTimer(new MultiplayerTimer(mgm));
						mgm.getMpTimer().setPlayerWaitTime(0);
						mgm.getMpTimer().startTimer();
					}
					else
						sender.sendMessage(ChatColor.RED + mgm.getName() + " has already started.");
				}
				else if(mgm.getThTimer() != null){
					sender.sendMessage(ChatColor.RED + mgm.getName() + " is already running!");
				}
			}
			else{
				sender.sendMessage(ChatColor.RED + "No Treasure Hunt or Multiplayer Minigame found by the name " + args[0]);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
		return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
	}

}
