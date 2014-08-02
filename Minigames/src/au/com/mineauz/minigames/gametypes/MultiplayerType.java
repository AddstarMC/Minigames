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

import au.com.mineauz.minigames.MinigameData;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.MultiplayerTimer;
import au.com.mineauz.minigames.PlayerData;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class MultiplayerType extends MinigameTypeBase{
	private static Minigames plugin = Minigames.plugin;
	private PlayerData pdata = plugin.pdata;
	private MinigameData mdata = plugin.mdata;
	
	public MultiplayerType(){
		setType(MinigameType.MULTIPLAYER);
	}

	@Override
	public boolean joinMinigame(MinigamePlayer player, Minigame mgm) {
		if(!LobbySettingsModule.getMinigameModule(mgm).canInteractPlayerWait())
			player.setCanInteract(false);
		if(!LobbySettingsModule.getMinigameModule(mgm).canMovePlayerWait())
			player.setFrozen(true);
		
		if(!mgm.isNotWaitingForPlayers()){
			if(mgm.getMpTimer() == null && mgm.getPlayers().size() == mgm.getMinPlayers()){
				mgm.setMpTimer(new MultiplayerTimer(mgm));
				mgm.getMpTimer().startTimer();
				if(mgm.getPlayers().size() == mgm.getMaxPlayers()){
					mgm.getMpTimer().setPlayerWaitTime(0);
					mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
				}
			}
			else if(mgm.getMpTimer() != null && mgm.getPlayers().size() == mgm.getMaxPlayers()){
				mgm.getMpTimer().setPlayerWaitTime(0);
				mdata.sendMinigameMessage(mgm, MinigameUtils.getLang("minigame.skipWaitTime"), "info", null);
			}
			else if(mgm.getMpTimer() == null){
				int neededPlayers = mgm.getMinPlayers() - mgm.getPlayers().size();
				mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("minigame.waitingForPlayers", neededPlayers), null, null);
			}
		}
		else{
			player.setLatejoining(true);
			player.sendMessage(MinigameUtils.formStr("minigame.lateJoin", 5)); //TODO: Late join delay variable
			final MinigamePlayer fply = player;
			final Minigame fmgm = mgm;
			if(mgm.isTeamGame()){
				Team smTeam = null;
				for(Team team : TeamsModule.getMinigameModule(mgm).getTeams()){
					if(smTeam == null || team.getPlayers().size() < smTeam.getPlayers().size()){
						smTeam = team;
					}
				}
				
				smTeam.addPlayer(player);
				player.sendMessage(MinigameUtils.formStr("player.team.assign.joinTeam", smTeam.getChatColor() + smTeam.getDisplayName()), null);
				
				final Team fteam = smTeam;
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						if(fply.isInMinigame()){
							List<Location> locs = new ArrayList<Location>();
							if(TeamsModule.getMinigameModule(fmgm).hasTeamStartLocations()){
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
						}
					}
				}, 5 * 20); //TODO: Latejoin variable
			}
			else{
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
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
						}
					}
				}, 5 * 20); //TODO: Latejoin variable
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
		
		if(mgm.isTeamGame()){
			player.removeTeam();
			for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
				if(t.getPlayers().size() > 0)
					teamsWithPlayers ++;
			}
			
			if(mgm.getMpBets() != null && !mgm.isNotWaitingForPlayers() && !forced){
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
		
		if(mgm.isTeamGame() && mgm.getPlayers().size() >= 1 && 
				teamsWithPlayers == 1 && mgm.hasStarted() && !forced){
			Team winner = null;
			for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
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
		else if(mgm.getPlayers().size() - 1 < mgm.getMinPlayers() && 
				mgm.getMpTimer() != null && 
				mgm.getMpTimer().getStartWaitTimeLeft() != 0){
			mgm.getMpTimer().setPlayerWaitTime(Minigames.plugin.getConfig().getInt("multiplayer.waitforplayers"));
			mgm.getMpTimer().pauseTimer();
			mgm.getMpTimer().removeTimer();
			mgm.setMpTimer(null);
			mdata.sendMinigameMessage(mgm, MinigameUtils.formStr("minigame.waitingForPlayers", 1), null, null);
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
			for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
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
			Location respawnPos;
			if(ply.getMinigame().isTeamGame()){
				Team team = ply.getTeam();
				if(mg.hasStarted() && !ply.isLatejoining()){
					if(mg.isAllowedMPCheckpoints() && ply.hasCheckpoint()){
						respawnPos = ply.getCheckpoint();
					}
					else{
						List<Location> starts = new ArrayList<Location>();
						if(TeamsModule.getMinigameModule(mg).hasTeamStartLocations()){
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
					respawnPos = mg.getLobbyPosition();
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
					respawnPos = mg.getLobbyPosition();
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
		if(event.getMinigame().getType() == MinigameType.MULTIPLAYER){
			if(event.getMinigame().isTeamGame()){
				Minigame mgm = event.getMinigame();
				if(TeamsModule.getMinigameModule(mgm).getDefaultWinner() != null){
					TeamsModule tm = TeamsModule.getMinigameModule(mgm);
					List<MinigamePlayer> w;
					List<MinigamePlayer> l;
					if(TeamsModule.getMinigameModule(mgm).hasTeam(TeamsModule.getMinigameModule(mgm).getDefaultWinner())){
						w = new ArrayList<MinigamePlayer>(tm.getTeam(tm.getDefaultWinner()).getPlayers().size());
						l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size() - tm.getTeam(tm.getDefaultWinner()).getPlayers().size());
						w.addAll(tm.getTeam(tm.getDefaultWinner()).getPlayers());
					}
					else{
						w = new ArrayList<MinigamePlayer>();
						l = new ArrayList<MinigamePlayer>(mgm.getPlayers().size());
					}
					
					for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
						if(t.getColor() != TeamsModule.getMinigameModule(mgm).getDefaultWinner())
							l.addAll(t.getPlayers());
					}
					plugin.pdata.endMinigame(mgm, w, l);
				}
				else{
					List<Team> drawTeams = new ArrayList<Team>();
					Team winner = null;
					for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
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
						for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
							if(t != winner)
								l.addAll(t.getPlayers());
						}
						pdata.endMinigame(mgm, w, l);
					}
					else{
						List<MinigamePlayer> players = new ArrayList<MinigamePlayer>(mgm.getPlayers());
						for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
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
											event.getMinigame().getName(true)), "error");
								}
								else{
									ply.sendMessage(MinigameUtils.formStr("player.end.team.tieCount", 
											drawTeams.size(), 
											event.getMinigame().getName(true)), "error");
								}
								String scores = "";
								int c = 1;
								for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
									scores += t.getChatColor().toString() + t.getScore();
									if(c != TeamsModule.getMinigameModule(mgm).getTeams().size())
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
							for(Team t : TeamsModule.getMinigameModule(mgm).getTeams()){
								scores += t.getChatColor().toString() + t.getScore();
								if(c != TeamsModule.getMinigameModule(mgm).getTeams().size())
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
