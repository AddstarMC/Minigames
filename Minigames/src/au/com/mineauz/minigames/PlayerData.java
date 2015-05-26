package au.com.mineauz.minigames;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.events.EndMinigameEvent;
import au.com.mineauz.minigames.events.QuitMinigameEvent;
import au.com.mineauz.minigames.events.RevertCheckpointEvent;
import au.com.mineauz.minigames.events.StartMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MinigameTypeBase;
import au.com.mineauz.minigames.mechanics.GameMechanics;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.GameOverModule;
import au.com.mineauz.minigames.minigame.modules.WeatherTimeModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;
import au.com.mineauz.minigames.sql.SQLPlayer;

public class PlayerData {
	private Map<String, MinigamePlayer> minigamePlayers = new HashMap<String, MinigamePlayer>();
	
	private boolean partyMode = false;
	
	private List<String> deniedCommands = new ArrayList<String>();
	
	private static Minigames plugin = Minigames.plugin;
	private MinigameData mdata = plugin.mdata;
	
	public PlayerData(){}
	
	public void startMPMinigame(Minigame minigame, boolean teleport){
		List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
		players.addAll(minigame.getPlayers());
		
		Collections.shuffle(players);
		
		if(minigame.isTeamGame() && GameMechanics.getGameMechanic(minigame.getMechanicName()) != null){
			GameMechanics.getGameMechanic(minigame.getMechanicName()).balanceTeam(players, minigame);
		}
		
		Location start = null;
		int pos = 0;
		Map<Team, Integer> tpos = new HashMap<Team, Integer>();
		for(Team t : minigame.getModule(TeamsModule.class).getTeams()){
			tpos.put(t, 0);
		}
		
		for(MinigamePlayer ply : players){
			if(!minigame.isTeamGame()){
				if(pos < minigame.getStartLocations().size()){
					ply.setStartTime(Calendar.getInstance().getTimeInMillis());
					if(teleport){
						start = minigame.getStartLocations().get(pos);
					}
				} 
				else{
					pos = 0;
					if(!minigame.getStartLocations().isEmpty()){
						if(teleport){
							start = minigame.getStartLocations().get(0);
						}
					}
					else {
						ply.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
						quitMinigame(ply, false);
					}
				}
				ply.setCheckpoint(start);
			}
			else{
				Team team = ply.getTeam();
				if(minigame.getModule(TeamsModule.class).hasTeamStartLocations()){
					if(tpos.get(team) >= team.getStartLocations().size()){
						tpos.put(team, 0);
					}
					start = team.getStartLocations().get(tpos.get(team));
					tpos.put(team, tpos.get(team) + 1);
				}
				else{
					if(pos < minigame.getStartLocations().size()){
						if(teleport){
							start = minigame.getStartLocations().get(pos);
						}
					} 
					else{
						pos = 0;
						if(!minigame.getStartLocations().isEmpty()){
							if(teleport){
								start = minigame.getStartLocations().get(0);
							}
						}
						else {
							ply.sendMessage(MinigameUtils.getLang("minigame.error.incorrectStart"), "error");
							quitMinigame(ply, false);
						}
					}
				}
				if(minigame.getLives() > 0){
					ply.sendMessage(MinigameUtils.formStr("minigame.livesLeft", minigame.getLives()), null);
				}
			}
			
			if(start != null){
				if(teleport){
					ply.teleport(start);
					ply.setCheckpoint(start);
				}
				if(minigame.getMaxScore() != 0){
					ply.sendMessage(MinigameUtils.formStr("minigame.scoreToWin", minigame.getMaxScorePerPlayer()), null);
				}
			}
			
			pos++;
			ply.getLoadout().equiptLoadout(ply);
			if(minigame.isAllowedFlight()){
				ply.setCanFly(true);
				if(minigame.isFlightEnabled())
					ply.getPlayer().setFlying(true);
			}
			
			PlayMGSound.playSound(ply, MGSounds.getSound("gameStart"));
		}
		
		Bukkit.getServer().getPluginManager().callEvent(new StartMinigameEvent(players, minigame, teleport));
		
		minigame.setState(MinigameState.STARTED);
	}
	
