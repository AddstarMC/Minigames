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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
			pdata.addOfflineMinigamePlayer(pdata.getMinigamePlayer(event.getPlayer()));
			pdata.quitMinigame(pdata.getMinigamePlayer(event.getPlayer()), false);
		}
		if(ply.isRequiredQuit()){
			pdata.addOfflineMinigamePlayer(pdata.getMinigamePlayer(event.getPlayer()));
		}
		
		pdata.removeMinigamePlayer(event.getPlayer());
		
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
			//Location loc = pdata.getDCPlayer(event.getPlayer());
			Location loc = oply.getLoginLocation();
			oply.restoreOfflineMinigamePlayer();
			pdata.removeOfflineMinigamePlayer(event.getPlayer().getName());
			
			//pdata.removeDCPlayer(event.getPlayer());
			/*plugin.getLogger().info("--------------------------DEBUG--------------------------");
			if(ply != null){
				plugin.getLogger().info("Player: " + ply.getName());
				if(loc == null){
					plugin.getLogger().info("Location: NO WHERE TO TELEPORT, ITS NULL! (Teleported them to spawn for safety!)");
					loc = plugin.getServer().getWorld("world").getSpawnLocation();
				}
				else
					plugin.getLogger().info("Location: X:" + loc.getBlockX() + ", Y:" + loc.getBlockY() + ", Z:" + loc.getBlockZ() + ", world:" + loc.getWorld().getName());
			}
			else
				plugin.getLogger().info("Player: OMG ITS NULL!!! D:");*/
			
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
			
			plugin.getLogger().info("--------------------------DEBUG--------------------------");
			plugin.getLogger().info("Player: " + ply.getName());
			plugin.getLogger().info("This player has had Minigame data restored. Please verify if they were in a Minigame before they quit.");
			
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
								
								if(!mgm.getType().equals("sp")){
									event.getPlayer().sendMessage(status);
									if(mgm.canLateJoin())
										event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.lateJoin.msg") + " " + ChatColor.GREEN + MinigameUtils.getLang("minigame.info.lateJoin.enabled"));
									else
										event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.lateJoin.msg") + " " + ChatColor.RED + MinigameUtils.getLang("minigame.info.lateJoin.disabled"));
								}
								
								if(mgm.getMinigameTimer() != null){
									event.getPlayer().sendMessage(ChatColor.AQUA + "Time left: " + MinigameUtils.convertTime(mgm.getMinigameTimer().getTimeLeft()));
								}
								
								if(mgm.getType().equals("teamdm")){
									event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.score") + MinigameUtils.formStr("player.end.team.score", ChatColor.RED.toString() + mgm.getRedTeamScore() + ChatColor.WHITE, ChatColor.BLUE.toString() + mgm.getBlueTeamScore()));
								}
								
								String playerCount = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.playerCount") + " " + ChatColor.GRAY;
								String players = ChatColor.AQUA + MinigameUtils.getLang("minigame.info.players.msg") + " ";
								
								if(mgm.hasPlayers()){
									playerCount += mgm.getPlayers().size() ;
									if(!mgm.getType().equals("sp")){
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
									
									if(!mgm.getType().equals("sp")){
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
		if(ply.isInMinigame() && !ply.getAllowGamemodeChange()){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noGamemode"));
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
		if(event.getMinigamePlayer().isInMinigame() && (event.getMinigamePlayer().getMinigame().getType().equals("lms") || event.getMinigamePlayer().getMinigame().getType().equals("dm") || event.getMinigamePlayer().getMinigame().getType().equals("teamdm"))){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noRevert", event.getMinigamePlayer().getMinigame().getType()));
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
						if(mgm.getType().equals("teamdm")){
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
}
