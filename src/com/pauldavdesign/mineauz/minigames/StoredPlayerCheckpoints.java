package com.pauldavdesign.mineauz.minigames;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

public class StoredPlayerCheckpoints {
	private String playerName;
	private Map<String, Location> checkpoints;
	
	public StoredPlayerCheckpoints(String name){
		playerName = name;
		checkpoints = new HashMap<String, Location>();
	}
	
	public StoredPlayerCheckpoints(String name, String minigame, Location checkpoint){
		playerName = name;
		checkpoints = new HashMap<String, Location>();
		checkpoints.put(minigame, checkpoint);
		saveCheckpoints();
	}
	
	public void addCheckpoint(String minigame, Location checkpoint){
		checkpoints.put(minigame, checkpoint);
		saveCheckpoints();
	}
	
	public void removeCheckpoint(String minigame){
		if(checkpoints.containsKey(minigame)){
			checkpoints.remove(minigame);
		}
		saveCheckpoints();
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
	
	public boolean hasNoCheckpoints(){
		return checkpoints.isEmpty();
	}
	
	public void saveCheckpoints(){
		MinigameSave save = new MinigameSave("storedCheckpoints");
		save.getConfig().set(playerName, null);
		for(String mgm : checkpoints.keySet()){
			save.getConfig().set(playerName + "." + mgm + ".x", checkpoints.get(mgm).getX());
			save.getConfig().set(playerName + "." + mgm + ".y", checkpoints.get(mgm).getY());
			save.getConfig().set(playerName + "." + mgm + ".z", checkpoints.get(mgm).getZ());
			save.getConfig().set(playerName + "." + mgm + ".yaw", checkpoints.get(mgm).getYaw());
			save.getConfig().set(playerName + "." + mgm + ".pitch", checkpoints.get(mgm).getPitch());
			save.getConfig().set(playerName + "." + mgm + ".world", checkpoints.get(mgm).getWorld().getName());
		}
		save.saveConfig();
	}
	
	public void loadCheckpoints(){
		MinigameSave save = new MinigameSave("storedCheckpoints");
		Set<String> mgms = save.getConfig().getConfigurationSection(playerName).getKeys(false);
		for(String mgm : mgms){
			Double locx = (Double) save.getConfig().get(playerName + "." + mgm + ".x");
			Double locy = (Double) save.getConfig().get(playerName + "." + mgm + ".y");
			Double locz = (Double) save.getConfig().get(playerName + "." + mgm + ".z");
			Float yaw = new Float(save.getConfig().get(playerName + "." + mgm + ".yaw").toString());
			Float pitch = new Float(save.getConfig().get(playerName + "." + mgm + ".pitch").toString());
			String world = (String) save.getConfig().get(playerName + "." + mgm + ".world");
			
			Location loc = new Location(Minigames.plugin.getServer().getWorld(world), locx, locy, locz, yaw, pitch);
			checkpoints.put(mgm, loc);
		}
	}
}
