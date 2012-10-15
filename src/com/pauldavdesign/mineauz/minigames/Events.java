package com.pauldavdesign.mineauz.minigames;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.pauldavdesign.mineauz.minigames.events.RevertCheckpointEvent;

public class Events implements Listener{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			String minigame = pdata.getPlayersMinigame(event.getPlayer());
			Minigame mgm = mdata.getMinigame(minigame);
			if(mdata.getMinigame(minigame).hasPlayers()){
				String mgtype = mgm.getType();
				if(mgtype.equals("spleef") || mgtype.equals("lms")){
					event.setRespawnLocation(mdata.getMinigame(minigame).getQuitPosition());
					pdata.quitMinigame(event.getPlayer(), true);
					event.getPlayer().sendMessage(ChatColor.GRAY + "Bad Luck! Leaving the minigame.");
				}
				else if(mgtype.equals("race")){
					event.setRespawnLocation(pdata.getPlayerCheckpoint(event.getPlayer()));
					event.getPlayer().sendMessage(ChatColor.GRAY + "Bad Luck! Returning to checkpoint.");
					
					if(!mgm.getLoadout().isEmpty()){
						mdata.equiptLoadout(minigame, event.getPlayer());
					}
				}
			}
			else {
				event.setRespawnLocation(pdata.getPlayerCheckpoint(event.getPlayer()));
				event.getPlayer().sendMessage(ChatColor.GRAY + "Bad Luck! Returning to checkpoint.");
				
				if(!mgm.getLoadout().isEmpty()){
					mdata.equiptLoadout(minigame, event.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	public void playerTakeDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			final Player ply = (Player) event.getEntity();
			if(pdata.playerInMinigame(ply)){
				String minigame = pdata.getPlayersMinigame(ply);
				Minigame mgm = mdata.getMinigame(minigame);
				if(mdata.getMinigame(minigame).hasPlayers() && ply.getHealth() - event.getDamage() <= 0){
					String mgtype = mgm.getType();
					if(mgtype.equals("spleef") || mgtype.equals("lms")){
						pdata.quitMinigame(ply, true);
						event.setCancelled(true);
						ply.sendMessage(ChatColor.GRAY + "Bad Luck! Leaving the minigame.");
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								ply.setFireTicks(0);
							}
						}, 20);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void playerDamagePlayer(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			final Player ply = (Player) event.getEntity();
			if(pdata.playerInMinigame(ply)){
				String minigame = pdata.getPlayersMinigame(ply);
				Minigame mgm = mdata.getMinigame(minigame);
				if(mdata.getMinigame(minigame).hasPlayers() && ply.getHealth() - event.getDamage() <= 0){
					String mgtype = mgm.getType();
					if(mgtype.equals("spleef") || mgtype.equals("lms")){
						pdata.quitMinigame(ply, true);
						event.setCancelled(true);
						if(event.getDamager() instanceof Player){
							Player att = (Player) event.getDamager();
							pdata.addPlayerKill(att);
							for(Player pl : mgm.getPlayers()){
								pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + att.getName() + " killed " + ply.getName());
							}
						}
						else if(event.getDamager() instanceof Arrow){
							Arrow arr = (Arrow) event.getDamager();
							if(arr.getShooter() instanceof Player){
								Player att = (Player) arr.getShooter();
								pdata.addPlayerKill(att);
								for(Player pl : mgm.getPlayers()){
									pl.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + att.getName() + " shot " + ply.getName());
								}
							}
						}
						ply.sendMessage(ChatColor.GRAY + "Bad Luck! Leaving the minigame.");
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								ply.setFireTicks(0);
							}
						}, 20);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(pdata.playerInMinigame(event.getEntity().getPlayer())){
			Player ply = event.getEntity().getPlayer();
			event.getDrops().clear();
			String msg = "";
			msg += event.getDeathMessage();
			event.setDeathMessage(null);
			event.setKeepLevel(true);
			event.setDroppedExp(0);
			
			pdata.addPlayerDeath(ply);
			
			String minigame = pdata.getPlayersMinigame(ply);
			if(mdata.getMinigame(minigame).hasPlayers()){
				for(Player pl : mdata.getMinigame(minigame).getPlayers()){
					pl.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + msg);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			pdata.quitMinigame(event.getPlayer(), false);
		}
	}
	
	@EventHandler
	public void onSignPlace(SignChangeEvent event){
		String[] signinfo = event.getLines();
		if((signinfo[0].equalsIgnoreCase("[minigame]") || signinfo[0].equalsIgnoreCase("[mgm]")) && event.getPlayer().hasPermission("minigame.sign.create")){
			event.setLine(0, ChatColor.DARK_BLUE + "[Minigame]");
			String minigame = "";
			for(int i = 0; i < plugin.getConfig().getStringList("minigames").size(); i++){
				if(plugin.getConfig().getStringList("minigames").get(i).equalsIgnoreCase(signinfo[2])){
					minigame = plugin.getConfig().getStringList("minigames").get(i);
				}
			}
				
			if(signinfo[1].equalsIgnoreCase("Join") && plugin.getConfig().getStringList("minigames").contains(minigame)){
				event.setLine(1, ChatColor.GREEN + "Join");
				event.setLine(2, minigame);
			}
			else if(signinfo[1].equalsIgnoreCase("Finish")){
				event.setLine(1, ChatColor.GREEN + "Finish");
			}
			else if(signinfo[1].equalsIgnoreCase("Checkpoint") && event.getBlock().getType() == Material.SIGN_POST){
				event.setLine(1, ChatColor.GREEN + "Checkpoint");
			}
			else if(signinfo[1].equalsIgnoreCase("Flag") && !signinfo[2].isEmpty()){
				event.setLine(1, ChatColor.GREEN + "Flag");
			}
			else if(signinfo[1].equalsIgnoreCase("Bet") && plugin.getConfig().getStringList("minigames").contains(minigame)){
				event.setLine(1, ChatColor.GREEN + "Bet");
				event.setLine(2, minigame);
			}
			else if(signinfo[1].equalsIgnoreCase("Quit")){
				event.setLine(1, ChatColor.GREEN + "Quit");
			}
			else {
				event.setLine(1, ChatColor.DARK_RED + signinfo[1]);
			}
		}
	}
	
	@EventHandler
	public void onSignUse(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().hasPermission("minigame.sign.use")){
			Block cblock = event.getClickedBlock();
			if(cblock.getState() instanceof Sign){
				Sign sign = (Sign) cblock.getState();
				String minigame = pdata.getPlayersMinigame(event.getPlayer());
				
				if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Finish") && pdata.playerInMinigame(event.getPlayer())){
					if(!mdata.getMinigame(minigame).getFlags().isEmpty()){
						Location loc = event.getPlayer().getLocation();
						loc.setY(loc.getY() - 1);
						if(loc.getBlock().getType() != Material.AIR){
							
							if(pdata.checkRequiredFlags(event.getPlayer(), minigame).isEmpty()){
								pdata.endMinigame(event.getPlayer());
							}
							else{
								List<String> requiredFlags = pdata.checkRequiredFlags(event.getPlayer(), minigame);
								String flags = "";
								int num = requiredFlags.size();
								
								for(int i = 0; i < num; i++){
									flags += requiredFlags.get(i);
									if(i != num - 1){
										flags += ", ";
									}
								}
								event.getPlayer().sendMessage(ChatColor.GRAY + "You still require the following flags:");
								event.getPlayer().sendMessage(ChatColor.GRAY + flags);
							}
						}
					}
					else{
						Location loc = event.getPlayer().getLocation();
						loc.setY(loc.getY() - 1);
						if(loc.getBlock().getType() != Material.AIR){
							pdata.endMinigame(event.getPlayer());
						}
					}
				}
				else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Join") && !pdata.playerInMinigame(event.getPlayer())){
					Minigame mgm = mdata.getMinigame(sign.getLine(2));
					if(mgm != null && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
						if(event.getPlayer().getItemInHand().getType() == Material.AIR && mgm.isEnabled()){
							pdata.joinMinigame(event.getPlayer(), sign.getLine(2));
						}
						else if(event.getPlayer().getItemInHand().getType() != Material.AIR){
							event.getPlayer().sendMessage(ChatColor.RED + "Your hand must be empty to join this minigame!");
						}
						else if(!mgm.isEnabled()){
							event.getPlayer().sendMessage(ChatColor.RED + "Error: This minigame is currently not enabled.");
						}
					}
					else if(mgm == null){
						event.getPlayer().sendMessage(ChatColor.RED + "Error: This minigame doesn't exist!");
					}
					else if(mgm.getUsePermissions()){
						event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
					}
				}
				else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet") && !pdata.playerInMinigame(event.getPlayer())){
					Minigame mgm = mdata.getMinigame(sign.getLine(2));
					if(mgm != null && mgm.isEnabled() && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
						pdata.joinWithBet(event.getPlayer(), sign.getLine(2));
					}
					else if(!mgm.isEnabled()){
						event.getPlayer().sendMessage(ChatColor.RED + "Error: This minigame is currently not enabled.");
					}
					else if(mgm.getUsePermissions()){
						event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
					}
					else{
						event.getPlayer().sendMessage(ChatColor.RED + "Error: This minigame doesn't exist!");
					}
				}
				else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Checkpoint") && pdata.playerInMinigame(event.getPlayer())){
					Location loc = event.getPlayer().getLocation();
					loc.setY(loc.getY() - 1);
					if(loc.getBlock().getType() != Material.AIR){
						Location newloc = event.getPlayer().getLocation();
						pdata.setPlayerCheckpoints(event.getPlayer(), newloc);
						event.getPlayer().sendMessage(ChatColor.GRAY + "Checkpoint set!");
					}
					else{
						event.getPlayer().sendMessage(ChatColor.RED + "You can not set a checkpoint here!");
					}
				}
				else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Flag") && pdata.playerInMinigame(event.getPlayer())){
					Location loc = event.getPlayer().getLocation();
					loc.setY(loc.getY() - 1);
					if(!sign.getLine(2).isEmpty() && loc.getBlock().getType() != Material.AIR){
						pdata.addPlayerFlags(event.getPlayer(), sign.getLine(2));
						event.getPlayer().sendMessage(ChatColor.GRAY + sign.getLine(2) + " flag taken!");
					}
				}
				else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Quit") && pdata.playerInMinigame(event.getPlayer())){
					pdata.quitMinigame(event.getPlayer(), false);
				}
			}
			else if(cblock.getState() instanceof Chest){
				if(mdata.hasTreasureHuntLocations()){
					for(String minigame : mdata.getAllTreasureHuntLocation()){
						if(mdata.getMinigame(minigame).getThTimer() != null){
							if(mdata.getMinigame(minigame).getThTimer().getTreasureFound() == false){
								int x1 = mdata.getTreasureHuntLocation(minigame).getBlockX();
								int x2 = cblock.getLocation().getBlockX();
								int y1 = mdata.getTreasureHuntLocation(minigame).getBlockY();
								int y2 = cblock.getLocation().getBlockY();
								int z1 = mdata.getTreasureHuntLocation(minigame).getBlockZ();
								int z2 = cblock.getLocation().getBlockZ();
								if(x2 <= x1 + 2 && x2 >= x1 - 2 && y2 <= y1 + 2 && y2 >= y1 - 2 && z2 <= z1 + 2 && z2 >= z1 - 2){
									plugin.getServer().broadcast(ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + " found the " + minigame + " treasure!", "minigame.treasure.announce");
									event.setCancelled(true);
									Chest chest = (Chest) cblock.getState();
									event.getPlayer().openInventory(chest.getInventory());
									mdata.getMinigame(minigame).getThTimer().setTreasureFound(true);
									mdata.getMinigame(minigame).getThTimer().setTimeLeft(5);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onTeleportAway(PlayerTeleportEvent event){
		if(event.getCause() == TeleportCause.COMMAND || event.getCause() == TeleportCause.PLUGIN || event.getCause() == TeleportCause.ENDER_PEARL){
			if(pdata.playerInMinigame(event.getPlayer()) && !pdata.getAllowTP(event.getPlayer())){
				Location from = event.getFrom();
				Location to = event.getTo();
				if(from.getWorld() != to.getWorld() || from.distance(to) > 2){
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "Error: You cannot teleport while in a Minigame!");
				}
			}
		}
	}
	
	@EventHandler
	public void onGMChange(PlayerGameModeChangeEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Error: You cannot change gamemode while playing a Minigame!");
		}
	}
	
	@EventHandler
	public void onFlyToggle(PlayerToggleFlightEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Error: You cannot fly while in a Minigame!");
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			String minigame = pdata.getPlayersMinigame(event.getPlayer());
			
			if(!mdata.getMinigame(minigame).getType().equals("spleef")){
				event.setCancelled(true);
			}
			else{
				if(mdata.getMinigame(minigame).getMpTimer() != null){
					if(mdata.getMinigame(minigame).getMpTimer().getStartWaitTimeLeft() != 0){
						event.setCancelled(true);
					}
					else{
						event.setCancelled(true);
						if(event.getBlock().getType() == Material.SNOW_BLOCK){
							event.getBlock().setType(Material.AIR);
						}
					}
				}
				else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void playerRevert(RevertCheckpointEvent event){
		if(pdata.playerInMinigame(event.getPlayer()) && (mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType().equals("lms") || mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType().equals("spleef") || mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType().equals("teamdm"))){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You can't revert while playing " + mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType());
		}
	}

}
