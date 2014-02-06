package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

public class StoredPlayerCheckpoints {
	private String playerName;
	private Map<String, Location> checkpoints;
	private Map<String, List<String>> flags;
	private Map<String, Long> storedTime;
	private Map<String, Integer> storedDeaths;
	private Map<String, Integer> storedReverts;
	private Location globalCheckpoint;
	
	public StoredPlayerCheckpoints(String name){
		playerName = name;
		checkpoints = new HashMap<String, Location>();
		flags = new HashMap<String, List<String>>();
		storedTime = new HashMap<String, Long>();
		storedDeaths = new HashMap<String, Integer>();
		storedReverts = new HashMap<String, Integer>();
	}
	
	public StoredPlayerCheckpoints(String name, String minigame, Location checkpoint, Long time, Integer deaths, Integer reverts){
		playerName = name;
		checkpoints = new HashMap<String, Location>();
		checkpoints.put(minigame, checkpoint);
		flags = new HashMap<String, List<String>>();
		storedTime = new HashMap<String, Long>();
		storedTime.put(minigame, time);
		storedDeaths = new HashMap<String, Integer>();
		storedDeaths.put(minigame, deaths);
		storedReverts = new HashMap<String, Integer>();
		storedReverts.put(minigame, reverts);
	}
	
	public StoredPlayerCheckpoints(String name, Location checkpoint){
		playerName = name;
		globalCheckpoint = checkpoint;
		checkpoints = new HashMap<String, Location>();
		flags = new HashMap<String, List<String>>();
		storedTime = new HashMap<String, Long>();
		storedDeaths = new HashMap<String, Integer>();
		storedReverts = new HashMap<String, Integer>();
	}
	
	public void addCheckpoint(String minigame, Location checkpoint){
		checkpoints.put(minigame, checkpoint);
	}
	
	public void removeCheckpoint(String minigame){
		if(checkpoints.containsKey(minigame)){
			checkpoints.remove(minigame);
		}
	}
	
	public boolean hasCheckpoint(String minigame){
		if(checkpoints.containsKey(minigame)){
			return true;
		}
		return false;
	}
	
	public Location getCheckpoint(String minigame){
		return checkpoints.get(minigame);
	}
	
	public boolean hasGlobalCheckpoint(){
		if(globalCheckpoint != null){
			return true;
		}
		return false;
	}
	
	public Location getGlobalCheckpoint(){
		return globalCheckpoint;
	}
	
	public void setGlobalCheckpoint(Location checkpoint){
		globalCheckpoint = checkpoint;
	}
	
	public boolean hasNoCheckpoints(){
		return checkpoints.isEmpty();
	}
	
	public boolean hasFlags(String minigame){
		if(flags.containsKey(minigame)){
			return true;
		}
		return false;
	}
	
	public void addFlags(String minigame, List<String> flagList){
		flags.put(minigame, new ArrayList<String>(flagList));
	}
	
	public List<String> getFlags(String minigame){
		return flags.get(minigame);
	}
	
	public void removeFlags(String minigame){
		flags.remove(minigame);
	}
	
	public void addTime(String minigame, long time){
		storedTime.put(minigame, time);
	}
	
	public Long getTime(String minigame){
		return storedTime.get(minigame);
	}
	
	public boolean hasTime(String minigame){
		if(storedTime.containsKey(minigame)){
			return true;
		}
		return false;
	}
	
	public void removeTime(String minigame){
		storedTime.remove(minigame);
	}
	
	public void addDeaths(String minigame, int deaths){
		storedDeaths.put(minigame, deaths);
	}
	
	public Integer getDeaths(String minigame){
		return storedDeaths.get(minigame);
	}
	
	public boolean hasDeaths(String minigame){
		if(storedDeaths.containsKey(minigame)){
			return true;
		}
		return false;
	}
	
	public void removeDeaths(String minigame){
		storedDeaths.remove(minigame);
	}
	
	public void addReverts(String minigame, int reverts){
		storedReverts.put(minigame, reverts);
	}
	
	public Integer getReverts(String minigame){
		return storedReverts.get(minigame);
	}
	
	public boolean hasReverts(String minigame){
		if(storedReverts.containsKey(minigame)){
			return true;
		}
		return false;
	}
	
	public void removeReverts(String minigame){
		storedReverts.remove(minigame);
	}
	
