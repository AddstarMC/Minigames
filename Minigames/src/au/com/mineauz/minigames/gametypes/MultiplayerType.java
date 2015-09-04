package au.com.mineauz.minigames.gametypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.PlayerData;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamSelection;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.MultiplayerModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class MultiplayerType extends MinigameTypeBase{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	
	public MultiplayerType(){
		setType(MinigameType.MULTIPLAYER);
	}

	@Override
	public boolean joinMinigame(MinigamePlayer player, Minigame mgm) {
		LobbySettingsModule lobbySettings = mgm.getModule(LobbySettingsModule.class);
		MultiplayerModule multiplayer = mgm.getModule(MultiplayerModule.class);
		
		if(!lobbySettings.canInteractPlayerWait())
			player.setCanInteract(false);
		if(!lobbySettings.canMovePlayerWait())
			player.setFrozen(true);
		
		if(!mgm.isWaitingForPlayers() && !mgm.hasStarted()){
			if(mgm.getMpTimer() == null && mgm.getPlayers().size() == multiplayer.getMinPlayers()){
				mgm.setMpTimer(new MultiplayerTimer(mgm));
				mgm.getMpTimer().startTimer();
				if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
					mgm.getMpTimer().setPlayerWaitTime(0);
					mgm.broadcast(MinigameUtils.getLang("minigame.skipWaitTime"), MessageType.Normal);
				}
			}
			else if(mgm.getMpTimer() != null && mgm.getPlayers().size() == mgm.getMaxPlayers()){
				mgm.getMpTimer().setPlayerWaitTime(0);
				mgm.broadcast(MinigameUtils.getLang("minigame.skipWaitTime"), MessageType.Normal);
			}
			else if(mgm.getMpTimer() == null){
				int neededPlayers = multiplayer.getMinPlayers() - mgm.getPlayers().size();
				mgm.broadcast(MinigameUtils.formStr("minigame.waitingForPlayers", neededPlayers), MessageType.Normal);
			}
		}
		else if(mgm.hasStarted()){
			player.setLatejoining(true);
			player.sendMessage(MinigameUtils.formStr("minigame.lateJoin", 5)); //TODO: Late join delay variable
			final MinigamePlayer fply = player;
			final Minigame fmgm = mgm;
			if(mgm.isTeamGame()){
				final TeamsModule teamsModule = mgm.getModule(TeamsModule.class);
				Team smTeam = null;
				for(Team team : teamsModule.getTeams()){
					if(smTeam == null || team.getPlayers().size() < smTeam.getPlayers().size()){
						smTeam = team;
					}
				}
				
				smTeam.addPlayer(player);
				player.sendMessage(String.format(smTeam.getAssignMessage(), smTeam.getChatColor() + smTeam.getDisplayName()), MessageType.Normal);
				
				final Team fteam = smTeam;
				player.setLateJoinTimer(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						if(fply.isInMinigame()){
							List<Location> locs = new ArrayList<Location>();
							if(teamsModule.hasTeamStartLocations()){
								locs.addAll(fteam.getStartLocations());
							}
							else{
								locs.addAll(fmgm.getStartLocations());
							}
							Collections.shuffle(locs);
							fply.teleport(locs.get(0));
							fply.getLoadout().equiptLoadout(fply);
							fply.setLatejoining(false);
							fply.setFrozen(false);
							fply.setCanInteract(true);
							fply.setLateJoinTimer(-1);
						}
					}
				}, 5 * 20)); //TODO: Latejoin variable
			}
			else{
				player.setLateJoinTimer(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						if(fply.isInMinigame()){
							List<Location> locs = new ArrayList<Location>(fmgm.getStartLocations());
							Collections.shuffle(locs);
							fply.teleport(locs.get(0));
							fply.getLoadout().equiptLoadout(fply);
							fply.setLatejoining(false);
							fply.setFrozen(false);
							fply.setCanInteract(true);
							fply.setLateJoinTimer(-1);
						}
					}
				}, 5 * 20)); //TODO: Latejoin variable
			}
			player.getPlayer().setScoreboard(mgm.getScoreboardManager());
			mgm.setScore(player, 1);
			mgm.setScore(player, 0);
		}
		return true;
	}

	@Override
	public void quitMinigame(MinigamePlayer player, Minigame mgm, boolean forced) {
		int teamsWithPlayers = 0;
		MultiplayerModule multiplayer = mgm.getModule(MultiplayerModule.class);
		
		if(mgm.isTeamGame()){
			player.removeTeam();
			for(Team t : mgm.getModule(TeamsModule.class).getTeams()){
				if(t.getPlayers().size() > 0)
					teamsWithPlayers ++;
			}
			
			if(mgm.getMpBets() != null && mgm.isWaitingForPlayers() && !forced){
				if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
					plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), mgm.getMpBets().getPlayersMoneyBet(player));
				}
				mgm.getMpBets().removePlayersBet(player);
			}
		}
		else{
			if(mgm.getMpBets() != null && (mgm.getMpTimer() == null || mgm.getMpTimer().getPlayerWaitTimeLeft() != 0)){
				if(mgm.getMpBets().getPlayersBet(player) != null){
					final ItemStack item = mgm.getMpBets().getPlayersBet(player).clone();
					final MinigamePlayer ply = player;
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							ply.getPlayer().getInventory().addItem(item);
						}
					});
				}
				else if(mgm.getMpBets().getPlayersMoneyBet(player) != null){
					plugin.getEconomy().depositPlayer(player.getPlayer().getPlayer(), mgm.getMpBets().getPlayersMoneyBet(player));
				}
				mgm.getMpBets().removePlayersBet(player);
			}
		}
		
		if(mgm.isTeamGame() && mgm.getPlayers().size() > 1 && 
				teamsWithPlayers == 1 && mgm.hasStarted() && !forced){
			TeamsModule module = mgm.getModule(TeamsModule.class);
			if(module.getTeams().size() != 1){
				Team winner = null;
				for(Team t : module.getTeams()){
					if(t.getPlayers().size() > 0){
						winner = t;
						break;
					}
				}
				List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(winner.getPlayers());
				List<MinigamePlayer> l = new ArrayList<MinigamePlayer>();
				plugin.pdata.endMinigame(mgm, w, l);
				
				if(mgm.getMpBets() != null){
					mgm.setMpBets(null);
				}
			}
		}
		else if(mgm.getPlayers().size() == 2 && mgm.hasStarted() && !forced){
			List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(mgm.getPlayers());
			w.remove(player);
			List<MinigamePlayer> l = new ArrayList<MinigamePlayer>();
			plugin.pdata.endMinigame(mgm, w, l);
			
			if(mgm.getMpBets() != null){
				mgm.setMpBets(null);
			}
		}
		else if(mgm.getPlayers().size() - 1 < multiplayer.getMinPlayers() && 
				mgm.getMpTimer() != null && 
				mgm.getMpTimer().getStartWaitTimeLeft() != 0 && 
				(mgm.getState() == MinigameState.STARTING || mgm.getState() == MinigameState.WAITING)){
			mgm.getMpTimer().setPlayerWaitTime(Minigames.plugin.getConfig().getInt("multiplayer.waitforplayers"));
			mgm.getMpTimer().pauseTimer();
			mgm.getMpTimer().removeTimer();
			mgm.setMpTimer(null);
			mgm.setState(MinigameState.IDLE);
			mgm.broadcast(MinigameUtils.formStr("minigame.waitingForPlayers", 1), MessageType.Normal);
		}
	}

	@Override
	public void endMinigame(List<MinigamePlayer> winners,
			List<MinigamePlayer> losers, Minigame mgm) {
		if(mgm.isTeamGame()){
			for(MinigamePlayer player : winners){
				player.removeTeam();
			}
			for(MinigamePlayer player : losers){
				player.removeTeam();
			}
			for(Team t : mgm.getModule(TeamsModule.class).getTeams()){
				t.resetScore();
			}
		}
		
		if(mgm.getMpTimer() == null) return;
		mgm.getMpTimer().setStartWaitTime(0);
		mgm.setMpTimer(null);
	}
	
	public static void switchTeam(Minigame mgm, MinigamePlayer player, Team newTeam){
		if(player.getTeam() != null)
			player.removeTeam();
		newTeam.addPlayer(player);
	}
	
	/*----------------*/
	/*-----EVENTS-----*/
	/*----------------*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerRespawn(PlayerRespawnEvent event){
		final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame() && ply.getMinigame().getType() == MinigameType.MULTIPLAYER){
			Minigame mg = ply.getMinigame();
			MultiplayerModule multiplayer = mg.getModule(MultiplayerModule.class);
			Location respawnPos;
			if(ply.getMinigame().isTeamGame()){
				Team team = ply.getTeam();
				if(mg.hasStarted() && !ply.isLatejoining()){
					if(mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()){
						respawnPos = ply.getCheckpoint();
					}
					else{
						List<Location> starts = new ArrayList<Location>();
						if(mg.getModule(TeamsModule.class).hasTeamStartLocations()){
							starts.addAll(team.getStartLocations());
							ply.getLoadout().equiptLoadout(ply);
						}
						else{
							starts.addAll(mg.getStartLocations());
						}
						Collections.shuffle(starts);
						respawnPos = starts.get(0);
					}
					ply.getLoadout().equiptLoadout(ply);
				}
				else{
					respawnPos = multiplayer.getLobbyPosition();
				}
			}
			else{
				if(mg.hasStarted() && !ply.isLatejoining()){
					if(mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()){
						respawnPos = ply.getCheckpoint();
					}
					else{
						List<Location> starts = new ArrayList<Location>();
						starts.addAll(mg.getStartLocations());
						Collections.shuffle(starts);
						respawnPos = starts.get(0);
					}
					
					ply.getLoadout().equiptLoadout(ply);
				}
				else{
					respawnPos = multiplayer.getLobbyPosition();
				}
			}
			
			event.setRespawnLocation(respawnPos);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					ply.getPlayer().setNoDamageTicks(60);
				}
			});
		}
	}
	
	@EventHandler
	public void timerExpire(TimerExpireEvent event){
		if(event.getMinigame().getType() == MinigameType.MULTIPLAYER && event.getMinigameState() == MinigameState.STARTED){
			if(event.getMinigame().isTeamGame()){
				Minigame mgm = event.getMinigame();
				TeamsModule teamModule = mgm.getModule(TeamsModule.class);
				if(teamModule.getDefaultWinner() != TeamSelection.NONE){
					List<MinigamePlayer> w;
					List<MinigamePlayer> l;
					if(teamModule.hasTeam(teamModule.getDefaultWinner().getTeam())){
						Team def = teamModule.getTeam(teamModule.getDefaultWinner().getTeam());
						w = new ArrayList<MinigamePlayer>(def.getPlayers().size());
						l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - def.getPlayers().size());
						w.addAll(def.getPlayers());
					}
					else{
						w = new ArrayList<MinigamePlayer>();
						l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size());
					}
					
					for(Team t : teamModule.getTeams()){
						if(t.getColor() != teamModule.getDefaultWinner().getTeam())
							l.addAll(t.getPlayers());
					}
					plugin.pdata.endMinigame(mgm, w, l);
				}
				else{
					List<Team> drawTeams = new ArrayList<Team>();
					Team winner = null;
					for(Team t : teamModule.getTeams()){
						if(winner == null || (t.getScore() > winner.getScore() && 
								(drawTeams.isEmpty() || t.getScore() > drawTeams.get(0).getScore()))){
							winner = t;
						}
						else if(winner != null && t.getScore() == winner.getScore()){
							if(!drawTeams.isEmpty()){
								drawTeams.clear();
							}
							drawTeams.add(winner);
							drawTeams.add(t);
							winner = null;
						}
						else if(!drawTeams.isEmpty() && drawTeams.get(0).getScore() == t.getScore()){
							drawTeams.add(t);
						}
					}
					
					if(winner != null){
						List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(winner.getPlayers());
						List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - winner.getPlayers().size());
						for(Team t : teamModule.getTeams()){
							if(t != winner)
								l.addAll(t.getPlayers());
						}
						pdata.endMinigame(mgm, w, l);
					}
					else{
						List<MinigamePlayer> players = new ArrayList<MinigamePlayer>(mgm.getPlayers());
						for(Team t : teamModule.getTeams()){
							t.resetScore();
						}
						
						if(mgm.getMinigameTimer() != null){
							mgm.getMinigameTimer().stopTimer();
							mgm.setMinigameTimer(null);
						}
						
						if(mgm.getMpTimer() != null){
							mgm.getMpTimer().setStartWaitTime(0);
							mgm.getMpTimer().pauseTimer();
							mgm.getMpTimer().removeTimer();
							mgm.setMpTimer(null);
						}
						
						if(mgm.getFloorDegenerator() != null && mgm.getPlayers().size() == 0){
							mgm.getFloorDegenerator().stopDegenerator();
						}
						
						if(mgm.getMpBets() != null && mgm.getPlayers().size() == 0){
							mgm.setMpBets(null);
						}
						
						for(MinigamePlayer ply : players){
							pdata.quitMinigame(ply, true);
							if(!plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
								if(drawTeams.size() == 2){
									ply.sendMessage(MinigameUtils.formStr("player.end.team.tie", 
											drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE, 
											drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE, 
											event.getMinigame().getName(true)), MessageType.Error);
								}
								else{
									ply.sendMessage(MinigameUtils.formStr("player.end.team.tieCount", 
											drawTeams.size(), 
											event.getMinigame().getName(true)), MessageType.Error);
								}
								String scores = "";
								int c = 1;
								for(Team t : teamModule.getTeams()){
									scores += t.getChatColor().toString() + t.getScore();
									if(c != teamModule.getTeams().size())
										scores += ChatColor.WHITE + " : ";
									c++;
								}
								ply.sendMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
							}
						}
						if(plugin.getConfig().getBoolean("multiplayer.broadcastwin")){
							if(drawTeams.size() == 2){
								plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MinigameUtils.formStr("player.end.team.tie", 
										drawTeams.get(0).getChatColor() + drawTeams.get(0).getDisplayName() + ChatColor.WHITE, 
										drawTeams.get(1).getChatColor() + drawTeams.get(1).getDisplayName() + ChatColor.WHITE, 
										event.getMinigame().getName(true)));
							}
							else{
								plugin.getServer().broadcastMessage(ChatColor.RED + "[Minigames] " + MinigameUtils.formStr("player.end.team.tieCount", 
										drawTeams.size(), 
										event.getMinigame().getName(true)));
							}
							
							String scores = "";
							int c = 1;
							for(Team t : teamModule.getTeams()){
								scores += t.getChatColor().toString() + t.getScore();
								if(c != teamModule.getTeams().size())
									scores += ChatColor.WHITE + " : ";
								c++;
							}
							plugin.getServer().broadcastMessage(MinigameUtils.getLang("minigame.info.score") + " " + scores);
						}
					}
				}
			}
			else{
				MinigamePlayer player = null;
				int score = 0;
				for(MinigamePlayer ply : event.getMinigame().getPlayers()){
					if(ply.getScore() > 0){
						if(ply.getScore() > score){
							player = ply;
							score = ply.getScore();
						}
						else if(ply.getScore() == score){
							if(player != null && ply.getDeaths() < player.getDeaths()){
								player = ply;
							}
							else if(player == null){
								player = ply;
							}
						}
					}
				}
				List<MinigamePlayer> losers = new ArrayList<MinigamePlayer>();
				losers.addAll(event.getMinigame().getPlayers());
				List<MinigamePlayer> winners = new ArrayList<MinigamePlayer>();
				if(player != null){
					losers.remove(player);
					winners.add(player);
				}
				
				pdata.endMinigame(event.getMinigame(), winners, losers);
			}
		}
	}

}
