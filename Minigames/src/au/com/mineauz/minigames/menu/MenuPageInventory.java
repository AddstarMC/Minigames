package au.com.mineauz.minigames.menu;

import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
	
	public void setSlot(ItemStack item, int slot) {
		Validate.isTrue(slot >= 0 && slot < slots.length);
		slots[slot] = item;
	}
	
	public void removeSlot(int slot) {
		Validate.isTrue(slot >= 0 && slot < slots.length);
		slots[slot] = null;
	}
	
	@Override
	public MenuItem getClickItem(int slot) {
		return null;
	}
	
	public void clear() {
		Arrays.fill(slots, null);
	}
	
	public ItemStack[] getInventory() {
		return slots;
	}
	
	@Override
	protected ItemStack[] getDisplayItems() {
		return slots;
	}

	@Override
	public void update() {}
	
	public void updateFrom(Inventory inventory) {
		for (int i = 0; i < slots.length; ++i) {
			slots[i] = inventory.getItem(i);
		}
	}
}
