package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.*;
import java.util.*;

public class RecorderData implements Listener{
	private static Minigames plugin;
	
	private Minigame minigame;
	private boolean whitelistMode = false;
    private static List<Material> physBlocks = new ArrayList<>();
	private boolean hasCreatedRegenBlocks = false;
	
	private Map<String, BlockData> blockdata;
	private Map<Integer, EntityData> entdata;
    private List<Material> wbBlocks = new ArrayList<>();
	
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
        physBlocks.add(Material.NETHER_WARTS);
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
		physBlocks.add(Material.WATER);
		physBlocks.add(Material.LAVA);
		physBlocks.add(Material.STATIONARY_WATER);
		physBlocks.add(Material.STATIONARY_LAVA);
		physBlocks.add(Material.ANVIL);
		physBlocks.add(Material.DRAGON_EGG);
		physBlocks.add(Material.SKULL);
		physBlocks.add(Material.SNOW);
        physBlocks.add(Material.VINE);
        physBlocks.add(Material.PORTAL);
        physBlocks.add(Material.COCOA);
        physBlocks.add(Material.CARROT);
        physBlocks.add(Material.POTATO);
        physBlocks.add(Material.WALL_BANNER);
        physBlocks.add(Material.PISTON_MOVING_PIECE);
        physBlocks.add(Material.PISTON_EXTENSION);
    }
	
	public RecorderData(Minigame minigame){
        plugin = Minigames.getPlugin();
		
		this.minigame = minigame;
        blockdata = new HashMap<>();
        entdata = new HashMap<>();
		
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
		    if(block instanceof InventoryHolder){
                InventoryHolder inv = (InventoryHolder) block;
                if(inv instanceof DoubleChest){
                    Location left = ((DoubleChest) inv).getLeftSide().getInventory().getLocation().clone();
                    Location right = ((DoubleChest) inv).getRightSide().getInventory().getLocation().clone();
                    if (bdata.getLocation() == left){
                        addInventory(bdata,((DoubleChest) inv).getLeftSide());
                        if(minigame.isRandomizeChests())
                            bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
                    }
					BlockData secondChest = addBlock(right.getBlock(), modifier);
					if(secondChest.getItems() == null){
                        addInventory(secondChest,((DoubleChest) inv).getRightSide());
                        if(minigame.isRandomizeChests())
                            secondChest.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
                    }
                else if(inv instanceof Chest){
                        addInventory(bdata,inv);
                        if(minigame.isRandomizeChests())
                            bdata.randomizeContents(minigame.getMinChestRandom(), minigame.getMaxChestRandom());
                    }
                }else{
                    addInventory(bdata,inv);
                }
            }
			else if(block.getType() == Material.FLOWER_POT){
				bdata.setSpecialData("contents", block.getData());
			}
			
			blockdata.put(sloc, bdata);
			return bdata;
		}
		else{
			if(block.getType() != Material.CHEST || !blockdata.get(sloc).hasRandomized())
				blockdata.get(sloc).setModifier(modifier);
			return blockdata.get(sloc);
		}
	}

	public void addInventory(BlockData bdata, InventoryHolder ih){
        List<ItemStack> items = new ArrayList<>();
            for (ItemStack item : ih.getInventory()) {
                if(item!=null) {
                    items.add(item.clone());
                }
            }
            ItemStack[] inventory = new ItemStack[items.size()];
            items.toArray(inventory);
            bdata.setItems(inventory);
    }

	
	public void addEntity(Entity ent, MinigamePlayer player, boolean created){
		EntityData edata = new EntityData(ent, player, created);
		entdata.put(ent.getEntityId(), edata);
	}
	
	public boolean hasEntity(Entity ent){
        return entdata.containsKey(ent.getEntityId());
    }
	
	public boolean hasBlock(Block block){
		String sloc = String.valueOf(block.getLocation().getBlockX()) + ":" + block.getLocation().getBlockY() + ":" + block.getLocation().getBlockZ();
        return blockdata.containsKey(sloc);
    }
	
	public void restoreAll(MinigamePlayer modifier) {
		if (!blockdata.isEmpty()) {
			restoreBlocks(modifier);
		}
		
		if (!entdata.isEmpty()) {
			restoreEntities(modifier);
		}
	}
	
	public void restoreBlocks(){
//		saveAllBlockData();
		restoreBlocks(null);
	}
	
	public void restoreEntities(){
		restoreEntities(null);
		entdata.clear();
	}
	
	public void restoreBlocks(final MinigamePlayer modifier){
		// When rolling back a single player's changes dont change the overall games state
		if (modifier == null) {
			minigame.setState(MinigameState.REGENERATING);
		}
		
		Iterator<BlockData> it = blockdata.values().iterator();
		final List<BlockData> resBlocks = Lists.newArrayList();
		final List<BlockData> addBlocks = Lists.newArrayList();
		
		while (it.hasNext()) {
			BlockData data = it.next();
			
			if (modifier == null || modifier.equals(data.getModifier())) {
				it.remove();
				
				// Clear inventories
				if(data.getLocation().getBlock().getState() instanceof InventoryHolder) {
					InventoryHolder block = (InventoryHolder) data.getLocation().getBlock().getState();
					block.getInventory().clear();
				}
				
				if(physBlocks.contains(data.getBlockState().getType()) || data.getItems() != null) {
					addBlocks.add(data);
				} else {
					resBlocks.add(data);
				}
			}
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				Collections.sort(resBlocks, new Comparator<BlockData>() {

					@Override
					public int compare(BlockData o1, BlockData o2) {
                        int comp = Integer.compare(o1.getBlockState().getChunk().getX(), o2.getBlockState().getChunk().getX());
						if(comp != 0)
							return comp;
                        comp = Integer.compare(o1.getBlockState().getChunk().getZ(), o2.getBlockState().getChunk().getZ());
						if(comp != 0)
							return comp;
                        return Integer.compare(o1.getBlockState().getY(), o2.getBlockState().getY());
					}
				});
				Collections.sort(addBlocks, new Comparator<BlockData>() {

					@Override
					public int compare(BlockData o1, BlockData o2) {
                        int comp = Integer.compare(o1.getBlockState().getChunk().getX(), o2.getBlockState().getChunk().getX());
						if(comp != 0)
							return comp;
                        comp = Integer.compare(o1.getBlockState().getChunk().getZ(), o2.getBlockState().getChunk().getZ());
						if(comp != 0)
							return comp;
                        return Integer.compare(o1.getBlockState().getY(), o2.getBlockState().getY());
					}
				});
				
				new RollbackScheduler(resBlocks, addBlocks, minigame, modifier);
			}
		});
	}
	
	public void restoreEntities(MinigamePlayer player) {
		Iterator<EntityData> it = entdata.values().iterator();
		while (it.hasNext()) {
			EntityData entdata = it.next();
			if (player == null || player.equals(entdata.getModifier())) {
				if (entdata.wasCreated()) {
					Entity ent = entdata.getEntity();
					// Entity needs to be removed
					if (ent.isValid()) {
						ent.remove();
					}
				} else {
					// Entity needs to be spawned
					Location location = entdata.getEntityLocation();
					location.getWorld().spawnEntity(location, entdata.getEntityType());
				}
				
				it.remove();
			}
		}
	}
	
	public void clearRestoreData(){
		entdata.clear();
		blockdata.clear();
	}
	
	public boolean hasData(){
        return !(blockdata.isEmpty() && entdata.isEmpty());
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
        return minigame.getRegenArea1() != null && minigame.getRegenArea2() != null;
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
        return location.getWorld() == minigame.getRegenArea1().getWorld() &&
                location.getBlockX() >= getRegenMinX() && location.getBlockX() <= getRegenMaxX() &&
                location.getBlockY() >= getRegenMinY() && location.getBlockY() <= getRegenMaxY() &&
                location.getBlockZ() >= getRegenMinZ() && location.getBlockZ() <= getRegenMaxZ();
    }
	
	public void saveAllBlockData(){
		File f = new File(plugin.getDataFolder() + "/minigames/" + minigame.getName(false) + "/backup.dat");
		
		try {
			BufferedWriter wr = new BufferedWriter(new FileWriter(f));
			int c = 0;
			for(BlockData bd : blockdata.values()){
				wr.write(bd.toString());
				c++;
				if(c >= 10){
					wr.newLine();
					c = 0;
				}
			}
			
			wr.close();
		} 
		catch (FileNotFoundException e) {
			Bukkit.getLogger().severe("File not found!!!");
			e.printStackTrace();
		} 
		catch (IOException e) {
			Bukkit.getLogger().severe("IO Error!");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean restoreBlockData(){
		File f = new File(plugin.getDataFolder() + "/minigames/" + minigame.getName(false) + "/backup.dat");
		
		if(!f.exists()){
			Bukkit.getLogger().info("No backup file found for " + minigame.getName(false));
			return false;
		}
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));

            Map<String, String> args = new HashMap<>();
			String line;
			String[] blocks;
			String[] block;
			World w;
			BlockData bd;
			BlockState state;
			ItemStack[] items;
			String[] sitems;
			ItemStack item;
            Map<String, String> iargs = new HashMap<>();
			
			while(br.ready()){
				line = br.readLine();
				
				blocks = line.split("\\}\\{");
				
				for(String bl : blocks){
					args.clear();
					
					bl = bl.replace("{", "");
					bl = bl.replace("}", "");
					
					block = bl.split(";");
					for(String b : block){
						String[] spl = b.split(":");
						if(spl.length > 1){
							args.put(spl[0], spl[1]);
						}
					}
					
					w = Bukkit.getWorld(args.get("world"));
					state = w.getBlockAt(Integer.valueOf(args.get("x")), Integer.valueOf(args.get("y")), Integer.valueOf(args.get("z"))).getState();
					state.setType(Material.getMaterial(args.get("mat")));
					state.setRawData(Byte.valueOf(args.get("data")));
					
					bd = new BlockData(state, null);
					
					if(args.containsKey("items")){
						if(state.getType() == Material.DISPENSER || state.getType() == Material.DROPPER){
							items = new ItemStack[InventoryType.DISPENSER.getDefaultSize()];
						}
						else if(state.getType() == Material.HOPPER){
							items = new ItemStack[InventoryType.HOPPER.getDefaultSize()];
						}
						else if(state.getType() == Material.FURNACE){
							items = new ItemStack[InventoryType.FURNACE.getDefaultSize()];
						}
						else if(state.getType() == Material.BREWING_STAND){
							items = new ItemStack[InventoryType.BREWING.getDefaultSize()];
						}
						else{
							items = new ItemStack[InventoryType.CHEST.getDefaultSize()];
						}
						
						sitems = args.get("items").split("\\)\\(");
						
						for(String i : sitems){
							i = i.replace("(", "");
							i = i.replace(")", "");
							
							for(String s : i.split("\\|")){
								String[] spl = s.split("-");
								if(spl.length > 1){
									iargs.put(s.split("-")[0], s.split("-")[1]);
								}
							}
							
							item = new ItemStack(Material.getMaterial(iargs.get("item")), 
									Integer.valueOf(iargs.get("c")), Short.valueOf(iargs.get("dur")));
							
							if(iargs.containsKey("enc")){
								for(String s : iargs.get("enc").split("\\]\\[")){
									item.addUnsafeEnchantment(Enchantment.getByName(s.split(",")[0].replace("[", "")), 
											Integer.valueOf(s.split(",")[1].replace("]", "")));
								}
							}
							
							items[Integer.valueOf(iargs.get("slot"))] = item;
							iargs.clear();
						}
						
						bd.setItems(items);
					}
					
					blockdata.put(MinigameUtils.createLocationID(bd.getLocation()), bd);
				}
			}
			
			br.close();
		}
		catch (FileNotFoundException e){
			Bukkit.getLogger().severe("File not found!!!");
			e.printStackTrace();
		} catch (IOException e) {
			Bukkit.getLogger().severe("IO Error!");
			e.printStackTrace();
		}
		
		return true;
	}
	
	@EventHandler(ignoreCancelled = true)
	private void vehicleCreate(VehicleCreateEvent event){
		if(hasRegenArea() && minigame.hasPlayers() && blockInRegenArea(event.getVehicle().getLocation())){
			addEntity(event.getVehicle(), null, true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void vehicleDestroy(VehicleDestroyEvent event){
		if(event.getAttacker() == null){
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
                List<Block> blocks = new ArrayList<Block>(event.blockList());
				
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
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void physicalBlock(EntityChangeBlockEvent event)
	{
		if(hasRegenArea() && blockInRegenArea(event.getBlock().getLocation()))
		{
			if(minigame.isRegenerating()){
				event.setCancelled(true);
				return;
			}
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
                event.getEntity().setMetadata("FellInMinigame", new FixedMetadataValue(Minigames.getPlugin(), true));
				addEntity(event.getEntity(), null, true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void cartHopperPickup(InventoryPickupItemEvent event){
		if(hasRegenArea() && minigame.hasPlayers() && event.getInventory().getHolder() instanceof HopperMinecart){
			Location loc = ((HopperMinecart)event.getInventory().getHolder()).getLocation();
			if(blockInRegenArea(loc)){
				addEntity((HopperMinecart)event.getInventory().getHolder(), null, false);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	private void cartkMoveItem(InventoryMoveItemEvent event){
		if(!hasRegenArea() || !minigame.hasPlayers()) return;
		
		Location loc = null;
		if(event.getInitiator().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getInitiator().getHolder()).getLocation().clone();
			if(blockInRegenArea(loc))
				addEntity((Entity)event.getInitiator().getHolder(), null, false);
		}
		
		loc = null;
		if(event.getDestination().getHolder() instanceof HopperMinecart){
			loc = ((HopperMinecart)event.getDestination().getHolder()).getLocation().clone();
			if(blockInRegenArea(loc))
				addEntity((Entity)event.getInitiator().getHolder(), null, false);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void physEvent(BlockPhysicsEvent event){
		if(minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getBlock().getLocation())){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void waterFlow(BlockFromToEvent event){
		if(minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getBlock().getLocation()))
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void fireSpread(BlockSpreadEvent event){
		if(minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getBlock().getLocation()))
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void interact(PlayerInteractEvent event){
		if(minigame.isRegenerating() && hasRegenArea() && blockInRegenArea(event.getClickedBlock().getLocation())){
			event.setCancelled(true);
		}
	}
}
