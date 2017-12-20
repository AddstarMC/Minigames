package au.com.mineauz.minigames.blockRecorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;

public class BlockData implements ConfigurationSerializable {
	private Location location;
	private BlockState state;
	private MinigamePlayer player = null;
	private ItemStack[] items = null;
	private Map<String, Object> specialData = new HashMap<>();
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
		
		List<ItemStack> itemRand = new ArrayList<>();

		for (ItemStack item1 : items) {
			if (item1 != null) {
				itemRand.add(item1.clone());
			}
		}
		
		Collections.shuffle(itemRand);
		List<ItemStack> itemChest = new ArrayList<>();
		
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
	
	public boolean hasRandomized(){
		return hasRandomized;
	}

	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder("{");
		ret.append("mat:").append(state.getType().toString()).append(";");
		ret.append("data:").append(state.getData().getData()).append(";");
		ret.append("x:").append(state.getX()).append(";");
		ret.append("y:").append(state.getY()).append(";");
		ret.append("z:").append(state.getZ()).append(";");
		ret.append("world:").append(state.getWorld().getName());
		if(items != null){
			ret.append(";");
			
			int c = 0;
			ret.append("items:");
			for(ItemStack i : items){
				if(i != null){
					ret.append("(");
					ret.append("item-").append(i.getType().toString()).append("|");
					ret.append("dur-").append(i.getDurability()).append("|");
					ret.append("c-").append(i.getAmount()).append("|");
					if(!i.getEnchantments().isEmpty()){
						ret.append("enc-");
						for(Enchantment e : i.getEnchantments().keySet()){
							ret.append("[");
							ret.append(e.getName()).append(",");
							ret.append(i.getEnchantments().get(e));
							ret.append("]");
						}
						ret.append("|");
					}
					ret.append("slot-").append(c);
					ret.append(")");
				}
				c++;
			}
		}
		ret.append("}");
		return ret.toString();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> out =  new HashMap<>();
		out.put("Location",location.serialize());
		out.put("Material",state.getType());
		if(state instanceof Container){
			List<ItemStack> itemStacks = new ArrayList<>();
			Container container = (Container) state;
			Inventory items = container.getInventory();
			for (ItemStack item:items) {
				itemStacks.add(item);
			}
			out.put("Contents",itemStacks);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	public BlockData deserialize(Map<String,Object> input) {
		Location location = null;
		Material material = Material.AIR;
		try {
			location = Location.deserialize((Map<String, Object>) input.get("Location"));
			material = (Material) input.get("Material");
			BlockState b = location.getBlock().getState();
			b.setType(material);
			if(b instanceof Container){
				List<ItemStack> items = (List<ItemStack>) input.get("Contents");
				ItemStack[] contents = new ItemStack[items.size()];
				items.toArray(contents);
				((Container) b).getInventory().setContents(contents);
			}
			return new BlockData(b, null);

		}catch (ClassCastException e){
			e.printStackTrace();
		}
		return null;
	}
}
