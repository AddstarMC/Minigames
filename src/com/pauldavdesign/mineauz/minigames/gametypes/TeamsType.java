package com.pauldavdesign.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.modules.LobbySettingsModule;

public class TeamsType extends MinigameTypeBase{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public TeamsType() {
		setType(MinigameType.TEAMS);
	}
	
	@Override
	public boolean joinMinigame(final MinigamePlayer player, Minigame mgm){
		if(!LobbySettingsModule.getMinigameModule(mgm).canInteractPlayerWait())
			player.setCanInteract(false);
		if(!LobbySettingsModule.getMinigameModule(mgm).canMovePlayerWait())
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
			Team smTeam = null;
			for(Team team : mgm.getTeams()){
				if(smTeam == null || team.getPlayers().size() < smTeam.getPlayers().size()){
					smTeam = team;
				}
			}
			
			smTeam.addPlayer(player);
			player.sendMessage(MinigameUtils.formStr("player.team.assign.joinTeam", smTeam.getChatColor() + smTeam.getDisplayName()), null);
			
			final Team fteam = smTeam;
			player.setLatejoining(true);
			player.sendMessage(MinigameUtils.formStr("minigame.lateJoin", 5)); //TODO: Late join delay variable
			final MinigamePlayer fply = player;
			final Minigame fmgm = mgm;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					if(fply.isInMinigame()){
						List<Location> locs = new ArrayList<Location>();
						if(fmgm.hasTeamStartLocations()){
							locs.addAll(fteam.getStartLocations());
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
		player.removeTeam();
		int teamsWithPlayers = 0;
		for(Team t : mgm.getTeams()){
			if(t.getPlayers().size() > 0)
				teamsWithPlayers ++;
		}
		
		if(mgm.getMpBets() != null && !mgm.isNotWaitingForPlayers() && !forced){
			if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
				plugin.getEconomy().depositPlayer(player.getName(), mgm.getMpBets().getPlayersMoneyBet(player));
			}
			mgm.getMpBets().removePlayersBet(player);
		}
		
		if(mgm.getPlayers().size() == 1 && !forced){
			if(mgm.getBlockRecorder().hasData() && !mgm.isNotWaitingForPlayers()){
				mgm.getBlockRecorder().clearRestoreData();
				mgm.getBlockRecorder().setCreatedRegenBlocks(false);
			}
			
			if(mgm.getMpTimer() != null){
				mgm.getMpTimer().pauseTimer();
				mgm.getMpTimer().removeTimer();
				mgm.setMpTimer(null);
			}
			mgm.setMpBets(null);
		}
		else if(mgm.getPlayers().size() >= 1 && 
				teamsWithPlayers == 1 && mgm.isNotWaitingForPlayers() && !forced){
			Team winner = null;
			for(Team t : mgm.getTeams()){
				if(t.getPlayers().size() > 0){
					winner = t;
					break;
				}
			}
			List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(winner.getPlayers());
			List<MinigamePlayer> l = new ArrayList<MinigamePlayer>();
			plugin.pdata.endMinigame(mgm, w, l);
			
			if(mgm.getMpBets() != null){
				mgm.setMpBets(null);
			}
		}
		else if(mgm.getPlayers().size() - 1 < mgm.getMinPlayers() && 
				mgm.getMpTimer() != null && 
				mgm.getMpTimer().getStartWaitTimeLeft() != 0
				&& !forced){
			mgm.getMpTimer().setPlayerWaitTime(10);
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
			player.removeTeam();
		}
		for(MinigamePlayer player : losers){
			player.removeTeam();
		}
		for(Team t : mgm.getTeams()){
			t.resetScore();
		}

		if(mgm.getMpTimer() == null) return;
		mgm.getMpTimer().setStartWaitTime(0);
		mgm.setMpTimer(null);
	}
	
	public static void switchTeam(Minigame mgm, MinigamePlayer player, Team newTeam){
		if(player.getTeam() != null)
			player.removeTeam();
		newTeam.addPlayer(player);
	}
	
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerRespawn(PlayerRespawnEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.TEAMS){
			Team team = ply.getTeam();
			Minigame mg = ply.getMinigame();
			
			Location respawnPos;
			if(mg.hasStarted() && !ply.isLatejoining()){
				if(mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()){
					respawnPos = ply.getCheckpoint();
				}
				else{
					List<Location> starts = new ArrayList<Location>();
					if(mg.hasTeamStartLocations()){
						starts.addAll(team.getStartLocations());
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
			if(mgm.getDefaultWinner() != null){
				List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(mgm.getDefaultWinner().getPlayers());
				List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - mgm.getDefaultWinner().getPlayers().size());
				for(Team t : mgm.getTeams()){
					if(t != mgm.getDefaultWinner())
						l.addAll(t.getPlayers());
				}
				plugin.pdata.endMinigame(mgm, w, l);
			}
			else{
				List<Team> drawTeams = new ArrayList<Team>();
				Team winner = null;
				for(Team t : mgm.getTeams()){
					if(winner == null || (t.getScore() > winner.getScore() && 
							(drawTeams.isEmpty() || t.getScore() > drawTeams.get(0).getScore()))){
						winner = t;
					}
					else if(winner != null && t.getScore() == winner.getScore()){
						if(!drawTeams.isEmpty()){
							drawTeams.clear();
						}
						drawTeams.add(winner);
						drawTeams.add(t);
						winner = null;
					}
					else if(!drawTeams.isEmpty() && drawTeams.get(0).getScore() == t.getScore()){
						drawTeams.add(t);
					}
				}
				
				if(winner != null){
					List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(winner.getPlayers());
					List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - winner.getPlayers().size());
					for(Team t : mgm.getTeams()){
						if(t != winner)
							l.addAll(t.getPlayers());
					}
					pdata.endMinigame(mgm, w, l);
				}
				else{
					List<MinigamePlayer> players = new ArrayList<MinigamePlayer>(mgm.getPlayers());
					for(Team t : mgm.getTeams()){
						t.resetScore();
					}
					
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
							if(drawTeams.size() == 2){
								ply.sendMessage(MinigameUtils.formStr("player.end.team.tie", 
										drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE, 
										drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE, 
										event.getMinigame().getName(true)), "error");
								String scores = "";
								int c = 1;
								for(Team t : mgm.getTeams()){
									scores += t.getChatColor().toString() + t.getScore();
									c++;
									if(c != mgm.getTeams().size())
										scores += ChatColor.WHITE + " : ";
								}
								ply.sendMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
							}
							else{
								ply.sendMessage(MinigameUtils.formStr("player.end.team.tieCount", 
										drawTeams.size(), 
										event.getMinigame().getName(true)), "error");
								String scores = "";
								int c = 1;
								for(Team t : mgm.getTeams()){
									scores += t.getChatColor().toString() + t.getScore();
									c++;
									if(c != mgm.getTeams().size())
										scores += ChatColor.WHITE + " : ";
								}
								ply.sendMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
							}
						}
					}
					if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
						if(drawTeams.size() == 2){
							plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MinigameUtils.formStr("player.end.team.tie", 
									drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE, 
									drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE, 
									event.getMinigame().getName(true)));
							String scores = "";
							int c = 1;
							for(Team t : mgm.getTeams()){
								scores += t.getChatColor().toString() + t.getScore();
								c++;
								if(c != mgm.getTeams().size())
									scores += ChatColor.WHITE + " : ";
							}
							plugin.getServer().broadcastMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
						}
						else{
							plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MinigameUtils.formStr("player.end.team.tieCount", 
									drawTeams.size(), 
									event.getMinigame().getName(true)));
							String scores = "";
							int c = 1;
							for(Team t : mgm.getTeams()){
								scores += t.getChatColor().toString() + t.getScore();
								c++;
								if(c != mgm.getTeams().size())
									scores += ChatColor.WHITE + " : ";
							}
							plugin.getServer().broadcastMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
						}
					}
				}
			}
		}
	}
}
