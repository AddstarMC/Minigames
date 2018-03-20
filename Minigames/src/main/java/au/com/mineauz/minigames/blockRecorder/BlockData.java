package au.com.mineauz.minigames.blockRecorder;

import au.com.mineauz.minigames.MinigamePlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlockData {
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
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString(){
		String ret = "{";
		ret += "mat:" + state.getType().toString() + ";";
		ret += "data:" + state.getData().getData() + ";";
		ret += "x:" + state.getX() + ";";
		ret += "y:" + state.getY() + ";";
		ret += "z:" + state.getZ() + ";";
		ret += "world:" + state.getWorld().getName();
		if(items != null){
			ret += ";";
			
			int c = 0;
			ret += "items:";
			for(ItemStack i : items){
				if(i != null){
					ret += "(";
					ret += "item-" + i.getType().toString() + "|";
					ret += "dur-" + i.getDurability() + "|";
					ret += "c-" + i.getAmount() + "|";
					if(!i.getEnchantments().isEmpty()){
						ret += "enc-";
						for(Enchantment e : i.getEnchantments().keySet()){
							ret += "[";
							ret += e.getName() + ",";
							ret += i.getEnchantments().get(e);
							ret += "]";
						}
						ret += "|";
					}
					ret += "slot-" + c;
					ret += ")";
				}
				c++;
			}
		}
		ret += "}";
		return ret;
	}
}
