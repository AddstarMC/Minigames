package com.pauldavdesign.mineauz.minigames.blockRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;

public class RecorderData implements Listener{
	private static Minigames plugin;
	private PlayerData pdata;
	
	private Minigame minigame;
	private boolean whitelistMode = false;
	private List<Material> wbBlocks = new ArrayList<Material>();
	
	private Map<String, BlockData> blockdata;
	
	public RecorderData(Minigame minigame){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
		
		this.minigame = minigame;
		blockdata = new HashMap<String, BlockData>();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void setWhitelistMode(boolean bool){
		whitelistMode = bool;
	}
	
	public boolean getWhitelistMode(){
		return whitelistMode;
	}
	
	public void addWBBlock(Material mat){
		wbBlocks.add(mat);
	}
	
	public List<Material> getWBBlocks(){
		return wbBlocks;
	}
	
	public boolean removeWBBlock(Material mat){
		if(wbBlocks.contains(mat)){
			wbBlocks.remove(mat);
			return true;
		}
		return false;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public void addBlock(Block block, Player modifier){
		BlockData bdata = new BlockData(block, modifier);
		String sloc = String.valueOf(bdata.getLocation().getBlockX()) + ":" + bdata.getLocation().getBlockY() + ":" + bdata.getLocation().getBlockZ();
		if(!blockdata.containsKey(sloc)){
			blockdata.put(sloc, bdata);
		}
		else{
			blockdata.get(sloc).setModifier(modifier);
		}
	}
	
	public void addBlock(BlockState block, Player modifier){
		BlockData bdata = new BlockData(block, modifier);
		String sloc = String.valueOf(bdata.getLocation().getBlockX()) + ":" + bdata.getLocation().getBlockY() + ":" + bdata.getLocation().getBlockZ();
		if(!blockdata.containsKey(sloc)){
			blockdata.put(sloc, bdata);
		}
		else{
			blockdata.get(sloc).setModifier(modifier);
		}
	}
	
	public boolean hasBlock(Block block){
		String sloc = String.valueOf(block.getLocation().getBlockX()) + ":" + block.getLocation().getBlockY() + ":" + block.getLocation().getBlockZ();
		if(blockdata.containsKey(sloc)){
			return true;
		}
		return false;
	}
	
	public void restoreBlocks(){
		for(String id : blockdata.keySet()){
			BlockData bdata = blockdata.get(id);
			bdata.getLocation().getBlock().setType(bdata.getBlockState().getType());
			bdata.getLocation().getBlock().setData(bdata.getBlockState().getRawData());
		}
		blockdata.clear();
	}
	
	public void restoreBlocks(Player modifier){
		List<String> changes = new ArrayList<String>();
		for(String id : blockdata.keySet()){
			BlockData bdata = blockdata.get(id);
			if(bdata.getModifier() == modifier){
				bdata.getLocation().getBlock().setType(bdata.getBlockState().getType());
				bdata.getLocation().getBlock().setData(bdata.getBlockState().getRawData());
				changes.add(id);
			}
		}
		for(String id : changes){
			blockdata.remove(id);
		}
	}
	
	public boolean hasData(){
		return !blockdata.isEmpty();
	}
	
	@EventHandler
	private void blockBreak(BlockBreakEvent event){
		Player ply = event.getPlayer();
		if(pdata.playerInMinigame(ply) && pdata.getPlayersMinigame(ply).equals(minigame.getName())){
			if((whitelistMode && getWBBlocks().contains(event.getBlock().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlock().getType()))){
				addBlock(event.getBlock(), ply);
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void blockPlace(BlockPlaceEvent event){
		Player ply = event.getPlayer();
		if(pdata.playerInMinigame(ply) && pdata.getPlayersMinigame(ply).equals(minigame.getName())){
			if((whitelistMode && getWBBlocks().contains(event.getBlock().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlock().getType()))){
				addBlock(event.getBlockReplacedState(), ply);
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void blockPhysics(BlockPhysicsEvent event){
		Location blockBelow = event.getBlock().getLocation();
		blockBelow.setY(blockBelow.getBlockY() - 1);
		if(hasBlock(blockBelow.getBlock())){
			event.setCancelled(true);
		}
	}
}
