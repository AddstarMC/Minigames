package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class ReloadCommand implements ICommand{

	@Override
	public String getName() {
		return "reload";
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
		return null;
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return null;
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to reload the plugin!";
	}

	@Override
	public String getPermission() {
		return "minigame.reload";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(Player p : players){
			if(pdata.playerInMinigame(p)){
				pdata.quitMinigame(p, true);
			}
		}
		
		plugin.getServer().getPluginManager().disablePlugin(plugin);
		plugin.getServer().getPluginManager().enablePlugin(plugin);
		
		sender.sendMessage(ChatColor.GREEN + "Reloaded Minigame configs");
		return true;
	}

}
