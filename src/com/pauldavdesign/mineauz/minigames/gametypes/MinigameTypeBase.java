package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.MultiplayerTimer;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;

public abstract class MinigameTypeBase implements Listener{
	private static Minigames plugin;
	private PlayerData pdata;
	private MinigameData mdata;
	
	protected MinigameTypeBase(){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
		mdata = plugin.mdata;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	private MinigameType type;
	
	public void setType(MinigameType type){
		this.type = type;
	}
	
	public MinigameType getType(){
		return type;
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
									if(fmgm.isAllowedMPCheckpoints())
										fply.setCheckpoint(locs.get(0));
								}
							}
						}, 100);

						player.getPlayer().setScoreboard(mgm.getScoreboardManager());
						mgm.setScore(player, 1);
						mgm.setScore(player, 0);
					}
					
					if(mgm.getGametypeName() == null)
						player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", mgm.getType().getName()), "win");
					else
						player.sendMessage(MinigameUtils.formStr("player.join.plyInfo", mgm.getGametypeName()), "win");
					
					if(mgm.getObjective() != null){
						player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
						player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + MinigameUtils.formStr("player.join.objective", 
								ChatColor.RESET.toString() + ChatColor.WHITE + mgm.getObjective()));
						player.sendMessage(ChatColor.GREEN + "----------------------------------------------------");
					}
				
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
	public static void issuePlayerRewards(MinigamePlayer player, Minigame save, boolean hascompleted){
		List<RewardItem> rewardL = save.getRewardItem();
		List<RewardItem> srewardL = save.getSecondaryRewardItem();
		double totalMoney = 0;
		if(!hascompleted && rewardL != null){
			for(RewardItem reward : rewardL){
				if(reward != null){
					if(reward.getItem() != null){
						if(!player.getPlayer().isDead())
							player.getPlayer().getInventory().addItem(reward.getItem());
						else{
							int c = 0;
							for(ItemStack i : player.getStoredItems()){
								if(i == null){
									player.getStoredItems()[c] = reward.getItem();
									break;
								}
								c++;
							}
						}
						player.sendMessage(MinigameUtils.formStr("player.end.awardItem", reward.getItem().getAmount(), MinigameUtils.getItemStackName(reward.getItem())), "win");
					}
					else{
						if(Minigames.plugin.hasEconomy() && reward.getMoney() != 0){
							Minigames.plugin.getEconomy().depositPlayer(player.getName(), reward.getMoney());
							totalMoney += reward.getMoney();
						}
					}
				}
			}
			if(totalMoney > 0){
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", totalMoney), "win");
			}
		}
		else if(hascompleted && srewardL != null){
			for(RewardItem sreward : srewardL){
				if(sreward != null){
					if(sreward.getItem() != null){
						if(!player.getPlayer().isDead())
							player.getPlayer().getInventory().addItem(sreward.getItem());
						else{
							int c = 0;
							for(ItemStack i : player.getStoredItems()){
								if(i == null){
									player.getStoredItems()[c] = sreward.getItem();
									break;
								}
								c++;
							}
						}
						player.sendMessage(MinigameUtils.formStr("player.end.awardItem", sreward.getItem().getAmount(), MinigameUtils.getItemStackName(sreward.getItem())), "win");
					}
					else{
						if(Minigames.plugin.hasEconomy() && sreward.getMoney() != 0){
							Minigames.plugin.getEconomy().depositPlayer(player.getName(), sreward.getMoney());
							totalMoney += sreward.getMoney();
						}
					}
				}
			}
			if(totalMoney > 0){
				player.sendMessage(MinigameUtils.formStr("player.end.awardMoney", totalMoney), "win");
			}
		}
		player.getPlayer().updateInventory();
	}
}
