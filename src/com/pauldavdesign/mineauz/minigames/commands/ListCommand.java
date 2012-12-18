package com.pauldavdesign.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class ListCommand implements ICommand{

	@Override
	public String getName() {
		return "list";
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
		return "You do not have permission to list all Minigames!";
	}

	@Override
	public String getPermission() {
		return "minigame.list";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		List<String> mglist = plugin.getConfig().getStringList("minigames");
		String minigames = "";
		
		for(int i = 0; i < mglist.size(); i++){
			minigames += mglist.get(i);
			if(i != mglist.size() - 1){
				minigames += ", ";
			}
		}
		
		sender.sendMessage(ChatColor.GRAY + minigames);
		return true;
	}

}
