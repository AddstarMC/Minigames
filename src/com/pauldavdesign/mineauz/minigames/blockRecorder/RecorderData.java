package com.pauldavdesign.mineauz.minigames.blockRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
			ItemStack[] items = null;
			if(block.getType() == Material.CHEST){
				if(block instanceof DoubleChest){
					DoubleChest dchest = (DoubleChest) block.getState();
					items = new ItemStack[dchest.getInventory().getContents().length];
					for(int i = 0; i < items.length; i++){
						if(dchest.getInventory().getItem(i) != null){
							items[i] = dchest.getInventory().getItem(i).clone();
						}
					}
				}
				else{
					Chest chest = (Chest) block.getState();
					items = new ItemStack[chest.getInventory().getContents().length];
					for(int i = 0; i < items.length; i++){
						if(chest.getInventory().getItem(i) != null){
							items[i] = chest.getInventory().getItem(i).clone();
						}
					}
				}
			}
			else if(block.getType() == Material.FURNACE){
				Furnace furnace = (Furnace) block.getState();
				items = new ItemStack[furnace.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(furnace.getInventory().getItem(i) != null){
						items[i] = furnace.getInventory().getItem(i).clone();
					}
				}
			}
			else if(block.getType() == Material.BREWING_STAND){
				BrewingStand stand = (BrewingStand) block.getState();
				items = new ItemStack[stand.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(stand.getInventory().getItem(i) != null){
						items[i] = stand.getInventory().getItem(i).clone();
					}
				}
			}
			else if(block.getType() == Material.DISPENSER){
				Dispenser dispenser = (Dispenser) block.getState();
				items = new ItemStack[dispenser.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(dispenser.getInventory().getItem(i) != null){
						items[i] = dispenser.getInventory().getItem(i).clone();
					}
				}
			}
			bdata.setItems(items);
			
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
			ItemStack[] items = null;
			if(block.getType() == Material.CHEST){
				if(block instanceof DoubleChest){
					DoubleChest dchest = (DoubleChest) block;
					items = new ItemStack[dchest.getInventory().getContents().length];
					for(int i = 0; i < items.length; i++){
						if(dchest.getInventory().getItem(i) != null){
							items[i] = dchest.getInventory().getItem(i).clone();
						}
					}
				}
				else{
					Chest chest = (Chest) block;
					items = new ItemStack[chest.getInventory().getContents().length];
					for(int i = 0; i < items.length; i++){
						if(chest.getInventory().getItem(i) != null){
							items[i] = chest.getInventory().getItem(i).clone();
						}
					}
				}
			}
			else if(block.getType() == Material.FURNACE){
				Furnace furnace = (Furnace) block;
				items = new ItemStack[furnace.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(furnace.getInventory().getItem(i) != null){
						items[i] = furnace.getInventory().getItem(i).clone();
					}
				}
			}
			else if(block.getType() == Material.BREWING_STAND){
				BrewingStand stand = (BrewingStand) block;
				items = new ItemStack[stand.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(stand.getInventory().getItem(i) != null){
						items[i] = stand.getInventory().getItem(i).clone();
					}
				}
			}
			else if(block.getType() == Material.DISPENSER){
				Dispenser dispenser = (Dispenser) block;
				items = new ItemStack[dispenser.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(dispenser.getInventory().getItem(i) != null){
						items[i] = dispenser.getInventory().getItem(i).clone();
					}
				}
			}
			bdata.setItems(items);
			
			blockdata.put(sloc, bdata);
		}
		else{
			blockdata.get(sloc).setModifier(modifier);
		}
	}
	
