package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetLateJoinCommand implements ICommand {

	@Override
	public String getName() {
		return "latejoin";
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
		return "Enables a player to join after the game has already started. This can only be used in Multiplayer Minigames.\n" +
				"Warning: Do not use this in an LMS Minigame, for obvious reasons.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> latejoin <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to enable late joining to a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.latejoin";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Boolean bool = Boolean.parseBoolean(args[0]);
			minigame.setLateJoin(bool);
			if(bool){
				sender.sendMessage(ChatColor.GRAY + "Late join has been enabled for " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.GRAY + "Late join has been disabled for " + minigame);
			}
			return true;
		}
		return false;
	}

}
