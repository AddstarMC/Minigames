package com.pauldavdesign.mineauz.minigames.blockRecorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public class BlockData {
	private Location location;
	private BlockState state;
	private MinigamePlayer player = null;
	private ItemStack[] items = null;
	private Map<String, Object> specialData = new HashMap<String, Object>();
	private boolean hasRandomized = false;
	
	public BlockData(Block original, MinigamePlayer modifier){
		location = original.getLocation();
		state = original.getState();
		player = modifier;
	}
	
	public BlockData(BlockState original, MinigamePlayer modifier){
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
	
	public MinigamePlayer getModifier(){
		return player;
	}
	
	public void setModifier(MinigamePlayer modifier){
		player = modifier;
	}
	
	public ItemStack[] getItems(){
		return items;
	}
	
	public void setItems(ItemStack[] items){
		this.items = items;
	}
	
	public void setSpecialData(String key, Object data){
		specialData.put(key, data);
	}
	
	public Object getSpecialData(String key){
		return specialData.get(key);
	}

	public void randomizeContents(int minContents, int maxContents){
		if(hasRandomized || items == null)
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