	public void revertToCheckpoint(MinigamePlayer player) {
		
		RevertCheckpointEvent event = new RevertCheckpointEvent(player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(!event.isCancelled()){
			player.teleport(player.getCheckpoint());
			player.addRevert();
			player.sendMessage(MinigameUtils.getLang("player.checkpoint.revert"), null);
		}
	}
	
	public void quitMinigame(MinigamePlayer player, boolean forced){
		Minigame minigame = player.getMinigame();
		
		boolean isWinner = false;
		GameOverModule module = minigame.getModule(GameOverModule.class);
		if(module.getWinners().contains(player))
			isWinner = true;
		
		QuitMinigameEvent event = new QuitMinigameEvent(player, minigame, forced, isWinner);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			if(!minigame.isSpectator(player)){
				if(player.getEndTime() == 0)
					player.setEndTime(System.currentTimeMillis());
				
				if(isWinner)
					module.getWinners().remove(player);
				else
					module.getLosers().remove(player);
				
				if(!isWinner){
					//SQL stuff
					if(plugin.getSQL() != null){
						if(minigame.canSaveCheckpoint() == false){
							plugin.addSQLToStore(new SQLPlayer(minigame.getName(false), player.getName(), player.getUUID().toString(), 0, 1, 
									player.getKills(), player.getDeaths(), player.getScore(), player.getReverts(), 
									player.getEndTime() - player.getStartTime() + player.getStoredTime()));
							plugin.startSQLCompletionSaver();
						}
					}
				}
				
				//Call Types quit.
				mdata.minigameType(minigame.getType()).quitMinigame(player, minigame, forced);
				
				//Call Mechanic quit.
				minigame.getMechanic().quitMinigame(minigame, player, forced);
				
				//Prepare player for quit
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				player.getPlayer().closeInventory();
				player.removeMinigame();
				minigame.removePlayer(player);
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				player.getPlayer().setFallDistance(0);
				player.getPlayer().setNoDamageTicks(60);
				final MinigamePlayer fplayer = player;
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						fplayer.getPlayer().setFireTicks(0);
					}
				});
				player.resetAllStats();
				
				if(!player.isDead()){
					player.restorePlayerData();
					if(!isWinner)
						player.teleport(minigame.getQuitPosition());
					else
						player.teleport(minigame.getEndPosition());
				}
				else{
					if(!isWinner)
						player.setQuitPos(minigame.getQuitPosition());
					else
						player.setQuitPos(minigame.getEndPosition());
					player.setRequiredQuit(true);
				}
				
				//Reward Player
				if(isWinner){
					player.claimTempRewardItems();
				}
				player.claimRewards();
				
				//Reset Minigame
				if(minigame.getPlayers().size() == 0){
					if(minigame.getMinigameTimer() != null){
						minigame.getMinigameTimer().stopTimer();
						minigame.setMinigameTimer(null);
					}
					
					if(minigame.getFloorDegenerator() != null){
						minigame.getFloorDegenerator().stopDegenerator();
					}
					
					minigame.setState(MinigameState.IDLE);
					
					if(minigame.getBlockRecorder().hasData()){
						minigame.getBlockRecorder().restoreBlocks();
						minigame.getBlockRecorder().restoreEntities();
						minigame.getBlockRecorder().setCreatedRegenBlocks(false);
					}
					
					if(minigame.getMpTimer() != null){
						minigame.getMpTimer().pauseTimer();
						minigame.getMpTimer().removeTimer();
						minigame.setMpTimer(null);
					}
					
					if(minigame.getMpBets() != null){
						minigame.setMpBets(null);
					}
					
					mdata.clearClaimedScore(minigame);
					minigame.getModule(WeatherTimeModule.class).stopTimeLoop();
					module.stopEndGameTimer();
					if (minigame.hasModule(TeamsModule.class)) {
						for(Team team : minigame.getModule(TeamsModule.class).getTeams()){
							team.setScore(0);
						}
					}
				}
				
				minigame.getScoreboardManager().resetScores(player.getName());
				
				for(MinigamePlayer pl : minigame.getSpectators()){
					player.getPlayer().showPlayer(pl.getPlayer());
				}
				
				if(minigame.getPlayers().size() == 0 && !minigame.isRegenerating()){
					HandlerList.unregisterAll(minigame.getBlockRecorder());
				}
				
