package com.pauldavdesign.mineauz.minigames;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MultiplayerBets {
	private Map<Player, ItemStack> bet = new HashMap<Player, ItemStack>();
	
	public MultiplayerBets(){
	}
	
	public void addBet(Player player, ItemStack item){
		if(!bet.containsKey(player)){
			if(betValue(item.getType()) >= highestBet()){
				item.setAmount(1);
				bet.put(player, item);
			}
		}
	}
	
	public boolean canBet(Player player, ItemStack item){
		if(bet.containsKey(player)){
			return false;
		}
		
		if(betValue(item.getType()) < highestBet()){
			return false;
		}
		return true;
	}
	
	public ItemStack[] claimBets(){
		ItemStack[] items = new ItemStack[bet.values().size()];
		int num = 0;
		for(ItemStack item : bet.values()){
			items[num] = item;
			num++;
		}
		return items;
	}
	
	public int highestBet(){
		int highest = 0;
		for(ItemStack item : bet.values()){
			if(betValue(item.getType()) > highest){
				highest = betValue(item.getType());
			}
		}
		return highest;
	}
	
	public String highestBetName(){
		String highest = "iron ingot";
		int largest = 0;
		for(ItemStack item : bet.values()){
			if(betValue(item.getType()) > largest){
				largest = betValue(item.getType());
				highest = item.getType().toString().toLowerCase().replace("_", " ");
			}
		}
		return highest;
	}
	
	public int betValue(Material material){
		if(material == Material.DIAMOND){
			return 3;
		}
		else if(material == Material.GOLD_INGOT){
			return 2;
		}
		else if(material == Material.IRON_INGOT){
			return 1;
		}
		return 0;
	}
	
	public ItemStack getPlayersBet(Player player){
		if(bet.containsKey(player)){
			return bet.get(player);
		}
		return null;
	}
	
	public void removePlayersBet(Player player){
		bet.remove(player);
	}
}
