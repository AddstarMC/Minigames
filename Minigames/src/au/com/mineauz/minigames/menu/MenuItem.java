package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItem {
	private ItemStack displayItem = null;
	private Menu container = null;
	private int slot = 0;
	private int page = 0;
	
	public MenuItem(String name, Material displayItem){
		boolean nullItem = false;
		if(displayItem == null){
			displayItem = Material.STAINED_GLASS_PANE;
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
			displayItem = Material.THIN_GLASS;
		this.displayItem = new ItemStack(displayItem);
		ItemMeta meta = this.displayItem.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		if (description != null)
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
	
	public void setName(String name) {
		ItemMeta meta = displayItem.getItemMeta();
		meta.setDisplayName(name);
		displayItem.setItemMeta(meta);
	}
	
	public ItemStack getItem(){
		return displayItem;
	}
	
	public void setItem(ItemStack item){
		ItemMeta ometa = displayItem.getItemMeta();
		displayItem = item.clone();
		ItemMeta nmeta = displayItem.getItemMeta();
		nmeta.setDisplayName(ometa.getDisplayName());
		nmeta.setLore(nmeta.getLore());
		displayItem.setItemMeta(nmeta);
	}
	
	public ItemStack onClick(MinigamePlayer player){
		//Do stuff
		return getItem();
	}

	public ItemStack onClickWithItem(MinigamePlayer player, ItemStack item){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onRightClick(MinigamePlayer player){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onShiftClick(MinigamePlayer player){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onShiftRightClick(MinigamePlayer player){
		//Do stuff
		return getItem();
	}
	
	public ItemStack onDoubleClick(MinigamePlayer player){
		//Do Stuff
		return getItem();
	}
	
	public void checkValidEntry(MinigamePlayer player, String entry){
		//Do Stuff
	}
	
	public Menu getContainer(){
		return container;
	}
	
	void onAdd(Menu container, int page, int slot) {
		this.container = container;
		this.page = page;
		this.slot = slot;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public int getPage() {
		return page;
	}
	
	public void remove() {
		container.removeItemFlow(slot, page);
		container.refresh();
	}
	
	public void removeStatic() {
		container.removeItem(slot, page);
		container.refresh();
	}
	
	public final void beginManualEntry(MinigamePlayer player, String message, int time) {
		player.setNoClose(true);
		player.getPlayer().closeInventory();
		player.sendMessage(message, null);
		player.startManualEntry(this, time);
	}
	
	public final void completeManualEntry(MinigamePlayer player, String value) {
		player.cancelMenuReopen();
		player.setNoClose(false);
		checkValidEntry(player, value);
		getContainer().displaySession(player, player.getMenuSession());
	}
}
