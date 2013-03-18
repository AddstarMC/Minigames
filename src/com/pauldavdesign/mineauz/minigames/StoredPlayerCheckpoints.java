package com.pauldavdesign.mineauz.minigames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

public class StoredPlayerCheckpoints {
	private String playerName;
	private Map<String, Location> checkpoints;
	private Map<String, List<String>> flags;
	private Location globalCheckpoint;
	
	public StoredPlayerCheckpoints(String name){
		playerName = name;
		checkpoints = new HashMap<String, Location>();
		flags = new HashMap<String, List<String>>();
	}
	
	public StoredPlayerCheckpoints(String name, String minigame, Location checkpoint){
		playerName = name;
		checkpoints = new HashMap<String, Location>();
		checkpoints.put(minigame, checkpoint);
		flags = new HashMap<String, List<String>>();
		saveCheckpoints();
	}
	
	public StoredPlayerCheckpoints(String name, Location checkpoint){
		playerName = name;
		globalCheckpoint = checkpoint;
		checkpoints = new HashMap<String, Location>();
		flags = new HashMap<String, List<String>>();
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
		flags.put(minigame, flagList);
		saveCheckpoints();
	}
	
	public List<String> getFlags(String minigame){
		return flags.get(minigame);
	}
	
	public void removeFlags(String minigame){
		flags.remove(minigame);
		saveCheckpoints();
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
		for(String mgm : flags.keySet()){
			save.getConfig().set(playerName + "." + mgm + ".flags", getFlags(mgm));
		}
		
		if(hasGlobalCheckpoint()){
			save.getConfig().set(playerName + ".globalcheckpoint.x", globalCheckpoint.getX());
			save.getConfig().set(playerName + ".globalcheckpoint.y", globalCheckpoint.getY());
			save.getConfig().set(playerName + ".globalcheckpoint.z", globalCheckpoint.getZ());
			save.getConfig().set(playerName + ".globalcheckpoint.yaw", globalCheckpoint.getYaw());
			save.getConfig().set(playerName + ".globalcheckpoint.pitch", globalCheckpoint.getPitch());
			save.getConfig().set(playerName + ".globalcheckpoint.world", globalCheckpoint.getWorld().getName());
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
			if(save.getConfig().contains(playerName + "." + mgm + ".flags")){
				flags.put(mgm, save.getConfig().getStringList(playerName + "." + mgm + ".flags"));
			}
		}
		
		if(save.getConfig().contains(playerName + ".globalcheckpoint")){
			double x = save.getConfig().getDouble(playerName + ".globalcheckpoint.x");
			double y = save.getConfig().getDouble(playerName + ".globalcheckpoint.y");
			double z = save.getConfig().getDouble(playerName + ".globalcheckpoint.z");
			Float yaw = new Float(save.getConfig().get(playerName + ".globalcheckpoint.yaw").toString());
			Float pitch = new Float(save.getConfig().get(playerName + ".globalcheckpoint.pitch").toString());
			String world = save.getConfig().getString(playerName + ".globalcheckpoint.world");
			
			globalCheckpoint = new Location(Minigames.plugin.getServer().getWorld(world), x, y, z, yaw, pitch);
		}
	}
}
