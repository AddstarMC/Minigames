package au.com.mineauz.minigames;

import java.io.File;
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
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.events.RevertCheckpointEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.GameOverModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.modules.WeatherTimeModule;
import au.com.mineauz.minigames.tool.MinigameTool;

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
			event.setDeathMessage("");
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
			if(mgm.getState() == MinigameState.STARTED){
				if(mgm.getLives() > 0 && mgm.getLives() <= ply.getDeaths()){
					ply.sendMessage(MinigameUtils.getLang("player.quit.plyOutOfLives"), "error");
					if(!event.getDrops().isEmpty() && mgm.getPlayers().size() == 1){
						event.getDrops().clear();
					}
					pdata.quitMinigame(ply, false);
				}
				else if(mgm.getLives() > 0){
					ply.sendMessage(MinigameUtils.formStr("minigame.livesLeft", mgm.getLives() - ply.getDeaths()), null);
				}
			}
			else if(mgm.getState() == MinigameState.ENDED){
				plugin.pdata.quitMinigame(ply, true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void playerSpawn(PlayerRespawnEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			final WeatherTimeModule mod = WeatherTimeModule.getMinigameModule(ply.getMinigame());
			if(mod.isUsingCustomWeather()){
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						ply.getPlayer().setPlayerWeather(mod.getCustomWeather());
					}
				});
			}
			
			if(ply.getMinigame().getState() == MinigameState.ENDED){
				plugin.pdata.quitMinigame(ply, true);
			}
		}
		if(ply.isRequiredQuit()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					ply.restorePlayerData();
				}
			});
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
			if(ply.getPlayer().isDead()){
				ply.getOfflineMinigamePlayer().setLoginLocation(ply.getMinigame().getQuitPosition());
				ply.getOfflineMinigamePlayer().savePlayerData();
			}
			pdata.quitMinigame(pdata.getMinigamePlayer(event.getPlayer()), false);
		}
		else if(ply.isRequiredQuit()){
			ply.getOfflineMinigamePlayer().setLoginLocation(ply.getQuitPos());
			ply.getOfflineMinigamePlayer().savePlayerData();
		}
		
		pdata.removeMinigamePlayer(event.getPlayer());
		plugin.display.removeAll(event.getPlayer());
		
		if(Bukkit.getServer().getOnlinePlayers().size() == 1){
			for(String mgm : mdata.getAllMinigames().keySet()){
				if(mdata.getMinigame(mgm).getType() == MinigameType.GLOBAL){
//					if(mdata.getMinigame(mgm).getThTimer() != null){
//						mdata.getMinigame(mgm).getThTimer().pauseTimer(true);
//					}
					if(mdata.getMinigame(mgm).getMinigameTimer() != null)
						mdata.getMinigame(mgm).getMinigameTimer().stopTimer();
				}
			}
		}
		ply.saveClaimedRewards();
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
		
		File pldata = new File(plugin.getDataFolder() + "/playerdata/inventories/" + event.getPlayer().getUniqueId().toString() + ".yml");
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(pldata.exists()){
			ply.setOfflineMinigamePlayer(new OfflineMinigamePlayer(event.getPlayer().getUniqueId().toString()));
			Location floc = ply.getOfflineMinigamePlayer().getLoginLocation();
			ply.setRequiredQuit(true);
			ply.setQuitPos(floc);
			
			if(!ply.getPlayer().isDead() && ply.isRequiredQuit()){
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						ply.restorePlayerData();
					}
				});
				ply.teleport(ply.getQuitPos());
				
				ply.setRequiredQuit(false);
				ply.setQuitPos(null);
			}
			
			plugin.getLogger().info(ply.getName() + "'s data has been restored from file.");
		}
		
		ply.loadClaimedRewards();
		
		if(Bukkit.getServer().getOnlinePlayers().size() == 1){
			for(String mgm : mdata.getAllMinigames().keySet()){
				if(mdata.getMinigame(mgm).getType() == MinigameType.GLOBAL){
//					if(mdata.getMinigame(mgm).getThTimer() != null){
//						mdata.getMinigame(mgm).getThTimer().pauseTimer(false);
//					}
					if(mdata.getMinigame(mgm).getMinigameTimer() != null)
						mdata.getMinigame(mgm).getMinigameTimer().startTimer();
				}
			}
		}
	}
	
	@EventHandler
	public void playerInterract(PlayerInteractEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		
		if(ply.isInMinigame() && !ply.canInteract()){
			event.setCancelled(true);
			return;
		}
		
		if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
			if (!ply.getMinigame().allowDragonEggTeleport()) {
				event.setCancelled(true);
				return;
			}
		}
		
		if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().hasPermission("minigame.sign.use.details")){
			Block cblock = event.getClickedBlock();
			if(cblock.getState() instanceof Sign && !event.isCancelled()){
				Sign sign = (Sign) cblock.getState();
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Minigame]")){
					if((sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Join") || sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet")) && !ply.isInMinigame()){
						Minigame mgm = mdata.getMinigame(sign.getLine(2));
						if(mgm != null && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName(false).toLowerCase()))){
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
								
								if(mgm.isTeamGame()){
									String sc = "";
									int c = 0;
									for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
										c++;
										sc += t.getColor().getColor().toString() + " " + t.getScore() + ChatColor.WHITE;
										if(c != TeamsModule.getMinigameModule(mgm).getTeams().size()){
											sc += " : ";
										}
									}
									event.getPlayer().sendMessage(ChatColor.AQUA + MinigameUtils.getLang("minigame.info.score") + sc);
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
							event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noPermission", "minigame.join." + mgm.getName(false).toLowerCase()));
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
			else if(event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST)){
				Sign sign = (Sign) event.getClickedBlock().getState();
				if(ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Minigame]") && ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("Join")){
					Minigame minigame = mdata.getMinigame(sign.getLine(2));
					tool.setMinigame(minigame);
					ply.sendMessage("Tools Minigame has been set to " + minigame, null);
					event.setCancelled(true);
				}
			}
			else if(tool.getMode() != null && tool.getMinigame() != null){
				Minigame mg = tool.getMinigame();
				if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
					tool.getMode().onRightClick(ply, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
				}
				else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
					tool.getMode().onLeftClick(ply, mg, TeamsModule.getMinigameModule(mg).getTeam(tool.getTeam()), event);
				}
			}
			else if(tool.getMinigame() == null) {
				ply.sendMessage("Please select a minigame. Click on the join sign, or /mg tool minigame <minigame>", null);
			}
			else if(tool.getMode() == null) {
				ply.sendMessage("Please select a tool mode. Shift + Right click", null);
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
		if(ply == null) return;
		if(ply.isInMinigame() && !ply.getAllowGamemodeChange()){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noGamemode"));
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onFlyToggle(PlayerToggleFlightEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && (!ply.getMinigame().isSpectator(ply) || !ply.getMinigame().canSpectateFly()) && 
				!ply.canFly()){
			event.setCancelled(true);
			pdata.quitMinigame(ply, true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noFly"));
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void playerRevert(RevertCheckpointEvent event){
		if(event.getMinigamePlayer().isInMinigame() && 
				event.getMinigamePlayer().getMinigame().getType() == MinigameType.MULTIPLAYER && 
				!event.getMinigamePlayer().getMinigame().isAllowedMPCheckpoints() && 
				!event.getMinigamePlayer().isLatejoining()){
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.error.noRevert", event.getMinigamePlayer().getMinigame().getType().getName()));
		}
		else if(!event.getMinigamePlayer().getMinigame().hasStarted()){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void commandExecute(PlayerCommandPreprocessEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
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
	private void entityDamageEntity(EntityDamageByEntityEvent event){
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
						if(!shooter.canPvP()){
							event.setCancelled(true);
							return;
						}
						
						Team plyTeam = ply.getTeam();
						Team atcTeam = shooter.getTeam();
						if(!mgm.isTeamGame() || plyTeam != atcTeam){
							int damage = mgm.getPaintBallDamage();
							event.setDamage(damage);
						}
					}
				}
			}
		}
		else if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			MinigamePlayer ply = pdata.getMinigamePlayer((Player) event.getDamager());
			if(ply == null) return;
			if(ply.isInMinigame() && !ply.canPvP())
				event.setCancelled(true);
			else if(ply.isInMinigame() && ply.getMinigame().getState() == MinigameState.ENDED &&
					GameOverModule.getMinigameModule(ply.getMinigame()).isHumiliationMode() && 
					GameOverModule.getMinigameModule(ply.getMinigame()).getLosers().contains(ply)){
				event.setCancelled(true);
			}
		}
		else if(event.getEntity() instanceof Player && event.getDamager() instanceof Arrow){
			Arrow arrow = (Arrow) event.getDamager();
			if(arrow.getShooter() instanceof Player){
				Player ply = (Player) arrow.getShooter();
				MinigamePlayer mgpl = pdata.getMinigamePlayer(ply);
				if(mgpl == null) return;
				if(mgpl.isInMinigame() && !mgpl.canPvP())
					event.setCancelled(true);
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
					ItemStack mainhand = ply.getPlayer().getInventory().getItemInMainHand();
					if (mainhand.getType() == Material.SNOW_BALL){
						mainhand.setAmount(16);
						ply.getPlayer().updateInventory();
					}else{
						ply.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL,1));
					}

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
				else if((!ply.getMinigame().hasStarted() && ply.getMinigame().getState() != MinigameState.ENDED) || ply.isLatejoining()){
					event.setCancelled(true);
				}
				else if(ply.isInvincible())
					event.setCancelled(true);
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
		else if(ply.isInMinigame()) {
			if (!ply.getLoadout().allowOffHand() && event.getSlot() == 40) {
				event.setCancelled(true);
			}
			else if((ply.getLoadout().isArmourLocked() && event.getSlot() >= 36 && event.getSlot() <= 39) || 
					(ply.getLoadout().isInventoryLocked() && event.getSlot() >= 0 && event.getSlot() <= 35)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void onOffhandSwap(PlayerSwapHandItemsEvent event) {
		MinigamePlayer player = pdata.getMinigamePlayer(event.getPlayer());
		if (player.isInMenu()) {
			event.setCancelled(true);
		}
		else if (player.isInMinigame()) {
			if (!player.getLoadout().allowOffHand()) {
				event.setCancelled(true);
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
	
	@EventHandler
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
	private void playerMove(PlayerMoveEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame()){
			if(ply.isFrozen()){
				if(event.getFrom().getBlockX() != event.getTo().getBlockX() || 
						event.getFrom().getBlockZ() != event.getTo().getBlockZ()){
					ply.teleport(new Location(event.getFrom().getWorld(), event.getFrom().getBlockX() + 0.5, 
							event.getTo().getBlockY(), event.getFrom().getBlockZ() + 0.5, 
							event.getPlayer().getLocation().getYaw(), event.getPlayer().getLocation().getPitch()));
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	private void breakScoreboard(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN) {
			if (block.hasMetadata("MGScoreboardSign")) {
				Minigame minigame = (Minigame)block.getMetadata("Minigame").get(0).value();
				minigame.getScoreboardData().removeDisplay(block);
			}
		}
	}
}
