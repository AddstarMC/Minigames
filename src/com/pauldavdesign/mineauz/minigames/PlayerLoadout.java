package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.pauldavdesign.mineauz.minigames.menu.Callback;

public class PlayerLoadout {
	private Map<Integer, ItemStack> itemSlot = new HashMap<Integer, ItemStack>();
	private List<PotionEffect> potions = new ArrayList<PotionEffect>();
	private String loadoutName = "default";
	private boolean usePermission = false;
	private boolean fallDamage = true;
	private boolean hunger = false;
	
	public PlayerLoadout(String name){
		loadoutName = name;
	}
	
	public void setUsePermissions(boolean bool){
		usePermission = bool;
	}
	
	public boolean getUsePermissions(){
		return usePermission;
	}
	
	public Callback<Boolean> getUsePermissionsCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				usePermission = value;
			}

			@Override
			public Boolean getValue() {
				return usePermission;
			}
		};
	}
	
	public void setName(String name){
		loadoutName = name;
	}
	
	public String getName(){
		return loadoutName;
	}
	
	public void addItem(ItemStack item, int slot){
		itemSlot.put(slot, item);
	}
	
	public void addPotionEffect(PotionEffect effect){
		for(PotionEffect pot : potions){
			if(effect.getType().getName().equals(pot.getType().getName())){
				potions.remove(pot);
				break;
			}
		}
		potions.add(effect);
	}
	
	public void removePotionEffect(PotionEffect effect){
		if(potions.contains(effect)){
			potions.remove(effect);
		}
		else{
			for(PotionEffect pot : potions){
				if(pot.getType().getName().equals(effect.getType().getName())){
					potions.remove(pot);
					break;
				}
			}
		}
	}
	
	public List<PotionEffect> getAllPotionEffects(){
		return potions;
	}
	
	@SuppressWarnings("deprecation")
	public void equiptLoadout(MinigamePlayer player){
		player.getPlayer().getInventory().clear();
		player.getPlayer().getInventory().setHelmet(null);
		player.getPlayer().getInventory().setChestplate(null);
		player.getPlayer().getInventory().setLeggings(null);
		player.getPlayer().getInventory().setBoots(null);
		for(PotionEffect potion : player.getPlayer().getActivePotionEffects()){
			player.getPlayer().removePotionEffect(potion.getType());
		}
		if(!itemSlot.isEmpty()){
			for(Integer slot : itemSlot.keySet()){
				if(slot < 100)
					player.getPlayer().getInventory().setItem(slot, getItem(slot));
				else if(slot == 100)
					player.getPlayer().getInventory().setBoots(getItem(slot));
				else if(slot == 101)
					player.getPlayer().getInventory().setLeggings(getItem(slot));
				else if(slot == 102)
					player.getPlayer().getInventory().setChestplate(getItem(slot));
				else if(slot == 103)
					player.getPlayer().getInventory().setHelmet(getItem(slot));
			}
			player.getPlayer().updateInventory();
		}
		
		final MinigamePlayer fplayer = player;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				fplayer.getPlayer().addPotionEffects(potions);
			}
		});
	}
	
	public Set<Integer> getItems(){
		return itemSlot.keySet();
	}
	
	public ItemStack getItem(int slot){
		return itemSlot.get(slot);
	}
	
	public void clearLoadout(){
		itemSlot.clear();
	}
	
	public boolean hasFallDamage(){
		return fallDamage;
	}
	
	public Callback<Boolean> getFallDamageCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				fallDamage = value;
			}

			@Override
			public Boolean getValue() {
				return fallDamage;
			}
		};
	}
	
	public boolean hasHunger(){
		return hunger;
	}
	
	public Callback<Boolean> getHungerCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				hunger = value;
			}

			@Override
			public Boolean getValue() {
				return hunger;
			}
		};
	}
}
