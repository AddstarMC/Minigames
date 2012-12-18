package com.pauldavdesign.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerLoadout {
	private List<ItemStack> items = new ArrayList<ItemStack>();
	private String loadoutName = "default";
	
	public PlayerLoadout(String name){
		loadoutName = name;
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
	
	public void equiptLoadout(Player player){
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(0));
		player.getInventory().setChestplate(new ItemStack(0));
		player.getInventory().setLeggings(new ItemStack(0));
		player.getInventory().setBoots(new ItemStack(0));
		if(!items.isEmpty()){
			for(ItemStack item : items){
				if(item.getTypeId() >= 298 && item.getTypeId() <= 317){
					if(item.getTypeId() == 298 ||
							item.getTypeId() == 302 ||
							item.getTypeId() == 306 ||
							item.getTypeId() == 310 ||
							item.getTypeId() == 314){
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
				}
				else{
					player.getInventory().addItem(item);
				}
			}
		}
	}
	
	public List<ItemStack> getItems(){
		return items;
	}
	
	public void clearLoadout(){
		items.clear();
	}
}
