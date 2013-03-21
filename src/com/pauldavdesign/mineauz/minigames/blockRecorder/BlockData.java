package com.pauldavdesign.mineauz.minigames.blockRecorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockData {
	private Location location;
	private BlockState state;
	private Player player;
	private ItemStack[] items = null;
	private boolean hasRandomized = false;
	
	public BlockData(Block original, Player modifier){
		location = original.getLocation();
		state = original.getState();
		player = modifier;
	}
	
	public BlockData(BlockState original, Player modifier){
		location = original.getLocation();
		state = original;
		player = modifier;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public BlockState getBlockState(){
		return state;
	}
	
	public Player getModifier(){
		return player;
	}
	
	public void setModifier(Player modifier){
		player = modifier;
	}
	
	public ItemStack[] getItems(){
		return items;
	}
	
	public void setItems(ItemStack[] items){
		this.items = items;
	}
	
	public void randomizeContents(int minContents, int maxContents){
		if(hasRandomized)
			return;
		
		List<ItemStack> itemRand = new ArrayList<ItemStack>();
		
		for(int i=0; i < items.length; i++){
			if(items[i] != null){
				itemRand.add(items[i].clone());
			}
		}
		
		Collections.shuffle(itemRand);
		List<ItemStack> itemChest = new ArrayList<ItemStack>();
		
		if(maxContents > itemRand.size()){
			maxContents = itemRand.size();
		}
		if(minContents > itemRand.size()){
			minContents = itemRand.size();
		}

		int rand = minContents + (int)(Math.random() * ((maxContents - minContents) + 1));
		
		for(int i=0;i < items.length; i++){
			if(i < rand){
				itemChest.add(i, itemRand.get(i));
			}
			else{
				itemChest.add(null);
			}
		}
		
		Collections.shuffle(itemChest);
		
		ItemStack[] newItems = new ItemStack[itemChest.size()];
		int inc = 0;
		for(ItemStack item : itemChest){
			newItems[inc] = item;
			inc++;
		}
		
		if(state instanceof Chest){
			Chest chest = (Chest) state;
			chest.getInventory().setContents(newItems);
		}
		
		hasRandomized = true;
	}
}
