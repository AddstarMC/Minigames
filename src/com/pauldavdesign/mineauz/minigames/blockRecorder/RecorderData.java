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
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;

public class RecorderData implements Listener{
	private static Minigames plugin;
	private PlayerData pdata;
	
	private Minigame minigame;
	private boolean whitelistMode = false;
	private List<Material> wbBlocks = new ArrayList<Material>();
	
	private Map<String, BlockData> blockdata;
	private Map<Integer, EntityData> entdata;
	
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
	
	public void addEntity(Entity ent, Player player, boolean created){
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
		for(String id : blockdata.keySet()){
			final BlockData bdata = blockdata.get(id);
			
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
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
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
			});
		}
		blockdata.clear();
	}
	
	public void restoreEntities(){
		for(Integer entID : entdata.keySet()){
			if(entdata.get(entID).getEntity().isValid()){
				if(entdata.get(entID).wasCreated()){
					entdata.get(entID).getEntity().remove();
				}
			}
			else{
				entdata.get(entID).getEntityLocation().getWorld().spawnEntity(entdata.get(entID).getEntityLocation(), 
							entdata.get(entID).getEntityType());
			}
		}
		entdata.clear();
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
	
	public void restoreEntities(Player player){
		List<Integer> removal = new ArrayList<Integer>();
		for(Integer entID : entdata.keySet()){
			if(entdata.get(entID).getEntity().isValid() && entdata.get(entID).getModifier() == player){
				if(entdata.get(entID).wasCreated()){
					entdata.get(entID).getEntity().remove();
					removal.add(entID);
				}
			}
			else if(entdata.get(entID).getModifier() == player){
				entdata.get(entID).getEntityLocation().getWorld().spawnEntity(entdata.get(entID).getEntityLocation(), 
					entdata.get(entID).getEntityType());
				removal.add(entID);
			}
		}
		for(Integer entID : removal){
			entdata.remove(entID);
		}
	}
	
	public boolean hasData(){
		if(blockdata.isEmpty() && entdata.isEmpty())
			return false;
		return true;
	}
	
	public boolean checkBlockSides(Location location){
		Location temp = location.clone();
		temp.setX(temp.getX() - 4);
		temp.setY(temp.getY() - 4);
		temp.setZ(temp.getZ() - 4);
		
		for(int y = 0; y < 8; y++){
			for(int x = 0; x < 8; x++){
				for(int z = 0; z < 8; z++){
					if(hasBlock(temp.getBlock())){
						return true;
					}
					temp.setZ(temp.getZ() + 1);
				}
				if(hasBlock(temp.getBlock())){
					return true;
				}
				temp.setZ(temp.getZ() - 8);
				temp.setX(temp.getX() + 1);
			}
			temp.setX(temp.getX() - 8);
			temp.setY(temp.getY() + 1);
		}
		return false;
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
					Location above = event.getBlock().getLocation().clone();
					above.setY(above.getY() + 1);
					addBlock(event.getBlock(), ply);
					
					if(above.getBlock().getType() == Material.GRAVEL || 
							above.getBlock().getType() == Material.SAND || 
							above.getBlock().getType() == Material.ANVIL || 
							above.getBlock().getType() == Material.DRAGON_EGG){
						addBlock(above.getBlock(), ply);
					}
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
				Chest chest = (Chest) event.getClickedBlock().getState();
				if(chest.getInventory().getSize() > 27){
					Location loc = event.getClickedBlock().getLocation().clone();
					boolean isLeft = false;
					//West = -z; East = +z; North = +x; South = -x;
					if(!isLeft && event.getClickedBlock().getData() == 0x2){
						loc.setX(loc.getX() + 1);
						if(loc.getBlock().getType() == Material.CHEST){
							Bukkit.getLogger().info(MinigameUtils.createLocationID(loc));
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					else if(!isLeft && event.getClickedBlock().getData() == 0x3){
						loc.setX(loc.getX() - 1);
						if(loc.getBlock().getType() == Material.CHEST){
							Bukkit.getLogger().info(MinigameUtils.createLocationID(loc));
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					else if(!isLeft && event.getClickedBlock().getData() == 0x4){
						loc.setZ(loc.getZ() - 1);
						if(loc.getBlock().getType() == Material.CHEST){
							Bukkit.getLogger().info(MinigameUtils.createLocationID(loc));
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					else if(!isLeft && event.getClickedBlock().getData() == 0x5){
						loc.setZ(loc.getZ() + 1);
						if(loc.getBlock().getType() == Material.CHEST){
							Bukkit.getLogger().info(MinigameUtils.createLocationID(loc));
							isLeft = true;
						}
						else{
							loc = event.getClickedBlock().getLocation().clone();
						}
					}
					addBlock(loc.getBlock(), ply);
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
		if((event.getBlock().getType() == Material.GRAVEL || 
				event.getBlock().getType() == Material.SAND ||
				event.getBlock().getType() == Material.ANVIL ||
				event.getBlock().getType() == Material.DRAGON_EGG) &&
				checkBlockSides(event.getBlock().getLocation())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler
	private void leafDecay(LeavesDecayEvent event){
		if(checkBlockSides(event.getBlock().getLocation())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler
	private void treeGrow(StructureGrowEvent event){
		if(hasBlock(event.getLocation().getBlock())){
			for(BlockState block : event.getBlocks()){
				addBlock(block.getLocation().getBlock(), event.getPlayer());
			}
		}
	}
	
	@EventHandler
	private void bucketFill(PlayerBucketFillEvent event){
		if(pdata.playerInMinigame(event.getPlayer()) && pdata.getPlayersMinigame(event.getPlayer()).equals(minigame.getName())){
			if(((whitelistMode && getWBBlocks().contains(event.getBlockClicked().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlockClicked().getType()))) && 
					minigame.canBlockBreak()){
				addBlock(event.getBlockClicked(), event.getPlayer());
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void bucketEmpty(PlayerBucketEmptyEvent event){
		if(pdata.playerInMinigame(event.getPlayer()) && pdata.getPlayersMinigame(event.getPlayer()).equals(minigame.getName())){
			if(((whitelistMode && getWBBlocks().contains(event.getBlockClicked().getType())) || 
					(!whitelistMode && !getWBBlocks().contains(event.getBlockClicked().getType()))) && 
					minigame.canBlockPlace()){
				Location loc = new Location(event.getBlockClicked().getWorld(), 
						event.getBlockFace().getModX() + event.getBlockClicked().getX(), 
						event.getBlockFace().getModY() + event.getBlockClicked().getY(), 
						event.getBlockFace().getModZ() + event.getBlockClicked().getZ());
				addBlock(loc.getBlock(), event.getPlayer());
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void blockFromTo(BlockFromToEvent event){
		if(checkBlockSides(event.getBlock().getLocation())){
			addBlock(event.getToBlock(), null);
		}
	}
	
	@EventHandler
	public void blockBurn(BlockBurnEvent event){
		if(checkBlockSides(event.getBlock().getLocation())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler
	public void fireSpread(BlockSpreadEvent event){
		if(hasBlock(event.getSource())){
			addBlock(event.getBlock(), null);
		}
	}
	
	@EventHandler
	public void igniteblock(BlockIgniteEvent event){
		if(event.getPlayer() != null && pdata.playerInMinigame(event.getPlayer()) && 
				pdata.getPlayersMinigame(event.getPlayer()).equals(minigame.getName()) && 
				(event.getCause() == IgniteCause.FIREBALL || event.getCause() == IgniteCause.FLINT_AND_STEEL)){
			if(((whitelistMode && getWBBlocks().contains(Material.FIRE)) || 
					(!whitelistMode && !getWBBlocks().contains(Material.FIRE))) && 
					minigame.canBlockPlace()){
				addBlock(event.getBlock(), event.getPlayer());
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void tntExplode(EntityExplodeEvent event){
		if(event.getEntity() instanceof TNTPrimed){
			List<Entity> ents = event.getEntity().getNearbyEntities(40, 40, 40);
			for(Entity ent : ents){
				if(ent instanceof Player){
					Player ply = (Player) ent;
					if(plugin.pdata.playerInMinigame(ply) && plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(ply)).equals(minigame) && 
							((whitelistMode && getWBBlocks().contains(Material.TNT)) || 
							(!whitelistMode && !getWBBlocks().contains(Material.TNT)))){
						List<Block> removal = new ArrayList<Block>();
						for(Block block : event.blockList()){
							if(((whitelistMode && getWBBlocks().contains(block.getType())) || 
									(!whitelistMode && !getWBBlocks().contains(block.getType()))) && 
									minigame.canBlockBreak()){
								addBlock(block, null);
							}
							else{
								removal.add(block);
							}
						}
						event.blockList().removeAll(removal);
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	private void vehicleCreate(VehicleCreateEvent event){
		List<Entity> ents = event.getVehicle().getNearbyEntities(8, 8, 8);
		for(Entity ent : ents){
			if(ent instanceof Player){
				Player ply = (Player) ent;
				if(plugin.pdata.playerInMinigame(ply) && plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(ply)).equals(minigame)){
					addEntity(event.getVehicle(), ply, true);
					break;
				}
			}
		}
	}
	
	@EventHandler
	private void vehicleDestroy(VehicleDestroyEvent event){
		List<Entity> ents = event.getVehicle().getNearbyEntities(15, 15, 15);
		if(event.getAttacker() == null){
			for(Entity ent : ents){
				if(ent instanceof Player){
					Player ply = (Player) ent;
					if(plugin.pdata.playerInMinigame(ply) && plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(ply)).equals(minigame)){
						if(!hasEntity(event.getVehicle())){
							addEntity(event.getVehicle(), ply, false);
						}
						break;
					}
				}
			}
		}
		else{
			if(event.getAttacker() instanceof Player){
				Player ply = (Player) event.getAttacker();
				if(plugin.pdata.playerInMinigame(ply) && plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(ply)).equals(minigame)){
					if(!hasEntity(event.getVehicle())){
						addEntity(event.getVehicle(), ply, false);
					}
				}
			}
		}
	}
	
	@EventHandler
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
					if(plugin.pdata.playerInMinigame(ply) && plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(ply)).equals(minigame)){
						addEntity(animal, ply, false);
					}
				}
			}
		}
	}
	
	@EventHandler
	private void paintingPlace(HangingPlaceEvent event){
		Player ply = event.getPlayer();
		if(plugin.pdata.playerInMinigame(ply) && plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(ply)).equals(minigame)){
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
	
	@EventHandler
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
			if(plugin.pdata.playerInMinigame(ply) && plugin.mdata.getMinigame(plugin.pdata.getPlayersMinigame(ply)).equals(minigame)){
				event.setCancelled(true);
			}
		}
	}
	
//	@EventHandler
//	private void blockForm(EntityBlockFormEvent event){
//		String idloc = MinigameUtils.createLocationID(event.getBlock().getLocation());
//		int y = event.getBlock().getY();
//		int x = event.getBlock().getX();
//		int z = event.getBlock().getZ();
//		String world = event.getBlock().getWorld().getName();
//		
//		while(y < 256){
//			y++;
//			idloc = x + ":" + y + ":" + z + ":" + world;
//			Bukkit.getLogger().info(idloc);
//			if(blockdata.containsKey(idloc)){
//				addBlock(event.getBlock().getLocation().getBlock(), null);
//				return;
//			}
//		}
//	}
}
