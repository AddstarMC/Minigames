package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
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
	
	public abstract boolean joinMinigame(MinigamePlayer player, Minigame mgm);
	
	public abstract void quitMinigame(MinigamePlayer player, Minigame mgm, boolean forced);
	
	public abstract void endMinigame(MinigamePlayer player, Minigame mgm);
	
	public void callGeneralQuit(final MinigamePlayer player, final Minigame minigame){
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(!player.getPlayer().isDead()){
					pdata.minigameTeleport(player, minigame.getQuitPosition());
				}
				else{
					player.setQuitPos(minigame.getQuitPosition());
					player.setRequiredQuit(true);
				}
			}
		});
	}
	
	public boolean callLMSJoin(MinigamePlayer player, Minigame mgm){
		if(mgm.getQuitPosition() != null && mgm.isEnabled() && mgm.getEndPosition() != null && mgm.getLobbyPosition() != null){
			
			String gametype = mgm.getType();
			if(gametype.equals("dm"))
				gametype = "deathmatch";
			if(mgm.getScoreType().equals("ctf"))
				gametype += " CTF";
			
			Location lobby = mgm.getLobbyPosition();
			if(mdata.getMinigame(mgm.getName()).getPlayers().size() < mgm.getMaxPlayers()){
				if((mgm.canLateJoin() && mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() == 0) 
						|| mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0){
					player.storePlayerData();
					player.setMinigame(mgm);
					
					mgm.addPlayer(player);
					if(mgm.getMpTimer() == null || mgm.getMpTimer().getStartWaitTimeLeft() != 0){
						pdata.minigameTeleport(player, lobby);
						if(mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMaxPlayers()){
							mgm.setMpTimer(new MultiplayerTimer(mgm));
							mgm.getMpTimer().startTimer();
							mgm.getMpTimer().setPlayerWaitTime(0);
							mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
						}
					}
					else{
						player.sendMessage(MinigameUtils.formStr("minigame.lateJoin", 5));
						pdata.minigameTeleport(player, lobby);
						final MinigamePlayer fply = player;
						final Minigame fmgm = mgm;
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							
							@Override
							public void run() {
								if(fply.isInMinigame()){
									List<Location> locs = new ArrayList<Location>();
									locs.addAll(fmgm.getStartLocations());
									Collections.shuffle(locs);
									pdata.minigameTeleport(fply, locs.get(0));
									fply.getLoadout().equiptLoadout(fply);
								}
							}
						}, 100);

						player.getPlayer().setScoreboard(mgm.getScoreboardManager());
						mgm.setScore(player, 1);
						mgm.setScore(player, 0);
					}
					player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", mgm.getType()), "win");
				
					if(mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMinPlayers()){
						mgm.setMpTimer(new MultiplayerTimer(mgm));
						mgm.getMpTimer().startTimer();
						if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
							mgm.getMpTimer().setPlayerWaitTime(0);
							mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
						}
					}
					else{
						int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
						if(neededPlayers == 1){
							player.sendMessage(MinigameUtils.formStr("minigame.waitingForPlayers", 1), null);
						}
						else if(neededPlayers > 1){
							player.sendMessage(MinigameUtils.formStr("minigame.waitingForPlayers", neededPlayers), null);
						}
					}
					return true;
				}
				else if((mgm.canLateJoin() && mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() != 0)){
					player.sendMessage(MinigameUtils.formStr("minigame.lateJoinWait", mgm.getMpTimer().getStartWaitTimeLeft()), null);
					return false;
				}
				else if(mgm.getMpTimer().getPlayerWaitTimeLeft() == 0){
					player.sendMessage(MinigameUtils.getLang("minigame.started"), "error");
					return false;
				}
			}
			else if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
				player.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
			}
		}
		else if(mgm.getQuitPosition() == null){
			player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), "error");
		}
		else if(mgm.getEndPosition() == null){
			player.sendMessage(MinigameUtils.getLang("minigame.error.noEnd"), "error");
		}
		else if(mgm.getLobbyPosition() == null){
			player.sendMessage(MinigameUtils.getLang("minigame.error.noLobby"), "error");
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void issuePlayerRewards(MinigamePlayer player, Minigame save, boolean hascompleted){
		if(save.getRewardItem() != null && !hascompleted){
			player.getPlayer().getInventory().addItem(save.getRewardItem());
		}
		else if(save.getSecondaryRewardItem() != null && hascompleted){
			player.getPlayer().getInventory().addItem(save.getSecondaryRewardItem());
		}
		player.getPlayer().updateInventory();
		
		if(Minigames.plugin.hasEconomy()){
			if(save.getRewardPrice() != 0 && !hascompleted){
				Minigames.plugin.getEconomy().depositPlayer(player.getName(), save.getRewardPrice());
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", save.getRewardPrice()), "win");
			}
			else if(save.getSecondaryRewardPrice() != 0 && hascompleted){
				Minigames.plugin.getEconomy().depositPlayer(player.getName(), save.getSecondaryRewardPrice());
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", save.getSecondaryRewardPrice()), "win");
			}
		}
	}
}
