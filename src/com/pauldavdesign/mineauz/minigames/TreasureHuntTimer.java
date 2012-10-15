package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TreasureHuntTimer extends Thread{
	private String minigame = null;
	private static Minigames plugin = Minigames.plugin;
	private Minigame mgm = null;
	private MinigameData mdata = plugin.mdata;
	private int time = 0;
	private int findtime;
	private int waittime;
	private boolean active = true;
	private boolean inworld = true;
	private boolean chestfound = false;
	private ArrayList<String> curHints = new ArrayList<String>();
	private Map<String, Long> lastCommand = new HashMap<String, Long>();
	private Location block = mdata.getTreasureHuntLocation(minigame);
	private int hintTime1, hintTime2, hintTime3, hintTime4;
	
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
	
	public void run(){
		while(active){
			if(inworld){
				time -= 1;
				if(time <= 0){
					inworld = false;
					time = waittime;
					Location old = mdata.getTreasureHuntLocation(minigame);
					mdata.removeTreasure(minigame);
					curHints = new ArrayList<String>();
					if(chestfound == false){
						plugin.getServer().broadcast(ChatColor.LIGHT_PURPLE + "Time's up! The " + minigame + " treasure was not found and has been removed from the world.", "minigame.treasure.announce");
						plugin.getServer().broadcast(ChatColor.GRAY + "Coordinates: X:" + old.getBlockX() + ", Y:" + old.getBlockY() + ", Z:" + old.getBlockZ(), "minigame.treasure.announce");
					}
				}
				else if(time == hintTime2 && chestfound == false){
					block = mdata.getTreasureHuntLocation(minigame);
					block.setY(block.getY() - 1);
					plugin.getServer().broadcast(ChatColor.GREEN + "Hint:" + ChatColor.GRAY + " The " + minigame + " treasure rests upon " + block.getBlock().getType().toString().toLowerCase().replace("_", " "), "minigame.treasure.announce");
					curHints.add(ChatColor.GRAY + "The " + minigame + " treasure rests upon " + block.getBlock().getType().toString().toLowerCase().replace("_", " "));
					block.setY(block.getY() + 1);
				}
				else if(time == hintTime1 && chestfound == false){
					double dfcx = 0.0;
					double dfcz = 0.0;
					String xdir = null;
					String zdir = null;
					
					if(mgm.getStartLocations().get(0).getX() > block.getX()){
						dfcx = mgm.getStartLocations().get(0).getX() - block.getX();
						xdir = "West";
					}
					else{
						dfcx = block.getX() - mgm.getStartLocations().get(0).getX();
						xdir = "East";
					}
					if(mgm.getStartLocations().get(0).getZ() > block.getZ()){
						dfcz = mgm.getStartLocations().get(0).getZ() - block.getZ();
						zdir = "North";
					}
					else{
						dfcz = block.getZ() - mgm.getStartLocations().get(0).getZ();
						zdir = "South";
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
					
					plugin.getServer().broadcast(ChatColor.GREEN + "Hint:" + ChatColor.GRAY + " The " + minigame + " treasure lies to the " + dir + " of " + mdata.getMinigame(minigame).getLocation(), "minigame.treasure.hint");
					curHints.add(ChatColor.GRAY + "The " + minigame + " treasure lies to the " + dir + " of " + mgm.getLocation());
				}
				else if(time == hintTime3 && chestfound == false){
					int height = block.getBlockY();
					if(height > 62){
						plugin.getServer().broadcast(ChatColor.GREEN + "Hint:" + ChatColor.GRAY + " The " + minigame + " treasure is " + (height - 62) + "m above sea level", "minigame.treasure.hint");
						curHints.add(ChatColor.GRAY + "The " + minigame + " treasure is " + (height - 62) + "m above sea level");
					}
					else{
						plugin.getServer().broadcast(ChatColor.GREEN + "Hint:" + ChatColor.GRAY + " The " + minigame + " treasure is " + (62 - height) + "m below sea level", "minigame.treasure.hint");
						curHints.add(ChatColor.GRAY + "The " + minigame + " treasure is " + (62 - height) + "m below sea level");
					}
				}
				else if(time == hintTime4 && chestfound == false){
					plugin.getServer().broadcast(ChatColor.GREEN + "Hint:" + ChatColor.GRAY + " The " + minigame + " treasure resides in the " + block.getBlock().getBiome().toString().toLowerCase().replace("_", " ") + " biome", "minigame.treasure.hint");
					curHints.add(ChatColor.GRAY + "The " + minigame + " treasure resides in the " + block.getBlock().getBiome().toString().toLowerCase().replace("_", " ") + " biome");
				}
				
			}
			else{
				time -= 1;
				if(time <= 0){
					inworld = true;
					chestfound = false;
					mdata.startGlobalMinigame(minigame);
					
					block = mdata.getTreasureHuntLocation(minigame);
					findtime = plugin.getConfig().getInt("treasurehunt.findtime");
					waittime = plugin.getConfig().getInt("treasurehunt.waittime");
					time = findtime;
					hintTime1 = findtime - 1;
					hintTime2 = (int) (findtime * 0.75);
					hintTime3 = (int) (findtime * 0.5);
					hintTime4 = (int) (findtime * 0.25);
				}
			}
			
			try{
				Thread.sleep(60000);
			}
			catch(Exception e){
				plugin.getLogger().log(Level.SEVERE, "Error in TreasureTimer Thread.sleep!");
			}
		}
	}
	
	public void hints(Player player){
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
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You are not even close to finding the treasure!");
			}
			else if(distance > maxradius / 2){
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You are still a fair way off");
			}
			else if(distance > maxradius / 4){
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You are close, but no cigar!");
			}
			else if(distance > 50){
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You are really close now!");
			}
			else if(distance > 20){
				player.sendMessage(ChatColor.LIGHT_PURPLE + "You are within 50m of it!");
			}
			else if(distance < 20){
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Oh my, you're so close! I can smell its riches!");
			}
			player.sendMessage(ChatColor.GRAY + "Time left: " + time + " minutes");
			player.sendMessage(ChatColor.GREEN + "Global Hints:");
			if(curHints.isEmpty()){
				player.sendMessage(ChatColor.GRAY + "Sorry, there are no public hints to be displayed right now");
			}
			else{
				for(String h : curHints){
					player.sendMessage(h);
				}
			}

			lastCommand.put(player.getName(), Calendar.getInstance().getTimeInMillis());
		}
		else{
			player.sendMessage(ChatColor.RED + "You currently cannot use this command for this treasure hunt");
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
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean getActive(){
		return active;
	}
}
