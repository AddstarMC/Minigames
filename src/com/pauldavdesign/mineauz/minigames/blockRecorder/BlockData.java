package com.pauldavdesign.mineauz.minigames.blockRecorder;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public class BlockData {
	private Location location;
	private BlockState state;
	private Player player;
	
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
}
