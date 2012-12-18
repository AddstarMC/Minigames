package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetBetsCommand implements ICommand {

	@Override
	public String getName() {
		return "bets";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"betting", "bet"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets whether players can place bets to win at the end of a Minigame. Only available in Race, LMS and Deathmatch.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> bets <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to change the betting state of a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.bets";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean par = Boolean.parseBoolean(args[0]);
			if(par){
				minigame.setBetting(par);
				sender.sendMessage(ChatColor.GRAY + "Betting has been enabled for " + minigame);
			}
			else{
				minigame.setBetting(par);
				sender.sendMessage(ChatColor.GRAY + "Betting has been disabled for " + minigame);
			}
			return true;
		}
		return false;
	}

}
