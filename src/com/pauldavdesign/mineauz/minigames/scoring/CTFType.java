package com.pauldavdesign.mineauz.minigames.scoring;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.pauldavdesign.mineauz.minigames.CTFFlag;
import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;

public class CTFType extends ScoreType{

	@Override
	public String getType() {
		return "ctf";
	}
	
	@EventHandler
	private void takeFlag(PlayerInteractEvent event){
		Player ply = event.getPlayer();
		if(pdata.playerInMinigame(ply)){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) && ply.getItemInHand().getType() == Material.AIR){
				Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
				Sign sign = (Sign) event.getClickedBlock().getState();
				if(mgm.getScoreType().equals("ctf")){
					if(!mgm.getBlueTeam().isEmpty() || !mgm.getRedTeam().isEmpty()){
						int team = 0;
						if(mgm.getBlueTeam().contains(event.getPlayer())){
							team = 1;
						}
						
						String sloc = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
						
						if((sign.getLine(2).equalsIgnoreCase(ChatColor.RED + "Red") && team == 1) || 
								(sign.getLine(2).equalsIgnoreCase(ChatColor.BLUE + "Blue") && team == 0) ||
								sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")){
							if(mgm.getFlagCarrier(ply) == null){
								if(!mgm.hasDroppedFlag(sloc)){
									int oTeam = 1;
									if(team == 1){
										oTeam = 0;
									}
									if(sign.getLine(2).equalsIgnoreCase(ChatColor.GRAY + "Neutral")){
										oTeam = -1;
									}
									CTFFlag flag = new CTFFlag(event.getClickedBlock().getLocation(), oTeam, event.getPlayer(), mgm);
									mgm.addFlagCarrier(ply, flag);
									flag.removeFlag();
								}
								else{
									CTFFlag flag = mgm.getDroppedFlag(sloc);
									mgm.addFlagCarrier(ply, flag);
									if(!flag.isAtHome()){
										flag.stopTimer();
									}
									flag.removeFlag();
								}
								for(Player pl : mgm.getPlayers()){
									if(team == 0 && mgm.getFlagCarrier(ply).getTeam() == 1){
										pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " stole " + ChatColor.BLUE + "Blue Team's" + ChatColor.WHITE + " flag!");
									}else if(team == 1 && mgm.getFlagCarrier(ply).getTeam() == 0){
										pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " stole " + ChatColor.RED + "Red Team's" + ChatColor.WHITE + " flag!");
									}
									else{
										pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " stole the " + ChatColor.GRAY + "neutral" + ChatColor.WHITE + " flag!");
									}
								}
							}
							
						}
						else if((sign.getLine(2).equalsIgnoreCase(ChatColor.RED + "Red") && team == 0) || 
								(sign.getLine(2).equalsIgnoreCase(ChatColor.BLUE + "Blue") && team == 1)){
							if(mgm.getFlagCarrier(ply) == null){
								if(mgm.hasDroppedFlag(sloc) && !mgm.getDroppedFlag(sloc).isAtHome()){
									CTFFlag flag = mgm.getDroppedFlag(sloc);
									if(mgm.hasDroppedFlag(sloc)){
										mgm.removeDroppedFlag(sloc);
										String newID = MinigameUtils.createLocationID(flag.getSpawnLocation());
										mgm.addDroppedFlag(newID, flag);
									}
									flag.respawnFlag();
									
									for(Player pl : mgm.getPlayers()){
										if(flag.getTeam() == 1){
											pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " returned " + ChatColor.BLUE + "Blue Team's" + ChatColor.WHITE + " flag!");
										}else if(flag.getTeam() == 0){
											pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " returned " + ChatColor.RED + "Red Team's" + ChatColor.WHITE + " flag!");
										}
										else{
											pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " stole the " + ChatColor.GRAY + "neutral" + ChatColor.WHITE + " flag!");
										}
									}
								}
							}
							else{
								ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You cannot return the flag if you're the carrier!");
							}
						}
						else if((team == 0 && sign.getLine(2).equalsIgnoreCase(ChatColor.RED + "Red") ||
								(team == 1 && sign.getLine(2).equalsIgnoreCase(ChatColor.BLUE + "Blue")) || 
								(team == 0 && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.RED + "Red")) ||
								(team == 1 && sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.BLUE + "Blue")) ||
								(sign.getLine(2).equalsIgnoreCase(ChatColor.GREEN + "Capture") && sign.getLine(3).equalsIgnoreCase(ChatColor.GRAY + "Neutral"))) && 
								mgm.getFlagCarrier(ply) != null){
							CTFFlag flag = mgm.getFlagCarrier(ply);
							
							String clickID = MinigameUtils.createLocationID(event.getClickedBlock().getLocation());
							
							if((mgm.hasDroppedFlag(clickID) && mgm.getDroppedFlag(clickID).isAtHome()) || !mgm.hasDroppedFlag(clickID)){
								flag.respawnFlag();
								String id = MinigameUtils.createLocationID(flag.getSpawnLocation());
								mgm.addDroppedFlag(id, flag);
								mgm.removeFlagCarrier(ply);
								
								boolean end = false;
								
								if(team == 1){
									mgm.incrementBlueTeamScore();
									
									if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
										end = true;
									}
								}
								else{
									mgm.incrementRedTeamScore();
									
									if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
										end = true;
									}
								}
								
								for(Player pl : mgm.getPlayers()){
									if(team == 0){
										pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " captured a flag for " + ChatColor.RED + "Red Team");
									}else{
										pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " captured a flag for " + ChatColor.BLUE + "Blue Team");
									}
									pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
								}
								
								if(end){
									for(Player pl : mgm.getPlayers()){
										if(team == 0){
											pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " captured the final flag for " + ChatColor.RED + "Red Team");
										}
										else
											pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " captured the final flag for " + ChatColor.BLUE + "Blue Team");
									}
									if(team == 1){
										pdata.endTeamMinigame(1, mgm);
										mgm.resetFlags();
									}
									else{
										pdata.endTeamMinigame(0, mgm);
										mgm.resetFlags();
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void dropFlag(PlayerDeathEvent event){
		Player ply = (Player) event.getEntity();
		if(pdata.playerInMinigame(ply)){
			Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
			if(mgm.isFlagCarrier(ply)){
				CTFFlag flag = mgm.getFlagCarrier(ply);
				Location loc = flag.spawnFlag(ply.getLocation());
				if(loc != null){
					String id = MinigameUtils.createLocationID(loc);
					mgm.addDroppedFlag(id, flag);
					mgm.removeFlagCarrier(ply);
					for(Player pl : mgm.getPlayers()){
						if(flag.getTeam() == 0){
							pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " dropped " + ChatColor.RED + "Red Team's" + ChatColor.WHITE + " flag!");
						}else if(flag.getTeam() == 1){
							pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " dropped " + ChatColor.BLUE + "Blue Team's" + ChatColor.WHITE + " flag!");
						}
						else{
							pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + ply.getName() + " dropped the " + ChatColor.GRAY + "neutral" + ChatColor.WHITE + " flag!");
						}
					}
				}
				else{
					flag.respawnFlag();
					mgm.removeFlagCarrier(ply);
				}
			}
		}
	}
	
	@EventHandler
	private void playerQuitMinigame(QuitMinigameEvent event){
		if(event.getMinigame().getScoreType().equals("ctf")){
			if(!event.isForced() && event.getMinigame().getPlayers().size() == 1){
				event.getMinigame().resetFlags();
				event.getMinigame().removeFlagCarrier(event.getPlayer());
			}
			else if(event.getMinigame().isFlagCarrier(event.getPlayer())){
				event.getMinigame().getFlagCarrier(event.getPlayer()).respawnFlag();
				event.getMinigame().removeFlagCarrier(event.getPlayer());
			}
		}
	}
	
	@EventHandler
	private void playerEndMinigame(EndMinigameEvent event){
		if(event.getMinigame().getScoreType().equals("ctf")){
			if(event.getMinigame().isFlagCarrier(event.getPlayer())){
				event.getMinigame().getFlagCarrier(event.getPlayer()).respawnFlag();
				event.getMinigame().removeFlagCarrier(event.getPlayer());
			}
		}
	}
}
