package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;

public class InfectionType extends ScoreType{
	
	private List<Player> infected = new ArrayList<Player>();

	@Override
	public String getType() {
		return "infection";
	}

	@Override
	public void startMinigame(List<Player> players, Minigame minigame) {
		Location start = null;
		int pos = 0;
		int bluepos = 0;
		int redpos = 0;
		
		for(int i = 0; i < players.size(); i++){
			Player ply = players.get(i);
			if(!minigame.getType().equals("teamdm")){
				pdata.quitMinigame(ply, true);
				ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Infection must be run on a team deathmatch Minigame!");
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
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You are " + ChatColor.RED + "Infected!");
						mdata.sendMinigameMessage(minigame, players.get(i).getName() + " has become " + ChatColor.RED + "Infected!", null, players.get(i));
					}
				}
				else if(team == -1){
					if(minigame.getRedTeam().size() < Math.ceil(players.size() * 0.18)){
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You are " + ChatColor.RED + "Infected!");
						mdata.sendMinigameMessage(minigame, players.get(i).getName() + " has become " + ChatColor.RED + "Infected!", null, players.get(i));
					}
					else{
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You are a " + ChatColor.BLUE + "Survivor");
						mdata.sendMinigameMessage(minigame, players.get(i).getName() + " is a " + ChatColor.BLUE + "Survivor", null, players.get(i));
					}
				}
				TeamDMMinigame.applyTeam(players.get(i), team);
				
				pos += 1;
				if(!minigame.getStartLocationsRed().isEmpty() && !minigame.getStartLocationsBlue().isEmpty()){
					if(team == 0 && redpos < minigame.getStartLocationsRed().size()){
						start = minigame.getStartLocationsRed().get(redpos);
						redpos++;
					}
					else if(team == 1 && bluepos < minigame.getStartLocationsBlue().size()){
						start = minigame.getStartLocationsBlue().get(bluepos);
						bluepos++;
					}
					else if(team == 0 && !minigame.getStartLocationsRed().isEmpty()){
						redpos = 0;
						start = minigame.getStartLocationsRed().get(redpos);
						redpos++;
					}
					else if(team == 1 && !minigame.getStartLocationsBlue().isEmpty()){
						bluepos = 0;
						start = minigame.getStartLocationsBlue().get(bluepos);
						bluepos++;
					}
					else if(minigame.getStartLocationsBlue().isEmpty() || minigame.getStartLocationsRed().isEmpty()){
						ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Starting positions are incorrectly configured!");
						pdata.quitMinigame(players.get(i), false);
					}
				}
				else{
					pos += 1;
					if(pos <= minigame.getStartLocations().size()){
						start = minigame.getStartLocations().get(i);
					} 
					else{
						pos = 1;
						if(!minigame.getStartLocations().isEmpty()){
							start = minigame.getStartLocations().get(0);
						}
						else {
							ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Starting positions are incorrectly configured!");
							pdata.quitMinigame(ply, false);
						}
					}
				}
				
				if(start != null){
					ply.teleport(start);
					pdata.setPlayerCheckpoints(ply, start);
					if(team == 0){
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Infect all the survivors to win!");
					}
					else{
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Survive the infection to win!");
					}
				}
			}
			
			if(!minigame.getPlayersLoadout(ply).getItems().isEmpty()){
				minigame.getPlayersLoadout(ply).equiptLoadout(players.get(i));
			}
			
			if(minigame.getLives() > 0){
				ply.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "Lives left: " + minigame.getLives());
			}
		}
	}
	
	@EventHandler
	private void playerDeath(PlayerDeathEvent event){
		if(pdata.playerInMinigame(event.getEntity())){
			Minigame mgm = pdata.getPlayersMinigame(event.getEntity());
			if(mgm.getType().equals("teamdm") && mgm.getScoreType().equals("infection")){
				if(mgm.getBlueTeam().contains(event.getEntity())){
					if(mgm.getBlueTeam().isEmpty()){
						event.getEntity().setHealth(2);
						pdata.endTeamMinigame(0, mgm);
						return;
					}
					TeamDMMinigame.switchTeam(mgm, event.getEntity());
					TeamDMMinigame.applyTeam(event.getEntity(), 0);
					infected.add(event.getEntity());
					if(mgm.getLives() != pdata.getPlayerDeath(event.getEntity())){
						mdata.sendMinigameMessage(mgm, event.getEntity().getName() + " has become " + ChatColor.RED + "Infected!", "error", null);
					}
				}
			}
		}
	}
	
	@EventHandler
	private void endMinigame(EndMinigameEvent event){
		if(infected.contains(event.getPlayer())){
			event.setCancelled(true);
			pdata.quitMinigame(event.getPlayer(), true);
			infected.remove(event.getPlayer());
		}
	}
	
	@EventHandler
	private void quitMinigame(QuitMinigameEvent event){
		if(infected.contains(event.getPlayer())){
			infected.remove(event.getPlayer());
		}
	}
}
