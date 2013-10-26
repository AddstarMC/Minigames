package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class JoinCommand implements ICommand{

	@Override
	public String getName() {
		return "join";
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
		return "Forces you to join a Minigame. Warning: This bypasses betting.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame join <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the command to join a Minigame";
	}

	@Override
	public String getPermission() {
		return "minigame.join";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		Player player = (Player)sender;
		if(args != null){
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
			if(mgm != null && (!mgm.getUsePermissions() || player.hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
				if(!plugin.pdata.getMinigamePlayer(player).isInMinigame()){
					sender.sendMessage(ChatColor.GREEN + "Starting " + mgm);
					plugin.pdata.joinMinigame(plugin.pdata.getMinigamePlayer(player), mgm);
				}
				else {
					player.sendMessage(ChatColor.RED + "Error: You are already playing a minigame! Quit this one before joining another.");
				}
			}
			else if(mgm != null && mgm.getUsePermissions()){
				player.sendMessage(ChatColor.RED + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
			}
			else{
				player.sendMessage(ChatColor.RED + "Error: That minigame doesn't exist!");
			}
			return true;
		}
		return false;
	}

}
