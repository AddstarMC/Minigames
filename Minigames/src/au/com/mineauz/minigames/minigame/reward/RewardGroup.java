package au.com.mineauz.minigames.minigame.reward;

import java.util.ArrayList;
import java.util.List;

import au.com.mineauz.minigames.menu.Callback;

public class RewardGroup {
	
	private String groupName;
//	private List<RewardItem> items = new ArrayList<RewardItem>();
	private List<RewardType> items = new ArrayList<RewardType>();
	private RewardRarity rarity;
	
	public RewardGroup(String groupName, RewardRarity rarity){
		this.groupName = groupName;
		this.rarity = rarity;
	}
	
	public String getName(){
		return groupName;
	}
	
	public void addItem(RewardType item){
		items.add(item);
	}
	
	public void removeItem(RewardType item){
		items.remove(item);
	}
	
	public List<RewardType> getItems(){
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
	
	public Callback<RewardRarity> getRarityCallback() {
		return new Callback<RewardRarity>() {
			@Override
			public void setValue(RewardRarity value) {
				rarity = value;
			}
			
			@Override
			public RewardRarity getValue() {
				return rarity;
			}
		};
	}
}
