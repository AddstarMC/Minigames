package com.pauldavdesign.mineauz.minigames.commands.set;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetScoreTypeCommand implements ICommand {

	@Override
	public String getName() {
		return "scoretype";
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
		return "Sets the scoring type for a multiplayer Minigame.";
	}

	@Override
	public String[] getParameters() {
		String[] types = new String[plugin.getScoreTypes().getScoreTypes().keySet().size()];
		int inc = 0;
		for(String type : plugin.getScoreTypes().getScoreTypes().keySet()){
			types[inc] = type;
			inc++;
		}
		return types;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> scoretype <Parameter>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the score type!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.scoretype";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = false;
			for(String par : getParameters()){
				if(par.equalsIgnoreCase(args[0])){
					bool = true;
					break;
				}
			}
			
			if(bool){
				minigame.setScoreType(args[0].toLowerCase());
				sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + " score type has been set to " + args[0]);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> types = new ArrayList<String>(plugin.getScoreTypes().getScoreTypes().keySet().size());
			for(String type : plugin.getScoreTypes().getScoreTypes().keySet()){
				types.add(type);
			}
			return MinigameUtils.tabCompleteMatch(types, args[0]);
		}
		return null;
	}

}
