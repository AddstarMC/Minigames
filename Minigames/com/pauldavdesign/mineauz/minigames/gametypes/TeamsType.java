package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.MultiplayerTimer;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.events.TimerExpireEvent;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class TeamsType extends MinigameTypeBase{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public TeamsType() {
		setType(MinigameType.TEAMS);
	}
	
	@Override
	public boolean joinMinigame(final MinigamePlayer player, Minigame mgm){
		if(!mgm.canInteractPlayerWait())
			player.setCanInteract(false);
		if(!mgm.canMovePlayerWait())
			player.setFrozen(true);
		
		if(!mgm.isNotWaitingForPlayers()){
			if(mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMinPlayers()){
				mgm.setMpTimer(new MultiplayerTimer(mgm));
				mgm.getMpTimer().startTimer();
				if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
					mgm.getMpTimer().setPlayerWaitTime(0);
					mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
				}
			}
			else if(mgm.getMpTimer() != null && mgm.getPlayers().size() == mgm.getMaxPlayers()){
				mgm.getMpTimer().setPlayerWaitTime(0);
				mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
			}
			else if(mgm.getMpTimer() == null){
				int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
				mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("minigame.waitingForPlayers", neededPlayers), null, null);
			}
		}
		else{
			int redSize = mgm.getRedTeam().size();
			int blueSize = mgm.getBlueTeam().size();
			
			int team;
			if(redSize <= blueSize){
				mgm.addRedTeamPlayer(player);
				player.sendMessage(MinigameUtils.formStr("player.team.assign.joinTeam", ChatColor.RED + "Red Team"), null);
				team = 0;
			}
			else{
				mgm.addBlueTeamPlayer(player);
				player.sendMessage(MinigameUtils.formStr("player.team.assign.joinTeam", ChatColor.BLUE + "Blue Team"), null);
				team = 1;
			}

			final int fteam = team;
			player.setLatejoining(true);
			player.sendMessage(MinigameUtils.formStr("minigame.lateJoin", 5)); //TODO: Late join delay variable
			final MinigamePlayer fply = player;
			final Minigame fmgm = mgm;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					if(fply.isInMinigame()){
						List<Location> locs = new ArrayList<Location>();
						if(!fmgm.getStartLocationsRed().isEmpty()){
							if(fteam == 0){
								locs.addAll(fmgm.getStartLocationsRed());
							}
							else{
								locs.addAll(fmgm.getStartLocationsBlue());
							}
						}
						else{
							locs.addAll(fmgm.getStartLocations());
						}
						Collections.shuffle(locs);
						fply.teleport(locs.get(0));
						fply.getLoadout().equiptLoadout(fply);
						fply.setLatejoining(false);
						fply.setFrozen(false);
						fply.setCanInteract(true);
					}
				}
			}, 5 * 20); //TODO: Latejoin variable

			player.getPlayer().setScoreboard(mgm.getScoreboardManager());
			mgm.setScore(player, 1);
			mgm.setScore(player, 0);
		}
		return true;
	}
	
	@Override
	public void quitMinigame(MinigamePlayer player, Minigame mgm, boolean forced){
		if(mgm.getRedTeam().contains(player.getPlayer())){
			mgm.removeRedTeamPlayer(player);
		}
		else{
			mgm.removeBlueTeamPlayer(player);
		}
		
		if(mgm.getMpBets() != null && !mgm.isNotWaitingForPlayers() && !forced){
			if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
				plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
			}
			mgm.getMpBets().removePlayersBet(player);
		}
		
		if(mgm.getPlayers().size() >= 1 && mgm.hasStarted() &&
				(mgm.getRedTeam().size() == 0 || mgm.getBlueTeam().size() == 0) && !forced){
			
			if(mgm.getRedTeam().size() == 0){
				List<MinigamePlayer> w;
				List<MinigamePlayer> l;
				l = new ArrayList<MinigamePlayer>(mgm.getRedTeam().size());
				w = new ArrayList<MinigamePlayer>(mgm.getBlueTeam().size());
				for(OfflinePlayer pl : mgm.getRedTeam()){
					l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				for(OfflinePlayer pl : mgm.getBlueTeam()){
					w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				plugin.pdata.endMinigame(mgm, w, l);
			}
			else{
				List<MinigamePlayer> w;
				List<MinigamePlayer> l;
				w = new ArrayList<MinigamePlayer>(mgm.getRedTeam().size());
				l = new ArrayList<MinigamePlayer>(mgm.getBlueTeam().size());
				for(OfflinePlayer pl : mgm.getRedTeam()){
					w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				for(OfflinePlayer pl : mgm.getBlueTeam()){
					l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				plugin.pdata.endMinigame(mgm, w, l);
			}
			
			if(mgm.getMpBets() != null){
				mgm.setMpBets(null);
			}
		}
		else if(mgm.getPlayers().size() - 1 < mgm.getMinPlayers() && 
				mgm.getMpTimer() != null && 
				mgm.getMpTimer().getStartWaitTimeLeft() != 0){
			mgm.getMpTimer().setPlayerWaitTime(Minigames.plugin.getConfig().getInt("multiplayer.waitforplayers"));
			mgm.getMpTimer().pauseTimer();
			mgm.getMpTimer().removeTimer();
			mgm.setMpTimer(null);
			for(MinigamePlayer pl : mgm.getPlayers()){
				pl.sendMessage(MinigameUtils.formStr("minigame.waitingForPlayers", 1));
			}
		}
	}
	
	@Override
	public void endMinigame(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame mgm){
		for(MinigamePlayer player : winners){
			if(mgm.getRedTeam().contains(player.getPlayer())){
				mgm.removeRedTeamPlayer(player);
			}
			else{
				mgm.removeBlueTeamPlayer(player);
			}
		}
		for(MinigamePlayer player : losers){
			if(mgm.getRedTeam().contains(player.getPlayer())){
				mgm.removeRedTeamPlayer(player);
			}
			else{
				mgm.removeBlueTeamPlayer(player);
			}
		}
		mgm.setRedTeamScore(0);
		mgm.setBlueTeamScore(0);

		if(mgm.getMpTimer() == null) return;
		mgm.getMpTimer().setStartWaitTime(0);
		mgm.setMpTimer(null);
	}
	
	public static void switchTeam(Minigame mgm, MinigamePlayer player){
		if(mgm.getBlueTeam().contains(player.getPlayer())){
			mgm.removeBlueTeamPlayer(player);
			mgm.addRedTeamPlayer(player);
			player.setLoadout(null);
		}
		else{
			mgm.removeRedTeamPlayer(player);
			mgm.addBlueTeamPlayer(player);
			player.setLoadout(null);
		}
	}
	
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerRespawn(PlayerRespawnEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.TEAMS){
			int team = 0;
			Minigame mg = ply.getMinigame();
			if(mg.getBlueTeam().contains(plugin.getServer().getOfflinePlayer(ply.getName()))){
				team = 1;
			}
			
			Location respawnPos;
			if(mg.hasStarted() && !ply.isLatejoining()){
				if(mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()){
					respawnPos = ply.getCheckpoint();
				}
				else{
					List<Location> starts = new ArrayList<Location>();
					if(!mg.getStartLocationsBlue().isEmpty() && !mg.getStartLocationsRed().isEmpty()){
						if(team == 1){
							starts.addAll(mg.getStartLocationsBlue());
						}
						else{
							starts.addAll(mg.getStartLocationsRed());
						}
						ply.getLoadout().equiptLoadout(ply);
					}
					else{
						starts.addAll(mg.getStartLocations());
					}
					Collections.shuffle(starts);
					respawnPos = starts.get(0);
				}
				ply.getLoadout().equiptLoadout(ply);
			}
			else{
				respawnPos = mg.getLobbyPosition();
			}
			event.setRespawnLocation(respawnPos);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					ply.getPlayer().setNoDamageTicks(60);
				}
			});
			
			
		}
	}
	
	@EventHandler
	public void timerExpire(TimerExpireEvent event){
		if(event.getMinigame().getType() == MinigameType.TEAMS){
			Minigame mgm = event.getMinigame();
			if(!event.getMinigame().getDefaultWinner().equals("none")){
				if(event.getMinigame().getDefaultWinner().equals("blue")){
					List<MinigamePlayer> w;
					List<MinigamePlayer> l;
					l = new ArrayList<MinigamePlayer>(mgm.getRedTeam().size());
					w = new ArrayList<MinigamePlayer>(mgm.getBlueTeam().size());
					for(OfflinePlayer pl : mgm.getRedTeam()){
						l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
					}
					for(OfflinePlayer pl : mgm.getBlueTeam()){
						w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
					}
					plugin.pdata.endMinigame(mgm, w, l);
				}
				else{
					List<MinigamePlayer> w;
					List<MinigamePlayer> l;
					w = new ArrayList<MinigamePlayer>(mgm.getRedTeam().size());
					l = new ArrayList<MinigamePlayer>(mgm.getBlueTeam().size());
					for(OfflinePlayer pl : mgm.getRedTeam()){
						w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
					}
					for(OfflinePlayer pl : mgm.getBlueTeam()){
						l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
					}
					plugin.pdata.endMinigame(mgm, w, l);
				}
			}
			else if(event.getMinigame().getBlueTeamScore() > event.getMinigame().getRedTeamScore()){
				List<MinigamePlayer> w;
				List<MinigamePlayer> l;
				l = new ArrayList<MinigamePlayer>(mgm.getRedTeam().size());
				w = new ArrayList<MinigamePlayer>(mgm.getBlueTeam().size());
				for(OfflinePlayer pl : mgm.getRedTeam()){
					l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				for(OfflinePlayer pl : mgm.getBlueTeam()){
					w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				plugin.pdata.endMinigame(mgm, w, l);
			}
			else if(event.getMinigame().getBlueTeamScore() < event.getMinigame().getRedTeamScore()){
				List<MinigamePlayer> w;
				List<MinigamePlayer> l;
				w = new ArrayList<MinigamePlayer>(mgm.getRedTeam().size());
				l = new ArrayList<MinigamePlayer>(mgm.getBlueTeam().size());
				for(OfflinePlayer pl : mgm.getRedTeam()){
					w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				for(OfflinePlayer pl : mgm.getBlueTeam()){
					l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
				}
				plugin.pdata.endMinigame(mgm, w, l);
			}
			else{
				List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
				players.addAll(event.getMinigame().getPlayers());
				
				mgm.setRedTeamScore(0);
				mgm.setBlueTeamScore(0);
				
				if(mgm.getMinigameTimer() != null){
					mgm.getMinigameTimer().stopTimer();
					mgm.setMinigameTimer(null);
				}
				
				if(mgm.getMpTimer() != null){
					mgm.getMpTimer().setStartWaitTime(0);
					mgm.getMpTimer().pauseTimer();
					mgm.getMpTimer().removeTimer();
					mgm.setMpTimer(null);
				}
				
				if(mgm.getFloorDegenerator() != null && mgm.getPlayers().size() == 0){
					mgm.getFloorDegenerator().stopDegenerator();
				}
				
				if(mgm.getMpBets() != null && mgm.getPlayers().size() == 0){
					mgm.setMpBets(null);
				}
				
				for(MinigamePlayer ply : players){
					pdata.quitMinigame(ply, true);
					if(!plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
						ply.sendMessage(MinigameUtils.formStr("player.end.team.tie", ChatColor.BLUE + "Blue Team" + ChatColor.WHITE, 
								ChatColor.RED + "Red Team" + ChatColor.WHITE, 
								event.getMinigame().getName(false)), "error");
						ply.sendMessage(MinigameUtils.getLang("minigame.info.score") + " " + MinigameUtils.formStr("player.end.team.score", ChatColor.BLUE + String.valueOf(event.getMinigame().getBlueTeamScore()) + ChatColor.WHITE,
								ChatColor.RED.toString() + event.getMinigame().getRedTeamScore()));
					}
				}
				if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
					plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MinigameUtils.formStr("player.end.team.tie", ChatColor.BLUE + "Blue Team" + ChatColor.WHITE, 
							ChatColor.RED + "Red Team" + ChatColor.WHITE, 
							event.getMinigame().getName(true)));
					plugin.getServer().broadcastMessage(MinigameUtils.getLang("minigame.info.score") + " " + MinigameUtils.formStr("player.end.team.score", ChatColor.BLUE + String.valueOf(event.getMinigame().getBlueTeamScore()) + ChatColor.WHITE,
							ChatColor.RED.toString() + event.getMinigame().getRedTeamScore()));
				}
			}
		}
	}
}
