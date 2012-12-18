package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetStartCommand implements ICommand{

	@Override
	public String getName() {
		return "start";
	}
	
	@Override
	public String[] getAliases(){
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Sets the start point for the Minigame. Adding a player number sets that specific players start point.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> start [player number]", 
				"/minigame set <Minigame> start <team colour> [player number]"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set a players start point!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.start";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
		Player player = (Player)sender;
		
		if(args == null){
			minigame.setStartLocation(player.getLocation());
			sender.sendMessage(ChatColor.GRAY + "Starting position has been set for " + minigame.getName());
			return true;
		}
		else if(args.length == 1 && args[0].matches("[0-9]+")){
			int position = Integer.parseInt(args[0]);
			
			if(position >= 1){
				minigame.addStartLocation(player.getLocation(), position);
				sender.sendMessage(ChatColor.GRAY + "Starting position has been set for player " + args[0]);
			}
			else{
				sender.sendMessage(ChatColor.RED + "Error: Invalid starting position: " + args[0]);
				return false;
			}
			return true;
		}
		else if(args.length == 2 && (args[0].matches("b|blue") || args[0].matches("r|red")) && args[1].matches("[0-9]+")){
			int position = Integer.parseInt(args[1]);
			int team = 0;
			
			if(args[0].matches("b|blue")){
				team = 1;
			}
			
			if(position >= 1){
				if(team == 0){
					minigame.addStartLocationRed(player.getLocation(), position);
					sender.sendMessage(ChatColor.GRAY + "Starting position for " + ChatColor.RED + "red team" + ChatColor.GRAY + " has been set for player " + position);
				}
				else{
					minigame.addStartLocationBlue(player.getLocation(), position);
					sender.sendMessage(ChatColor.GRAY + "Starting position for " + ChatColor.BLUE + "blue team" + ChatColor.GRAY + " has been set for player " + position);
				}
			}
			else{
				sender.sendMessage(ChatColor.RED + "Error: Invalid starting position: " + args[1]);
				return false;
			}
			return true;
		}
		return false;
	}
}
