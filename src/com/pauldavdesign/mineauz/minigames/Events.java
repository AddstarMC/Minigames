package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.events.RevertCheckpointEvent;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class Events implements Listener{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity().getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			Minigame mgm = ply.getMinigame();
			if(!mgm.hasDeathDrops()){
				event.getDrops().clear();
			}
			String msg = "";
			msg = event.getDeathMessage();
			event.setDeathMessage(null);
			event.setKeepLevel(true);
			event.setDroppedExp(0);
			
			ply.addDeath();
			ply.addRevert();
			
			pdata.partyMode(ply);
			
			if(ply.getPlayer().getKiller() != null){
				MinigamePlayer killer = pdata.getMinigamePlayer(ply.getPlayer().getKiller());
				if(killer != null)
					killer.addKill();
			}
			
			if(!msg.equals("")){
				mdata.sendMinigameMessage(mgm, msg, "error", null);
			}
			if(mgm.getLives() > 0 && mgm.getLives() <= ply.getDeaths()){
				ply.sendMessage(MinigameUtils.getLang("player.quit.plyOutOfLives"), "error");
				ply.getPlayer().setHealth(2);
				if(ply.getPlayer().getLastDamageCause() != null && ply.getPlayer().getLastDamageCause().getCause() == DamageCause.FALLING_BLOCK){
					ply.getMinigame().getBlockRecorder().addBlock(ply.getPlayer().getLocation().getBlock(), null);
				}
				pdata.quitMinigame(ply, false);
			}
			else if(mgm.getLives() > 0){
				ply.sendMessage(MinigameUtils.formStr("minigame.livesLeft", mgm.getLives() - ply.getDeaths()), null);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerSpawn(PlayerRespawnEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isRequiredQuit()){
			ply.restorePlayerData();
			event.setRespawnLocation(ply.getQuitPos());
			
			ply.setRequiredQuit(false);
			ply.setQuitPos(null);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void playerDropItem(PlayerDropItemEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			Minigame mgm = pdata.getMinigamePlayer(event.getPlayer()).getMinigame();
			if(!mgm.hasItemDrops() || 
					mgm.isSpectator(pdata.getMinigamePlayer(event.getPlayer()))){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void itemPickup(PlayerPickupItemEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame()){
			Minigame mgm = ply.getMinigame();
			if(!mgm.hasItemPickup() || 
					mgm.isSpectator(ply)){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame()){
			if(ply.getPlayer().isDead())
				pdata.addOfflineMinigamePlayer(pdata.getMinigamePlayer(event.getPlayer()));
			else{
				ply.restorePlayerData();
				pdata.minigameTeleport(ply, ply.getMinigame().getQuitPosition());
			}
			pdata.quitMinigame(pdata.getMinigamePlayer(event.getPlayer()), false);
		}
		else if(ply.hasStoredData()){
			pdata.addOfflineMinigamePlayer(pdata.getMinigamePlayer(event.getPlayer()));
		}
		if(ply.isRequiredQuit()){
			pdata.addOfflineMinigamePlayer(pdata.getMinigamePlayer(event.getPlayer()));
		}
		
		pdata.removeMinigamePlayer(event.getPlayer());
		
		if(Bukkit.getServer().getOnlinePlayers().length == 1){
			for(String mgm : mdata.getAllMinigames().keySet()){
				if(mdata.getMinigame(mgm).getType() == MinigameType.TREASURE_HUNT){
					if(mdata.getMinigame(mgm).getThTimer() != null){
						mdata.getMinigame(mgm).getThTimer().pauseTimer(true);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerConnect(PlayerJoinEvent event){
		pdata.addMinigamePlayer(event.getPlayer());
		if(event.getPlayer().isOp() && plugin.getConfig().getBoolean("updateChecker")){
			long nextCheck = plugin.getLastUpdateCheck() + 86400000;
			if(nextCheck <= Calendar.getInstance().getTimeInMillis()){
				UpdateChecker check = new UpdateChecker(event.getPlayer());
				check.start();
				plugin.setLastUpdateCheck(Calendar.getInstance().getTimeInMillis());
			}
		}
		if(pdata.hasOfflineMinigamePlayer(event.getPlayer().getName())){
			final Player ply = event.getPlayer();
			OfflineMinigamePlayer oply = pdata.getOfflineMinigamePlayer(event.getPlayer().getName());
			Location loc = oply.getLoginLocation();
			oply.restoreOfflineMinigamePlayer();
			pdata.removeOfflineMinigamePlayer(event.getPlayer().getName());
			
			final Location floc = loc;
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					ply.teleport(floc);
					ply.setFireTicks(0);
				}
			}, 5L);
			
			final MinigamePlayer fply = pdata.getMinigamePlayer(event.getPlayer());
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					fply.restorePlayerData();
				}
			});
			
			plugin.getLogger().info(ply.getName() + "'s data has been restored from file.");
			
		}
		
		if(Bukkit.getServer().getOnlinePlayers().length == 1){
			for(String mgm : mdata.getAllMinigames().keySet()){
				if(mdata.getMinigame(mgm).getType() == MinigameType.TREASURE_HUNT){
					if(mdata.getMinigame(mgm).getThTimer() != null){
						mdata.getMinigame(mgm).getThTimer().pauseTimer(false);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void playerInterract(PlayerInteractEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
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
					if((sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Join") || sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet")) && !ply.isInMinigame()){
						Minigame mgm = mdata.getMinigame(sign.getLine(2));
						if(mgm != null && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
							if(!mgm.isEnabled()){
								event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.notEnabled"));
							}
							else{
								event.getPlayer().sendMessage(ChatColor.GREEN + MinigameUtils.getLang("minigame.info.description"));
								String status = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.status.title");
								if(!mgm.hasPlayers()){
									status += " " + ChatColor.GREEN + MinigameUtils.getLang("minigame.info.status.empty");
								}
								else if(mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() > 0){
									status += " " + ChatColor.GREEN + MinigameUtils.getLang("minigame.info.status.waitingForPlayers");
								}
								else{
									status += " " + ChatColor.RED + MinigameUtils.getLang("minigame.info.status.started");
								}
								
								if(mgm.getType() != MinigameType.SINGLEPLAYER){
									event.getPlayer().sendMessage(status);
									if(mgm.canLateJoin())
										event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.lateJoin.msg") + " " + ChatColor.GREEN + MinigameUtils.getLang("minigame.info.lateJoin.enabled"));
									else
										event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.lateJoin.msg") + " " + ChatColor.RED + MinigameUtils.getLang("minigame.info.lateJoin.disabled"));
								}
								
								if(mgm.getMinigameTimer() != null){
									event.getPlayer().sendMessage(ChatColor.AQUA + "Time left: " + MinigameUtils.convertTime(mgm.getMinigameTimer().getTimeLeft()));
								}
								
								if(mgm.getType() == MinigameType.TEAMS){
									event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.score") + MinigameUtils.formStr("player.end.team.score", ChatColor.RED.toString() + mgm.getRedTeamScore() + ChatColor.WHITE, ChatColor.BLUE.toString() + mgm.getBlueTeamScore()));
								}
								
								String playerCount = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.playerCount") + " " + ChatColor.GRAY;
								String players = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.players.msg") + " ";
								
								if(mgm.hasPlayers()){
									playerCount += mgm.getPlayers().size() ;
									if(mgm.getType() != MinigameType.SINGLEPLAYER){
										playerCount += "/" + mgm.getMaxPlayers();
									}
									
									List<String> plyList = new ArrayList<String>();
									for(MinigamePlayer pl : mgm.getPlayers()){
										plyList.add(pl.getName());
									}
									players += MinigameUtils.listToString(plyList);
								}
								else{
									playerCount += "0";
									
									if(mgm.getType() != MinigameType.SINGLEPLAYER){
										playerCount += "/" + mgm.getMaxPlayers();
									}
									
									players += ChatColor.GRAY + MinigameUtils.getLang("minigame.info.players.none");
								}
								
								event.getPlayer().sendMessage(playerCount);
								event.getPlayer().sendMessage(players);
							}
						}
						else if(mgm == null){
							event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noMinigame"));
						}
						else if(mgm.getUsePermissions()){
							event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName().toLowerCase()));
						}
					}
				}	
			}
		}
		
		ItemStack item = event.getItem();
		if(item != null && MinigameUtils.isMinigameTool(item) && ply.getPlayer().hasPermission("minigame.tool")){
			MinigameTool tool = new MinigameTool(item);
			event.setCancelled(true);

			if(event.getPlayer().isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
				tool.openMenu(ply);
				event.setCancelled(true);
			}
			else if(tool.getMode() != null && tool.getMinigame() != null){
				Minigame mg = tool.getMinigame();
				if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
					if(tool.getMode() == MinigameToolMode.START && ply.getPlayer().hasPermission("minigame.set.start")){
						if(!tool.getTeam().equals("none")){
							if(tool.getTeam().equals("red")){
								mg.addStartLocationRed(ply.getPlayer().getLocation());
								ply.sendMessage("Added " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " start location to " + mg.getName(), null);
							}
							else{
								mg.addStartLocationRed(ply.getPlayer().getLocation());
								ply.sendMessage("Added " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " start location to " + mg.getName(), null);
							}
						}
						else{
							mg.addStartLocation(ply.getPlayer().getLocation());
							ply.sendMessage("Added start location to " + mg.getName(), null);
						}
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && ply.getPlayer().hasPermission("minigame.set.quit")){
						mg.setQuitPosition(ply.getPlayer().getLocation());
						ply.sendMessage("Set quit location for " + mg.getName(), null);
					}
					else if(tool.getMode() == MinigameToolMode.END && ply.getPlayer().hasPermission("minigame.set.end")){
						mg.setEndPosition(ply.getPlayer().getLocation());
						ply.sendMessage("Set end location for " + mg.getName(), null);
					}
					else if(tool.getMode() == MinigameToolMode.LOBBY && ply.getPlayer().hasPermission("minigame.set.lobby")){
						mg.setLobbyPosition(ply.getPlayer().getLocation());
						ply.sendMessage("Set lobby location for " + mg.getName(), null);
					}
					else if(tool.getMode() == MinigameToolMode.RESTORE_BLOCK && ply.getPlayer().hasPermission("minigame.set.restoreblock")){
						RestoreBlock resblock = new RestoreBlock(MinigameUtils.createLocationID(event.getClickedBlock().getLocation()), event.getMaterial(), event.getClickedBlock().getLocation());
						mg.addRestoreBlock(resblock);
						ply.sendMessage("Added restore block to " + mg.getName(), null);
					}
					else if(event.getAction() == Action.RIGHT_CLICK_BLOCK && tool.getMode() != null){
						ply.addSelectionPoint(event.getClickedBlock().getLocation());
					}
				}
				else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
					if(tool.getMode() == MinigameToolMode.DEGEN_AREA && ply.getPlayer().hasPermission("minigame.set.floordegenerator")){
						if(ply.hasSelection()){
							mg.setFloorDegen1(ply.getSelectionPoints()[0]);
							mg.setFloorDegen2(ply.getSelectionPoints()[1]);
							ply.sendMessage("Set floor degenerator area for " + mg.getName(), null);
							ply.showSelection(true);
							ply.clearSelection();
						}
					}
					else if(tool.getMode() == MinigameToolMode.REGEN_AREA && ply.getPlayer().hasPermission("minigame.set.regenarea")){
						if(ply.hasSelection()){
							mg.setRegenArea1(ply.getSelectionPoints()[0]);
							mg.setRegenArea2(ply.getSelectionPoints()[1]);
							ply.sendMessage("Set regeneration area for " + mg.getName(), null);
							ply.showSelection(true);
							ply.clearSelection();
						}
					}
					else if(event.getAction() == Action.LEFT_CLICK_BLOCK && 
							tool.getMode() == MinigameToolMode.START && ply.getPlayer().hasPermission("minigame.set.start")){
						int x = event.getClickedBlock().getLocation().getBlockX();
						int y = event.getClickedBlock().getLocation().getBlockY();
						int z = event.getClickedBlock().getLocation().getBlockZ();
						String world = event.getClickedBlock().getLocation().getWorld().getName();
						
						int nx;
						int ny;
						int nz;
						String nworld;
						Location delLoc = null;
						if(!tool.getTeam().equals("none")){
							if(tool.getTeam().equals("red")){
								for(Location loc : mg.getStartLocationsRed()){
									nx = loc.getBlockX();
									ny = loc.getBlockY();
									nz = loc.getBlockZ();
									nworld = loc.getWorld().getName();
									
									if(x == nx && y == ny && z == nz && world.equals(nworld)){
										delLoc = loc;
										break;
									}
								}
								if(delLoc != null){
									mg.getStartLocationsRed().remove(delLoc);
									ply.sendMessage("Removed selected " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " start location.", null);
								}
								else
									ply.sendMessage("Could not find a " + ChatColor.RED + "Red Team" + ChatColor.WHITE + " start location at that point.", "error");
							}
							else if(tool.getTeam().equals("blue")){
								for(Location loc : mg.getStartLocationsBlue()){
									nx = loc.getBlockX();
									ny = loc.getBlockY();
									nz = loc.getBlockZ();
									nworld = loc.getWorld().getName();
									
									if(x == nx && y == ny && z == nz && world.equals(nworld)){
										delLoc = loc;
										break;
									}
								}
								if(delLoc != null){
									mg.getStartLocationsBlue().remove(delLoc);
									ply.sendMessage("Removed selected " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " start location.", null);
								}
								else
									ply.sendMessage("Could not find a " + ChatColor.BLUE + "Blue Team" + ChatColor.WHITE + " start location at that point.", "error");
							}
						}
						else{
							for(Location loc : mg.getStartLocations()){
								nx = loc.getBlockX();
								ny = loc.getBlockY();
								nz = loc.getBlockZ();
								nworld = loc.getWorld().getName();
								
								if(x == nx && y == ny && z == nz && world.equals(nworld)){
									delLoc = loc;
									break;
								}
							}
							if(delLoc != null){
								mg.getStartLocations().remove(delLoc);
								ply.sendMessage("Removed selected start location.", null);
							}
							else
								ply.sendMessage("Could not find a start location at that point.", "error");
						}
					}
					else if(event.getAction() == Action.LEFT_CLICK_BLOCK && 
							tool.getMode() == MinigameToolMode.RESTORE_BLOCK && ply.getPlayer().hasPermission("minigame.set.restoreblock")){
						if(mg.getRestoreBlocks().containsKey(MinigameUtils.createLocationID(event.getClickedBlock().getLocation()))){
							mg.removeRestoreBlock(MinigameUtils.createLocationID(event.getClickedBlock().getLocation()));
							ply.sendMessage("Removed selected restore block from " + mg.getName(), null);
						}
					}
				}
			}
		}
		
		//Spectator disables:
		if(ply.isInMinigame() && pdata.getMinigamePlayer(event.getPlayer()).getMinigame().isSpectator(pdata.getMinigamePlayer(event.getPlayer()))){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTeleportAway(PlayerTeleportEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && (event.getCause() == TeleportCause.COMMAND || event.getCause() == TeleportCause.PLUGIN || (!ply.getMinigame().isAllowedEnderpearls() && event.getCause() == TeleportCause.ENDER_PEARL))){
			if(!ply.getAllowTeleport()){
				Location from = event.getFrom();
				Location to = event.getTo();
				if(from.getWorld() != to.getWorld() || from.distance(to) > 2){
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noTeleport"));
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onGMChange(PlayerGameModeChangeEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply != null){
			if(ply.isInMinigame() && !ply.getAllowGamemodeChange()){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noGamemode"));
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onFlyToggle(PlayerToggleFlightEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame() && (!ply.getMinigame().isSpectator(ply) || !ply.getMinigame().canSpectateFly())){
			event.setCancelled(true);
			pdata.quitMinigame(ply, true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noFly"));
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void playerRevert(RevertCheckpointEvent event){
		if(event.getMinigamePlayer().isInMinigame() && 
				(event.getMinigamePlayer().getMinigame().getType() == MinigameType.FREE_FOR_ALL || 
				event.getMinigamePlayer().getMinigame().getType() == MinigameType.TEAMS) && 
				!event.getMinigamePlayer().getMinigame().isAllowedMPCheckpoints()){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noRevert", event.getMinigamePlayer().getMinigame().getType().getName()));
		}
		else if(event.getMinigamePlayer().getMinigame().getMpTimer() != null && 
				event.getMinigamePlayer().getMinigame().getMpTimer().getStartWaitTimeLeft() != 0){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void commandExecute(PlayerCommandPreprocessEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame()){
			for(String comd : pdata.getDeniedCommands()){
				if(event.getMessage().contains(comd)){
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noCommand"));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void paintballHit(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Snowball){
			MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getEntity());
			if(ply == null) return;
			Snowball sb = (Snowball) event.getDamager();
			if(ply.isInMinigame() && ply.getMinigame().hasPaintBallMode()){
				if(sb.getShooter() instanceof Player){
					MinigamePlayer shooter = pdata.getMinigamePlayer((Player) sb.getShooter());
					Minigame mgm = ply.getMinigame();
					if(shooter == null) return;
					if(shooter.isInMinigame() && shooter.getMinigame().equals(ply.getMinigame())){
						int plyTeam = -1;
						int atcTeam = -2;
						if(mgm.getType() == MinigameType.TEAMS){
							plyTeam = 0;
							if(mgm.getBlueTeam().contains(ply.getPlayer())){
								plyTeam = 1;
							}
							atcTeam = 0;
							if(mgm.getBlueTeam().contains(shooter.getPlayer())){
								atcTeam = 1;
							}
						}
						if(plyTeam != atcTeam){
							int damage = mgm.getPaintBallDamage();
							event.setDamage(damage);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void playerShoot(ProjectileLaunchEvent event){
		if(event.getEntityType() == EntityType.SNOWBALL){
			Snowball snowball = (Snowball) event.getEntity();
			if(snowball.getShooter() != null && snowball.getShooter() instanceof Player){
				MinigamePlayer ply = pdata.getMinigamePlayer((Player) snowball.getShooter());
				if(ply == null) return;
				if(ply.isInMinigame() && ply.getMinigame().hasUnlimitedAmmo()){
					ply.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL));
				}
			}
		}
		else if(event.getEntityType() == EntityType.EGG){
			Egg egg = (Egg) event.getEntity();
			if(egg.getShooter() != null && egg.getShooter() instanceof Player){
				MinigamePlayer ply = pdata.getMinigamePlayer((Player) egg.getShooter());
				if(ply == null) return;
				if(ply.isInMinigame() && ply.getMinigame().hasUnlimitedAmmo()){
					ply.getPlayer().getInventory().addItem(new ItemStack(Material.EGG));
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void playerHurt(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getEntity());
			if(ply == null) return;
			if(ply.isInMinigame()){
				Minigame mgm = ply.getMinigame();
				if(mgm.isSpectator(ply)){
					event.setCancelled(true);
				}
				else if(event.getCause() == DamageCause.FALL && 
						ply.getLoadout() != null && !ply.getLoadout().hasFallDamage()){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void spectatorAttack(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getDamager());
			if(ply == null) return;
			if(ply.isInMinigame() && ply.getMinigame().isSpectator(ply)){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void clickMenu(InventoryClickEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer((Player)event.getWhoClicked());
		if(ply.isInMenu()){
			if(event.getRawSlot() < ply.getMenu().getSize()){
				if(!ply.getMenu().getAllowModify() || ply.getMenu().hasMenuItem(event.getRawSlot()))
					event.setCancelled(true);
				
				MenuItem item = ply.getMenu().getClicked(event.getRawSlot());
				if(item != null){
					ItemStack disItem = null;
					if(event.getClick() == ClickType.LEFT){
						if(event.getCursor().getType() != Material.AIR)
							disItem = item.onClickWithItem(event.getCursor());
						else
							disItem = item.onClick();
					}
					else if(event.getClick() == ClickType.RIGHT)
						disItem = item.onRightClick();
					else if(event.getClick() == ClickType.SHIFT_LEFT)
						disItem = item.onShiftClick();
					else if(event.getClick() == ClickType.SHIFT_RIGHT)
						disItem = item.onShiftRightClick();
					else if(event.getClick() == ClickType.DOUBLE_CLICK)
						disItem = item.onDoubleClick();
					
					if(item != null)
						event.setCurrentItem(disItem);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void dragMenu(InventoryDragEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer((Player)event.getWhoClicked());
		if(ply.isInMenu()){
			if(!ply.getMenu().getAllowModify()){
				for(int slot : event.getRawSlots()){
					if(slot < ply.getMenu().getSize()){
						event.setCancelled(true);
						break;
					}
				}
			}
			else{
				Set<Integer> slots = new HashSet<Integer>();
				slots.addAll(event.getRawSlots());
				
				for(int slot : slots){
					if(ply.getMenu().hasMenuItem(slot)){
						event.getRawSlots().remove(slot);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void closeMenu(InventoryCloseEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer((Player)event.getPlayer());
		if(ply == null) return;
		
		if(ply.isInMenu() && !ply.getNoClose()){
			ply.setMenu(null);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void manualItemEntry(AsyncPlayerChatEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMenu() && ply.getNoClose() && ply.getManualEntry() != null){
			event.setCancelled(true);
			ply.setNoClose(false);
			ply.getManualEntry().checkValidEntry(event.getMessage());
			ply.setManualEntry(null);
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	private void playerHungry(FoodLevelChangeEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer((Player)event.getEntity());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getLoadout() != null && 
				!ply.getLoadout().hasHunger()){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void blockBreak(BlockBreakEvent event){
		if(event.getBlock().hasMetadata("MGScoreboardSign")){
			if(event.getPlayer().hasPermission("minigame.sign.scoreboard.create")){
				Minigame mg = mdata.getMinigame(event.getBlock().getMetadata("Minigame").get(0).asString());
				if(mg != null){
					mg.getScoreboardData().removeDisplay(MinigameUtils.createLocationID(event.getBlock().getLocation()));
				}
			}
			else{
				event.setCancelled(true);
			}
		}
	}
}
