package au.com.mineauz.minigames.minigame.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rewards {
	
	private List<RewardType> items = new ArrayList<RewardType>();
	private List<RewardGroup> groups = new ArrayList<RewardGroup>();
	
	public List<RewardType> getReward(){
		double rand = Math.random();
		RewardRarity rarity = null;
		List<Object> itemsCopy = new ArrayList<Object>();
		itemsCopy.addAll(items);
		itemsCopy.addAll(groups);
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
			RewardType item = null;
			RewardGroup group = null;
			RewardRarity orarity = rarity;
			boolean up = true;
			
			while(item == null && group == null){
				for(Object ritem : itemsCopy){
					if(ritem instanceof RewardType){
						RewardType ri = (RewardType)ritem;
						if(ri.getRarity() == rarity){
							item = ri;
							break;
						}
					}
					else{
						RewardGroup rg = (RewardGroup)ritem;
						if(rg.getRarity() == rarity){
							group = rg;
							break;
						}
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
			if(item != null){
				List<RewardType> items = new ArrayList<RewardType>();
				items.add(item);
				return items;
			}
			else if(group != null){
				return group.getItems();
			}
		}
		
		return null;
	}
	
	public void addReward(RewardType reward){
		items.add(reward);
	}
	
//	public RewardItem addItem(ItemStack item, RewardRarity rarity){
//		RewardItem ritem = new RewardItem(item, rarity);
//		items.add(ritem);
//		return ritem;
//	}
//	
//	public RewardItem addMoney(double money, RewardRarity rarity){
//		RewardItem ritem = new RewardItem(money, rarity);
//		items.add(ritem);
//		return ritem;
//	}
	
	public void removeReward(RewardType item){
		items.remove(item);
	}
	
	public List<RewardType> getRewards(){
		return items;
	}
	
	public RewardGroup addGroup(String groupName, RewardRarity rarity){
		RewardGroup group = new RewardGroup(groupName, rarity);
		groups.add(group);
		return group;
	}
	
	public void removeGroup(RewardGroup group){
		groups.remove(group);
	}
	
	public List<RewardGroup> getGroups(){
		return groups;
	}
}
