package com.pauldavdesign.mineauz.minigames.blockRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class RecorderData implements Listener{
	private static Minigames plugin;
	private PlayerData pdata;
	
	private Minigame minigame;
	private boolean whitelistMode = false;
	private List<Material> wbBlocks = new ArrayList<Material>();
	
	private Map<String, BlockData> blockdata;
	private Map<Integer, EntityData> entdata;
	
	private static List<Material> physBlocks = new ArrayList<Material>();
	
	static{
		physBlocks.add(Material.TORCH);
		physBlocks.add(Material.SIGN_POST);
		physBlocks.add(Material.WALL_SIGN);
		physBlocks.add(Material.STRING);
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
	
	public BlockData addBlock(Block block, MinigamePlayer modifier){
		return addBlock(block.getState(), modifier);
	}
	
	public BlockData addBlock(BlockState block, MinigamePlayer modifier){
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
			else if(block.getType() == Material.DROPPER){
				Dropper dropper = (Dropper) block;
				items = new ItemStack[dropper.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(dropper.getInventory().getItem(i) != null){
						items[i] = dropper.getInventory().getItem(i).clone();
					}
				}
			}
			else if(block.getType() == Material.HOPPER){
				Hopper hopper = (Hopper) block;
				items = new ItemStack[hopper.getInventory().getContents().length];
				for(int i = 0; i < items.length; i++){
					if(hopper.getInventory().getItem(i) != null){
						items[i] = hopper.getInventory().getItem(i).clone();
					}
				}
			}
			bdata.setItems(items);
			
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
		List<BlockData> addBlocks = new ArrayList<BlockData>();
		for(String id : blockdata.keySet()){
			final BlockData bdata = blockdata.get(id);
			if(bdata.getModifier() == modifier || modifier == null){
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
				changes.add(id);
				
				if(physBlocks.contains(bdata.getBlockState().getType())){
					addBlocks.add(bdata);
				}
				else{
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							bdata.getLocation().getBlock().setType(bdata.getBlockState().getType());
//							bdata.getLocation().getBlock().setData(bdata.getBlockState().getRawData());
							bdata.getBlockState().update();
							
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
							else if(bdata.getLocation().getBlock().getType() == Material.DROPPER){
								Dropper dropper = (Dropper) bdata.getLocation().getBlock().getState();
								if(bdata.getItems() != null){
									dropper.getInventory().setContents(bdata.getItems().clone());
								}
							}
							else if(bdata.getLocation().getBlock().getType() == Material.HOPPER){
								Hopper hopper = (Hopper) bdata.getLocation().getBlock().getState();
								if(bdata.getItems() != null){
									hopper.getInventory().setContents(bdata.getItems().clone());
								}
							}
						}
					});
				}
			}
		}
		
		for(final BlockData bdata : addBlocks){
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					bdata.getLocation().getBlock().setType(bdata.getBlockState().getType());
//					bdata.getLocation().getBlock().setData(bdata.getBlockState().getRawData());
					bdata.getBlockState().update();
					
					if(bdata.getBlockState().getType() == Material.SIGN_POST || bdata.getBlockState().getType() == Material.WALL_SIGN){
						Sign sign = (Sign) bdata.getLocation().getBlock().getState();
						Sign signOld = (Sign) bdata.getBlockState();
						sign.setLine(0, signOld.getLine(0));
						sign.setLine(1, signOld.getLine(1));
						sign.setLine(2, signOld.getLine(2));
						sign.setLine(3, signOld.getLine(3));
						sign.update();
					}
				}
			}, 5);
		}
		addBlocks = null;
		
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
		for(Integer entID : entdata.keySet()){
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
	
	@EventHandler(priority = EventPriority.HIGH)
	private void blockBreak(BlockBreakEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().equals(minigame)){
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
					Location above = event.getBlock().getLocation().clone();
					above.setY(above.getY() + 1);
					addBlock(event.getBlock(), ply);
					
					if(!minigame.canBlocksdrop()){
						event.setCancelled(true);
						event.getBlock().setType(Material.AIR);
					}
				}
				
				if(physBlocks.contains(event.getBlock().getRelative(BlockFace.UP).getType())){
					addBlock(event.getBlock().getRelative(BlockFace.UP), ply);
				}
				if(physBlocks.contains(event.getBlock().getRelative(BlockFace.NORTH).getType())){
					addBlock(event.getBlock().getRelative(BlockFace.NORTH), ply);
				}
				if(physBlocks.contains(event.getBlock().getRelative(BlockFace.EAST).getType())){
					addBlock(event.getBlock().getRelative(BlockFace.EAST), ply);
				}
				if(physBlocks.contains(event.getBlock().getRelative(BlockFace.SOUTH).getType())){
					addBlock(event.getBlock().getRelative(BlockFace.SOUTH), ply);
				}
				if(physBlocks.contains(event.getBlock().getRelative(BlockFace.WEST).getType())){
					addBlock(event.getBlock().getRelative(BlockFace.WEST), ply);
				}
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void blockPlace(BlockPlaceEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().equals(minigame) && !event.isCancelled()){
			if(((whitelistMode && getWBBlocks().contains(event.getBlock().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlock().getType()))) &&
					 minigame.canBlockPlace()){
				addBlock(event.getBlockReplacedState(), ply);
				//TODO: Add double chest check in here
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void takeItem(PlayerInteractEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().equals(minigame) && event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& !minigame.isSpectator(ply)){
			if(event.getClickedBlock().getType() == Material.CHEST){
				Chest chest = (Chest) event.getClickedBlock().getState();
				if(chest.getInventory().getSize() > 27){
					Location loc = event.getClickedBlock().getLocation().clone();
					boolean isLeft = false;
					BlockFace dir = ((org.bukkit.material.Chest)chest.getData()).getFacing();
					//West = -z; East = +z; North = +x; South = -x;
					if(!isLeft && dir == BlockFace.NORTH){
						loc.setX(loc.getX() + 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					else if(!isLeft && dir == BlockFace.SOUTH){
						loc.setX(loc.getX() - 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					else if(!isLeft && dir == BlockFace.WEST){
						loc.setZ(loc.getZ() - 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					else if(!isLeft && dir == BlockFace.EAST){
						loc.setZ(loc.getZ() + 1);
						if(loc.getBlock().getType() == Material.CHEST){
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					BlockData bdata = addBlock(loc.getBlock(), ply);
					if(minigame.isRandomizeChests()){
						bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
					}
				}
				else if(event.getClickedBlock().getState() instanceof Chest){
					BlockData bdata = addBlock(event.getClickedBlock().getLocation().getBlock(), ply);
					if(minigame.isRandomizeChests()){
						bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
					}
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
	
	@EventHandler(ignoreCancelled = true)
	private void leafDecay(LeavesDecayEvent event){
		if(hasRegenArea() && minigame.hasPlayers()){
			Location block = event.getBlock().getLocation();
			if(block.getWorld() == minigame.getRegenArea1().getWorld() && 
					block.getBlockX() >= getRegenMinX() && block.getBlockX() <= getRegenMaxX() &&
					block.getBlockY() >= getRegenMinY() && block.getBlockY() <= getRegenMaxY() &&
					block.getBlockZ() >= getRegenMinZ() && block.getBlockZ() <= getRegenMaxZ()){
				addBlock(event.getBlock(), null);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void treeGrow(StructureGrowEvent event){
		if(hasBlock(event.getLocation().getBlock())){
			for(BlockState block : event.getBlocks()){
				addBlock(block.getLocation().getBlock(), pdata.getMinigamePlayer(event.getPlayer()));
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void bucketFill(PlayerBucketFillEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().equals(minigame)){
			if(((whitelistMode && getWBBlocks().contains(event.getBlockClicked().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlockClicked().getType()))) && 
					minigame.canBlockBreak()){
				addBlock(event.getBlockClicked(), pdata.getMinigamePlayer(event.getPlayer()));
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void bucketEmpty(PlayerBucketEmptyEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply == null) return;
		if(ply.isInMinigame() && ply.getMinigame().equals(minigame)){
			if(((whitelistMode && getWBBlocks().contains(event.getBlockClicked().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlockClicked().getType()))) && 
					minigame.canBlockPlace()){
				Location loc = new Location(event.getBlockClicked().getWorld(), 
						event.getBlockFace().getModX() + event.getBlockClicked().getX(), 
						event.getBlockFace().getModY() + event.getBlockClicked().getY(), 
						event.getBlockFace().getModZ() + event.getBlockClicked().getZ());
				addBlock(loc.getBlock(), pdata.getMinigamePlayer(event.getPlayer()));
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void blockFromTo(BlockFromToEvent event){
		if(hasRegenArea() && minigame.hasPlayers()){
			if(blockInRegenArea(event.getBlock().getLocation()) && event.getToBlock().getType() != Material.BEDROCK){
				addBlock(event.getBlock(), null);
				addBlock(event.getToBlock(), null);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void blockBurn(BlockBurnEvent event){
		if(hasRegenArea() && minigame.hasPlayers()){
			Location block = event.getBlock().getLocation();
			if(block.getWorld() == minigame.getRegenArea1().getWorld() && 
					block.getBlockX() >= getRegenMinX() && block.getBlockX() <= getRegenMaxX() &&
					block.getBlockY() >= getRegenMinY() && block.getBlockY() <= getRegenMaxY() &&
					block.getBlockZ() >= getRegenMinZ() && block.getBlockZ() <= getRegenMaxZ()){
				addBlock(event.getBlock(), null);
			}
		}
		else if(checkBlockSides(event.getBlock().getLocation())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void fireSpread(BlockSpreadEvent event){
		if(hasRegenArea() && minigame.hasPlayers()){
			Location block = event.getBlock().getLocation();
			if(block.getWorld() == minigame.getRegenArea1().getWorld() && 
					block.getBlockX() >= getRegenMinX() && block.getBlockX() <= getRegenMaxX() &&
					block.getBlockY() >= getRegenMinY() && block.getBlockY() <= getRegenMaxY() &&
					block.getBlockZ() >= getRegenMinZ() && block.getBlockZ() <= getRegenMaxZ()){
				addBlock(event.getBlock(), null);
			}
		}
		else if(hasBlock(event.getSource())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void igniteblock(BlockIgniteEvent event){
		if(event.getPlayer() != null && pdata.getMinigamePlayer(event.getPlayer()).isInMinigame() && 
				pdata.getMinigamePlayer(event.getPlayer()).getMinigame().equals(minigame) && 
				(event.getCause() == IgniteCause.FIREBALL || event.getCause() == IgniteCause.FLINT_AND_STEEL)){
			if(((whitelistMode && getWBBlocks().contains(Material.FIRE)) || 
					(!whitelistMode && !getWBBlocks().contains(Material.FIRE))) && 
					minigame.canBlockPlace()){
				addBlock(event.getBlock(), pdata.getMinigamePlayer(event.getPlayer()));
			}
			else{
				event.setCancelled(true);
			}
		}
		else if(hasRegenArea() && minigame.hasPlayers()){
			Location block = event.getBlock().getLocation();
			if(block.getWorld() == minigame.getRegenArea1().getWorld() && 
					block.getBlockX() >= getRegenMinX() && block.getBlockX() <= getRegenMaxX() &&
					block.getBlockY() >= getRegenMinY() && block.getBlockY() <= getRegenMaxY() &&
					block.getBlockZ() >= getRegenMinZ() && block.getBlockZ() <= getRegenMaxZ()){
				addBlock(event.getBlock(), null);
			}
		}
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
			if(animal.getHealth() <= event.getDamage()){
				Player ply = null;
				if(event.getDamager() instanceof Player){
					ply = (Player) event.getDamager();
				}
				else if(event.getDamager() instanceof Arrow){
					Arrow arr = (Arrow) event.getDamager();
					if(arr.getShooter() instanceof Player){
						ply = (Player) arr.getShooter();
					}
				}
				if(ply != null){
					if(pdata.getMinigamePlayer(ply).isInMinigame() && pdata.getMinigamePlayer(ply).getMinigame().equals(minigame)){
						addEntity(animal, pdata.getMinigamePlayer(ply), false);
					}
				}
			}
			else if(hasRegenArea() && minigame.hasPlayers()){
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
	private void paintingPlace(HangingPlaceEvent event){
		MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
		if(ply.isInMinigame() && ply.getMinigame().equals(minigame)){
			if(((whitelistMode && getWBBlocks().contains(Material.PAINTING)) || 
					(!whitelistMode && !getWBBlocks().contains(Material.PAINTING))) ||
					((whitelistMode && getWBBlocks().contains(Material.ITEM_FRAME)) || 
							(!whitelistMode && !getWBBlocks().contains(Material.ITEM_FRAME)))){
				addEntity(event.getEntity(), ply, true);
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void paintingBreak(HangingBreakByEntityEvent event){
		Player ply = null;
		if(event.getRemover() instanceof Player){
			ply = (Player) event.getRemover();
		}
		else if(event.getRemover() instanceof Arrow){
			if(((Arrow)event.getRemover()).getShooter() instanceof Player){
				ply = (Player)((Arrow)event.getRemover()).getShooter();
			}
		}
		if(ply != null){
			if(pdata.getMinigamePlayer(ply).isInMinigame() && pdata.getMinigamePlayer(ply).getMinigame().equals(minigame)){
				event.setCancelled(true);
			}
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
	private void arrowShoot(EntityShootBowEvent event){
		if(event.getEntity() instanceof Player){
			Player ply = (Player) event.getEntity();
			if(pdata.getMinigamePlayer(ply).isInMinigame()){
				addEntity(event.getProjectile(), pdata.getMinigamePlayer(ply), true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void pistonPush(BlockPistonExtendEvent event){
		if(hasBlock(event.getBlock())){
			for(Block bl : event.getBlocks()){
				if((whitelistMode && !getWBBlocks().contains(bl.getType())) || 
						!whitelistMode && getWBBlocks().contains(bl.getType())){
					event.setCancelled(true);
				}
				else{
					addBlock(bl, null);
					Location extra = event.getBlocks().get(event.getBlocks().size() - 1).getLocation();
					extra.setX(extra.getX() + event.getDirection().getModX());
					extra.setY(extra.getY() + event.getDirection().getModY());
					extra.setZ(extra.getZ() + event.getDirection().getModZ());
					addBlock(extra.getBlock(), null);
				}
			}
		}else if(hasRegenArea() && minigame.hasPlayers()){
			Location block = event.getBlock().getLocation();
			if(blockInRegenArea(block)){
				addBlock(event.getBlock(), null);
				for(Block bl : event.getBlocks()){
					if((whitelistMode && !getWBBlocks().contains(bl.getType())) || 
							!whitelistMode && getWBBlocks().contains(bl.getType())){
						event.setCancelled(true);
					}
					else{
						addBlock(bl, null);
						Location extra = event.getBlocks().get(event.getBlocks().size() - 1).getLocation();
						extra.setX(extra.getX() + event.getDirection().getModX());
						extra.setY(extra.getY() + event.getDirection().getModY());
						extra.setZ(extra.getZ() + event.getDirection().getModZ());
						addBlock(extra.getBlock(), null);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void pistonPull(BlockPistonRetractEvent event){
		if(hasBlock(event.getBlock())){
			if((whitelistMode && !getWBBlocks().contains(event.getRetractLocation().getBlock().getType())) || 
					!whitelistMode && getWBBlocks().contains(event.getRetractLocation().getBlock().getType())){
				event.setCancelled(true);
			}
			else{
				addBlock(event.getRetractLocation().getBlock(), null);
				Location extra = event.getRetractLocation();
				extra.setX(extra.getX() + event.getDirection().getModX());
				extra.setY(extra.getY() + event.getDirection().getModY());
				extra.setZ(extra.getZ() + event.getDirection().getModZ());
				addBlock(extra.getBlock(), null);
			}
		}else if(hasRegenArea() && minigame.hasPlayers()){
			Location block = event.getBlock().getLocation();
			if(blockInRegenArea(block)){
				addBlock(event.getBlock(), null);
				if((whitelistMode && !getWBBlocks().contains(event.getRetractLocation().getBlock().getType())) || 
						!whitelistMode && getWBBlocks().contains(event.getRetractLocation().getBlock().getType())){
					event.setCancelled(true);
				}
				else{
					addBlock(event.getRetractLocation().getBlock(), null);
					Location extra = event.getRetractLocation();
					extra.setX(extra.getX() + event.getDirection().getModX());
					extra.setY(extra.getY() + event.getDirection().getModY());
					extra.setZ(extra.getZ() + event.getDirection().getModZ());
					addBlock(extra.getBlock(), null);
				}
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
					addBlock(event.getBlock(), null);
					addEntity(event.getEntity(), null, true);
				}
			}
			else if(event.getEntityType() == EntityType.FALLING_BLOCK && minigame.hasPlayers())
			{
				event.getEntity().setMetadata("FellInMinigame", new FixedMetadataValue(Minigames.plugin, true));
				addBlock(event.getBlock(), null);
				addEntity(event.getEntity(), null, true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void dispenser(BlockDispenseEvent event){
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getBlock().getLocation())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void blockForm(BlockFormEvent event){
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getBlock().getLocation())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void hopperPickup(InventoryPickupItemEvent event){
		Location loc = null;
		boolean isCart = false;
		if(event.getInventory().getHolder() instanceof Hopper){
			loc = ((Hopper)event.getInventory().getHolder()).getLocation();
		}
		else if(event.getInventory().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getInventory().getHolder()).getLocation();
			isCart = true;
		}
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(loc)){
			if(!isCart){
				addBlock((Hopper)event.getInventory().getHolder(), null);
			}
			else{
				addEntity((HopperMinecart)event.getInventory().getHolder(), null, false);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void blockMoveItem(InventoryMoveItemEvent event){
		Location loc = null;
		boolean isCart = false;
		if(event.getInitiator().getHolder() instanceof BlockState){
			loc = ((BlockState)event.getInitiator().getHolder()).getLocation();
		}
		else if(event.getInitiator().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getInitiator().getHolder()).getLocation();
			isCart = true;
		}
		
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(loc)){
			if(!isCart){
				addBlock((BlockState)event.getInitiator().getHolder(), null);
			}
			else{
				addEntity((Entity)event.getInitiator().getHolder(), null, false);
			}
		}
		
		loc = null;
		isCart = false;
		if(event.getDestination().getHolder() instanceof BlockState){
			loc = ((BlockState)event.getDestination().getHolder()).getLocation();
		}
		else if(event.getDestination().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getDestination().getHolder()).getLocation();
			isCart = true;
		}
		
		if(loc == null) return;
		
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(loc)){
			if(!isCart){
				addBlock((BlockState)event.getDestination().getHolder(), null);
			}
			else{
				addEntity((Entity)event.getDestination().getHolder(), null, false);
			}
		}
	}
}
