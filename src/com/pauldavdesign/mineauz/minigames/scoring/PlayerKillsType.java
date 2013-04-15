package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamDMMinigame;

public class PlayerKillsType extends ScoreType{

	@Override
	public String getType() {
		return "kills";
	}

	@Override
	public void balanceTeam(List<Player> players, Minigame minigame) {
		for(int i = 0; i < players.size(); i++){
			if(minigame.getType().equals("teamdm")){
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
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been auto balanced to " + ChatColor.RED + "Red Team");
						mdata.sendMinigameMessage(minigame, players.get(i).getName() + " has been auto balanced to " + ChatColor.RED + "Red Team", null, players.get(i));
					}
				}
				else if(team == 0){
					if(minigame.getBlueTeam().size() < minigame.getRedTeam().size() - 1){
						minigame.getRedTeam().remove(players.get(i));
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been auto balanced to " + ChatColor.BLUE + "Blue Team");
						mdata.sendMinigameMessage(minigame, players.get(i).getName() + " has been auto balanced to " + ChatColor.BLUE + "Blue Team", null, players.get(i));
					}
				}
				else{
					if(minigame.getRedTeam().size() <= minigame.getBlueTeam().size()){
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been auto balanced to " + ChatColor.RED + "Red Team");
						mdata.sendMinigameMessage(minigame, players.get(i).getName() + " has been auto balanced to " + ChatColor.RED + "Red Team", null, players.get(i));
					}
					else if(minigame.getBlueTeam().size() <= minigame.getRedTeam().size()){
						minigame.addBlueTeamPlayer(players.get(i));
						team = 1;
						players.get(i).sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been auto balanced to " + ChatColor.BLUE + "Blue Team");
						mdata.sendMinigameMessage(minigame, players.get(i).getName() + " has been auto balanced to " + ChatColor.BLUE + "Blue Team", null, players.get(i));
					}
				}
			}
		}
		
	}
	
	@EventHandler
	private void playerAttackPlayer(PlayerDeathEvent event){
		if(event.getEntity() instanceof Player){
			Player ply = (Player) event.getEntity();
			Minigame mgm = null;
			if(pdata.getPlayersMinigame(ply) != null){
				mgm = pdata.getPlayersMinigame(ply);
			}
			if(mgm != null && mgm.getScoreType().equals("kills")){
				if(mgm.getBlueTeam().isEmpty() && mgm.getRedTeam().isEmpty()){
					Player attacker = null;
					if(ply.getKiller() != null){
						attacker = (Player) ply.getKiller();
						if(attacker == ply){
							return;
						}
					}
					else{
						return;
					}
					
					if(!mgm.equals(pdata.getPlayersMinigame(attacker))){
						return;
					}

					pdata.addPlayerScore(attacker);
					mgm.setScore(attacker, pdata.getPlayerScore(attacker));
				
					if(mgm.getMaxScore() != 0 && pdata.getPlayerScore(attacker) >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
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
				else{
					Player attacker = null;
					if(ply.getKiller() instanceof Player){
						attacker = (Player) ply.getKiller();
						if(attacker == ply){
							return;
						}
					}
					else{
						return;
					}
					
					if(!mgm.equals(pdata.getPlayersMinigame(attacker))){
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
					
					if(team != ateam){
						pdata.addPlayerScore(attacker);
						mgm.setScore(attacker, pdata.getPlayerScore(attacker));
						
						boolean end = false;
						
						if(ateam == 0){
							mgm.incrementRedTeamScore();
							
							if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
								end = true;
							}
						}
						else{
							mgm.incrementBlueTeamScore();
							
							if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
								end = true;
							}
						}
						
						if(end){
							mdata.sendMinigameMessage(mgm, attacker.getName() + " took the final kill against " + ply.getName(), null, null);
							if(ateam == 1){
								if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
									pdata.endTeamMinigame(1, mgm);
								}
							}
							else{
								if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
									pdata.endTeamMinigame(0, mgm);
								}
							}
						}
						
					}
				}
			}
		}
	}
	
	@EventHandler
	private void playerSuicide(PlayerDeathEvent event){
		Player ply = event.getEntity();
		if(pdata.playerInMinigame(ply) && (ply.getKiller() == null || ply.getKiller() == ply)){
			Minigame mgm = pdata.getPlayersMinigame(ply);
			if(mgm.getScoreType().equals("kills")){
				if(mgm.getRedTeam().isEmpty() && mgm.getBlueTeam().isEmpty()){
					pdata.takePlayerScore(ply);
					mgm.setScore(ply, pdata.getPlayerScore(ply));
					
//					mdata.sendMinigameMessage(mgm, ply.getName() + "'s Score: " + pdata.getPlayerScore(ply), null, null);
				}
				else{
					pdata.takePlayerScore(ply);
					if(mgm.getRedTeam().contains(ply)){
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
		Player ply = (Player) event.getEntity();
		if(pdata.getPlayersMinigame(ply) != null && pdata.getPlayersMinigame(ply).getType().equals("teamdm")){
			int pteam = 0;
			if(pdata.getPlayersMinigame(ply).getBlueTeam().contains(ply)){
				pteam = 1;
			}
			final Minigame mgm = pdata.getPlayersMinigame(ply);
			
			if(mgm.getScoreType().equals("kills")){
				if(pteam == 1){
					if(mgm.getRedTeam().size() < mgm.getBlueTeam().size() - 1){
						TeamDMMinigame.switchTeam(mgm, ply);
						ply.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been switched to " + ChatColor.RED + "Red Team");
						for(Player pl : mgm.getPlayers()){
							if(pl != ply){
								pl.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + ply.getName() + " has been switched to " + ChatColor.RED + "Red Team");
							}
						}
					}
				}
				else{
					if(mgm.getBlueTeam().size() < mgm.getRedTeam().size()  - 1){
						TeamDMMinigame.switchTeam(mgm, ply);
						ply.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "You have been switched to " + ChatColor.BLUE + "Blue Team");
						for(Player pl : mgm.getPlayers()){
							if(pl != ply){
								pl.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + ply.getName() + " has been switched to " + ChatColor.BLUE + "Blue Team");
							}
						}
					}
				}
			}
		}
	}
}
