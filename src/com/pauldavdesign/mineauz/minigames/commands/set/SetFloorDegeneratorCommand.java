package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetFloorDegeneratorCommand implements ICommand{
	@Override
	public String getName() {
		return "floordegenerator";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"floord", "floordegen"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Sets the two corners of a floor to degenerate (cubeoid/rectangle) or clears both of them (if set).";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"1", "2", "clear"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> floordegenerator <Parameters>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the Minigames floor area!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.floordegenerator";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Player player = (Player)sender;
			if(args[0].equals("1")){
				minigame.setSpleefFloor1(player.getLocation());
				sender.sendMessage(ChatColor.GRAY + "Floor degenerator corner 1 has been set for " + minigame);
			}
			else if(args[0].equals("2")){
				minigame.setSpleefFloor2(player.getLocation());
				sender.sendMessage(ChatColor.GRAY + "Floor degenerator corner 2 has been set for " + minigame);
			}
			else if(args[0].equalsIgnoreCase("clear")){
				minigame.setSpleefFloor1(null);
				minigame.setSpleefFloor2(null);
				sender.sendMessage(ChatColor.GRAY + "Floor degenerator corners have been removed for " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.RED + "Error: Invalid floor degenerator position, use 1, 2 or clear");
			}
			return true;
		}
		return false;
	}

}
