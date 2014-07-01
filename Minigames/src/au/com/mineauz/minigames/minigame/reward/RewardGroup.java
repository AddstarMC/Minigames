package au.com.mineauz.minigames.minigame.reward;

import java.util.ArrayList;
import java.util.List;

public class RewardGroup {
	
	private String groupName;
	private List<RewardItem> items = new ArrayList<RewardItem>();
	private RewardRarity rarity;
	
	public RewardGroup(String groupName, RewardRarity rarity){
		this.groupName = groupName;
		this.rarity = rarity;
	}
	
	public String getName(){
		return groupName;
	}
	
	public void addItem(RewardItem item){
		items.add(item);
	}
	
	public void removeItem(RewardItem item){
		items.remove(item);
	}
	
	public List<RewardItem> getItems(){
		return items;
	}
	
	public RewardRarity getRarity(){
		return rarity;
	}
	
	public void setRarity(RewardRarity rarity){
		this.rarity = rarity;
	}
	
	public void clearGroup(){
		items.clear();
	}
}
