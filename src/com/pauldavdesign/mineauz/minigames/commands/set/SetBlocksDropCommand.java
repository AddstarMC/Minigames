package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetBlocksDropCommand implements ICommand {

	@Override
	public String getName() {
		return "blocksdrop";
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
		return "Sets whether blocks drop item when broken within a Minigame. (Default: true)";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> blocksdrop <true/false>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set whether blocks can drop!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.blocksdrop";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = Boolean.parseBoolean(args[0]);
			minigame.setBlocksdrop(bool);
			if(bool){
				sender.sendMessage(ChatColor.GRAY + "Blocks can now drop when broken in " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.GRAY + "Blocks will no longer drop when broken in " + minigame);
			}
			return true;
		}
		return false;
	}

}
