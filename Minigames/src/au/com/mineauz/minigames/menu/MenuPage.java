package au.com.mineauz.minigames.menu;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class MenuPage {
	private Menu container;
	private MenuPage previous;
	private MenuPage next;
	private int pageNumber;
	
	public MenuPage(MenuPage previous) {
		this.previous = previous;
		if (previous != null) {
			previous.next = this;
			pageNumber = previous.pageNumber + 1;
		} else {
			pageNumber = 0;
		}
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public MenuPage getPrevious() {
		return previous;
	}
	
	public MenuPage getNext() {
		return next;
	}
	
	void setNext(MenuPage page) {
		next = page;
	}
	
	public Menu getContainer() {
		return container;
	}
	
	protected void setContainer(Menu menu) {
		container = menu;
	}
	
	protected abstract ItemStack[] getDisplayItems();
	
	public void displayIn(Inventory inventory) {
		ItemStack[] slots = getDisplayItems();
		
		Validate.isTrue(inventory.getSize() >= slots.length);
		
		// Use this method as to only update slots we have (dont override page controls etc.)
		for (int i = 0; i < slots.length; ++i) {
			inventory.setItem(i, slots[i]);
		}
	}
	
	public abstract MenuItem getClickItem(int slot);
	
	public abstract void update();
}
