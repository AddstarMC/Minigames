package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetBlockPlaceCommand implements ICommand {

	@Override
	public String getName() {
		return "blockplace";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"bplace"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets whether players can place blocks in Minigames. These will be reverted when the Minigame ends. (Default: false)";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> blockplace <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set block placing!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.blockplace";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = Boolean.parseBoolean(args[0]);
			minigame.setCanBlockPlace(bool);
			if(bool){
				sender.sendMessage(ChatColor.GRAY + "Block placing has been enabled for " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.GRAY + "Block placing has been disabled for " + minigame);
			}
			return true;
		}
		return false;
	}

}
