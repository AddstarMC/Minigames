package au.com.mineauz.minigames.minigame.reward;

import org.bukkit.inventory.ItemStack;

public class RewardItem {
	private ItemStack item = null;
	private double money = 0;
	private RewardRarity rarity;
	
	public RewardItem(ItemStack item, RewardRarity rarity){
		this.item = item;
		this.rarity = rarity;
	}
	
	public RewardItem(double money, RewardRarity rarity){
		this.money = money;
		this.rarity = rarity;
	}
	
	public ItemStack getItem(){
		return item;
	}
	
	public double getMoney(){
		return money;
	}
	
	public RewardRarity getRarity(){
		return rarity;
	}
	
	public void setRarity(RewardRarity rarity){
		this.rarity = rarity;
	}
}