				//Send out messages
				if(!forced){
					mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.quit.plyMsg", player.getName(), minigame.getName(true)), "error", player);
				}
				plugin.getLogger().info(player.getName() + " quit " + minigame);
				player.updateInventory();
			}
			else{
				if(player.getPlayer().getVehicle() != null){
					Vehicle vehicle = (Vehicle) player.getPlayer().getVehicle();
					vehicle.eject();
				}
				player.getPlayer().setFallDistance(0);
				player.getPlayer().setNoDamageTicks(60);
				final Player fplayer = player.getPlayer();
				for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
					player.getPlayer().removePotionEffect(potion.getType());
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						fplayer.setFireTicks(0);
					}
				});
				
				player.getPlayer().closeInventory();
				
				if(!player.isDead()){
					player.restorePlayerData();
				}
				
				player.teleport(minigame.getQuitPosition());
				player.removeMinigame();
				minigame.removeSpectator(player);

				for(MinigamePlayer pl : minigame.getPlayers()){
					pl.getPlayer().showPlayer(player.getPlayer());
				}
				
				player.sendMessage(MinigameUtils.formStr("player.spectate.quit.plyMsg", minigame.getName(true)), "error");
				mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("player.spectate.quit.minigameMsg", player.getName(), minigame.getName(true)), "error", player);
			}
			
			if(player.getPlayer().getGameMode() != GameMode.CREATIVE)
				player.setCanFly(false);
		}
	}
	
	public void endMinigame(MinigamePlayer player){
		List<MinigamePlayer> w = new ArrayList<MinigamePlayer>();
		List<MinigamePlayer> l = new ArrayList<MinigamePlayer>();
		w.add(player);
		endMinigame(player.getMinigame(), w, l);
	}
	
	
	public void endMinigame(Minigame minigame, List<MinigamePlayer> winners, List<MinigamePlayer> losers){
		EndMinigameEvent event = new EndMinigameEvent(winners, losers, minigame);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if(!event.isCancelled()){
			winners = event.getWinners();
			losers = event.getLosers();

			//Call Mechanics End
			minigame.getMechanic().endMinigame(minigame, winners, losers);
			
			//Prepare split money
			double bets = 0;
			if(minigame.getMpBets() != null){
				if(minigame.getMpBets().hasMoneyBets()){
					List<MinigamePlayer> plys = new ArrayList<MinigamePlayer>();
					plys.addAll(event.getWinners());
					
					if(!plys.isEmpty()){
						bets = minigame.getMpBets().claimMoneyBets() / (double) plys.size();
						BigDecimal roundBets = new BigDecimal(bets);
						roundBets = roundBets.setScale(2, BigDecimal.ROUND_HALF_UP);
						bets = roundBets.doubleValue();
					}
					minigame.setMpBets(null);
				}
			}
			
			//Broadcast Message
			if(plugin.getConfig().getBoolean("broadcastCompletion") && minigame.isEnabled()){
				if(minigame.isTeamGame()){
					TeamsModule teamsModule = minigame.getModule(TeamsModule.class);
					if(winners.size() > 0 || teamsModule.getDefaultWinner() != null){
						Team team;
						if(winners.size() > 0)
							team = winners.get(0).getTeam();
						else
							team = teamsModule.getTeam(teamsModule.getDefaultWinner());
						String score = "";
						List<Team> teams = minigame.getModule(TeamsModule.class).getTeams();
						for(Team t : teams){
							score += t.getColor().getColor().toString() + t.getScore();
							if(t != teams.get(teams.size() - 1)){
								score += ChatColor.WHITE + " : ";
							}
						}
						String nscore = ", " + MinigameUtils.formStr("player.end.team.score", score);
						if(team.getScore() > 0){
							MinigameUtils.broadcast(MinigameUtils.formStr("player.end.team.win", 
								team.getChatColor() + team.getDisplayName() + ChatColor.WHITE, minigame.getName(true)) + nscore, minigame, ChatColor.GREEN);
						}
						else{
							MinigameUtils.broadcast(MinigameUtils.formStr("player.end.team.win", 
									team.getChatColor() + team.getDisplayName() + ChatColor.WHITE, minigame.getName(true)), minigame, ChatColor.GREEN);
						}
					}
					else{
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastNobodyWon", minigame.getName(true)), minigame, ChatColor.RED);
					}
				}
				else{
					if(winners.size() == 1){
						String score = "";
						if(winners.get(0).getScore() != 0)
							score = MinigameUtils.formStr("player.end.broadcastScore", winners.get(0).getScore());
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastMsg", winners.get(0).getDisplayName(), minigame.getName(true)) + ". " + score, minigame, ChatColor.GREEN);
					}
					else if(winners.size() > 1){
						String win = "";
						Collections.sort(winners, new Comparator<MinigamePlayer>() {
							@Override
							public int compare(MinigamePlayer o1,
									MinigamePlayer o2) {
								return Integer.valueOf(o1.getScore()).compareTo(o2.getScore());
							}
						});
						
						for(MinigamePlayer pl : winners){
							if(winners.indexOf(pl) < 2){
								win += pl.getDisplayName();
								if(winners.indexOf(pl) + 2 >= winners.size()){
									win += " and ";
								}
								else{
									win += ", ";
								}
							}
							else{
								win += String.valueOf(winners.size() - 3) + " others";
							}
						}
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastMsg", win, minigame.getName(true)) + ". ", minigame, ChatColor.GREEN);
					}
					else{
						MinigameUtils.broadcast(MinigameUtils.formStr("player.end.broadcastNobodyWon", minigame.getName(true)), minigame, ChatColor.RED);
					}
				}
			}
			
			boolean usedTimer = false;
			GameOverModule gom = minigame.getModule(GameOverModule.class);
			if (gom != null) {
				
				gom.setWinners(winners);
				gom.setLosers(losers);
				
				if(gom.getTimer() > 0 && minigame.getType() == MinigameType.MULTIPLAYER){
					gom.startEndGameTimer();
					usedTimer = true;
				}
			}
			
			for(MinigamePlayer player : losers){
				player.setEndTime(System.currentTimeMillis());
				if(!usedTimer)
					quitMinigame(player, true);
				PlayMGSound.playSound(player, MGSounds.getSound("lose"));
			}
			
			for(MinigamePlayer player : winners){
				player.setEndTime(System.currentTimeMillis());
				SQLPlayer sply = new SQLPlayer(minigame.getName(false), player.getName(), player.getUUID().toString(), 1, 0, 
						player.getKills(), player.getDeaths(), player.getScore(), player.getReverts(), 
						player.getEndTime() - player.getStartTime());
				if(!usedTimer)
					quitMinigame(player, true);
				
				//Group money bets
				if(bets != 0){
					plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), bets);
					player.sendMessage(MinigameUtils.formStr("player.bet.winMoney", Minigames.plugin.getEconomy().format(bets)), null);
				}
				
				//Reward Player
				boolean hascompleted = false;
				Configuration completion = null;
				if(plugin.getSQL() == null && minigame.isEnabled()){
					completion = mdata.getConfigurationFile("completion");
					hascompleted = completion.getStringList(minigame.getName(false)).contains(player.getUUID().toString().replace("-", "_"));
					
					if(!hascompleted){
						List<String> completionlist = completion.getStringList(minigame.getName(false));
						completionlist.add(player.getUUID().toString().replace("-", "_"));
						completion.set(minigame.getName(false), completionlist);
						MinigameSave completionsave = new MinigameSave("completion");
						completionsave.getConfig().set(minigame.getName(false), completionlist);
						completionsave.saveConfig();
					}
					
					MinigameTypeBase.issuePlayerRewards(player, minigame, hascompleted);
				}
				else if(minigame.isEnabled()){
					plugin.addSQLToStore(sply);
					plugin.startSQLCompletionSaver();
				}
				
				//Item Bets (for non groups)
				if(minigame.getMpBets() != null){
					if(minigame.getMpBets().hasBets()){
						if(!player.isInMinigame())
							player.getPlayer().getInventory().addItem(minigame.getMpBets().claimBets());
						else{
							for(ItemStack i : minigame.getMpBets().claimBets()){
								player.addTempRewardItem(i);
							}
						}
						minigame.setMpBets(null);
					}
				}
				
				PlayMGSound.playSound(player, MGSounds.getSound("win"));
			}
			
			if(gom != null && !usedTimer){
				gom.clearLosers();
				gom.clearWinners();
			}
			
			mdata.clearClaimedScore(minigame);
			
			//Call Types End.
			mdata.minigameType(minigame.getType()).endMinigame(winners, losers, minigame);
			
		}
	}
	
	@Deprecated
	public boolean playerInMinigame(Player player){
		return minigamePlayers.get(player.getName()).isInMinigame();
	}
	
	@Deprecated
	public List<Player> playersInMinigame(){
		List<Player> players = new ArrayList<Player>();
		for(Player player : plugin.getServer().getOnlinePlayers()){
			if(playerInMinigame(player)){
				players.add(player);
			}
		}
		return players;
	}
	
	public void addMinigamePlayer(Player player){
		minigamePlayers.put(player.getName(), new MinigamePlayer(player));
	}
	
	public void removeMinigamePlayer(Player player){
		if(minigamePlayers.containsKey(player.getName())){
			minigamePlayers.remove(player.getName());
		}
	}
	
	public MinigamePlayer getMinigamePlayer(Player player){
		return minigamePlayers.get(player.getName());
	}
	
	public MinigamePlayer getMinigamePlayer(UUID uuid){
		for(MinigamePlayer p : minigamePlayers.values()){
			if(p.getUUID() == uuid)
				return p;
		}
		return null;
	}
	
	public MinigamePlayer getMinigamePlayer(String player){
		return minigamePlayers.get(player);
	}
	
	public Collection<MinigamePlayer> getAllMinigamePlayers(){
		return minigamePlayers.values();
	}
	
	public boolean hasMinigamePlayer(String name){
		return minigamePlayers.containsKey(name);
	}
	
	public boolean hasMinigamePlayer(UUID uuid){
		for(MinigamePlayer p : minigamePlayers.values()){
			if(p.getUUID() == uuid)
				return true;
		}
		return false;
	}
	
	public List<String> checkRequiredFlags(MinigamePlayer player, String minigame){
		List<String> checkpoints = new ArrayList<String>();
		checkpoints.addAll(mdata.getMinigame(minigame).getFlags());
		List<String> pchecks = player.getFlags();
		
		if(!pchecks.isEmpty()){
			checkpoints.removeAll(pchecks);
		}
		
		return checkpoints;
	}
	
	public boolean onPartyMode(){
		return partyMode;
	}
	
	public void setPartyMode(boolean mode){
		partyMode = mode;
	}
	
	public void partyMode(MinigamePlayer player){
		if(onPartyMode()){
			Location loc = player.getPlayer().getLocation();
			Firework firework = (Firework) player.getPlayer().getWorld().spawnEntity(loc, EntityType.FIREWORK);
			FireworkMeta fwm = firework.getFireworkMeta();
			
			Random chance = new Random();
			Type type = Type.BALL_LARGE;
			if(chance.nextInt(100) < 50){
				type = Type.BALL;
			}
			
			Color col = Color.fromRGB(chance.nextInt(255), chance.nextInt(255), chance.nextInt(255));
			
			FireworkEffect effect = FireworkEffect.builder().with(type).withColor(col).flicker(chance.nextBoolean()).trail(chance.nextBoolean()).build();
			fwm.addEffect(effect);
			fwm.setPower(1);
			firework.setFireworkMeta(fwm);
		}
	}
	
	public void partyMode(MinigamePlayer player, int amount, long delay){
		if(!onPartyMode()) return;
		final int fcount = amount;
		final MinigamePlayer fplayer = player;
		final long fdelay = delay;
		partyMode(fplayer);
		if(amount == 1) return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if(fplayer != null){
					partyMode(fplayer, fcount - 1, fdelay);
				}
				
			}
		}, delay);
	}

	public List<String> getDeniedCommands() {
		return deniedCommands;
	}

	public void setDeniedCommands(List<String> deniedCommands) {
		this.deniedCommands = deniedCommands;
	}
	
	public void addDeniedCommand(String command){
		deniedCommands.add(command);
	}
	
	public void removeDeniedCommand(String command){
		deniedCommands.remove(command);
	}
	
	public void saveDeniedCommands(){
		plugin.getConfig().set("disabledCommands", deniedCommands);
		plugin.saveConfig();
	}
	
	public void loadDeniedCommands(){
		setDeniedCommands(plugin.getConfig().getStringList("disabledCommands"));
	}
}
