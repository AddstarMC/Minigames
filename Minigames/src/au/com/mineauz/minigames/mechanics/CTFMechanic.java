package au.com.mineauz.minigames.mechanics;

import java.util.ArrayList;
import java.util.EnumSet;
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

import au.com.mineauz.minigames.CTFFlag;
import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.events.FlagCaptureEvent;
import au.com.mineauz.minigames.events.TakeFlagEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.CTFModule;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class CTFMechanic extends GameMechanicBase{

	@Override
	public String getMechanic() {
		return "ctf";
	}

	@Override
	public EnumSet<MinigameType> validTypes() {
		return EnumSet.of(MinigameType.MULTIPLAYER);
	}
	
	@Override
	public void addRequiredModules(Minigame minigame) {
		minigame.addModule(CTFModule.class);
		minigame.addModule(TeamsModule.class);
	}
	
	@Override
	public boolean checkCanStart(Minigame minigame){
		return true;
	}
	
	@Override
	public MinigameModule displaySettings(Minigame minigame){
		return minigame.getModule(CTFModule.class);
	}

	@Override
	public void startMinigame(Minigame minigame, MinigamePlayer caller) {
	}

	@Override
	public void stopMinigame(Minigame minigame, MinigamePlayer caller) {
	}

	@Override
	public void joinMinigame(Minigame minigame, MinigamePlayer player) {
	}

	@Override
	public void quitMinigame(Minigame minigame, MinigamePlayer player, boolean forced) {
		CTFModule module = minigame.getModule(CTFModule.class);
		if(module.isFlagCarrier(player)){
			module.getFlagCarrier(player).stopCarrierParticleEffect();
			module.getFlagCarrier(player).respawnFlag();
			module.removeFlagCarrier(player);
		}
		if(minigame.getPlayers().size() == 1){
			module.resetFlags();
		}
	}

	@Override
	public void endMinigame(Minigame minigame, List<MinigamePlayer> winners, List<MinigamePlayer> losers) {
		CTFModule module = minigame.getModule(CTFModule.class);
		for(MinigamePlayer pl : winners){
			if(module.isFlagCarrier(pl)){
				module.getFlagCarrier(pl).stopCarrierParticleEffect();
				module.getFlagCarrier(pl).respawnFlag();
				module.removeFlagCarrier(pl);
			}
		}
		if(minigame.getPlayers().size() == 1){
			module.resetFlags();
		}
	}
	
	@EventHandler
	private void takeFlag(PlayerInteractEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && !ply.getPlayer().isDead() && ply.getMinigame().hasStarted()){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) && ply.getPlayer().getItemInHand().getType() == Material.AIR){
				Minigame mgm = ply.getMinigame();
				Sign sign = (Sign) event.getClickedBlock().getState();
				if(mgm.getMechanicName().equals("ctf") && sign.getLine(1).equals(ChatColor.GREEN + "Flag")){
					TeamsModule teamModule = mgm.getModule(TeamsModule.class);
					CTFModule ctf = mgm.getModule(CTFModule.class);
					Team team = ply.getTeam();
					
					String sloc = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
					
					if((!sign.getLine(2).equalsIgnoreCase(team.getChatColor() + team.getColor().toString()) && !sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) || 
							sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")){
						if(ctf.getFlagCarrier(ply) == null){
							TakeFlagEvent ev = null;
							if(!ctf.hasDroppedFlag(sloc) && 
									(teamModule.hasTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) || 
											sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))){
								Team oTeam = teamModule.getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2))));
								CTFFlag flag = new CTFFlag(event.getClickedBlock().getLocation(), oTeam, event.getPlayer(), mgm);
								ev = new TakeFlagEvent(mgm, ply, flag);
								Bukkit.getPluginManager().callEvent(ev);
								if(!ev.isCancelled()){
									ctf.addFlagCarrier(ply, flag);
									flag.removeFlag();
								}
							}
							else if(ctf.hasDroppedFlag(sloc)){
								CTFFlag flag = ctf.getDroppedFlag(sloc);
								ev = new TakeFlagEvent(mgm, ply, flag);
								Bukkit.getPluginManager().callEvent(ev);
								if(!ev.isCancelled()){
									ctf.addFlagCarrier(ply, flag);
									if(!flag.isAtHome()){
										flag.stopTimer();
									}
									flag.removeFlag();
								}
							}
							
							
							if(ctf.getFlagCarrier(ply) != null && !ev.isCancelled()){
								if(ctf.getFlagCarrier(ply).getTeam() != null){
									Team fteam = ctf.getFlagCarrier(ply).getTeam();
									String message = ply.getName() + " stole " + fteam.getChatColor() + fteam.getDisplayName() + ChatColor.WHITE + "'s flag!";
									mgm.broadcast(message, MessageType.Normal);
									ctf.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
								}
								else{
									String message = ply.getName() + " stole the " + ChatColor.GRAY + "neutral" + ChatColor.WHITE + " flag!";
									mgm.broadcast(message, MessageType.Normal);
									ctf.getFlagCarrier(ply).startCarrierParticleEffect(ply.getPlayer());
								}
							}
						}
						
					}
					else if(team == teamModule.getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(2)))) || 
							(team == teamModule.getTeam(TeamColor.matchColor(ChatColor.stripColor(sign.getLine(3)))) && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture")) ||
							(sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))){
						
						String clickID = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
						
						if(ctf.getFlagCarrier(ply) != null && ((ctf.hasDroppedFlag(clickID) && ctf.getDroppedFlag(clickID).isAtHome()) || !ctf.hasDroppedFlag(clickID))){
							CTFFlag flag = ctf.getFlagCarrier(ply);
							FlagCaptureEvent ev = new FlagCaptureEvent(mgm, ply, flag);
							Bukkit.getPluginManager().callEvent(ev);
							if(!ev.isCancelled()){
								flag.respawnFlag();
								String id = MinigameUtils.createLocationID(flag.getSpawnLocation());
								ctf.addDroppedFlag(id, flag);
								ctf.removeFlagCarrier(ply);
								
								boolean end = false;
								
								if(mgm.isTeamGame()){
									ply.getTeam().addScore();
									if(mgm.getMaxScore() != 0 && ply.getTeam().getScore() >= mgm.getMaxScorePerPlayer())
										end = true;
									
									if(!end){
										String message = MinigameUtils.formStr("player.ctf.capture", 
												ply.getName(), ply.getTeam().getChatColor() + ply.getTeam().getDisplayName());
										mgm.broadcast(message, MessageType.Normal);
									}
									flag.stopCarrierParticleEffect();
									ply.addScore();
									mgm.setScore(ply, ply.getScore());
									
									if(end){
										mgm.broadcast(MinigameUtils.formStr("player.ctf.captureFinal", ply.getName(), 
												ply.getTeam().getChatColor() + ply.getTeam().getDisplayName()), MessageType.Normal);
										List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(ply.getTeam().getPlayers());
										List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - ply.getTeam().getPlayers().size());
										for(Team t : teamModule.getTeams()){
											if(t != ply.getTeam())
												l.addAll(t.getPlayers());
										}
										plugin.pdata.endMinigame(mgm, w, l);
										ctf.resetFlags();
									}
								}
								else{
									ply.addScore();
									mgm.setScore(ply, ply.getScore());
									if(mgm.getMaxScore() != 0 && ply.getScore() >= mgm.getMaxScorePerPlayer()){
										end = true;
									}
									
									mgm.broadcast(MinigameUtils.formStr("player.ctf.captureNeutral", ply.getName()), MessageType.Normal);
									flag.stopCarrierParticleEffect();
									
									if(end){
										mgm.broadcast(MinigameUtils.formStr("player.ctf.captureNeutralFinal", ply.getName()), MessageType.Normal);
										
										pdata.endMinigame(ply);
										ctf.resetFlags();
									}
								}
							}
						}
						else if(ctf.getFlagCarrier(ply) == null && ctf.hasDroppedFlag(clickID) && !ctf.getDroppedFlag(clickID).isAtHome()){
							CTFFlag flag = ctf.getDroppedFlag(sloc);
							if(ctf.hasDroppedFlag(sloc)){
								ctf.removeDroppedFlag(sloc);
								String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
								ctf.addDroppedFlag(newID, flag);
							}
							flag.respawnFlag();
							mgm.broadcast(MinigameUtils.formStr("player.ctf.returned", ply.getName(), 
									ply.getTeam().getChatColor() + ply.getTeam().getDisplayName() + ChatColor.WHITE), MessageType.Normal);
						}
						else if(ctf.getFlagCarrier(ply) != null && ctf.hasDroppedFlag(clickID) && !ctf.getDroppedFlag(clickID).isAtHome()){
							ply.sendMessage(MinigameUtils.getLang("player.ctf.returnFail"), MessageType.Normal);
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
			CTFModule ctf = mgm.getModule(CTFModule.class);
			if(ctf.isFlagCarrier(ply)){
				CTFFlag flag = ctf.getFlagCarrier(ply);
				Location loc = flag.spawnFlag(ply.getPlayer().getLocation());
				if(loc != null){
					String id = MinigameUtils.createLocationID(loc);
					Team team = ctf.getFlagCarrier(ply).getTeam();
					ctf.addDroppedFlag(id, flag);
					ctf.removeFlagCarrier(ply);
					
					if(team != null)
						mgm.broadcast(MinigameUtils.formStr("player.ctf.dropped", ply.getName(), 
							team.getChatColor() + team.getDisplayName() + ChatColor.WHITE), MessageType.Normal);
					else
						mgm.broadcast(MinigameUtils.formStr("player.ctf.droppedNeutral", ply.getName()), MessageType.Normal);
					flag.stopCarrierParticleEffect();
					flag.startReturnTimer();
				}
				else{
					flag.respawnFlag();
					ctf.removeFlagCarrier(ply);
					flag.stopCarrierParticleEffect();
				}
			}
		}
	}
	
	@EventHandler
	public void playerAutoBalance(PlayerDeathEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.MULTIPLAYER && ply.getMinigame().isTeamGame()){
			Minigame mgm = ply.getMinigame();
			
			if(mgm.getMechanicName().equals("ctf")){
				Team smt = null;
				Team lgt = ply.getTeam();
				for(Team t : mgm.getModule(TeamsModule.class).getTeams()){
					if(smt == null || t.getPlayers().size() < smt.getPlayers().size() - 1)
						smt = t;
				}
				if(lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MultiplayerType.switchTeam(mgm, ply, smt);
					ply.sendMessage(String.format(smt.getAutobalanceMessage(), smt.getChatColor() + smt.getDisplayName()), MessageType.Normal);
					mgm.broadcastExcept(String.format(smt.getGameAutobalanceMessage(), 
						ply.getName(), smt.getChatColor() + smt.getDisplayName()), MessageType.Normal, ply);
				}
			}
		}
	}
}
