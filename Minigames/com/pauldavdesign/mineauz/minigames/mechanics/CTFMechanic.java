package com.pauldavdesign.mineauz.minigames.mechanics;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import com.pauldavdesign.mineauz.minigames.gametypes.MultiplayerType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;

public class CTFMechanic extends GameMechanicBase{

	@Override
	public String getMechanic() {
		return "ctf";
	}

	@Override
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame) {
		if(minigame.isTeamGame()){
			boolean sorted = false;
			for(MinigamePlayer ply : players){
				if(ply.getTeam() == null){
					Team smt = null;
					for(Team t : TeamsModule.getMinigameModule(minigame).getTeams()){
						if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() && t.getPlayers().size() != t.getMaxPlayers()))
							smt = t;
					}
					if(smt == null){
						pdata.quitMinigame(ply, false);
						ply.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
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
				for(Team t : TeamsModule.getMinigameModule(minigame).getTeams()){
					if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() - 1 && !t.isFull()))
						smt = t;
					if((lgt == null || (t.getPlayers().size() > lgt.getPlayers().size() && !t.isFull())) && t != smt)
						lgt = t;
				}
				if(smt != null && lgt != null && lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MinigamePlayer pl = lgt.getPlayers().get(0);
					MultiplayerType.switchTeam(minigame, pl, smt);
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
	private void takeFlag(PlayerInteractEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && !ply.getPlayer().isDead()){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) && ply.getPlayer().getItemInHand().getType() == Material.AIR){
				Minigame mgm = ply.getMinigame();
				Sign sign = (Sign) event.getClickedBlock().getState();
				if(mgm.getScoreType().equals("ctf") && sign.getLine(1).equals(ChatColor.GREEN + "Flag")){
					Team team = ply.getTeam();
					
					String sloc = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
					
					if((!sign.getLine(2).equalsIgnoreCase(team.getChatColor() + team.getColor().toString()) && !sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) || 
							sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")){
						if(mgm.getFlagCarrier(ply) == null){
							TakeFlagEvent ev = null;
							if(!mgm.hasDroppedFlag(sloc) && 
									(TeamsModule.getMinigameModule(mgm).hasTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) || 
											sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))){
								Team oTeam = TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2))));
								CTFFlag flag = new CTFFlag(event.getClickedBlock().getLocation(), oTeam, event.getPlayer(), mgm);
								ev = new TakeFlagEvent(mgm, ply, flag);
								Bukkit.getPluginManager().callEvent(ev);
								if(!ev.isCancelled()){
									mgm.addFlagCarrier(ply, flag);
									flag.removeFlag();
								}
							}
							else if(mgm.hasDroppedFlag(sloc)){
								CTFFlag flag = mgm.getDroppedFlag(sloc);
								ev = new TakeFlagEvent(mgm, ply, flag);
								Bukkit.getPluginManager().callEvent(ev);
								if(!ev.isCancelled()){
									mgm.addFlagCarrier(ply, flag);
									if(!flag.isAtHome()){
										flag.stopTimer();
									}
									flag.removeFlag();
								}
							}
							
							
							if(mgm.getFlagCarrier(ply) != null && !ev.isCancelled()){
								if(mgm.getFlagCarrier(ply).getTeam() != null){
									Team fteam = mgm.getFlagCarrier(ply).getTeam();
									String message = ply.getName() + " stole " + fteam.getChatColor() + fteam.getDisplayName() + ChatColor.WHITE + "'s flag!";
									mdata.sendMinigameMessage(mgm, message, null, null);
									mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
								}
								else{
									String message = ply.getName() + " stole the " + ChatColor.GRAY + "neutral" + ChatColor.WHITE + " flag!";
									mdata.sendMinigameMessage(mgm, message, null, null);
									mgm.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
								}
							}
						}
						
					}
					else if(team == TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) || 
							(team == TeamsModule.getMinigameModule(mgm).getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(3)))) && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
							(sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))){
						
						String clickID = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
						
						if(mgm.getFlagCarrier(ply) != null && ((mgm.hasDroppedFlag(clickID) && mgm.getDroppedFlag(clickID).isAtHome()) || !mgm.hasDroppedFlag(clickID))){
							CTFFlag flag = mgm.getFlagCarrier(ply);
							FlagCaptureEvent ev = new FlagCaptureEvent(mgm, ply, flag);
							Bukkit.getPluginManager().callEvent(ev);
							if(!ev.isCancelled()){
								flag.respawnFlag();
								String id = MinigameUtils.createLocationID(flag.getSpawnLocation());
								mgm.addDroppedFlag(id, flag);
								mgm.removeFlagCarrier(ply);
								
								boolean end = false;
								
								if(mgm.isTeamGame()){
									ply.getTeam().addScore();
									if(mgm.getMaxScore() != 0 && ply.getTeam().getScore() >= mgm.getMaxScorePerPlayer())
										end = true;
									
									if(!end){
										String message = MinigameUtils.formStr("player.ctf.capture", 
												ply.getName(), ply.getTeam().getChatColor() + ply.getTeam().getDisplayName());
										mdata.sendMinigameMessage(mgm, message, null, null);
									}
									flag.stopCarrierParticleEffect();
									ply.addScore();
									mgm.setScore(ply, ply.getScore());
									
									if(end){
										mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureFinal", ply.getName(), 
												ply.getTeam().getChatColor() + ply.getTeam().getDisplayName()), null, null);
										List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(ply.getTeam().getPlayers());
										List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - ply.getTeam().getPlayers().size());
										for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
											if(t != ply.getTeam())
												l.addAll(t.getPlayers());
										}
										plugin.pdata.endMinigame(mgm, w, l);
										mgm.resetFlags();
									}
								}
								else{
									ply.addScore();
									mgm.setScore(ply, ply.getScore());
									if(mgm.getMaxScore() != 0 && ply.getScore() >= mgm.getMaxScorePerPlayer()){
										end = true;
									}
									
									mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.captureNeutral", ply.getName()), null, null);
									flag.stopCarrierParticleEffect();
									
									if(end){
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
							mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.returned", ply.getName(), 
									ply.getTeam().getChatColor() + ply.getTeam().getDisplayName() + ChatColor.WHITE), null, null);
						}
						else if(mgm.getFlagCarrier(ply) != null && mgm.hasDroppedFlag(clickID) && !mgm.getDroppedFlag(clickID).isAtHome()){
							ply.sendMessage(MinigameUtils.getLang("player.ctf.returnFail"), null);
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
					Team team = mgm.getFlagCarrier(ply).getTeam();
					mgm.addDroppedFlag(id, flag);
					mgm.removeFlagCarrier(ply);
					
					if(team != null)
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.dropped", ply.getName(), 
							team.getChatColor() + team.getDisplayName() + ChatColor.WHITE), null, null);
					else
						mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("player.ctf.droppedNeutral", ply.getName()), null, null);
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
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.MULTIPLAYER && ply.getMinigame().isTeamGame()){
			Minigame mgm = ply.getMinigame();
			
			if(mgm.getScoreType().equals("ctf")){
				Team smt = null;
				Team lgt = ply.getTeam();
				for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
					if(smt == null || t.getPlayers().size() < smt.getPlayers().size() - 1)
						smt = t;
				}
				if(lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MultiplayerType.switchTeam(mgm, ply, smt);
					ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(mgm, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
				}
			}
		}
	}
}
