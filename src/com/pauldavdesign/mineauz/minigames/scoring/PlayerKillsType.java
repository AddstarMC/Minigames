package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
//import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class PlayerKillsType extends ScoreType{

	@Override
	public String getType() {
		return "kills";
	}
	
	@EventHandler
	private void playerAttackPlayer(PlayerDeathEvent event){
		if(event.getEntity() instanceof Player){
			Player ply = (Player) event.getEntity();
			Minigame mgm = null;
			if(pdata.getPlayersMinigame(ply) != null){
				mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
			}
			if(mgm != null){
				if(mgm.getBlueTeam().isEmpty() && mgm.getRedTeam().isEmpty()){
					Player attacker = null;
					if(ply.getKiller() != null){
						attacker = (Player) ply.getKiller();
					}
//					else if(event.getDamager() instanceof Arrow){
//						Arrow arr = (Arrow) event.getDamager();
//						if(arr.getShooter() instanceof Player){
//							attacker = (Player) arr.getShooter();
//						}
//						else{
//							return;
//						}
//					}
					else{
						return;
					}
					
					if(!mgm.getName().equals(pdata.getPlayersMinigame(attacker))){
//						event.setCancelled(true);
						return;
					}

					pdata.addPlayerKill(attacker);
					
					if(mgm.getScoreType().equals("kills")){
						for(Player pl : mgm.getPlayers()){
							pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getKiller().getName() + "'s Score: " + pdata.getPlayerKills(ply.getKiller()));
						}
					
						if(mgm.getMaxScore() != 0 && pdata.getPlayerKills(attacker) >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
//							event.setCancelled(true);
							List<Player> conPlayers = new ArrayList<Player>();
							conPlayers.addAll(mgm.getPlayers());
							conPlayers.remove(attacker);
							for(Player pl : conPlayers){
								if(pl != attacker){
									pdata.quitMinigame(pl, false);
								}
							}
						}
					}
				}
				else{
					Player attacker = null;
					if(ply.getKiller() instanceof Player){
						attacker = (Player) ply.getKiller();
					}
//					else if(event.getDamager() instanceof Arrow){
//						Arrow arr = (Arrow) event.getDamager();
//						if(arr.getShooter() instanceof Player){
//							attacker = (Player) arr.getShooter();
//						}
//						else{
//							return;
//						}
//					}
					else{
						return;
					}
					
					if(!mgm.getName().equals(pdata.getPlayersMinigame(attacker))){
//						event.setCancelled(true);
						return;
					}
					
					int team = 0;
					int ateam = 0;
					if(mgm.getBlueTeam().contains(ply)){
						team = 1;
					}
					
					if(mgm.getBlueTeam().contains(attacker)){
						ateam = 1;
					}
					
					if(team == ateam){
//						event.setCancelled(true);
					}
					else if(team != ateam){
						pdata.addPlayerKill(attacker);
						
						if(mgm.getScoreType().equals("kills")){
							boolean end = false;
							
							if(ateam == 0){
								mgm.incrementBlueTeamScore();
								
								if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
									end = true;
								}
							}
							else{
								mgm.incrementRedTeamScore();
								
								if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
									end = true;
								}
							}
							
							for(Player pl : mgm.getPlayers()){
								pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
							}
							
							if(end){
								for(Player pl : mgm.getPlayers()){
									pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + attacker.getName() + " took the final kill against " + ply.getName());
								}
								if(ateam == 1){
									if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
//										event.setCancelled(true);
										pdata.endTeamMinigame(1, mgm);
									}
								}
								else{
									if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
//										event.setCancelled(true);
										pdata.endTeamMinigame(0, mgm);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
