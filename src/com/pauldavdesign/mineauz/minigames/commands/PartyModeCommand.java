package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class PartyModeCommand implements ICommand{

	@Override
	public String getName() {
		return "partymode";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"pm", "party"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Changes party mode state between on and off.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame partymode <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You don't have permission to change party mode!";
	}

	@Override
	public String getPermission() {
		return "minigame.partymode";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = Boolean.parseBoolean(args[0]);
			plugin.pdata.setPartyMode(bool);
			if(bool){
				sender.sendMessage(ChatColor.GREEN + "Party mode has been enabled! WooHoo!");
			}
			else{
				sender.sendMessage(ChatColor.RED + "Party mode has been disabled. :(");
			}
			return true;
		}
		return false;
	}

}
