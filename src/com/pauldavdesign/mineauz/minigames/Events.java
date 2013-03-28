package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.events.RevertCheckpointEvent;

public class Events implements Listener{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(pdata.playerInMinigame(event.getEntity().getPlayer())){
			final Player ply = event.getEntity().getPlayer();
			if(!mdata.getMinigame(pdata.getPlayersMinigame(ply)).hasDeathDrops()){
				event.getDrops().clear();
			}
			String msg = "";
			msg = event.getDeathMessage();
			event.setDeathMessage(null);
			event.setKeepLevel(true);
			event.setDroppedExp(0);
			
			pdata.addPlayerDeath(ply);
			
			pdata.partyMode(ply);
			
			String minigame = pdata.getPlayersMinigame(ply);
			if(mdata.getMinigame(minigame).hasPlayers()){
				mdata.sendMinigameMessage(mdata.getMinigame(minigame), msg, "error", null);
			}
			Minigame mgm = mdata.getMinigame(minigame);
			if(mgm.getLives() > 0 && mgm.getLives() <= pdata.getPlayerDeath(ply)){
				ply.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Bad Luck! Leaving the minigame.");
				ply.setHealth(2);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						ply.setFireTicks(0);
						pdata.quitMinigame(ply, false);
					}
				});
			}
			else if(mgm.getLives() > 0){
				ply.sendMessage(ChatColor.AQUA + "[Minigame] " + ChatColor.WHITE + "Lives left: " + (mgm.getLives() - pdata.getPlayerDeath(ply)));
			}
		}
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			if(!mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).hasItemDrops() || 
					mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).isSpectator(event.getPlayer())){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void itemPickup(PlayerPickupItemEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			if(!mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).hasItemPickup() || 
					mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).isSpectator(event.getPlayer())){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			pdata.addDCPlayer(event.getPlayer(), mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getQuitPosition());
			pdata.quitMinigame(event.getPlayer(), false);
		}
		
		if(Bukkit.getServer().getOnlinePlayers().length == 1){
			for(String mgm : mdata.getAllMinigames().keySet()){
				if(mdata.getMinigame(mgm).getType().equals("th")){
					if(mdata.getMinigame(mgm).getThTimer() != null){
						mdata.getMinigame(mgm).getThTimer().pauseTimer(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerConnect(PlayerJoinEvent event){
		if(event.getPlayer().isOp() && plugin.getConfig().getBoolean("updateChecker")){
			long nextCheck = plugin.getLastUpdateCheck() + 86400000;
			if(nextCheck <= Calendar.getInstance().getTimeInMillis()){
				UpdateChecker check = new UpdateChecker(event.getPlayer());
				check.start();
				plugin.setLastUpdateCheck(Calendar.getInstance().getTimeInMillis());
			}
		}
		if(pdata.hasDCPlayer(event.getPlayer())){
			final Player ply = event.getPlayer();
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					ply.teleport(pdata.getDCPlayer(ply));
					pdata.removeDCPlayer(ply);
				}
			});
		}
		
		if(pdata.playerHasStoredItems(event.getPlayer())){
			pdata.restorePlayerData(event.getPlayer());
		}
		
		if(Bukkit.getServer().getOnlinePlayers().length == 1){
			for(String mgm : mdata.getAllMinigames().keySet()){
				if(mdata.getMinigame(mgm).getType().equals("th")){
					if(mdata.getMinigame(mgm).getThTimer() != null){
						mdata.getMinigame(mgm).getThTimer().pauseTimer(false);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void playerInterract(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Block cblock = event.getClickedBlock();
			if(cblock.getState() instanceof Chest){
				if(mdata.hasTreasureHuntLocations()){
					for(String minigame : mdata.getAllTreasureHuntLocation()){
						if(mdata.getMinigame(minigame).getThTimer() != null){
							if(mdata.getMinigame(minigame).getThTimer().getTreasureFound() == false && mdata.getMinigame(minigame).getThTimer().getChestInWorld()){
								int x1 = mdata.getTreasureHuntLocation(minigame).getBlockX();
								int x2 = cblock.getLocation().getBlockX();
								int y1 = mdata.getTreasureHuntLocation(minigame).getBlockY();
								int y2 = cblock.getLocation().getBlockY();
								int z1 = mdata.getTreasureHuntLocation(minigame).getBlockZ();
								int z2 = cblock.getLocation().getBlockZ();
								if(x2 == x1 && y2 == y1 && z2 == z1){
									plugin.getServer().broadcast(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + event.getPlayer().getName() + " found the " + minigame + " treasure!", "minigame.treasure.announce");
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
		else if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().hasPermission("minigame.sign.use.details")){
			Block cblock = event.getClickedBlock();
			if(cblock.getState() instanceof Sign){
				Sign sign = (Sign) cblock.getState();
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Minigame]")){
					if((sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Join") || sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet")) && !pdata.playerInMinigame(event.getPlayer())){
						Minigame mgm = mdata.getMinigame(sign.getLine(2));
						if(mgm != null && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
							if(!mgm.isEnabled()){
								event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This minigame is currently not enabled.");
							}
							else{
								event.getPlayer().sendMessage(ChatColor.GREEN + "------------------Minigame Info------------------");
								String status = ChatColor.AQUA + "Status: ";
								if(!mgm.hasPlayers()){
									status += ChatColor.GREEN + "Empty";
								}
								else if(mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() > 0){
									status += ChatColor.GREEN + "Waiting for Players";
								}
								else{
									status += ChatColor.RED + "Started";
								}
								
								if(!mgm.getType().equals("sp"))
									event.getPlayer().sendMessage(status);
								

								if(mgm.canLateJoin())
									event.getPlayer().sendMessage(ChatColor.AQUA + "Late join: " + ChatColor.WHITE + "enabled");
								
								if(mgm.getMinigameTimer() != null){
									event.getPlayer().sendMessage(ChatColor.AQUA + "Time left: " + MinigameUtils.convertTime(mgm.getMinigameTimer().getTimeLeft()));
								}
								
								if(mgm.getType().equals("teamdm")){
									event.getPlayer().sendMessage(ChatColor.AQUA + "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
								}
								
								String playerCount = ChatColor.AQUA + "Player Count: " + ChatColor.GRAY;
								String players = ChatColor.AQUA + "Players: ";
								
								if(mgm.hasPlayers()){
									playerCount += mgm.getPlayers().size() ;
									if(!mgm.getType().equals("sp")){
										playerCount += "/" + mgm.getMaxPlayers();
									}
									
									List<String> plyList = new ArrayList<String>();
									for(Player ply : mgm.getPlayers()){
										plyList.add(ply.getName());
									}
									players += MinigameUtils.listToString(plyList);
								}
								else{
									playerCount += "0";
									
									if(!mgm.getType().equals("sp")){
										playerCount += "/" + mgm.getMaxPlayers();
									}
									
									players += ChatColor.GRAY + "None";
								}
								
								event.getPlayer().sendMessage(playerCount);
								event.getPlayer().sendMessage(players);
							}
						}
						else if(mgm == null){
							event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "This minigame doesn't exist!");
						}
						else if(mgm.getUsePermissions()){
							event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
						}
					}
				}	
			}
		}
		
		//Spectator disables:
		if(pdata.playerInMinigame(event.getPlayer()) && mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).isSpectator(event.getPlayer())){
			event.setCancelled(true);
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
					event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You cannot teleport while in a Minigame!");
				}
			}
		}
	}
	
	@EventHandler
	public void onGMChange(PlayerGameModeChangeEvent event){
		if(pdata.playerInMinigame(event.getPlayer()) && !pdata.getAllowGMChange(event.getPlayer())){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You cannot change gamemode while playing a Minigame!");
		}
	}
	
	@EventHandler
	public void onFlyToggle(PlayerToggleFlightEvent event){
		if(pdata.playerInMinigame(event.getPlayer()) && (!mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).isSpectator(event.getPlayer()) || !mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).canSpectateFly())){
			event.setCancelled(true);
			pdata.quitMinigame(event.getPlayer(), true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Error: You cannot fly while in a Minigame!");
		}
	}
	
	@EventHandler
	public void playerRevert(RevertCheckpointEvent event){
		if(pdata.playerInMinigame(event.getPlayer()) && (mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType().equals("lms") || mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType().equals("dm") || mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType().equals("teamdm"))){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You can't revert while playing " + mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType());
		}
	}
	
	@EventHandler
	private void commandExecute(PlayerCommandPreprocessEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			for(String comd : pdata.getDeniedCommands()){
				if(event.getMessage().contains(comd)){
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You are not allowed to use that command while playing a Minigame!");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void paintballHit(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Snowball){
			Player ply = (Player) event.getEntity();
			Snowball sb = (Snowball) event.getDamager();
			if(pdata.playerInMinigame(ply) && mdata.getMinigame(pdata.getPlayersMinigame(ply)).hasPaintBallMode()){
				if(sb.getShooter() instanceof Player){
					Player shooter = (Player) sb.getShooter();
					Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
					if(pdata.playerInMinigame(shooter) && pdata.getPlayersMinigame(shooter).equals(pdata.getPlayersMinigame(ply))){
						int plyTeam = -1;
						int atcTeam = -2;
						if(mgm.getType().equals("teamdm")){
							plyTeam = 0;
							if(mgm.getBlueTeam().contains(ply)){
								plyTeam = 1;
							}
							atcTeam = 0;
							if(mgm.getBlueTeam().contains(shooter)){
								atcTeam = 1;
							}
						}
						if(plyTeam != atcTeam){
							int damage = mdata.getMinigame(pdata.getPlayersMinigame(ply)).getPaintBallDamage();
							event.setDamage(damage);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void playerShoot(ProjectileLaunchEvent event){
		if(event.getEntityType() == EntityType.SNOWBALL){
			Snowball snowball = (Snowball) event.getEntity();
			if(snowball.getShooter() != null && snowball.getShooter() instanceof Player){
				Player ply = (Player) snowball.getShooter();
				if(pdata.playerInMinigame(ply) && mdata.getMinigame(pdata.getPlayersMinigame(ply)).hasUnlimitedAmmo()){
					ply.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
				}
			}
		}
		else if(event.getEntityType() == EntityType.EGG){
			Egg egg = (Egg) event.getEntity();
			if(egg.getShooter() != null && egg.getShooter() instanceof Player){
				Player ply = (Player) egg.getShooter();
				if(pdata.playerInMinigame(ply) && mdata.getMinigame(pdata.getPlayersMinigame(ply)).hasUnlimitedAmmo()){
					ply.getInventory().addItem(new ItemStack(Material.EGG));
				}
			}
		}
	}
	
	@EventHandler
	private void playerHurt(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player ply = (Player) event.getEntity();
			if(pdata.playerInMinigame(ply)){
				Minigame mgm = mdata.getMinigame(pdata.getPlayersMinigame(ply));
				if(mgm.isSpectator(ply)){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void spectatorAttack(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			Player ply = (Player) event.getDamager();
			if(pdata.playerInMinigame(ply) && mdata.getMinigame(pdata.getPlayersMinigame(ply)).isSpectator(ply)){
				event.setCancelled(true);
			}
		}
	}
}
