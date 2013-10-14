package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.events.EndTeamMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;

public class InfectionType extends ScoreTypeBase{
	
	private List<MinigamePlayer> infected = new ArrayList<MinigamePlayer>();

	@Override
	public String getType() {
		return "infection";
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		for(int i = 0; i < players.size(); i++){
			MinigamePlayer ply = players.get(i);
			if(!minigame.getType().equals("teamdm")){
				pdata.quitMinigame(ply, true);
				ply.sendMessage(MinigameUtils.getLang("minigame.error.noInfection"), "error");
			}
			else{
				int team = -1;
				if(minigame.getBlueTeam().contains(players.get(i))){
					team = 1;
				}
				else if(minigame.getRedTeam().contains(players.get(i))){
					team = 0;
				}
				
				if(team == 1){
					if(minigame.getRedTeam().size() < Math.ceil(players.size() * 0.18)){
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.assign.infectedAssign", ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.assign.infectedAnnounce", players.get(i).getName(), ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null, players.get(i));
					}
				}
				else if(team == -1){
					if(minigame.getRedTeam().size() < Math.ceil(players.size() * 0.18)){
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.assign.infectedAssign", ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.assign.infectedAnnounce", players.get(i).getName(), ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null, players.get(i));
					}
					else{
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.assign.survivor", ChatColor.BLUE + MinigameUtils.getLang("player.team.assign.survivor")), null);
						mdata.sendMinigameMessage(minigame,MinigameUtils.formStr("player.team.assign.survivorAnnounce", players.get(i).getName(), ChatColor.BLUE + MinigameUtils.getLang("player.team.assign.survivor")), null, players.get(i));
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void playerDeath(PlayerDeathEvent event){
		MinigamePlayer player = pdata.getMinigamePlayer(event.getEntity());
		if(player == null) return;
		if(player.isInMinigame()){
			Minigame mgm = player.getMinigame();
			if(mgm.getType().equals("teamdm") && mgm.getScoreType().equals("infection")){
				if(mgm.getBlueTeam().contains(event.getEntity())){
					TeamDMMinigame.switchTeam(mgm, player);
					infected.add(player);
					if(event.getEntity().getKiller() != null){
						MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
//						pdata.addPlayerScore(killer);
						killer.addScore();
						mgm.setScore(killer, killer.getScore());
					}
//					pdata.setPlayerScore(event.getEntity(), 0);
					player.resetScore();
					mgm.setScore(player, player.getScore());
					
					if(mgm.getLives() != player.getDeaths()){
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.infectedAnnounce", player.getName(), ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), "error", null);
					}
					if(mgm.getBlueTeam().isEmpty()){
						event.getEntity().setHealth(2);
						pdata.endTeamMinigame(0, mgm);
					}
				}
				else{
					if(event.getEntity().getKiller() != null){
						MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
//						pdata.addPlayerScore(killer);
						killer.addScore();
						mgm.setScore(killer, killer.getScore());
					}
				}
			}
		}
	}
	
	@EventHandler
	private void endTeamMinigame(EndTeamMinigameEvent event){
		if(event.getMinigame().getScoreType().equals("infection")){
			List<MinigamePlayer> infect = new ArrayList<MinigamePlayer>();
			infect.addAll(infected);
			for(MinigamePlayer inf : infect){
				if(event.getWinnningPlayers().contains(inf)){
					if(event.getWinningTeamInt() == 0){
						event.getWinnningPlayers().remove(inf);
						event.getLosingPlayers().add(inf);
					}
					infected.remove(inf);
				}
			}
		}
	}
	
	@EventHandler
	private void quitMinigame(QuitMinigameEvent event){
		if(infected.contains(event.getPlayer())){
			infected.remove(event.getPlayer());
		}
	}
}
