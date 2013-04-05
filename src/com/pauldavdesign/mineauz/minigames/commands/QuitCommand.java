package com.pauldavdesign.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class QuitCommand implements ICommand{

	@Override
	public String getName() {
		return "quit";
	}
	
	@Override
	public String[] getAliases(){
		return new String[] {"q"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Quits the player that issues the command or if a player name is inserted, the named player.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame quit [Player]"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use this command to quit a Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.quit";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args == null && sender instanceof Player){
			if(plugin.pdata.playerInMinigame((Player)sender)){
				plugin.pdata.quitMinigame((Player)sender, false);
			}
			else {
				sender.sendMessage(ChatColor.RED + "Error: You are not in a minigame!");
			}
			return true;
		}
		else if(args != null){
			Player player = null;
			if(sender instanceof Player){
				player = (Player)sender;
			}
			if(player == null || player.hasPermission("minigame.quit.other")){
				List<Player> plist = plugin.pdata.playersInMinigame();
				Player ply = null;
				for(Player p : plist){
					if(p.getName().toLowerCase().contains(args[0].toLowerCase())){
						ply = p;
					}
				}
				if(ply != null){
					plugin.pdata.quitMinigame(ply, false);
					sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to quit the minigame.");
				}
				else{
					sender.sendMessage(ChatColor.RED + "Error: There is no player by that name!");
				}
			}
			else if(player != null){
				sender.sendMessage(ChatColor.RED + "You don't have permission to quit another player!");
				sender.sendMessage(ChatColor.RED + "minigame.quit.other");
			}
			return true;
		}
		return false;
	}

}
