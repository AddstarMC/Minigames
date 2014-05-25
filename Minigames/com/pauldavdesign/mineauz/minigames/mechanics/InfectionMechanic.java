package com.pauldavdesign.mineauz.minigames.mechanics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.gametypes.MultiplayerType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;

public class InfectionMechanic extends GameMechanicBase{
	
	private List<MinigamePlayer> infected = new ArrayList<MinigamePlayer>();

	@Override
	public String getMechanic() {
		return "infection";
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		for(int i = 0; i < players.size(); i++){
			MinigamePlayer ply = players.get(i);
			if(!minigame.isTeamGame() || 
					TeamsModule.getMinigameModule(minigame).getTeams().size() != 2 || 
					!TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.RED) || 
					!TeamsModule.getMinigameModule(minigame).hasTeam(TeamColor.BLUE)){
				pdata.quitMinigame(ply, true);
				ply.sendMessage(MinigameUtils.getLang("minigame.error.noInfection"), "error");
			}
			else{
				Team red = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.RED);
				Team blue = TeamsModule.getMinigameModule(minigame).getTeam(TeamColor.BLUE);
				Team team = ply.getTeam();
				
				if(team == blue){
					if(red.getPlayers().size() < Math.ceil(players.size() * 0.18) && !red.isFull()){
						MultiplayerType.switchTeam(minigame, ply, red);
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.assign.infectedAssign", ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.assign.infectedAnnounce", players.get(i).getName(), ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null, players.get(i));
					}
				}
				else if(team == null){
					if(red.getPlayers().size() < Math.ceil(players.size() * 0.18) && !red.isFull()){
						red.addPlayer(ply);
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.assign.infectedAssign", ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.assign.infectedAnnounce", players.get(i).getName(), ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), null, players.get(i));
					}
					else if(!blue.isFull()){
						blue.addPlayer(ply);
						ply.sendMessage(MinigameUtils.formStr("player.team.assign.survivor", ChatColor.BLUE + MinigameUtils.getLang("player.team.assign.survivor")), null);
						mdata.sendMinigameMessage(minigame,MinigameUtils.formStr("player.team.assign.survivorAnnounce", players.get(i).getName(), ChatColor.BLUE + MinigameUtils.getLang("player.team.assign.survivor")), null, players.get(i));
					}
					else{
						pdata.quitMinigame(ply, false);
						ply.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
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
			if(mgm.isTeamGame() && mgm.getScoreType().equals("infection")){
				Team blue = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.BLUE);
				Team red = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.RED);
				if(blue.getPlayers().contains(player)){
					if(!red.isFull()){
						MultiplayerType.switchTeam(mgm, player, red);
						infected.add(player);
						if(event.getEntity().getKiller() != null){
							MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
							killer.addScore();
							mgm.setScore(killer, killer.getScore());
						}
						player.resetScore();
						mgm.setScore(player, player.getScore());
						
						if(mgm.getLives() != player.getDeaths()){
							mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.team.assign.infectedAnnounce", player.getName(), ChatColor.RED + MinigameUtils.getLang("player.team.assign.infected")), "error", null);
						}
						if(blue.getPlayers().isEmpty()){
							List<MinigamePlayer> w;
							List<MinigamePlayer> l;
							w = new ArrayList<MinigamePlayer>(red.getPlayers());
							l = new ArrayList<MinigamePlayer>();
							pdata.endMinigame(mgm, w, l);
						}
					}
					else{
						pdata.quitMinigame(player, false);
					}
				}
				else{
					if(event.getEntity().getKiller() != null){
						MinigamePlayer killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
						killer.addScore();
						mgm.setScore(killer, killer.getScore());
					}
				}
			}
		}
	}
	
	@EventHandler
	private void endTeamMinigame(EndMinigameEvent event){
		if(event.getMinigame().getScoreType().equals("infection")){
			List<MinigamePlayer> infect = new ArrayList<MinigamePlayer>();
			infect.addAll(infected);
			for(MinigamePlayer inf : infect){
				if(event.getWinners().contains(inf)){
					event.getWinners().remove(inf);
					event.getLosers().add(inf);
					infected.remove(inf);
				}
			}
		}
	}
	
	@EventHandler
	private void quitMinigame(QuitMinigameEvent event){
		if(infected.contains(pdata.getMinigamePlayer(event.getPlayer()))){
			infected.remove(pdata.getMinigamePlayer(event.getPlayer()));
		}
	}
}
