package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetSpleefFloorCommand implements ICommand{
	@Override
	public String getName() {
		return "spleeffloor";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"sfloor"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Sets the two corners of a spleef floor (cubeoid/rectangle)";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> spleeffloor <1/2>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the Minigames floor area!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.spleeffloor";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Player player = (Player)sender;
			if(args[0].equals("1")){
				minigame.setSpleefFloor1(player.getLocation());
				sender.sendMessage(ChatColor.GRAY + "Spleef floor corner 1 has been set for " + minigame);
			}
			else if(args[0].equals("2")){
				minigame.setSpleefFloor2(player.getLocation());
				sender.sendMessage(ChatColor.GRAY + "Spleef floor corner 2 has been set for " + minigame);
			}
			else{
				sender.sendMessage(ChatColor.RED + "Error: Invalid spleef floor position, use 1 or 2");
			}
			return true;
		}
		return false;
	}

}
