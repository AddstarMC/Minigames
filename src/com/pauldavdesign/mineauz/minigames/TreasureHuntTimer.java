package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class TreasureHuntTimer{
	private String minigame = null;
	private static Minigames plugin = Minigames.plugin;
	private Minigame mgm = null;
	private MinigameData mdata = plugin.mdata;
	private int time = 0;
	private int findtime;
	private int waittime;
	private boolean inworld = true;
	private boolean chestfound = false;
	private ArrayList<String> curHints = new ArrayList<String>();
	private Map<String, Long> lastCommand = new HashMap<String, Long>();
	private Location block = mdata.getTreasureHuntLocation(minigame);
	private int hintTime1, hintTime2, hintTime3, hintTime4;
	private int taskID = -1;
	private boolean paused = false;
	
	
	public TreasureHuntTimer(String minigame){
		this.minigame = minigame;
		mgm = mdata.getMinigame(minigame);
		block = mdata.getTreasureHuntLocation(minigame);
		findtime = plugin.getConfig().getInt("treasurehunt.findtime");
		waittime = plugin.getConfig().getInt("treasurehunt.waittime");
		time = findtime;
		hintTime1 = findtime - 1;
		hintTime2 = (int) (findtime * 0.75);
		hintTime3 = (int) (findtime * 0.5);
		hintTime4 = (int) (findtime * 0.25);
	}
	
	public void startTimer(){
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(inworld && !paused){
					time -= 1;
					if(time <= 0){
						inworld = false;
						time = waittime;
						Location old = mdata.getTreasureHuntLocation(minigame);
						mdata.removeTreasure(minigame);
						curHints = new ArrayList<String>();
						if(chestfound == false){
							plugin.getServer().broadcast(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.treasurehunt.plyDespawn", minigame), "minigame.treasure.announce");
							plugin.getServer().broadcast(ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.plyDespawnCoords", old.getBlockX(), old.getBlockY(), old.getBlockZ()), "minigame.treasure.announce");
						}
					}
					else if(time == hintTime2 && chestfound == false){
						block.setY(block.getY() - 1);
						String hint = MinigameUtils.formStr("minigame.treasurehunt.hint2", minigame, block.getBlock().getType().toString().toLowerCase().replace("_", " "));
						plugin.getServer().broadcast(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + hint, "minigame.treasure.announce");
						curHints.add(ChatColor.GRAY + hint);
						block.setY(block.getY() + 1);
					}
					else if(time == hintTime1 && chestfound == false){
						block = mdata.getTreasureHuntLocation(minigame);
						
						double dfcx = 0.0;
						double dfcz = 0.0;
						String xdir = null;
						String zdir = null;
						
						if(mgm.getStartLocations().get(0).getX() > block.getX()){
							dfcx = mgm.getStartLocations().get(0).getX() - block.getX();
							xdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.west");
						}
						else{
							dfcx = block.getX() - mgm.getStartLocations().get(0).getX();
							xdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.east");
						}
						if(mgm.getStartLocations().get(0).getZ() > block.getZ()){
							dfcz = mgm.getStartLocations().get(0).getZ() - block.getZ();
							zdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.north");
						}
						else{
							dfcz = block.getZ() - mgm.getStartLocations().get(0).getZ();
							zdir = MinigameUtils.getLang("minigame.treasurehunt.hint1.south");
						}
						String dir = null;
						
						if(dfcz > dfcx){
							if(dfcx > dfcz / 2){
								dir = zdir + xdir.toLowerCase();
							}
							else{
								dir = zdir;
							}
						}
						else{
							if(dfcz > dfcx / 2){
								dir = zdir + xdir.toLowerCase();
							}
							else{
								dir = xdir;
							}
						}
						String hint = MinigameUtils.formStr("minigame.treasurehunt.hint1.hint", minigame, dir, mdata.getMinigame(minigame).getLocation());
						plugin.getServer().broadcast(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + hint, "minigame.treasure.hint");
						curHints.add(ChatColor.GRAY + hint);
					}
					else if(time == hintTime3 && chestfound == false){
						int height = block.getBlockY();
						String dir;
						int dist;
						if(height > 62){
							dist = height - 62;
							dir = MinigameUtils.getLang("minigame.treasurehunt.hint3.above");
						}
						else{
							dist = 62 - height;
							dir = MinigameUtils.getLang("minigame.treasurehunt.hint3.below");
						}
						String hint = MinigameUtils.formStr("minigame.treasurehunt.hint3.hint", minigame, dist, dir);
						plugin.getServer().broadcast(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + hint, "minigame.treasure.hint");
						curHints.add(ChatColor.GRAY + hint);
					}
					else if(time == hintTime4 && chestfound == false){
						String hint = MinigameUtils.formStr("minigame.treasurehunt.hint4", minigame, block.getBlock().getBiome().toString().toLowerCase().replace("_", " "));
						plugin.getServer().broadcast(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + hint, "minigame.treasure.hint");
						curHints.add(ChatColor.GRAY + hint);
					}
					
				}
				else if(!paused){
					time -= 1;
					if(time <= 0){
						inworld = true;
						chestfound = false;
						mdata.startGlobalMinigame(minigame);
						
						findtime = plugin.getConfig().getInt("treasurehunt.findtime");
						waittime = plugin.getConfig().getInt("treasurehunt.waittime");
						time = findtime;
						hintTime1 = findtime - 1;
						hintTime2 = (int) (findtime * 0.75);
						hintTime3 = (int) (findtime * 0.5);
						hintTime4 = (int) (findtime * 0.25);
					}
				}
			}
		}, 1200, 1200);
	}
	
	public void hints(Player player){
		if(player.getWorld().getName().equals(block.getWorld().getName())){
			long lastuse = 300000;
			Location ploc = player.getLocation();
			if(lastCommand.containsKey(player.getName())){
				long curtime = Calendar.getInstance().getTimeInMillis();
				lastuse = curtime - lastCommand.get(player.getName());
			}
			double distance = ploc.distance(block);
			int maxradius = mgm.getMaxRadius();
			if(lastuse >= 300000){
				if(distance > maxradius){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance6"));
				}
				else if(distance > maxradius / 2){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance5"));
				}
				else if(distance > maxradius / 4){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance4"));
				}
				else if(distance > 50){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance3"));
				}
				else if(distance > 20){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance2"));
				}
				else if(distance < 20){
					player.sendMessage(ChatColor.LIGHT_PURPLE + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.distance1"));
				}
				player.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.timeLeft", time));
				player.sendMessage(ChatColor.GREEN + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.globalHints"));
				if(curHints.isEmpty()){
					player.sendMessage(ChatColor.GRAY + MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.noHint"));
				}
				else{
					for(String h : curHints){
						player.sendMessage(h);
					}
				}
	
				lastCommand.put(player.getName(), Calendar.getInstance().getTimeInMillis());
			}
			else{
				player.sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.noUse", minigame));
				int nextuse = (300000 - (int) (Calendar.getInstance().getTimeInMillis() - lastCommand.get(player.getName()))) / 1000;
				player.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.nextUse",MinigameUtils.convertTime(nextuse)));
				player.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.treasureTimeLeft", time));
			}
		}
		else{
			String world = block.getWorld().getName();
			if(world.equalsIgnoreCase("world")){
				world = MinigameUtils.getLang("minigame.treasurehunt.playerSpecificHint.wrongWorld.overworld");
			}
			player.sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.treasurehunt.playerSpecificHint.wrongWorld", world));
		}
	}
	
	public boolean getTreasureFound(){
		return chestfound;
	}
	
	public void setTreasureFound(boolean found){
		chestfound = found;
	}
	
	public void setTimeLeft(int time){
		this.time = time;
	}
	
	public boolean getChestInWorld(){
		return inworld;
	}
	
	public void stopTimer(){
		if(taskID != -1){
			Bukkit.getScheduler().cancelTask(taskID);
		}
	}
	
	public void pauseTimer(boolean pause){
		paused = pause;
	}
}
