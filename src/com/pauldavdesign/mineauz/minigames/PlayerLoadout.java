package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerLoadout {
	private List<ItemStack> items = new ArrayList<ItemStack>();
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
	
	public void addItemToLoadout(ItemStack item){
		items.add(item);
	}
	
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
	public void equiptLoadout(Player player){
		player.getInventory().clear();
		for(PotionEffect potion : player.getActivePotionEffects()){
			player.removePotionEffect(potion.getType());
		}
		player.getInventory().setHelmet(new ItemStack(0));
		player.getInventory().setChestplate(new ItemStack(0));
		player.getInventory().setLeggings(new ItemStack(0));
		player.getInventory().setBoots(new ItemStack(0));
		if(!items.isEmpty()){
			for(ItemStack item : items){
				if(item.getTypeId() == 298 ||
						item.getTypeId() == 302 ||
						item.getTypeId() == 306 ||
						item.getTypeId() == 310 ||
						item.getTypeId() == 314 ||
						item.getTypeId() == 397){
					player.getInventory().setHelmet(item);
				}
				else if(item.getTypeId() == 299 ||
						item.getTypeId() == 303 ||
						item.getTypeId() == 307 ||
						item.getTypeId() == 311 ||
						item.getTypeId() == 315){
					player.getInventory().setChestplate(item);
				}
				else if(item.getTypeId() == 300 ||
						item.getTypeId() == 304 ||
						item.getTypeId() == 308 ||
						item.getTypeId() == 312 ||
						item.getTypeId() == 316){
					player.getInventory().setLeggings(item);
				}
				else if(item.getTypeId() == 301 ||
						item.getTypeId() == 305 ||
						item.getTypeId() == 309 ||
						item.getTypeId() == 313 ||
						item.getTypeId() == 317){
					player.getInventory().setBoots(item);
				}
				else{
					player.getInventory().addItem(item);
				}
			}
			player.updateInventory();
		}
		
		final Player fplayer = player;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				fplayer.addPotionEffects(potions);
			}
		});
	}
	
	public List<ItemStack> getItems(){
		return items;
	}
	
	public void clearLoadout(){
		items.clear();
	}
}
