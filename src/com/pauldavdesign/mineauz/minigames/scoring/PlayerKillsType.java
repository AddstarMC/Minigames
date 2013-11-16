package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;
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
//				pdata.addPlayerScore(attacker);
				attacker.addScore();
				mgm.setScore(attacker, attacker.getScore());
			
				if(mgm.getMaxScore() != 0 && attacker.getScore() >= mgm.getMaxScorePerPlayer()){
					List<MinigamePlayer> conPlayers = new ArrayList<MinigamePlayer>();
					conPlayers.addAll(mgm.getPlayers());
					conPlayers.remove(attacker);
					for(MinigamePlayer pl : conPlayers){
						if(pl != attacker){
							pdata.quitMinigame(pl, false);
						}
					}
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
								pdata.endTeamMinigame(1, mgm);
							}
						}
						else{
							if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer()){
								pdata.endTeamMinigame(0, mgm);
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
				if(mgm.getRedTeam().isEmpty() && mgm.getBlueTeam().isEmpty()){
//					pdata.takePlayerScore(ply);
					mgm.setScore(ply, ply.getScore());
					
//					mdata.sendMinigameMessage(mgm, ply.getName() + "'s Score: " + pdata.getPlayerScore(ply), null, null);
				}
				else{
					if(mgm.getRedTeam().contains(ply.getPlayer())){
						mgm.setRedTeamScore(mgm.getRedTeamScore() - 1);
					}
					else{
						mgm.setBlueTeamScore(mgm.getBlueTeamScore() - 1);
					}
//					mdata.sendMinigameMessage(mgm, "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore(), null, null);
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
						TeamDMMinigame.switchTeam(mgm, ply);
						ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.autobalance.minigameMsg", ply.getName(), ChatColor.RED + "Red Team"), null, ply);
					}
				}
				else{
					if(mgm.getBlueTeam().size() < mgm.getRedTeam().size()  - 1){
						TeamDMMinigame.switchTeam(mgm, ply);
						ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.autobalance.minigameMsg", ply.getName(), ChatColor.BLUE + "Blue Team"), null, ply);
					}
				}
			}
		}
	}
}
