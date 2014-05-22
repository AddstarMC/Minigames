package com.pauldavdesign.mineauz.minigames.commands.set;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetDefaultWinnerCommand implements ICommand {

	@Override
	public String getName() {
		return "defaultwinner";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"defwin"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets which team will win when the timer expires and neither team has won. (Useful for attack/defend modes of CTF) (Default: none).";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"red", "r", "blue", "b", "none"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> defaultwinner <Parameter>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the default winner of a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.defaultwinner";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("red")){
				minigame.setDefaultWinner("red");
				sender.sendMessage(ChatColor.GRAY + "The default winner of " + minigame + " has been set to red.");
				return true;
			}
			else if(args[0].equalsIgnoreCase("b") || args[0].equalsIgnoreCase("blue")){
				minigame.setDefaultWinner("blue");
				sender.sendMessage(ChatColor.GRAY + "The default winner of " + minigame + " has been set to blue.");
				return true;
			}
			else if(args[0].equalsIgnoreCase("none")){
				minigame.setDefaultWinner("none");
				sender.sendMessage(ChatColor.GRAY + "The default winner of " + minigame + " has been set to none.");
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1)
			return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("red;blue;none"), args[0]);
		return null;
	}

}
