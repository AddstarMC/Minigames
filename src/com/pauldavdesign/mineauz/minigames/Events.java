package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
//import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

import com.pauldavdesign.mineauz.minigames.events.RevertCheckpointEvent;

public class Events implements Listener{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(pdata.playerInMinigame(event.getEntity().getPlayer())){
			Player ply = event.getEntity().getPlayer();
			if(!mdata.getMinigame(pdata.getPlayersMinigame(ply)).hasDeathDrops()){
				event.getDrops().clear();
			}
			String msg = "";
			msg += event.getDeathMessage();
			event.setDeathMessage(null);
			event.setKeepLevel(true);
			event.setDroppedExp(0);
			
			pdata.addPlayerDeath(ply);
			
			pdata.partyMode(ply);
			
			String minigame = pdata.getPlayersMinigame(ply);
			if(mdata.getMinigame(minigame).hasPlayers()){
				for(Player pl : mdata.getMinigame(minigame).getPlayers()){
					pl.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + msg);
				}
			}
		}
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			if(!mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).hasItemDrops()){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void itemPickup(PlayerPickupItemEvent event){
		if(pdata.playerInMinigame(event.getPlayer())){
			if(!mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).hasItemPickup()){
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
	}
	
	@EventHandler
	public void onPlayerConnect(PlayerJoinEvent event){
		if(event.getPlayer().isOp() && plugin.getConfig().getBoolean("updateChecker")){
			List<String> update = MinigameUtils.checkForUpdate("http://mineauz.pauldavdesign.com/mgmversion.txt", plugin.getDescription().getVersion());
			if(update != null){
				Player ply = event.getPlayer();
				ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "There is an update available! Version: " + update.get(0));
				if(update.size() > 1){
					ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Changes:");
					for(int i = 1; i < update.size(); i++){
						ply.sendMessage("- " + update.get(i));
					}
				}
			}
		}
		if(pdata.hasDCPlayer(event.getPlayer())){
			event.getPlayer().teleport(pdata.getDCPlayer(event.getPlayer()));
			pdata.removeDCPlayer(event.getPlayer());
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
			else if(signinfo[1].equalsIgnoreCase("Checkpoint")){
				event.setLine(1, ChatColor.GREEN + "Checkpoint");
			}
			else if(signinfo[1].equalsIgnoreCase("Flag") && !signinfo[2].isEmpty()){
				event.setLine(1, ChatColor.GREEN + "Flag");
				if(event.getLine(2).equalsIgnoreCase("red")){
					event.setLine(2, ChatColor.RED + "Red");
				}
				else if(event.getLine(2).equalsIgnoreCase("blue")){
					event.setLine(2, ChatColor.BLUE + "Blue");
				}
				else if(event.getLine(2).equalsIgnoreCase("neutral")){
					event.setLine(2, ChatColor.GRAY + "Neutral");
				}
				else if(event.getLine(2).equalsIgnoreCase("capture") && !event.getLine(3).isEmpty()){
					event.setLine(2, ChatColor.GREEN + "Capture");
					if(event.getLine(3).equalsIgnoreCase("red")){
						event.setLine(3, ChatColor.RED + "Red");
					}
					else if(event.getLine(3).equalsIgnoreCase("blue")){
						event.setLine(3, ChatColor.BLUE + "Blue");
					}
					else if(event.getLine(3).equalsIgnoreCase("neutral")){
						event.setLine(3, ChatColor.GRAY + "Neutral");
					}
					else{
						event.getBlock().breakNaturally();
						event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Invalid sign syntax!" +
								" Acceptable arguments for capture signs are red, blue and neutral.");
					}
				}
			}
			else if(signinfo[1].equalsIgnoreCase("Bet") && plugin.getConfig().getStringList("minigames").contains(minigame)){
				event.setLine(1, ChatColor.GREEN + "Bet");
				event.setLine(2, minigame);
				if(event.getLine(3).matches("[0-9]+")){
					event.setLine(3, "$" + event.getLine(3));
				}
			}
			else if(signinfo[1].equalsIgnoreCase("Quit")){
				event.setLine(1, ChatColor.GREEN + "Quit");
			}
			else if(signinfo[1].equalsIgnoreCase("Loadout") && !signinfo[2].isEmpty()){
				event.setLine(1, ChatColor.GREEN + "Loadout");
			}
			else {
				//event.setLine(1, ChatColor.DARK_RED + signinfo[1]);
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Invalid Minigame Sign!");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSignUse(PlayerInteractEvent event){ //TODO: Sign API
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().hasPermission("minigame.sign.use")){
			Block cblock = event.getClickedBlock();
			if(cblock.getState() instanceof Sign){
				Sign sign = (Sign) cblock.getState();
				String minigame = pdata.getPlayersMinigame(event.getPlayer());
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Minigame]")){
					if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Finish") && pdata.playerInMinigame(event.getPlayer()) && event.getPlayer().getItemInHand().getType() == Material.AIR){
						if(!mdata.getMinigame(minigame).getFlags().isEmpty()){
							Location loc = event.getPlayer().getLocation();
							loc.setY(loc.getY() - 1);
							if(loc.getBlock().getType() != Material.AIR){
								
								if(pdata.checkRequiredFlags(event.getPlayer(), minigame).isEmpty()){
									pdata.endMinigame(event.getPlayer());
									pdata.partyMode(event.getPlayer());
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
									event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You still require the following flags:");
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
						if(event.getPlayer().getItemInHand().getType() == Material.AIR){
							Minigame mgm = mdata.getMinigame(sign.getLine(2));
							if(mgm != null && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
								if(mgm.isEnabled()){
									pdata.joinMinigame(event.getPlayer(), mdata.getMinigame(sign.getLine(2)));
								}
								else if(!mgm.isEnabled()){
									event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This minigame is currently not enabled.");
								}
							}
							else if(mgm == null){
								event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "This minigame doesn't exist!");
							}
							else if(mgm.getUsePermissions()){
								event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
							}
						}
						else
							event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
					}
					else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet") && !pdata.playerInMinigame(event.getPlayer())){
						Minigame mgm = mdata.getMinigame(sign.getLine(2));
						if(mgm != null && mgm.isEnabled() && (!mgm.getUsePermissions() || event.getPlayer().hasPermission("minigame.join." + mgm.getName().toLowerCase()))){
							if(!sign.getLine(3).startsWith("$")){
								pdata.joinWithBet(event.getPlayer(), mdata.getMinigame(sign.getLine(2)), 0d);
							}
							else{
								if(plugin.hasEconomy()){
									Double bet = Double.parseDouble(sign.getLine(3).replace("$", ""));
									pdata.joinWithBet(event.getPlayer(), mdata.getMinigame(sign.getLine(2)), bet);
								}
								else{
									event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "This server does not have Vault! Money bets are not enabled.");
								}
							}
						}
						else if(!mgm.isEnabled()){
							event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This minigame is currently not enabled.");
						}
						else if(mgm.getUsePermissions()){
							event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You do not have permission minigame.join." + mgm.getName().toLowerCase());
						}
						else{
							event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "This minigame doesn't exist!");
						}
					}
					else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Checkpoint") && pdata.playerInMinigame(event.getPlayer())){
						if(event.getPlayer().getItemInHand().getType() == Material.AIR){
							Location loc = event.getPlayer().getLocation();
							loc.setY(loc.getY() - 1);
							if(loc.getBlock().getType() != Material.AIR){
								Location newloc = event.getPlayer().getLocation();
								pdata.setPlayerCheckpoints(event.getPlayer(), newloc);
								event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Checkpoint set!");
							}
							else{
								event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "You can not set a checkpoint here!");
							}
						}
						else
							event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
					}
					else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Flag") && pdata.playerInMinigame(event.getPlayer())){
						if(event.getPlayer().getItemInHand().getType() == Material.AIR){
							Location loc = event.getPlayer().getLocation();
							loc.setY(loc.getY() - 1);
							if(!sign.getLine(2).isEmpty() && loc.getBlock().getType() != Material.AIR && 
									!mdata.getMinigame(pdata.getPlayersMinigame(event.getPlayer())).getType().equals("ctf") &&
									!pdata.playerHasFlag(event.getPlayer(), sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), ""))){
								pdata.addPlayerFlags(event.getPlayer(), sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), ""));
								event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + 
										ChatColor.WHITE + sign.getLine(2).replaceAll(ChatColor.RED.toString(), "").replaceAll(ChatColor.BLUE.toString(), "") + " flag taken!");
							}
						}
						else
							event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
					}
					else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Quit")){
						if(pdata.playerInMinigame(event.getPlayer()) && event.getPlayer().getItemInHand().getType() == Material.AIR)
							pdata.quitMinigame(event.getPlayer(), false);
						else
							event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
					}
					else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Loadout") && pdata.playerInMinigame(event.getPlayer())){
						if(event.getPlayer().getItemInHand().getType() == Material.AIR){
							Player ply = event.getPlayer();
							Minigame mgm = mdata.getMinigame(minigame);
							if(mgm.hasLoadout(sign.getLine(2))){
								if(mgm.getType().equals("sp") || (mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() == 0)){
									mgm.getLoadout(sign.getLine(2)).equiptLoadout(ply);
								}
								mgm.setPlayersLoadout(ply, mgm.getLoadout(sign.getLine(2)));
								ply.updateInventory();
								ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have been equip with the " + sign.getLine(2) + " loadout.");
							}
							else if(mdata.hasLoadout(sign.getLine(2))){
								if(mgm.getType().equals("sp") || (mgm.getMpTimer() != null && mgm.getMpTimer().getStartWaitTimeLeft() == 0)){
									mdata.getLoadout(sign.getLine(2)).equiptLoadout(ply);
								}
								mgm.setPlayersLoadout(ply, mdata.getLoadout(sign.getLine(2)));
								ply.updateInventory();
								ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "You have been equip with the " + sign.getLine(2) + " loadout.");
							}
							else{
								ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "This loadout does not exist!");
							}
						}
						else
							event.getPlayer().sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "Your hand must be empty to use this sign!");
					}
					else if(sign.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Bet") && event.getPlayer().getItemInHand().getType() == Material.AIR){
						event.getPlayer().sendMessage(ChatColor.RED + "You cannot bet nothing!");
					}
					event.setCancelled(true);
				}
			}
			else if(cblock.getState() instanceof Chest){
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
		else if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().hasPermission("minigame.sign.use")){
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
								String status = ChatColor.BLUE + "Status: ";
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
								
								if(mgm.getMinigameTimer() != null){
									event.getPlayer().sendMessage(ChatColor.BLUE + "Time left: " + MinigameUtils.convertTime(mgm.getMinigameTimer().getTimeLeft()));
								}
								
								if(mgm.getType().equals("teamdm")){
									event.getPlayer().sendMessage(ChatColor.BLUE + "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore());
								}
								
								String playerCount = ChatColor.BLUE + "Player Count: " + ChatColor.GRAY;
								String players = ChatColor.BLUE + "Players: ";
								
								if(mgm.hasPlayers()){
									playerCount += mgm.getPlayers().size() + "/";
									if(!mgm.getType().equals("sp")){
										playerCount += mgm.getMaxPlayers();
									}
									else{
										playerCount += "inf";
									}
									
									List<String> plyList = new ArrayList<String>();
									for(Player ply : mgm.getPlayers()){
										plyList.add(ply.getName());
									}
									players += MinigameUtils.listToString(plyList);
								}
								else{
									playerCount += "0/";
									
									if(!mgm.getType().equals("sp")){
										playerCount += mgm.getMaxPlayers();
									}
									else{
										playerCount += "inf";
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
		if(pdata.playerInMinigame(event.getPlayer())){
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
	
	@EventHandler
	private void paintballHit(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Snowball){
			Player ply = (Player) event.getEntity();
			Snowball sb = (Snowball) event.getDamager();
			if(pdata.playerInMinigame(ply) && mdata.getMinigame(pdata.getPlayersMinigame(ply)).hasPaintBallMode()){
				if(sb.getShooter() instanceof Player){
					Player shooter = (Player) sb.getShooter();
					if(pdata.playerInMinigame(shooter) && pdata.getPlayersMinigame(shooter).equals(pdata.getPlayersMinigame(ply))){
						ply.damage(mdata.getMinigame(pdata.getPlayersMinigame(ply)).getPaintBallDamage());
						event.setCancelled(true);
					}
				}
			}
		}
	}

}
