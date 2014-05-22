package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamsType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class PlayerKillsType extends ScoreTypeBase{

	@Override
	public String getType() {
		return "kills";
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		for(int i = 0; i < players.size(); i++){
			if(minigame.getType() == MinigameType.TEAMS){
				int team = -1;
				if(minigame.getBlueTeam().contains(players.get(i))){
					team = 1;
				}
				else if(minigame.getRedTeam().contains(players.get(i))){
					team = 0;
				}
				
				if(team == 1){
					if(minigame.getRedTeam().size() < minigame.getBlueTeam().size() - 1){
						minigame.getBlueTeam().remove(players.get(i));
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.RED + "Red Team"), null, players.get(i));
					}
				}
				else if(team == 0){
					if(minigame.getBlueTeam().size() < minigame.getRedTeam().size() - 1){
						minigame.getRedTeam().remove(players.get(i));
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.BLUE + "Blue Team"), null, players.get(i));
					}
				}
				else{
					if(minigame.getRedTeam().size() <= minigame.getBlueTeam().size()){
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.RED + "Red Team"), null, players.get(i));
					}
					else if(minigame.getBlueTeam().size() <= minigame.getRedTeam().size()){
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.BLUE + "Blue Team"), null, players.get(i));
					}
				}
			}
		}
		
	}
	
	@EventHandler
	private void playerAttackPlayer(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		Minigame mgm = ply.getMinigame();
		if(ply.isInMinigame() && mgm.getScoreType().equals("kills")){
			MinigamePlayer attacker = null;
			if(ply.getPlayer().getKiller() != null){
				attacker = pdata.getMinigamePlayer(ply.getPlayer().getKiller());
				if(attacker == ply){
					return;
				}
			}
			else{
				return;
			}
			
			if(!mgm.equals(attacker.getMinigame())){
				return;
			}
			
			if(mgm.getBlueTeam().isEmpty() && mgm.getRedTeam().isEmpty()){
				attacker.addScore();
				mgm.setScore(attacker, attacker.getScore());
			
				if(mgm.getMaxScore() != 0 && attacker.getScore() >= mgm.getMaxScorePerPlayer()){
					List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - 1);
					List<MinigamePlayer> winner = new ArrayList<MinigamePlayer>(1);
					winner.add(attacker);
					for(MinigamePlayer player : mgm.getPlayers()){
						if(player != attacker)
							losers.add(player);
					}
					pdata.endMinigame(mgm, winner, losers);
				}
			}
			else{
				int team = 0;
				int ateam = 0;
				if(mgm.getBlueTeam().contains(ply.getPlayer())){
					team = 1;
				}
				
				if(mgm.getBlueTeam().contains(attacker.getPlayer())){
					ateam = 1;
				}
				
				if(team != ateam){
					attacker.addScore();
					mgm.setScore(attacker, attacker.getScore());
					
					boolean end = false;
					
					if(ateam == 0){
						mgm.incrementRedTeamScore();
						
						if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer()){
							end = true;
						}
					}
					else{
						mgm.incrementBlueTeamScore();
						
						if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer()){
							end = true;
						}
					}
					
					if(end){
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.kills.finalKill", attacker.getName(), ply.getName()), null, null);
						if(ateam == 1){
							if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer()){
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
						}
						else{
							if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer()){
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
					}
					
				}
			}
		}
	}
	
	@EventHandler
	private void playerSuicide(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && (ply.getPlayer().getKiller() == null || ply.getPlayer().getKiller() == ply.getPlayer())){
			Minigame mgm = ply.getMinigame();
			if(mgm.getScoreType().equals("kills")){
				ply.takeScore();
				mgm.setScore(ply, ply.getScore());
				if(!mgm.getRedTeam().isEmpty() && !mgm.getBlueTeam().isEmpty()){
					if(mgm.getRedTeam().contains(ply.getPlayer())){
						mgm.setRedTeamScore(mgm.getRedTeamScore() - 1);
					}
					else{
						mgm.setBlueTeamScore(mgm.getBlueTeamScore() - 1);
					}
					mgm.setScore(ply, ply.getScore());
				}
			}
		}
	}
	
	@EventHandler
	public void playerAutoBalance(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.TEAMS){
			int pteam = 0;
			if(ply.getMinigame().getBlueTeam().contains(ply.getPlayer())){
				pteam = 1;
			}
			final Minigame mgm = ply.getMinigame();
			
			if(mgm.getScoreType().equals("kills")){
				if(pteam == 1){
					if(mgm.getRedTeam().size() < mgm.getBlueTeam().size() - 1){
						TeamsType.switchTeam(mgm, ply);
						ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.autobalance.minigameMsg", ply.getName(), ChatColor.RED + "Red Team"), null, ply);
					}
				}
				else{
					if(mgm.getBlueTeam().size() < mgm.getRedTeam().size()  - 1){
						TeamsType.switchTeam(mgm, ply);
						ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.autobalance.minigameMsg", ply.getName(), ChatColor.BLUE + "Blue Team"), null, ply);
					}
				}
			}
		}
	}
}