	public void saveCheckpoints(){
		MinigameSave save = new MinigameSave("playerdata/checkpoints/" + playerName.toLowerCase());
		save.deleteFile();
		if(hasNoCheckpoints()) return;
		
		save = new MinigameSave("playerdata/checkpoints/" + playerName.toLowerCase());
		for(String mgm : checkpoints.keySet()){
			save.getConfig().set(mgm, null);
			save.getConfig().set(mgm + ".x", checkpoints.get(mgm).getX());
			save.getConfig().set(mgm + ".y", checkpoints.get(mgm).getY());
			save.getConfig().set(mgm + ".z", checkpoints.get(mgm).getZ());
			save.getConfig().set(mgm + ".yaw", checkpoints.get(mgm).getYaw());
			save.getConfig().set(mgm + ".pitch", checkpoints.get(mgm).getPitch());
			save.getConfig().set(mgm + ".world", checkpoints.get(mgm).getWorld().getName());
		}
		for(String mgm : flags.keySet()){
			save.getConfig().set(mgm + ".flags", getFlags(mgm));
		}
		
		for(String mgm : storedTime.keySet()){
			save.getConfig().set(mgm + ".time", getTime(mgm));
		}
		
		for(String mgm : storedDeaths.keySet()){
			save.getConfig().set(mgm + ".deaths", getDeaths(mgm));
		}
		
		for(String mgm : storedReverts.keySet()){
			save.getConfig().set(mgm + ".reverts", getReverts(mgm));
		}
		
		if(hasGlobalCheckpoint()){
			save.getConfig().set("globalcheckpoint.x", globalCheckpoint.getX());
			save.getConfig().set("globalcheckpoint.y", globalCheckpoint.getY());
			save.getConfig().set("globalcheckpoint.z", globalCheckpoint.getZ());
			save.getConfig().set("globalcheckpoint.yaw", globalCheckpoint.getYaw());
			save.getConfig().set("globalcheckpoint.pitch", globalCheckpoint.getPitch());
			save.getConfig().set("globalcheckpoint.world", globalCheckpoint.getWorld().getName());
		}
		save.saveConfig();
	}
	
	public void loadCheckpoints(){
		MinigameSave save = new MinigameSave("playerdata/checkpoints/" + playerName.toLowerCase());
		Set<String> mgms = save.getConfig().getKeys(false);
		for(String mgm : mgms){
			if(!mgm.equals("globalcheckpoint")){
				Double locx = (Double) save.getConfig().get(mgm + ".x");
				Double locy = (Double) save.getConfig().get(mgm + ".y");
				Double locz = (Double) save.getConfig().get(mgm + ".z");
				Float yaw = new Float(save.getConfig().get(mgm + ".yaw").toString());
				Float pitch = new Float(save.getConfig().get(mgm + ".pitch").toString());
				String world = (String) save.getConfig().get(mgm + ".world");
				
				Location loc = new Location(Minigames.plugin.getServer().getWorld(world), locx, locy, locz, yaw, pitch);
				checkpoints.put(mgm, loc);
				if(save.getConfig().contains(mgm + ".flags")){
					flags.put(mgm, save.getConfig().getStringList(mgm + ".flags"));
				}
				
				if(save.getConfig().contains(mgm + ".time")){
					storedTime.put(mgm, save.getConfig().getLong(playerName + "." + mgm + ".time"));
				}
				
				if(save.getConfig().contains(mgm + ".deaths")){
					storedDeaths.put(mgm, save.getConfig().getInt(mgm + ".deaths"));
				}
				
				if(save.getConfig().contains(mgm + ".reverts")){
					storedReverts.put(mgm, save.getConfig().getInt(playerName + "." + mgm + ".reverts"));
				}
			}
		}
		
		if(save.getConfig().contains("globalcheckpoint")){
			double x = save.getConfig().getDouble("globalcheckpoint.x");
			double y = save.getConfig().getDouble("globalcheckpoint.y");
			double z = save.getConfig().getDouble("globalcheckpoint.z");
			Float yaw = new Float(save.getConfig().get("globalcheckpoint.yaw").toString());
			Float pitch = new Float(save.getConfig().get("globalcheckpoint.pitch").toString());
			String world = save.getConfig().getString("globalcheckpoint.world");
			
			globalCheckpoint = new Location(Minigames.plugin.getServer().getWorld(world), x, y, z, yaw, pitch);
		}
	}
}
