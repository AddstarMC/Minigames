package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
@SuppressWarnings("deprecation")
public class MenuItem {
	private ItemStack displayItem = null;
	private Menu container = null;
	private int slot = 0;
	
	public MenuItem(String name, Material displayItem){
		boolean nullItem = false;
		if(displayItem == null){
			displayItem = Material.LEGACY_STAINED_GLASS_PANE;
			nullItem = true;
		}
		this.displayItem = new ItemStack(displayItem);
		ItemMeta meta = this.displayItem.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		this.displayItem.setItemMeta(meta);
		if(nullItem){
			this.displayItem.setDurability((short)14);
		}
	}
	
	public MenuItem(String name, List<String> description, Material displayItem){
		if(displayItem == null)
			displayItem = Material.LEGACY_THIN_GLASS;
		this.displayItem = new ItemStack(displayItem);
		ItemMeta meta = this.displayItem.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(description);
		this.displayItem.setItemMeta(meta);
	}
	
	public void setDescription(List<String> description){
		ItemMeta meta = displayItem.getItemMeta();
		
		meta.setLore(description);
		displayItem.setItemMeta(meta);
	}
	
	public List<String> getDescription(){
		return displayItem.getItemMeta().getLore();
	}
	
	public String getName(){
		return displayItem.getItemMeta().getDisplayName();
	}
	
	public ItemStack getItem(){
		return displayItem;
	}
	
	public void setItem(ItemStack item){
		if(item == null){
			Bukkit.getLogger().fine("Item Stack was null on: " + this.getDescription().toString());
			return;
		}
		ItemMeta ometa = displayItem.getItemMeta();
		displayItem = item.clone();
		ItemMeta nmeta = displayItem.getItemMeta();
		nmeta.setDisplayName(ometa.getDisplayName());
		nmeta.setLore(nmeta.getLore());
		displayItem.setItemMeta(nmeta);
	}
	
	public void update() {
	}
	
	public ItemStack onClick(){
		//Do stuff
		return getItem();
	}

	public ItemStack onClickWithItem(ItemStack item){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onRightClick(){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onShiftClick(){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onShiftRightClick(){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onDoubleClick(){
		//Do Stuff
		return getItem();
	}
	
	public void checkValidEntry(String entry){
		//Do Stuff
	}
	
	public void setContainer(Menu container){
		this.container = container;
	}
	
	public Menu getContainer(){
		return container;
	}
	
	public void setSlot(int slot){
		this.slot = slot;
	}
	
	public int getSlot(){
		return slot;
	}
}
