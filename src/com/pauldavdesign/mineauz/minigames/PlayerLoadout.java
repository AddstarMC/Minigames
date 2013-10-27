package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerLoadout {
	private List<ItemStack> items = new ArrayList<ItemStack>();
	private Map<Integer, ItemStack> itemSlot = new HashMap<Integer, ItemStack>();
	private List<PotionEffect> potions = new ArrayList<PotionEffect>();
	private String loadoutName = "default";
	private boolean usePermission = false;
	
	public PlayerLoadout(String name){
		loadoutName = name;
	}
	
	public void setUsePermissions(boolean bool){
		usePermission = bool;
	}
	
	public boolean getUsePermissions(){
		return usePermission;
	}
	
	public void setName(String name){
		loadoutName = name;
	}
	
	public String getName(){
		return loadoutName;
	}
	
	@Deprecated
	public void addItemToLoadout(ItemStack item){
		items.add(item);
	}
	
	public void addItem(ItemStack item, int slot){
		itemSlot.put(slot, item);
	}
	
	@Deprecated
	public void removeItemFromLoadout(ItemStack item){
		for(ItemStack listitem : items){
			if(listitem.getType() == item.getType()){
				items.remove(listitem);
				break;
			}
		}
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
	
	@Deprecated
	public List<ItemStack> getItemsOld(){
		return items;
	}
	
	public Set<Integer> getItems(){
		return itemSlot.keySet();
	}
	
	public ItemStack getItem(int slot){
		return itemSlot.get(slot);
	}
	
	public void clearLoadout(){
		items.clear();
		itemSlot.clear();
	}
}
