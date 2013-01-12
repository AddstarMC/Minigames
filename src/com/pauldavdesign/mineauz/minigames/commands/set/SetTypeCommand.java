package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetTypeCommand implements ICommand{

	@Override
	public String getName() {
		return "type";
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
		return "Sets a Minigames game type. All types can be seen in the parameter section.";
	}

	@Override
	public String[] getParameters() {
		String[] mgtypes = new String[plugin.mdata.getMinigameTypes().size() + 1];
		int inc = 0;
		for(String type : plugin.mdata.getMinigameTypes()){
			mgtypes[inc] = type;
			inc++;
		}
		mgtypes[mgtypes.length - 1] = "th";
		return mgtypes;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> type <Type>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set a Minigames type!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.type";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(plugin.mdata.getMinigameTypes().contains(args[0].toLowerCase()) || args[0].equalsIgnoreCase("th")){
				minigame.setType(args[0].toLowerCase());
				sender.sendMessage(ChatColor.GRAY + "Minigame type has been set to " + args[0]);
			}
			else{
				sender.sendMessage(ChatColor.RED + "Error: Invalid minigame type!");
			}
			return true;
		}
		return false;
	}

}
