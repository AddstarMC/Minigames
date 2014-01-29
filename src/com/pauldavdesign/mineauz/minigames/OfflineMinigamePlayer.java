package com.pauldavdesign.mineauz.minigames;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class OfflineMinigamePlayer {
	private String player;
	private ItemStack[] storedItems = null;
	private ItemStack[] storedArmour = null;
	private int food = 20;
	private double health = 20;
	private float saturation = 15;
	private GameMode lastGM = GameMode.SURVIVAL;
	private Location loginLocation;
	
	public OfflineMinigamePlayer(String player, ItemStack[] items, ItemStack[] armour, int food, double health, float saturation, GameMode lastGM, Location loginLocation){
		this.player = player;
		storedItems = items;
		storedArmour = armour;
		this.food = food;
		this.health = health;
		this.saturation = saturation;
		this.lastGM = lastGM;
		if(loginLocation != null && loginLocation.getWorld() == null)
			loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		this.loginLocation = loginLocation;
		savePlayerData();
	}
	
	public OfflineMinigamePlayer(String player){
		MinigameSave save = new MinigameSave("playerdata/inventories/" + player.toLowerCase());
		FileConfiguration con = save.getConfig();
		this.player = player;
		food = con.getInt("food");
		health = con.getDouble("health");
		saturation = con.getInt("saturation");
		lastGM = GameMode.valueOf(con.getString("gamemode"));
		loginLocation = new Location(Minigames.plugin.getServer().getWorld(con.getString("location.world")), 
				con.getDouble("location.x"), 
				con.getDouble("location.y"), 
				con.getDouble("location.z"), 
				new Float(con.getString("location.yaw")), 
				new Float(con.getString("location.pitch")));
		if(loginLocation.getWorld() == null)
			loginLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		ItemStack[] items = Minigames.plugin.getServer().createInventory(null, InventoryType.PLAYER).getContents();
		ItemStack[] armour = new ItemStack[4];
		for(int i = 0; i < items.length; i++){
			if(con.contains("items." + i)){
				items[i] = con.getItemStack("items." + i);
			}
		}
		for(int i = 0; i < 4; i++){
			armour[i] = con.getItemStack("armour." + i);
		}
		storedItems = items;
		storedArmour = armour;
	}
	
	public String getPlayer(){
		return player;
	}
	
	public MinigamePlayer getMinigamePlayer(){
		if(Minigames.plugin.pdata.hasMinigamePlayer(player)){
			return Minigames.plugin.pdata.getMinigamePlayer(player);
		}
		return null;
	}
	
	public ItemStack[] getStoredItems(){
		return storedItems;
	}
	
	public ItemStack[] getStoredArmour(){
		return storedArmour;
	}
	
	public int getFood(){
		return food;
	}
	
	public double getHealth(){
		return health;
	}
	
	public float getSaturation(){
		return saturation;
	}
	
	public GameMode getLastGamemode(){
		return lastGM;
	}
	
	public Location getLoginLocation(){
		return loginLocation;
	}
	
	public void setLoginLocation(Location loc){
		loginLocation = loc;
	}
	
	public void savePlayerData(){
		MinigameSave save = new MinigameSave("playerdata/inventories/" + player.toLowerCase());
		FileConfiguration con = save.getConfig();
		if(storedItems != null){
			int num = 0;
			for(ItemStack item : storedItems){
				if(item != null){
					con.set("items." + num, item);
				}
				num++;
			}
		}
		
		if(storedArmour != null){
			int num = 0;
			for(ItemStack item : storedArmour){
				if(item != null){
					con.set("armour." + num, item);
				}
				num++;
			}
		}
		
		con.set("food", food);
		con.set("saturation", saturation);
		con.set("health", health);
		con.set("gamemode", lastGM.toString());
		if(loginLocation != null){
			con.set("location.x", loginLocation.getBlockX());
			con.set("location.y", loginLocation.getBlockY());
			con.set("location.z", loginLocation.getBlockZ());
			con.set("location.yaw", loginLocation.getYaw());
			con.set("location.pitch", loginLocation.getPitch());
			con.set("location.world", loginLocation.getWorld().getName());
		}
		save.saveConfig();
	}
	
	public void deletePlayerData(){
		MinigameSave save = new MinigameSave("playerdata/inventories/" + player.toLowerCase());
		save.deleteFile();
	}
}
