package com.pauldavdesign.mineauz.minigames;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;

public class MinigameData {
	private Map<String, Minigame> minigames = new HashMap<String, Minigame>();
	private Map<String, Configuration> configs = new HashMap<String, Configuration>();
	private Map<String, MinigameType> minigameTypes = new HashMap<String, MinigameType>();
	private Map<String, Location> treasureLoc = new HashMap<String, Location>();
	private static Minigames plugin = Minigames.plugin;
	
	public MinigameData(){}
	
	public void startGlobalMinigame(final String minigame){
		final Minigame mgm = getMinigame(minigame);
		String gametype = mgm.getType();
		if(gametype.equals("th") && mgm.getLocation() != null){
			Location tcpos = mgm.getStartLocations().get(0).clone();
			Location maxcpos = new Location(tcpos.getWorld(), 0, 0, 0);
			Location mincpos = new Location(tcpos.getWorld(), 0, 0, 0);
			final Location rpos = tcpos;
			double rx = 0;
			double ry = 0;
			double rz = 0;
			final int maxradius;
			if(mgm.getMaxRadius() == 0){
				maxradius = 1000;
			}
			else{
				maxradius = mgm.getMaxRadius();
			}
			
			maxcpos.setX(tcpos.getX() + maxradius);
			mincpos.setX(tcpos.getX() - maxradius);
			maxcpos.setZ(tcpos.getZ() + maxradius);
			mincpos.setZ(tcpos.getZ() - maxradius);
			maxcpos.setY(tcpos.getY() + maxradius);
			mincpos.setY(tcpos.getY());
			
			rx = mincpos.getX() + (Math.random() * ((maxcpos.getX() - mincpos.getX()) + 1));
			ry = mincpos.getY() + (Math.random() * ((maxcpos.getY() - mincpos.getY()) + 1));
			rz = mincpos.getZ() + (Math.random() * ((maxcpos.getZ() - mincpos.getZ()) + 1));
			
			rpos.setX(rx);
			rpos.setY(ry);
			rpos.setZ(rz);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					if(getTreasureHuntLocation(minigame) != null){
						Location old = getTreasureHuntLocation(minigame);
						old.getBlock().setType(Material.AIR);
					}
				}
			});
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					if(getTreasureHuntLocation(minigame) != null){
						Location old = getTreasureHuntLocation(minigame);
						old.getBlock().setType(Material.AIR);
					}
					
					if(rpos.getBlock().getType() == Material.AIR){
						while(rpos.getBlock().getType() == Material.AIR){
							rpos.setY(rpos.getY() - 1);
						}
						rpos.setY(rpos.getY() + 1);

						rpos.getBlock().setType(Material.CHEST);
					}
					else
					{
						while(rpos.getBlock().getType() != Material.AIR){
							rpos.setY(rpos.getY() + 1);
						}
						rpos.getBlock().setType(Material.CHEST);
					}
					
					if(rpos.getBlock().getState() instanceof Chest){
						Chest chest = (Chest) rpos.getBlock().getState();
						
						if(!getMinigame(minigame).getDefaultPlayerLoadout().getItems().isEmpty()){
							int numitems = (int) Math.round(Math.random() * (mgm.getMaxTreasure() - mgm.getMinTreasure())) + mgm.getMinTreasure();
							if(numitems > mgm.getDefaultPlayerLoadout().getItems().size()){
								numitems = mgm.getDefaultPlayerLoadout().getItems().size();
							}
							Collections.shuffle(getMinigame(minigame).getDefaultPlayerLoadout().getItems());
							ItemStack[] items = new ItemStack[27];
							for(int i = 0; i < numitems; i++){
								items[i] = mgm.getDefaultPlayerLoadout().getItems().get(i);
							}
							Collections.shuffle(Arrays.asList(items));
							chest.getInventory().setContents(items);
						}
					}
					
					setTreasureHuntLocation(minigame, rpos.getBlock().getLocation());
					
					plugin.getServer().broadcast(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + "A treasure chest has appeared within " + maxradius + "m of " + getMinigame(minigame).getLocation() + "!", "minigame.treasure.announce");
					if(getMinigame(minigame).getThTimer() == null){
						getMinigame(minigame).setThTimer(new TreasureHuntTimer(minigame));
						getMinigame(minigame).getThTimer().start();
					}
				}
			});
			
		}
	}
	
	public void addMinigame(Minigame game){
		minigames.put(game.getName(), game);
	}
	
	public Minigame getMinigame(String minigame){
		if(minigames.containsKey(minigame)){
			return minigames.get(minigame);
		}
		
		for(String mg : minigames.keySet()){
			if(minigame.equalsIgnoreCase(mg)){
				return minigames.get(mg);
			}
		}
		
		return null;
	}
	
	public Map<String, Minigame> getAllMinigames(){
		return minigames;
	}
	
	public boolean hasMinigame(String minigame){
		boolean hasmg = minigames.containsKey(minigame);
		if(!hasmg){
			for(String mg : minigames.keySet()){
				if(mg.equalsIgnoreCase(minigame)){
					hasmg = true;
					break;
				}
			}
		}
		return hasmg;
	}
	
	public void removeMinigame(String minigame){
		if(minigames.containsKey(minigame)){
			minigames.remove(minigame);
		}
	}
	
	public void addConfigurationFile(String filename, Configuration config){
		configs.put(filename, config);
	}
	
	public Configuration getConfigurationFile(String filename){
		if(configs.containsKey(filename)){
			return configs.get(filename);
		}
		return null;
	}
	
	public boolean hasConfigurationFile(String filename){
		return configs.containsKey(filename);
	}
	
	public void removeConfigurationFile(String filename){
		if(configs.containsKey(filename)){
			configs.remove(filename);
		}
	}
	
	public void setTreasureHuntLocation(String minigame, Location location){
		treasureLoc.put(minigame, location);
	}
	
	public boolean hasTreasureHuntLocation(String minigame){
		return treasureLoc.containsKey(minigame);
	}
	
	public Location getTreasureHuntLocation(String minigame){
		if(treasureLoc.containsKey(minigame)){
			return treasureLoc.get(minigame);
		}
		return null;
	}
	
	public Set<String> getAllTreasureHuntLocation(){
		return treasureLoc.keySet();
	}
	
	public boolean hasTreasureHuntLocations(){
		if(!treasureLoc.isEmpty()){
			return true;
		}
		return false;
	}
	
	public void removeTreasureHuntLocation(String minigame){
		treasureLoc.remove(minigame);
	}
	
	public void removeTreasure(String minigame){
		if(getTreasureHuntLocation(minigame) != null){
			if(getTreasureHuntLocation(minigame).getBlock().getState() instanceof Chest){
				Chest chest = (Chest) getTreasureHuntLocation(minigame).getBlock().getState();
				chest.getInventory().clear();
			}
			Location old = getTreasureHuntLocation(minigame);
			old.getBlock().setType(Material.AIR);
		}
	}
	
	public Location minigameLocations(String minigame, String type, Configuration save) {
		Double locx = (Double) save.get(minigame + "." + type + ".x");
		Double locy = (Double) save.get(minigame + "." + type + ".y");
		Double locz = (Double) save.get(minigame + "." + type + ".z");
		Float yaw = new Float(save.get(minigame + "." + type + ".yaw").toString());
		Float pitch = new Float(save.get(minigame + "." + type + ".pitch").toString());
		String world = (String) save.get(minigame + "." + type + ".world");
		
		Location loc = new Location(plugin.getServer().getWorld(world), locx, locy, locz, yaw, pitch);
		return loc;
	}
	
	public Location minigameLocationsShort(String minigame, String type, Configuration save) {
		Double locx = (Double) save.get(minigame + "." + type + ".x");
		Double locy = (Double) save.get(minigame + "." + type + ".y");
		Double locz = (Double) save.get(minigame + "." + type + ".z");
		String world = (String) save.get(minigame + "." + type + ".world");
		
		Location loc = new Location(plugin.getServer().getWorld(world), locx, locy, locz);
		return loc;
	}
	
	public void minigameSetLocations(String minigame, Location loc, String type, FileConfiguration save){
		save.set(minigame + "." + type + "." + ".x", loc.getX());
		save.set(minigame + "." + type + "." + ".y", loc.getY());
		save.set(minigame + "." + type + "." + ".z", loc.getZ());
		save.set(minigame + "." + type + "." + ".yaw", loc.getYaw());
		save.set(minigame + "." + type + "." + ".pitch", loc.getPitch());
		save.set(minigame + "." + type + "." + ".world", loc.getWorld().getName());
	}
	
	public void minigameSetLocationsShort(String minigame, Location loc, String type, FileConfiguration save){
		save.set(minigame + "." + type + "." + ".x", loc.getX());
		save.set(minigame + "." + type + "." + ".y", loc.getY());
		save.set(minigame + "." + type + "." + ".z", loc.getZ());
		save.set(minigame + "." + type + "." + ".world", loc.getWorld().getName());
	}
	
	public void addMinigameType(MinigameType minigameType){
		minigameTypes.put(minigameType.getLabel(), minigameType);
		Minigames.log.info("Loaded " + minigameType.getLabel() + " minigame type.");
	}
	
	public MinigameType minigameType(String name){
		if(minigameTypes.containsKey(name)){
			return minigameTypes.get(name);
		}
		return null;
	}
	
	public Set<String> getMinigameTypes(){
		return minigameTypes.keySet();
	}
}
