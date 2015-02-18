package au.com.mineauz.minigames.menu;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

public class MenuPageNormal extends MenuPage implements Iterable<MenuItem> {
	private MenuItem[] slots;
	private ItemStack[] cached;
	private boolean cacheDirty;
	
	public MenuPageNormal(int size, MenuPageNormal previous) {
		super(previous);
		slots = new MenuItem[size];
		cached = new ItemStack[size];
		cacheDirty = true;
	}
	
	@Override
	public MenuPageNormal getPrevious() {
		return (MenuPageNormal)super.getPrevious();
	}
	
	@Override
	public MenuPageNormal getNext() {
		return (MenuPageNormal)super.getNext();
	}
	
	public void setItem(MenuItem item, int slot) {
		Validate.isTrue(slot >= 0 && slot < slots.length);
		slots[slot] = item;
		if (item != null) {
			item.onAdd(getContainer(), getPageNumber(), slot);
		}
		cacheDirty = true;
	}
	
	public void removeItem(int slot) {
		Validate.isTrue(slot >= 0 && slot < slots.length);
		slots[slot] = null;
		cacheDirty = true;
	}
	
	/**
	 * Adds an item to this page.
	 * @param item The item to add. Must not be null
	 * @return True if the item was added, false if there was no space
	 */
	public boolean addItem(MenuItem item) {
		Validate.notNull(item);
		for (int i = 0; i < slots.length; ++i) {
			if (slots[i] instanceof MenuItemNewLine) {
				// Go to next line
				i += 9 - (i % 9) - 1;
				continue;
			}
			
			if (slots[i] == null) {
				slots[i] = item;
				item.onAdd(getContainer(), getPageNumber(), i);
				cacheDirty = true;
				return true;
			}
		}
		
		// No free space
		return false;
	}
	
	public void clear() {
		Arrays.fill(slots, null);
		cacheDirty = true;
	}
	
	public MenuItem getItem(int slot) {
		Validate.isTrue(slot >= 0 && slot < slots.length);
		return slots[slot];
	}
	
	@Override
	public MenuItem getClickItem(int slot) {
		return getItem(slot);
	}
	
	@Override
	public void update() {
		Arrays.fill(cached, null);
		for (int i = 0; i < slots.length; ++i) {
			MenuItem item = slots[i];
			if (item instanceof MenuItemNewLine) {
				// Go to next line
				i += 9 - (i % 9) - 1;
				continue;
			}
			
			if (item != null) {
				cached[i] = item.getItem();
			}
		}
		
		cacheDirty = false;
	}
	
	public void updateIfDirty() {
		if (cacheDirty) {
			update();
		}
	}
	
	@Override
	protected ItemStack[] getDisplayItems() {
		updateIfDirty();
		return cached;
	}
	
	@Override
	public Iterator<MenuItem> iterator() {
		return Iterators.filter(Iterators.forArray(slots), Predicates.notNull());
	}
}
