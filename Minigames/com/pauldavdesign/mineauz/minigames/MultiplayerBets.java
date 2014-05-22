package com.pauldavdesign.mineauz.minigames;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MultiplayerBets {
	private Map<MinigamePlayer, ItemStack> bet = new HashMap<MinigamePlayer, ItemStack>();
	private double greatestBet = 0;
	private Map<MinigamePlayer, Double> moneyBet = new HashMap<MinigamePlayer, Double>();
	
	public MultiplayerBets(){
	}
	
	public void addBet(MinigamePlayer player, ItemStack item){
		if(!bet.containsKey(player)){
			if(betValue(item.getType()) >= highestBet()){
				item.setAmount(1);
				bet.put(player, item);
			}
		}
	}
	
	public void addBet(MinigamePlayer player, Double money){
		if(!moneyBet.containsKey(player)){
			if(money >= greatestBet){
				greatestBet = money;
				moneyBet.put(player, money);
			}
		}
	}
	
	public boolean canBet(MinigamePlayer player, ItemStack item){
		if(bet.containsKey(player)){
			return false;
		}
		
		if(!bet.isEmpty() && betValue(item.getType()) != highestBet()){
			return false;
		}
		return true;
	}
	
	public boolean canBet(MinigamePlayer player, Double money){
		if(moneyBet.containsKey(player)){
			return false;
		}
		
		if(greatestBet != 0 && money != greatestBet){
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
	
	public Double claimMoneyBets(){
		Double money = 0d;
		for(Double mon : moneyBet.values()){
			money += mon;
		}
		return money;
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
	
	public ItemStack getPlayersBet(MinigamePlayer player){
		if(bet.containsKey(player)){
			return bet.get(player);
		}
		return null;
	}
	
	public Double getPlayersMoneyBet(MinigamePlayer player){
		if(moneyBet.containsKey(player)){
			return moneyBet.get(player);
		}
		return null;
	}
	
	public void removePlayersBet(MinigamePlayer player){
		bet.remove(player);
		moneyBet.remove(player);
	}
	
	public boolean hasBets(){
		return !bet.isEmpty();
	}
	
	public boolean hasMoneyBets(){
		return !moneyBet.isEmpty();
	}
	
	public double getHighestMoneyBet(){
		return greatestBet;
	}
}