//	public void addBlock(Block block, Player modifier, ItemStack[] items){
//		BlockData bdata = new BlockData(block, modifier, items);
//		String sloc = String.valueOf(bdata.getLocation().getBlockX()) + ":" + bdata.getLocation().getBlockY() + ":" + bdata.getLocation().getBlockZ();
//		if(!blockdata.containsKey(sloc)){
//			blockdata.put(sloc, bdata);
//		}
//		else{
//			blockdata.get(sloc).setModifier(modifier);
//		}
//	}
	
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
			
			if(bdata.getLocation().getBlock().getType() == Material.CHEST){
				if(bdata.getLocation().getBlock().getState() instanceof DoubleChest){
					DoubleChest dchest = (DoubleChest) bdata.getLocation().getBlock().getState();
					dchest.getInventory().clear();
				}
				else{
					Chest chest = (Chest) bdata.getLocation().getBlock().getState();
					chest.getInventory().clear();
				}
			}
			else if(bdata.getLocation().getBlock().getType() == Material.FURNACE){
				Furnace furnace = (Furnace) bdata.getLocation().getBlock().getState();
				furnace.getInventory().clear();
			}
			else if(bdata.getLocation().getBlock().getType() == Material.DISPENSER){
				Dispenser dispenser = (Dispenser) bdata.getLocation().getBlock().getState();
				dispenser.getInventory().clear();
			}
			else if(bdata.getLocation().getBlock().getType() == Material.BREWING_STAND){
				BrewingStand stand = (BrewingStand) bdata.getLocation().getBlock().getState();
				stand.getInventory().clear();
			}
			
			bdata.getLocation().getBlock().setType(bdata.getBlockState().getType());
			bdata.getLocation().getBlock().setData(bdata.getBlockState().getRawData());
			
			if(bdata.getLocation().getBlock().getType() == Material.CHEST){
				if(bdata.getLocation().getBlock().getState() instanceof DoubleChest){
					DoubleChest dchest = (DoubleChest) bdata.getLocation().getBlock().getState();
					if(bdata.getItems() != null){
						dchest.getInventory().setContents(bdata.getItems().clone());
					}
				}
				else{
					Chest chest = (Chest) bdata.getLocation().getBlock().getState();
					if(bdata.getItems() != null){
						chest.getInventory().setContents(bdata.getItems().clone());
					}
				}
			}
			else if(bdata.getLocation().getBlock().getType() == Material.FURNACE){
				Furnace furnace = (Furnace) bdata.getLocation().getBlock().getState();
				if(bdata.getItems() != null){
					furnace.getInventory().setContents(bdata.getItems().clone());
				}
			}
			else if(bdata.getLocation().getBlock().getType() == Material.BREWING_STAND){
				BrewingStand bstand = (BrewingStand) bdata.getLocation().getBlock().getState();
				if(bdata.getItems() != null){
					bstand.getInventory().setContents(bdata.getItems().clone());
				}
			}
			else if(bdata.getLocation().getBlock().getType() == Material.DISPENSER){
				Dispenser dispenser = (Dispenser) bdata.getLocation().getBlock().getState();
				if(bdata.getItems() != null){
					dispenser.getInventory().setContents(bdata.getItems().clone());
				}
			}
		}
		blockdata.clear();
	}
	
	public void restoreBlocks(Player modifier){
		List<String> changes = new ArrayList<String>();
		for(String id : blockdata.keySet()){
			BlockData bdata = blockdata.get(id);
			if(bdata.getModifier() == modifier){
				if(bdata.getLocation().getBlock().getType() == Material.CHEST){
					if(bdata.getLocation().getBlock().getState() instanceof DoubleChest){
						DoubleChest dchest = (DoubleChest) bdata.getLocation().getBlock().getState();
						dchest.getInventory().clear();
					}
					else{
						Chest chest = (Chest) bdata.getLocation().getBlock().getState();
						chest.getInventory().clear();
					}
				}
				else if(bdata.getLocation().getBlock().getType() == Material.FURNACE){
					Furnace furnace = (Furnace) bdata.getLocation().getBlock().getState();
					furnace.getInventory().clear();
				}
				else if(bdata.getLocation().getBlock().getType() == Material.DISPENSER){
					Dispenser dispenser = (Dispenser) bdata.getLocation().getBlock().getState();
					dispenser.getInventory().clear();
				}
				else if(bdata.getLocation().getBlock().getType() == Material.BREWING_STAND){
					BrewingStand stand = (BrewingStand) bdata.getLocation().getBlock().getState();
					stand.getInventory().clear();
				}
				
				if(bdata.getLocation().getBlock().getType() != bdata.getBlockState().getType()){
					bdata.getLocation().getBlock().setType(bdata.getBlockState().getType());
				}
				bdata.getLocation().getBlock().setData(bdata.getBlockState().getRawData());
				changes.add(id);
				
				if(bdata.getLocation().getBlock().getType() == Material.CHEST){
					if(bdata.getLocation().getBlock().getState() instanceof DoubleChest){
						DoubleChest dchest = (DoubleChest) bdata.getLocation().getBlock().getState();
						if(bdata.getItems() != null){
							dchest.getInventory().setContents(bdata.getItems().clone());
						}
					}
					else{
						Chest chest = (Chest) bdata.getLocation().getBlock().getState();
						if(bdata.getItems() != null){
							chest.getInventory().setContents(bdata.getItems().clone());
						}
					}
				}
				else if(bdata.getLocation().getBlock().getType() == Material.FURNACE){
					Furnace furnace = (Furnace) bdata.getLocation().getBlock().getState();
					if(bdata.getItems() != null){
						furnace.getInventory().setContents(bdata.getItems().clone());
					}
				}
				else if(bdata.getLocation().getBlock().getType() == Material.BREWING_STAND){
					BrewingStand bstand = (BrewingStand) bdata.getLocation().getBlock().getState();
					if(bdata.getItems() != null){
						bstand.getInventory().setContents(bdata.getItems().clone());
					}
				}
				else if(bdata.getLocation().getBlock().getType() == Material.DISPENSER){
					Dispenser dispenser = (Dispenser) bdata.getLocation().getBlock().getState();
					if(bdata.getItems() != null){
						dispenser.getInventory().setContents(bdata.getItems().clone());
					}
				}
			}
		}
		for(String id : changes){
			blockdata.remove(id);
		}
	}
	
	public boolean hasData(){
		return !blockdata.isEmpty();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void blockBreak(BlockBreakEvent event){
		Player ply = event.getPlayer();
		if(pdata.playerInMinigame(ply) && pdata.getPlayersMinigame(ply).equals(minigame.getName())){
			if(((whitelistMode && getWBBlocks().contains(event.getBlock().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlock().getType()))) && 
					minigame.canBlockBreak()){
				if(event.getBlock().getState() instanceof Sign){
					Sign sign = (Sign) event.getBlock().getState();
					if(sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "[Minigame]")){
						event.setCancelled(true);
					}
					else{
						addBlock(event.getBlock(), ply);
						if(!minigame.canBlocksdrop()){
							event.setCancelled(true);
							event.getBlock().setType(Material.AIR);
						}
					}
				}
				else{
					addBlock(event.getBlock(), ply);
					if(!minigame.canBlocksdrop()){
						event.setCancelled(true);
						event.getBlock().setType(Material.AIR);
					}
				}
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void blockPlace(BlockPlaceEvent event){
		Player ply = event.getPlayer();
		if(pdata.playerInMinigame(ply) && pdata.getPlayersMinigame(ply).equals(minigame.getName()) && !event.isCancelled()){
			if(((whitelistMode && getWBBlocks().contains(event.getBlock().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlock().getType()))) &&
					 minigame.canBlockPlace()){
				addBlock(event.getBlockReplacedState(), ply);
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void takeItem(PlayerInteractEvent event){
		Player ply = (Player) event.getPlayer();
		if(pdata.playerInMinigame(ply) && pdata.getPlayersMinigame(ply).equals(minigame.getName()) && event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getType() == Material.CHEST){
				if(event.getClickedBlock().getState() instanceof DoubleChest){
					addBlock(event.getClickedBlock().getLocation().getBlock(), ply);
				}
				else if(event.getClickedBlock().getState() instanceof Chest){
					addBlock(event.getClickedBlock().getLocation().getBlock(), ply);
				}
			}
			else if(event.getClickedBlock().getType() == Material.FURNACE){
				addBlock(event.getClickedBlock().getLocation().getBlock(), ply);
			}
			else if(event.getClickedBlock().getType() == Material.BREWING_STAND){
				addBlock(event.getClickedBlock().getLocation().getBlock(), ply);
			}
			else if(event.getClickedBlock().getType() == Material.DISPENSER){
				addBlock(event.getClickedBlock().getLocation().getBlock(), ply);
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
