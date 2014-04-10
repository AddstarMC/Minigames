package com.pauldavdesign.mineauz.minigames.minigame.regions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Team;

public class Region {
	private String name;
	private Location point1;
	private Location point2;
	private List<RegionExecutor> executors = new ArrayList<RegionExecutor>();
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	
	public Region(String name, Location point1, Location point2){
		Location[] locs = MinigameUtils.getMinMaxSelection(point1, point2);
		this.point1 = locs[0].clone();
		this.point2 = locs[1].clone();
		this.name = name;
	}
	
	public boolean playerInRegion(MinigamePlayer player){
		if(player.getLocation().getWorld() == point1.getWorld()){
			int minx = point1.getBlockX();
			int maxx = point2.getBlockX();
			int plyx = player.getLocation().getBlockX();
			
			if(plyx >= minx && plyx <= maxx){
				int miny = point1.getBlockY();
				int maxy = point2.getBlockY();
				int plyy = player.getLocation().getBlockY();
				
				if(plyy >= miny && plyy <= maxy){
					int minz = point1.getBlockZ();
					int maxz = point2.getBlockZ();
					int plyz = player.getLocation().getBlockZ();
					
					if(plyz >= minz && plyz <= maxz){
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	public String getName(){
		return name;
	}
	
	public Location getFirstPoint(){
		return point1.clone();
	}
	
	public Location getSecondPoint(){
		return point2.clone();
	}
	
	public boolean hasPlayer(MinigamePlayer player){
		return players.contains(player);
	}
	
	public void addPlayer(MinigamePlayer player){
		players.add(player);
	}
	
	public void removePlayer(MinigamePlayer player){
		players.remove(player);
	}
	
	public List<MinigamePlayer> getPlayers(){
		return players;
	}
	
	public int addExecutor(RegionTrigger trigger, RegionAction action){
		executors.add(new RegionExecutor(trigger, action));
		return executors.size();
	}
	
	public int addExecutor(RegionExecutor exec){
		executors.add(exec);
		return executors.size();
	}
	
	public List<RegionExecutor> getExecutors(){
		return executors;
	}
	
	public void removeExecutor(int id){
		if(executors.size() <= id){
			executors.remove(id - 1);
		}
	}
	
	public void removeExecutor(RegionExecutor executor){
		if(executors.contains(executor)){
			executors.remove(executor);
		}
	}
	
	public void execute(RegionTrigger trigger, MinigamePlayer player){
		for(RegionExecutor exec : executors){
			if(exec.getTrigger() == trigger && !player.isDead()){
				RegionAction act = exec.getAction();
				if(act == RegionAction.KILL)
					player.getPlayer().damage(player.getPlayer().getHealth());
				else if(act == RegionAction.REVERT)
					Minigames.plugin.pdata.revertToCheckpoint(player);
				else if(act == RegionAction.QUIT)
					Minigames.plugin.pdata.quitMinigame(player, false);
				else if(act == RegionAction.END){
					if(player.getMinigame().getType() != MinigameType.SINGLEPLAYER){
						List<MinigamePlayer> w = null;
						List<MinigamePlayer> l = null;
						if(player.getMinigame().getType() == MinigameType.TEAMS){
							w = new ArrayList<MinigamePlayer>(player.getTeam().getPlayers());
							l = new ArrayList<MinigamePlayer>(player.getMinigame().getPlayers().size() - player.getTeam().getPlayers().size());
							for(Team t : player.getMinigame().getTeams()){
								if(t != player.getTeam())
									l.addAll(t.getPlayers());
							}
						}
						else{
							w = new ArrayList<MinigamePlayer>(1);
							l = new ArrayList<MinigamePlayer>(player.getMinigame().getPlayers().size());
							w.add(player);
							l.addAll(player.getMinigame().getPlayers());
							l.remove(player);
						}
						Minigames.plugin.pdata.endMinigame(player.getMinigame(), w, l);
					}
					else{
						Minigames.plugin.pdata.endMinigame(player);
					}
				}
			}
		}
	}
}
