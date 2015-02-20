package au.com.mineauz.minigames.menu;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The base MenuPage for creating different types of pages for Menus.
 */
public abstract class MenuPage {
	private Menu container;
	private MenuPage previous;
	private MenuPage next;
	private int pageNumber;
	
	/**
	 * Creates a new menu page that links to the provided previous page.
	 * @param previous The previous page or null if this is the first page
	 */
	public MenuPage(MenuPage previous) {
		this.previous = previous;
		if (previous != null) {
			previous.next = this;
			pageNumber = previous.pageNumber + 1;
		} else {
			pageNumber = 0;
		}
	}
	
	/**
	 * @return Returns the 0 based page number of this page
	 */
	public int getPageNumber() {
		return pageNumber;
	}
	
	/**
	 * @return Returns the previous page or null if this is the first
	 */
	public MenuPage getPrevious() {
		return previous;
	}
	
	/**
	 * @return Returns the next page or null if this is the last
	 */
	public MenuPage getNext() {
		return next;
	}
	
	void setNext(MenuPage page) {
		next = page;
	}
	
	/**
	 * @return Returns the Menu that owns this page
	 */
	public Menu getContainer() {
		return container;
	}
	
	protected void setContainer(Menu menu) {
		container = menu;
	}
	
	/**
	 * Turns this page into an array of ItemStacks for display. 
	 * @return An array of ItemStack, the length should be the size of the page
	 */
	protected abstract ItemStack[] getDisplayItems();
	
	/**
	 * Displays this page into an Inventory.
	 * @param inventory The inventory to display into. The size of the inventory must be greater or equal to the size of the page
	 */
	public void displayIn(Inventory inventory) {
		ItemStack[] slots = getDisplayItems();
		
		Validate.isTrue(inventory.getSize() >= slots.length);
		
		// Use this method as to only update slots we have (dont override page controls etc.)
		for (int i = 0; i < slots.length; ++i) {
			inventory.setItem(i, slots[i]);
		}
	}
	
	/**
	 * Gets the MenuItem in the specified slot for click purposes
	 * @param slot The slot to use
	 * @return The MenuItem or null if the slot is empty
	 */
	public abstract MenuItem getClickItem(int slot);
	
	/**
	 * Updates this page. Child classes should use this to update items within the page and any other updating the page needs.
	 */
	public abstract void update();
}
