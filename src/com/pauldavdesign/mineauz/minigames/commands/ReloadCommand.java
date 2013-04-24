package com.pauldavdesign.mineauz.minigames.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
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
		return "Reloads the Minigames config files.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame reload"};
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
			if(plugin.pdata.playerInMinigame(p)){
				plugin.pdata.quitMinigame(p, true);
			}
		}
		
		for(Minigame mgm : plugin.getMinigameData().getAllMinigames().values()){
			mgm.loadMinigame();
		}
		
		try{
			plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
			List<String> mgs = new ArrayList<String>();
			if(plugin.getConfig().contains("minigames")){
				mgs = plugin.getConfig().getStringList("minigames");
			}
			final List<String> allMGS = new ArrayList<String>();
			allMGS.addAll(mgs);
			
			if(!mgs.isEmpty()){
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						for(String minigame : allMGS){
							Minigame game = new Minigame(minigame);
							game.loadMinigame();
							plugin.mdata.addMinigame(game);
						}
					}
				}, 1L);
			}
		}
		catch(FileNotFoundException ex){
			plugin.getLogger().info("Failed to load config, creating one.");
			try{
				plugin.getConfig().save(plugin.getDataFolder() + "/config.yml");
			} 
			catch(IOException e){
				plugin.getLogger().log(Level.SEVERE, "Could not save config.yml!");
				e.printStackTrace();
			}
		}
		catch(Exception e){
			plugin.getLogger().log(Level.SEVERE, "Failed to load config!");
			e.printStackTrace();
		}
		
		sender.sendMessage(ChatColor.GREEN + "Reloaded Minigame configs");
		return true;
	}

}
