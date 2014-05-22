package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.pauldavdesign.mineauz.minigames.CTFFlag;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.FlagCaptureEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.TakeFlagEvent;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.gametypes.TeamsType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class CTFType extends ScoreTypeBase{

	@Override
	public String getType() {
		return "ctf";
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
//						minigame.getBlueTeam().remove(players.get(i));
						minigame.removeBlueTeamPlayer(players.get(i));
						minigame.addRedTeamPlayer(players.get(i));
						team = 0;
						players.get(i).sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.team.autobalance.minigameMsg", players.get(i).getName(), ChatColor.RED + "Red Team"), null, players.get(i));
					}
				}
				else if(team == 0){
					if(minigame.getBlueTeam().size() < minigame.getRedTeam().size() - 1){
//						minigame.getRedTeam().remove(players.get(i));
						minigame.removeRedTeamPlayer(players.get(i));
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
//				TeamDMMinigame.applyTeam(players.get(i), team);
			}
		}
	}
	
	@EventHandler
	private void takeFlag(PlayerInteractEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && !ply.getPlayer().isDead()){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) && ply.getPlayer().getItemInHand().getType() == Material.AIR){
				Minigame mgm = ply.getMinigame();
				Sign sign = (Sign) event.getClickedBlock().getState();
				if(mgm.getScoreType().equals("ctf") && sign.getLine(1).equals(ChatColor.GREEN + "Flag")){
					if(!mgm.getBlueTeam().isEmpty() || !mgm.getRedTeam().isEmpty() || mgm.getType() != MinigameType.TEAMS){
						int team = 0;
						if(mgm.getBlueTeam().contains(event.getPlayer())){
							team = 1;
						}
						
						if(mgm.getType() != MinigameType.TEAMS){
							team = -1;
						}
						
						String sloc = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
						
						if((sign.getLine(2).equalsIgnoreCase(ChatColor.RED + "Red") && team == 1) || 
								(sign.getLine(2).equalsIgnoreCase(ChatColor.BLUE + "Blue") && team == 0) ||
								sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")){
							if(mgm.getFlagCarrier(ply) == null){
								TakeFlagEvent ev;
								if(!mgm.hasDroppedFlag(sloc)){
									int oTeam = 1;
									if(team == 1){
										oTeam = 0;
									}
									if(sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")){
										oTeam = -1;
									}
									CTFFlag flag = new CTFFlag(event.getClickedBlock().getLocation(), oTeam, event.getPlayer(), mgm);
									ev = new TakeFlagEvent(mgm, ply, flag);
									if(!ev.isCancelled()){
										mgm.addFlagCarrier(ply, flag);
										flag.removeFlag();
									}
								}
								else{
									CTFFlag flag = mgm.getDroppedFlag(sloc);
									ev = new TakeFlagEvent(mgm, ply, flag);
									if(!ev.isCancelled()){
										mgm.addFlagCarrier(ply, flag);
										if(!flag.isAtHome()){
											flag.stopTimer();
										}
										flag.removeFlag();
									}
								}
								
								if(!ev.isCancelled()){
									if(team == 0 && mgm.getFlagCarrier(ply).getTeam() == 1){
										String message = ply.getName() + " stole " + ChatColor.BLUE + "Blue Team's" + ChatColor.WHITE + " flag!";
										if(ev.shouldDisplayMessage())
											mdata.sendMinigameMessage(mgm, message, null, null);
										mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
									}else if(team == 1 && mgm.getFlagCarrier(ply).getTeam() == 0){
										String message = ply.getName() + " stole " + ChatColor.RED + "Red Team's" + ChatColor.WHITE + " flag!";
										if(ev.shouldDisplayMessage())
											mdata.sendMinigameMessage(mgm, message, null, null);
										mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
									}
									else{
										String message = ply.getName() + " stole the " + ChatColor.GRAY + "neutral" + ChatColor.WHITE + " flag!";
										if(ev.shouldDisplayMessage())
											mdata.sendMinigameMessage(mgm, message, null, null);
										mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
									}
								}
							}
							
						}
						else if((team == 0 && sign.getLine(2).equalsIgnoreCase(ChatColor.RED + "Red") ||
								(team == 1 && sign.getLine(2).equalsIgnoreCase(ChatColor.BLUE + "Blue")) || 
								(team == 0 && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.RED + "Red")) ||
								(team == 1 && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.BLUE + "Blue")) ||
								(sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.GRAY + "Neutral")))){
							
							String clickID = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
							
							if(mgm.getFlagCarrier(ply) != null && ((mgm.hasDroppedFlag(clickID) && mgm.getDroppedFlag(clickID).isAtHome()) || !mgm.hasDroppedFlag(clickID))){
								CTFFlag flag = mgm.getFlagCarrier(ply);
								FlagCaptureEvent ev = new FlagCaptureEvent(mgm, ply, flag);
								if(!ev.isCancelled()){
									flag.respawnFlag();
									String id = MinigameUtils.createLocationID(flag.getSpawnLocation());
									mgm.addDroppedFlag(id, flag);
									mgm.removeFlagCarrier(ply);
									
									boolean end = false;
									
									if(mgm.getType() == MinigameType.TEAMS){
										if(team == 1){
											mgm.incrementBlueTeamScore();
											
											if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer()){
												end = true;
											}
										}
										else{
											mgm.incrementRedTeamScore();
											
											if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer()){
												end = true;
											}
										}
										
										if(team == 0){
											String message = MinigameUtils.formStr("player.ctf.capture", ply.getName(), ChatColor.RED + "Red Team");
											if(ev.shouldDisplayMessage())
												mdata.sendMinigameMessage(mgm, message, null, null);
										}else{
											String message = MinigameUtils.formStr("player.ctf.capture", ply.getName(), ChatColor.BLUE + "Blue Team");
											if(ev.shouldDisplayMessage())
												mdata.sendMinigameMessage(mgm, message, null, null);
										}
										flag.stopCarrierParticleEffect();
	//									pdata.addPlayerScore(ply);
										ply.addScore();
										mgm.setScore(ply, ply.getScore());
										
										if(end){
											if(ev.shouldDisplayMessage()){
												if(team == 0){
													mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureFinal", ply.getName(), ChatColor.RED + "Red Team"), null, null);
												}
												else{
													mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureFinal", ply.getName(), ChatColor.BLUE + "Blue Team"), null, null);
												}
											}
											if(team == 1){
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
												mgm.resetFlags();
											}
											else{
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
												mgm.resetFlags();
											}
										}
									}
									else{
	//									pdata.addPlayerScore(ply);
										ply.addScore();
										mgm.setScore(ply, ply.getScore());
										if(mgm.getMaxScore() != 0 && ply.getScore() >= mgm.getMaxScorePerPlayer()){
											end = true;
										}
										
										if(ev.shouldDisplayMessage())
											mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureNeutral", ply.getName()), null, null);
										flag.stopCarrierParticleEffect();
										
										if(end){
											if(ev.shouldDisplayMessage())
												mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureNeutralFinal", ply.getName()), null, null);
											
											pdata.endMinigame(ply);
											mgm.resetFlags();
										}
									}
								}
							}
							else if(mgm.getFlagCarrier(ply) == null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()){
								CTFFlag flag = mgm.getDroppedFlag(sloc);
								if(mgm.hasDroppedFlag(sloc)){
									mgm.removeDroppedFlag(sloc);
									String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
									mgm.addDroppedFlag(newID, flag);
								}
								flag.respawnFlag();
								
								if(flag.getTeam() == 1){
									mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.returned", ply.getName(), ChatColor.BLUE + "Blue Team" + ChatColor.WHITE), null, null);
								}else if(flag.getTeam() == 0){
									mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.returned", ply.getName(), ChatColor.RED + "Red Team" + ChatColor.WHITE), null, null);
								}
								else{
									mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.stoleNeutral", ply.getName()), null, null);
									mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
								}
							}
							else if(mgm.getFlagCarrier(ply) != null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()){
								ply.sendMessage(MinigameUtils.getLang("player.ctf.returnFail"), null);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void dropFlag(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame()){
			Minigame mgm = ply.getMinigame();
			if(mgm.isFlagCarrier(ply)){
				CTFFlag flag = mgm.getFlagCarrier(ply);
				Location loc = flag.spawnFlag(ply.getPlayer().getLocation());
				if(loc != null){
					String id = MinigameUtils.createLocationID(loc);
					mgm.addDroppedFlag(id, flag);
					mgm.removeFlagCarrier(ply);

					if(flag.getTeam() == 0){
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.dropped", ply.getName(), ChatColor.RED + "Red Team" + ChatColor.WHITE), null, null);
					}else if(flag.getTeam() == 1){
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.dropped", ply.getName(), ChatColor.BLUE + "Blue Team" + ChatColor.WHITE), null, null);
					}
					else{
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.droppedNeutral", ply.getName()), null, null);
					}
					flag.stopCarrierParticleEffect();
					flag.startReturnTimer();
				}
				else{
					flag.respawnFlag();
					mgm.removeFlagCarrier(ply);
					flag.stopCarrierParticleEffect();
				}
			}
		}
	}
	
	@EventHandler
	private void playerQuitMinigame(QuitMinigameEvent event){
		if(event.getMinigame().getScoreType().equals("ctf")){
			if(event.getMinigame().isFlagCarrier(event.getMinigamePlayer())){
				event.getMinigame().getFlagCarrier(event.getMinigamePlayer()).stopCarrierParticleEffect();
				event.getMinigame().getFlagCarrier(event.getMinigamePlayer()).respawnFlag();
				event.getMinigame().removeFlagCarrier(event.getMinigamePlayer());
			}
			if(event.getMinigame().getPlayers().size() == 1){
				event.getMinigame().resetFlags();
			}
		}
	}
	
	@EventHandler
	private void playerEndMinigame(EndMinigameEvent event){
		if(event.getMinigame().getScoreType().equals("ctf")){
			for(MinigamePlayer pl : event.getWinners()){
				if(event.getMinigame().isFlagCarrier(pl)){
					event.getMinigame().getFlagCarrier(pl).stopCarrierParticleEffect();
					event.getMinigame().getFlagCarrier(pl).respawnFlag();
					event.getMinigame().removeFlagCarrier(pl);
				}
			}
			if(event.getMinigame().getPlayers().size() == 1){
				event.getMinigame().resetFlags();
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
			
			if(mgm.getScoreType().equals("ctf")){
				if(pteam == 1){
					if(mgm.getRedTeam().size() < mgm.getBlueTeam().size() - 1){
						TeamsType.switchTeam(mgm, ply);
						ply.sendMessage(MinigameUtils.formStr("player.autobalance.plyMsg", ChatColor.RED + "Red Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.autobalance.minigameMsg", ply.getName(), ChatColor.RED + "Red Team"), null, ply);
					}
				}
				else{
					if(mgm.getBlueTeam().size() < mgm.getRedTeam().size()  - 1){
						TeamsType.switchTeam(mgm, ply);
						ply.sendMessage(String.format(MinigameUtils.getLang("player.autobalance.plyMsg"), ChatColor.BLUE + "Blue Team"), null);
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.autobalance.minigameMsg", ply.getName(), ChatColor.BLUE + "Blue Team"), null, ply);
					}
				}
			}
		}
	}
}
