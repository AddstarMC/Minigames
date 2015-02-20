package au.com.mineauz.minigames.menu;

import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a inventory based page. 
 * This page stores actual ItemStacks as opposed to MenuItems
 */
public class MenuPageInventory extends MenuPage {
	private ItemStack[] slots;
	
	public MenuPageInventory(int size, MenuPageInventory previous) {
		super(previous);
		
		slots = new ItemStack[size];
	}
	
	@Override
	public MenuPageInventory getPrevious() {
		return (MenuPageInventory)super.getPrevious();
	}
	
	@Override
	public MenuPageInventory getNext() {
		return (MenuPageInventory)super.getNext();
	}
	
	/**
	 * Changes a slot
	 * @param item The ItemStack to set or null
	 * @param slot The slot to change.
	 */
	public void setSlot(ItemStack item, int slot) {
		Validate.isTrue(slot >= 0 && slot < slots.length);
		slots[slot] = item;
	}
	
	/**
	 * Clears a slot
	 * @param slot The slot to change.
	 */
	public void removeSlot(int slot) {
		Validate.isTrue(slot >= 0 && slot < slots.length);
		slots[slot] = null;
	}
	
	@Override
	public MenuItem getClickItem(int slot) {
		return null;
	}
	
	/**
	 * Clears the page
	 */
	public void clear() {
		Arrays.fill(slots, null);
	}
	
	/**
	 * @return Returns the array of ItemStacks on this page
	 */
	public ItemStack[] getInventory() {
		return slots;
	}
	
	@Override
	protected ItemStack[] getDisplayItems() {
		return slots;
	}

	@Override
	public void update() {}
	
	/**
	 * Updates the page using the contents of an inventory
	 * @param inventory The inventory to use
	 */
	public void updateFrom(Inventory inventory) {
		for (int i = 0; i < slots.length; ++i) {
			slots[i] = inventory.getItem(i);
		}
	}
}
