package com.pauldavdesign.mineauz.minigames.blockRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class RecorderData implements Listener{
	private static Minigames plugin;
	private PlayerData pdata;
	
	private Minigame minigame;
	private boolean whitelistMode = false;
	private List<Material> wbBlocks = new ArrayList<Material>();
	private boolean hasCreatedRegenBlocks = false;
	
	private Map<String, BlockData> blockdata;
	private Map<Integer, EntityData> entdata;
	
	private static List<Material> physBlocks = new ArrayList<Material>();
	
	static{
		physBlocks.add(Material.TORCH);
		physBlocks.add(Material.SIGN_POST);
		physBlocks.add(Material.WALL_SIGN);
		physBlocks.add(Material.TRIPWIRE);
		physBlocks.add(Material.RAILS);
		physBlocks.add(Material.POWERED_RAIL);
		physBlocks.add(Material.ACTIVATOR_RAIL);
		physBlocks.add(Material.REDSTONE_WIRE);
		physBlocks.add(Material.REDSTONE_TORCH_OFF);
		physBlocks.add(Material.REDSTONE_TORCH_ON);
		physBlocks.add(Material.SAPLING);
		physBlocks.add(Material.RED_ROSE);
		physBlocks.add(Material.YELLOW_FLOWER);
		physBlocks.add(Material.WOOD_PLATE);
		physBlocks.add(Material.STONE_PLATE);
		physBlocks.add(Material.GOLD_PLATE);
		physBlocks.add(Material.IRON_PLATE);
		physBlocks.add(Material.STONE_BUTTON);
		physBlocks.add(Material.WOOD_BUTTON);
		physBlocks.add(Material.LEVER);
		physBlocks.add(Material.LADDER);
		physBlocks.add(Material.IRON_DOOR);
		physBlocks.add(Material.WOODEN_DOOR);
		physBlocks.add(Material.RED_MUSHROOM);
		physBlocks.add(Material.BROWN_MUSHROOM);
		physBlocks.add(Material.DOUBLE_PLANT);
		physBlocks.add(Material.FLOWER_POT);
		physBlocks.add(Material.WATER_LILY);
		physBlocks.add(Material.TRIPWIRE_HOOK);
		physBlocks.add(Material.TRAP_DOOR);
		physBlocks.add(Material.CARPET);
		physBlocks.add(Material.LONG_GRASS);
		physBlocks.add(Material.DEAD_BUSH);
		physBlocks.add(Material.REDSTONE_COMPARATOR_ON);
		physBlocks.add(Material.REDSTONE_COMPARATOR_OFF);
		physBlocks.add(Material.DIODE_BLOCK_OFF);
		physBlocks.add(Material.DIODE_BLOCK_ON);
	}
	
	public RecorderData(Minigame minigame){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
		
		this.minigame = minigame;
		blockdata = new HashMap<String, BlockData>();
		entdata = new HashMap<Integer, EntityData>();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void setWhitelistMode(boolean bool){
		whitelistMode = bool;
	}
	
	public boolean getWhitelistMode(){
		return whitelistMode;
	}
	
	public Callback<Boolean> getWhitelistModeCallback(){
		return new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				whitelistMode = value;
			}

			@Override
			public Boolean getValue() {
				return whitelistMode;
			}
		};
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
	
	public boolean hasCreatedRegenBlocks(){
		return hasCreatedRegenBlocks;
	}
	
	public void setCreatedRegenBlocks(boolean bool){
		hasCreatedRegenBlocks = bool;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public BlockData addBlock(Block block, MinigamePlayer modifier){
		return addBlock(block.getState(), modifier);
	}
	
	public BlockData addBlock(BlockState block, MinigamePlayer modifier){
		BlockData bdata = new BlockData(block, modifier);
		String sloc = String.valueOf(bdata.getLocation().getBlockX()) + ":" + bdata.getLocation().getBlockY() + ":" + bdata.getLocation().getBlockZ();
		if(!blockdata.containsKey(sloc)){
			if(block.getType() == Material.CHEST){
				Chest chest = (Chest) block;
				if(chest.getInventory().getSize() > 27){
					Location loc = block.getLocation().clone();
					boolean isRight = false;
					BlockFace dir = ((org.bukkit.material.Chest)chest.getData()).getFacing();
					BlockData secondChest = null;
					//West = -z; East = +z; North = +x; South = -x;
					if(dir == BlockFace.NORTH){
						loc.setX(loc.getX() + 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isRight = true;
						}
						secondChest = addBlock(loc.getBlock().getState(), modifier);
					}
					else if(dir == BlockFace.SOUTH){
						loc.setX(loc.getX() - 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isRight = true;
						}
						secondChest = addBlock(loc.getBlock().getState(), modifier);
					}
					else if(dir == BlockFace.WEST){
						loc.setZ(loc.getZ() - 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isRight = true;
						}
						secondChest = addBlock(loc.getBlock().getState(), modifier);
					}
					else if(dir == BlockFace.EAST){
						loc.setZ(loc.getZ() + 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isRight = true;
						}
						secondChest = addBlock(loc.getBlock().getState(), modifier);
					}
					
					if(!isRight){
						bdata.setItems(chest.getInventory().getContents().clone());
						if(minigame.isRandomizeChests())
							bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
					}
					else{
						if(secondChest.getItems() == null){
							secondChest.setItems(chest.getInventory().getContents().clone());
							if(minigame.isRandomizeChests())
								secondChest.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
						}
					}
				}
				else{
					bdata.setItems(chest.getInventory().getContents().clone());
					if(minigame.isRandomizeChests())
						bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
				}
			}
			else if(block instanceof InventoryHolder){
				InventoryHolder inv = (InventoryHolder) block;
				bdata.setItems(inv.getInventory().getContents().clone());
			}
			
			blockdata.put(sloc, bdata);
			return bdata;
		}
		else{
			blockdata.get(sloc).setModifier(modifier);
			return blockdata.get(sloc);
		}
	}
	
	public void addEntity(Entity ent, MinigamePlayer player, boolean created){
		EntityData edata = new EntityData(ent, player, created);
		entdata.put(ent.getEntityId(), edata);
	}
	
	public boolean hasEntity(Entity ent){
		if(entdata.containsKey(ent.getEntityId())){
			return true;
		}
		return false;
	}
	
	public boolean hasBlock(Block block){
		String sloc = String.valueOf(block.getLocation().getBlockX()) + ":" + block.getLocation().getBlockY() + ":" + block.getLocation().getBlockZ();
		if(blockdata.containsKey(sloc)){
			return true;
		}
		return false;
	}
	
	public void restoreBlocks(){
		restoreBlocks(null);
	}
	
	public void restoreEntities(){
		restoreEntities(null);
		entdata.clear();
	}
	
	public void restoreBlocks(MinigamePlayer modifier){
		List<String> changes = new ArrayList<String>();
		List<BlockData> resBlocks = new ArrayList<BlockData>();
		List<BlockData> addBlocks = new ArrayList<BlockData>();
		for(String id : blockdata.keySet()){
			final BlockData bdata = blockdata.get(id);
			if(bdata.getModifier() == modifier || modifier == null){
				if(bdata.getLocation().getBlock().getState() instanceof InventoryHolder){
					InventoryHolder block = (InventoryHolder) bdata.getLocation().getBlock().getState();
					block.getInventory().clear();
				}
				changes.add(id);
				
				if(physBlocks.contains(bdata.getBlockState().getType()) || bdata.getItems() != null){
					addBlocks.add(bdata);
				}
				else{
					resBlocks.add(bdata);
				}
			}
		}
		new RollbackScheduler(resBlocks, addBlocks);
		
		if(modifier == null){
			blockdata.clear();
		}
		else{
			for(String id : changes){
				blockdata.remove(id);
			}
		}
	}
	
	public void restoreEntities(MinigamePlayer player){
		List<Integer> removal = new ArrayList<Integer>();
		Set<Integer> set = new HashSet<Integer>(entdata.keySet());
		for(Integer entID : set){
			if(entdata.get(entID).getEntity().isValid() && (entdata.get(entID).getModifier() == player || player == null)){
				if(entdata.get(entID).wasCreated()){
					entdata.get(entID).getEntity().remove();
					removal.add(entID);
				}
			}
			else if(!entdata.get(entID).wasCreated() && (entdata.get(entID).getModifier() == player || player == null)){
				entdata.get(entID).getEntityLocation().getWorld().spawnEntity(entdata.get(entID).getEntityLocation(), 
					entdata.get(entID).getEntityType());
				removal.add(entID);
			}
		}
		
		if(player == null){
			entdata.clear();
		}
		else{
			for(Integer entID : removal){
				entdata.remove(entID);
			}
		}
	}
	
	public void clearRestoreData(){
		entdata.clear();
		blockdata.clear();
	}
	
	public boolean hasData(){
		if(blockdata.isEmpty() && entdata.isEmpty())
			return false;
		return true;
	}
	
	public boolean checkBlockSides(Location location){
		Location temp = location.clone();
		temp.setX(temp.getX() - 1);
		temp.setY(temp.getY() - 1);
		temp.setZ(temp.getZ() - 1);
		
		for(int y = 0; y < 2; y++){
			for(int x = 0; x < 2; x++){
				for(int z = 0; z < 2; z++){
					if(hasBlock(temp.getBlock())){
						return true;
					}
					temp.setZ(temp.getZ() + 1);
				}
				if(hasBlock(temp.getBlock())){
					return true;
				}
				temp.setZ(temp.getZ() - 2);
				temp.setX(temp.getX() + 1);
			}
			temp.setX(temp.getX() - 2);
			temp.setY(temp.getY() + 1);
		}
		return false;
	}
	
	public boolean hasRegenArea(){
		if(minigame.getRegenArea1() != null && minigame.getRegenArea2() != null){
			return true;
		}
		return false;
	}
	
	public double getRegenMinX(){
		if(minigame.getRegenArea1().getX() > minigame.getRegenArea2().getX()){
			return minigame.getRegenArea2().getX();
		}
		return minigame.getRegenArea1().getX();
	}
	public double getRegenMaxX(){
		if(minigame.getRegenArea1().getX() < minigame.getRegenArea2().getX()){
			return minigame.getRegenArea2().getX();
		}
		return minigame.getRegenArea1().getX();
	}
	public double getRegenMinY(){
		if(minigame.getRegenArea1().getY() > minigame.getRegenArea2().getY()){
			return minigame.getRegenArea2().getY();
		}
		return minigame.getRegenArea1().getY();
	}
	public double getRegenMaxY(){
		if(minigame.getRegenArea1().getY() < minigame.getRegenArea2().getY()){
			return minigame.getRegenArea2().getY();
		}
		return minigame.getRegenArea1().getY();
	}
	public double getRegenMinZ(){
		if(minigame.getRegenArea1().getZ() > minigame.getRegenArea2().getZ()){
			return minigame.getRegenArea2().getZ();
		}
		return minigame.getRegenArea1().getZ();
	}
	public double getRegenMaxZ(){
		if(minigame.getRegenArea1().getZ() < minigame.getRegenArea2().getZ()){
			return minigame.getRegenArea2().getZ();
		}
		return minigame.getRegenArea1().getZ();
	}
	
	public boolean blockInRegenArea(Location location){
		if(location.getWorld() == minigame.getRegenArea1().getWorld() && 
				location.getBlockX() >= getRegenMinX() && location.getBlockX() <= getRegenMaxX() &&
				location.getBlockY() >= getRegenMinY() && location.getBlockY() <= getRegenMaxY() &&
				location.getBlockZ() >= getRegenMinZ() && location.getBlockZ() <= getRegenMaxZ()){
			return true;
		}
		return false;
	}
	
	@EventHandler(ignoreCancelled = true)
	private void vehicleCreate(VehicleCreateEvent event){
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getVehicle().getLocation())){
			addEntity(event.getVehicle(), null, true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void vehicleDestroy(VehicleDestroyEvent event){
		if(event.getAttacker() != null){
			if(event.getAttacker() instanceof Player){
				Player ply = (Player) event.getAttacker();
				if(pdata.getMinigamePlayer(ply).isInMinigame() && pdata.getMinigamePlayer(ply).getMinigame().equals(minigame)){
					if(!hasEntity(event.getVehicle())){
						addEntity(event.getVehicle(), pdata.getMinigamePlayer(ply), false);
					}
				}
			}
		}
		else{
			if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getVehicle().getLocation())){
				addEntity(event.getVehicle(), null, false);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void animalDeath(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Animals){
			Animals animal = (Animals) event.getEntity();
			if(hasRegenArea() && minigame.hasPlayers() && !(event.getDamager() instanceof Player)){
				Location ent = event.getEntity().getLocation();
				if(blockInRegenArea(ent)){
					if(animal.getHealth() <= event.getDamage()){
						addEntity(event.getEntity(), null, true);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void mobSpawnEvent(CreatureSpawnEvent event){
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getLocation())){
			addEntity(event.getEntity(), null, true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void entityExplode(EntityExplodeEvent event){
		if(hasRegenArea() && minigame.hasPlayers()){
			Location block = event.getLocation().getBlock().getLocation();
			if(blockInRegenArea(block)){
				List<Block> blocks = new ArrayList<Block>();
				blocks.addAll(event.blockList());
				
				for(Block bl : blocks){
					if((whitelistMode && getWBBlocks().contains(bl.getType())) ||
							(!whitelistMode && !getWBBlocks().contains(bl.getType()))){
						addBlock(bl, null);
					}
					else{
						event.blockList().remove(bl);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void itemDrop(ItemSpawnEvent event){
		if(hasRegenArea() && minigame.hasPlayers()){
			Location ent = event.getLocation();
			if(blockInRegenArea(ent)){
				addEntity(event.getEntity(), null, true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void physicalBlock(EntityChangeBlockEvent event)
	{
		if(hasRegenArea() && blockInRegenArea(event.getBlock().getLocation()))
		{
			if(event.getTo() == Material.SAND ||
				event.getTo() == Material.GRAVEL ||
				event.getTo() == Material.DRAGON_EGG ||
				event.getTo() == Material.ANVIL)
			{
				
				if(minigame.hasPlayers() || event.getEntity().hasMetadata("FellInMinigame"))
				{
					addEntity(event.getEntity(), null, true);
				}
			}
			else if(event.getEntityType() == EntityType.FALLING_BLOCK && minigame.hasPlayers())
			{
				event.getEntity().setMetadata("FellInMinigame", new FixedMetadataValue(Minigames.plugin, true));
				addEntity(event.getEntity(), null, true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void cartHopperPickup(InventoryPickupItemEvent event){
		Location loc = null;
		boolean isCart = false;
		if(event.getInventory().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getInventory().getHolder()).getLocation();
			isCart = true;
		}
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(loc) && isCart){
			addEntity((HopperMinecart)event.getInventory().getHolder(), null, false);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void cartkMoveItem(InventoryMoveItemEvent event){
		if(!hasRegenArea() || !minigame.hasPlayers()) return;
		
		Location loc = null;
		boolean isCart = false;
		if(event.getInitiator().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getInitiator().getHolder()).getLocation().clone();
			isCart = true;
		}
		
		if(loc != null && blockInRegenArea(loc) && isCart){
			addEntity((Entity)event.getInitiator().getHolder(), null, false);
		}
		
		loc = null;
		isCart = false;
		if(event.getDestination().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getDestination().getHolder()).getLocation().clone();
			isCart = true;
		}
		
		if(loc == null) return;
		
		if(blockInRegenArea(loc) && isCart){
			addEntity((Entity)event.getDestination().getHolder(), null, false);
		}
	}
}
