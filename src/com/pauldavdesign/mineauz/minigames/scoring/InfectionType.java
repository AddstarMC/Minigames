package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.events.EndTeamMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;

public class InfectionType extends ScoreType{
	
	private List<Player> infected = new ArrayList<Player>();

	@Override
	public String getType() {
		return "infection";
	}

	@Override
	public void balanceTeam(List<Player> players, Minigame minigame) {
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
			}
		}
	}
	
	@EventHandler
	private void playerDeath(PlayerDeathEvent event){
		if(pdata.playerInMinigame(event.getEntity())){
			Minigame mgm = pdata.getPlayersMinigame(event.getEntity());
			if(mgm.getType().equals("teamdm") && mgm.getScoreType().equals("infection")){
				if(mgm.getBlueTeam().contains(event.getEntity())){
					TeamDMMinigame.switchTeam(mgm, event.getEntity());
					TeamDMMinigame.applyTeam(event.getEntity(), 0);
					infected.add(event.getEntity());
					if(mgm.getLives() != pdata.getPlayerDeath(event.getEntity())){
						mdata.sendMinigameMessage(mgm, event.getEntity().getName() + " has become " + ChatColor.RED + "Infected!", "error", null);
					}
					if(mgm.getBlueTeam().isEmpty()){
						event.getEntity().setHealth(2);
						pdata.endTeamMinigame(0, mgm);
					}
				}
			}
		}
	}
	
	@EventHandler
	private void endTeamMinigame(EndTeamMinigameEvent event){
		if(event.getMinigame().getScoreType().equals("infection")){
			List<Player> infect = new ArrayList<Player>();
			infect.addAll(infected);
			for(Player inf : infect){
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
