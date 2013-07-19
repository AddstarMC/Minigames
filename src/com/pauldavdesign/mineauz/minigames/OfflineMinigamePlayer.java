package com.pauldavdesign.mineauz.minigames;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class OfflineMinigamePlayer {
	private String player;
	private ItemStack[] storedItems = null;
	private ItemStack[] storedArmour = null;
	private int food = 20;
	private int health = 20;
	private float saturation = 15;
	private GameMode lastGM = GameMode.SURVIVAL;
	private Location loginLocation;
	
	public OfflineMinigamePlayer(String player, ItemStack[] items, ItemStack[] armour, int food, int health, float saturation, GameMode lastGM, Location loginLocation){
		this.player = player;
		storedItems = items;
		storedArmour = armour;
		this.food = food;
		this.health = health;
		this.saturation = saturation;
		this.lastGM = lastGM;
		this.loginLocation = loginLocation;
		FileConfiguration conf = Minigames.plugin.pdata.invsave.getConfig();
		conf.set("inventories." + player + ".location.x", loginLocation.getBlockX());
		conf.set("inventories." + player + ".location.y", loginLocation.getBlockY());
		conf.set("inventories." + player + ".location.z", loginLocation.getBlockZ());
		conf.set("inventories." + player + ".location.yaw", loginLocation.getYaw());
		conf.set("inventories." + player + ".location.pitch", loginLocation.getPitch());
		conf.set("inventories." + player + ".location.world", loginLocation.getWorld().getName());
		Minigames.plugin.pdata.invsave.saveConfig();
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
	
	public int getHealth(){
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
	
	public void restoreOfflineMinigamePlayer(){
		if(getMinigamePlayer() != null){
			MinigamePlayer player = getMinigamePlayer();
			player.setPlayerData(storedItems, storedArmour, food, health, saturation, lastGM);
		}
	}
}
