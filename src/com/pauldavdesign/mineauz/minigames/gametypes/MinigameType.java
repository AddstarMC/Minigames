package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
//import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.MultiplayerTimer;
import com.pauldavdesign.mineauz.minigames.PlayerData;

public abstract class MinigameType implements Listener{
	private static Minigames plugin;
	private PlayerData pdata;
	private MinigameData mdata;
	
	protected MinigameType(){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
		mdata = plugin.mdata;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public String typeLabel;
	
	public void setLabel(String label){
		typeLabel = label;
	}
	
	public String getLabel(){
		return typeLabel;
	}
	
	public abstract boolean joinMinigame(Player player, Minigame mgm);
	
	public abstract void quitMinigame(Player player, Minigame mgm, boolean forced);
	
	public abstract void endMinigame(Player player, Minigame mgm);
	
	public void callGeneralQuit(final Player player){
		final String minigame = pdata.getPlayersMinigame(player);
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				player.teleport(mdata.getMinigame(minigame).getQuitPosition());
			}
		});
		
		pdata.removePlayerCheckpoints(player);
		pdata.removeAllPlayerFlags(player);
		
		pdata.removePlayerDeath(player);
		pdata.removePlayerKills(player);
		
		player.sendMessage(ChatColor.RED + "You've left the " + minigame + " minigame.");
		
		player.setFireTicks(0);
		
		plugin.getLogger().info(player.getName() + " quit " + minigame);
	}
	
	public boolean callLMSJoin(Player player, Minigame mgm, GameMode gm){ //TODO: Remove gamemode later as its defined in the Minigame
		if(mgm.getQuitPosition() != null && mgm.isEnabled() && mgm.getEndPosition() != null && mgm.getLobbyPosition() != null){
			
			for(PotionEffect potion : player.getActivePotionEffects()){
				player.removePotionEffect(potion.getType());
			}
			player.setAllowFlight(false);
			plugin.getLogger().info(player.getName() + " started " + mgm.getName());

			String gametype = mgm.getType();
			
			Location lobby = mgm.getLobbyPosition();
			if(!mgm.getPlayers().isEmpty() && mdata.getMinigame(mgm.getName()).getPlayers().size() < mgm.getMaxPlayers()){
				if(mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0){
					pdata.storePlayerData(player, gm);
					
					player.teleport(lobby);
					mgm.addPlayer(player);
					player.sendMessage(ChatColor.GREEN + "You have started a spleef minigame, type /minigame quit to exit.");
				
					if(mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMinPlayers()){
						mgm.setMpTimer(new MultiplayerTimer(mgm.getName()));
						mgm.getMpTimer().start();
					}
					else{
						int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
						if(neededPlayers == 1){
							player.sendMessage(ChatColor.BLUE + "Waiting for 1 more player.");
						}
						else if(neededPlayers > 1){
							player.sendMessage(ChatColor.BLUE + "Waiting for " + neededPlayers + " more players.");
						}
					}

					List<Player> plys = pdata.playersInMinigame();
					for(Player ply : plys){
						if(mgm.getName().equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
							ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + mgm.getName());
						}
					}
					return true;
				}
				else if(mgm.getMpTimer().getPlayerWaitTimeLeft() == 0){
					player.sendMessage(ChatColor.RED + "The minigame has already started. Try again soon.");
					return false;
				}
			}
			else if(mgm.getPlayers().isEmpty()){
				pdata.storePlayerData(player, gm);
				
				player.teleport(lobby);
				mgm.addPlayer(player);
				player.sendMessage(ChatColor.GREEN + "You have started a " + gametype + " minigame, type /minigame quit to exit.");
				
				int neededPlayers = mgm.getMinPlayers() - 1;
				
				if(neededPlayers > 0){
					player.sendMessage(ChatColor.BLUE + "Waiting for " + neededPlayers + " more players.");
				}
				else
				{
					mgm.setMpTimer(new MultiplayerTimer(mgm.getName()));
					mgm.getMpTimer().start();
				}

				List<Player> plys = pdata.playersInMinigame();
				for(Player ply : plys){
					if(mgm.getName().equals(pdata.getPlayersMinigame(ply)) && !ply.getName().equals(player.getName())){
						ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + player.getName() + " has joined " + mgm.getName());
					}
				}
				return true;
			}
			else if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
				player.sendMessage(ChatColor.RED + "Sorry, this minigame is full.");
			}
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no quit position!");
		}
		else if(mgm.getEndPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no end position!");
		}
		else if(mgm.getLobbyPosition() == null){
			player.sendMessage(ChatColor.RED + "This minigame has no lobby!");
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void issuePlayerRewards(Player player, Minigame save, boolean hascompleted){
		if(save.getRewardItem() != null && !hascompleted){
			player.getInventory().addItem(save.getRewardItem());
		}
		else if(save.getSecondaryRewardItem() != null && hascompleted){
			player.getInventory().addItem(save.getSecondaryRewardItem());
		}
		player.updateInventory();
		
		if(Minigames.plugin.hasEconomy()){
			if(save.getRewardPrice() != 0 && !hascompleted){
				Minigames.plugin.getEconomy().depositPlayer(player.getName(), save.getRewardPrice());
				player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + String.format("You have been awarded $%s.", save.getRewardPrice()));
			}
			else if(save.getSecondaryRewardPrice() != 0 && hascompleted){
				Minigames.plugin.getEconomy().depositPlayer(player.getName(), save.getSecondaryRewardPrice());
				player.sendMessage(ChatColor.GREEN + "[Minigames] " + ChatColor.WHITE + String.format("You have been awarded $%s.", save.getSecondaryRewardPrice()));
			}
		}
	}
}
