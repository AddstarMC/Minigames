package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetMinTreasureCommand implements ICommand {

	@Override
	public String getName() {
		return "mintreasure";
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
		return "Sets the minimum number of items to spawn in a treasure hunt chest.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> mintreasure <Number>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the minimum treasure!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.mintreasure";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(args[0].matches("[0-9]+")){
				int amount = Integer.parseInt(args[0]);
				minigame.setMinTreasure(amount);
				sender.sendMessage(ChatColor.GRAY + "Minimum items has been set to " + amount + " for " + minigame);
				return true;
			}
		}
		return false;
	}

}
