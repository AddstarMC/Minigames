package com.pauldavdesign.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class EndCommand implements ICommand{

	@Override
	public String getName() {
		return "end";
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
		return "Ends a players Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame end [Player]"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to force end your Minigame!";
	}

	@Override
	public String getPermission() {
		return "minigame.end";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args == null && sender instanceof Player){
			if(pdata.playerInMinigame((Player)sender)){
				pdata.endMinigame((Player)sender);
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
			if(player == null || player.hasPermission("minigame.end.other")){
				List<Player> plist = pdata.playersInMinigame();
				Player ply = null;
				for(Player p : plist){
					if(p.getName().toLowerCase().contains(args[0].toLowerCase())){
						ply = p;
					}
				}
				
				if(ply != null){
					pdata.endMinigame(ply);
					sender.sendMessage(ChatColor.GRAY + "You forced " + ply.getName() + " to end the minigame.");
				}
				else{
					sender.sendMessage(ChatColor.RED + "This player is not playing a minigame.");
				}
			}
			else if(player != null){
				sender.sendMessage(ChatColor.RED + "Error: You don't have permission to force end another players Minigame!");
				sender.sendMessage(ChatColor.RED + "minigame.end.other");
			}
			return true;
		}
		return false;
	}

}
