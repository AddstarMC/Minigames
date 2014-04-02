package com.pauldavdesign.mineauz.minigames.mechanics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamsType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.Team;

public class PlayerKillsMechanic extends GameMechanicBase{

	@Override
	public String getMechanic() {
		return "kills";
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		if(minigame.getType() == MinigameType.TEAMS){
			boolean sorted = false;
			for(MinigamePlayer ply : players){
				if(ply.getTeam() == null){
					Team smt = null;
					for(Team t : minigame.getTeams()){
						if(smt == null || t.getPlayers().size() < smt.getPlayers().size())
							smt = t;
					}
					smt.addPlayer(ply);
					ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
				}
			}
			
			while(!sorted){
				Team smt = null;
				Team lgt = null;
				for(Team t : minigame.getTeams()){
					if(smt == null || t.getPlayers().size() < smt.getPlayers().size() - 1)
						smt = t;
					if((lgt == null || t.getPlayers().size() > lgt.getPlayers().size()) && t != smt)
						lgt = t;
				}
				if(lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MinigamePlayer pl = lgt.getPlayers().get(0);
					TeamsType.switchTeam(minigame, pl, smt);
					pl.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									pl.getName(), smt.getChatColor() + smt.getDisplayName()), null, pl);
				}
				else{
					sorted = true;
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
			
			if(ply.getTeam() == null){
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
				Team team = ply.getTeam();
				Team ateam = attacker.getTeam();
				
				if(team != ateam){
					attacker.addScore();
					mgm.setScore(attacker, attacker.getScore());
					
					ateam.addScore();
					if(mgm.getMaxScore() != 0 && mgm.getMaxScorePerPlayer() <= ateam.getScore()){
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.kills.finalKill", attacker.getName(), ply.getName()), null, null);
						
						List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(ateam.getPlayers());
						List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - ateam.getPlayers().size());
						for(Team t : mgm.getTeams()){
							if(t != ateam)
								l.addAll(t.getPlayers());
						}
						plugin.pdata.endMinigame(mgm, w, l);
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
				ply.getTeam().setScore(ply.getTeam().getScore() - 1);
			}
		}
	}
	
	@EventHandler
	public void playerAutoBalance(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.TEAMS){
			Minigame mgm = ply.getMinigame();
			
			if(mgm.getScoreType().equals("custom")){
				Team smt = null;
				Team lgt = ply.getTeam();
				for(Team t : mgm.getTeams()){
					if(smt == null || t.getPlayers().size() < smt.getPlayers().size() - 1)
						smt = t;
				}
				if(lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					TeamsType.switchTeam(mgm, ply, smt);
					ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(mgm, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
				}
			}
		}
	}
}
