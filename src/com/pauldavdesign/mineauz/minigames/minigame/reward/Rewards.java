package com.pauldavdesign.mineauz.minigames.minigame.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Rewards {
	
	private List<RewardItem> items = new ArrayList<RewardItem>();
	
	public RewardItem getReward(){
		double rand = Math.random();
		RewardRarity rarity = null;
		List<RewardItem> itemsCopy = new ArrayList<RewardItem>();
		itemsCopy.addAll(items);
		Collections.shuffle(itemsCopy);
		
		if(rand > RewardRarity.VERY_COMMON.getRarity())
			rarity = RewardRarity.VERY_COMMON;
		else if(rand > RewardRarity.COMMON.getRarity())
			rarity = RewardRarity.COMMON;
		else if(rand > RewardRarity.NORMAL.getRarity())
			rarity = RewardRarity.NORMAL;
		else if(rand > RewardRarity.RARE.getRarity())
			rarity = RewardRarity.RARE;
		else
			rarity = RewardRarity.VERY_RARE;
		
		
		if(!itemsCopy.isEmpty()){
			RewardItem item = null;
			RewardRarity orarity = rarity;
			boolean up = true;
			
			while(item == null){
				for(RewardItem ritem : itemsCopy){
					if(ritem.getRarity() == rarity){
						item = ritem;
						break;
					}
				}
				
				if(rarity == RewardRarity.VERY_COMMON){
					rarity = orarity;
					up = false;
				}
				
				if(up)
					rarity = rarity.getNextRarity();
				else{
					rarity = rarity.getPreviousRarity();
				}
			}
			return item;
		}
		
		return null;
	}
	
	public RewardItem addItem(ItemStack item, RewardRarity rarity){
		RewardItem ritem = new RewardItem(item, rarity);
		items.add(ritem);
		return ritem;
	}
	
	public RewardItem addMoney(double money, RewardRarity rarity){
		RewardItem ritem = new RewardItem(money, rarity);
		items.add(ritem);
		return ritem;
	}
	
	public void removeReward(RewardItem item){
		items.remove(item);
	}
	
	public List<RewardItem> getRewards(){
		return items;
	}
}
