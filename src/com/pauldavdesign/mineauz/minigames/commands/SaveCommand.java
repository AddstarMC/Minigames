package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SaveCommand implements ICommand {

	@Override
	public String getName() {
		return "save";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"s"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Saves a Minigame to disk.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame save <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to save a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.save";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			if(plugin.mdata.hasMinigame(args[0])){
				Minigame mg = plugin.mdata.getMinigame(args[0]);
				mg.saveMinigame();
				sender.sendMessage(ChatColor.GRAY + mg.getName() + " has been saved.");
			}
			else{
				sender.sendMessage(ChatColor.RED + "There is no Minigame by the name: " + args[0]);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
		return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
	}

}
