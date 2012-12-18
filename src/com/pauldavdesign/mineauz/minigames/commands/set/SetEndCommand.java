package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetEndCommand implements ICommand{

	@Override
	public String getName() {
		return "end";
	}
	
	@Override
	public String[] getAliases(){
		return null;
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
		return "You do not have permission to set the end position!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.end";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		minigame.setEndPosition(((Player) sender).getLocation());
		sender.sendMessage(ChatColor.GRAY + "Ending position has been set for " + minigame);
		return true;
	}

}
