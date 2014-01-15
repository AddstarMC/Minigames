package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

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
		return "Sets a Minigames game type. All types can be seen in the parameter section. (also can be used as an alias of preset).";
	}

	@Override
	public String[] getParameters() {
		String[] mgtypes = new String[plugin.mdata.getMinigameTypes().size() + 1];
		int inc = 0;
		for(MinigameType type : plugin.mdata.getMinigameTypes()){
			mgtypes[inc] = type.toString();
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
			if(MinigameType.hasValue(args[0])){
				minigame.setType(MinigameType.valueOf(args[0].toUpperCase()));
				sender.sendMessage(ChatColor.GRAY + "Minigame type has been set to " + args[0]);
			}
			else if(plugin.mdata.hasPreset(args[0].toLowerCase())){
				plugin.mdata.getPreset(args[0].toLowerCase()).execute(minigame);
				sender.sendMessage(ChatColor.GRAY + "Applied the Minigame preset \"" + args[0] + "\" to " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.RED + "Error: Invalid minigame type!");
			}
			return true;
		}
		return false;
	}

}
