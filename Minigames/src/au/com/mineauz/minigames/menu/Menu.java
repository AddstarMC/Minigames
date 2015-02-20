package au.com.mineauz.minigames.menu;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;

public class Menu implements Iterable<MenuItem> {
	private int rows;
	private MenuPage firstPage;
	private MenuItem[] controlSlots;
	private boolean trackHistory = true;
	
	private String name;
	private boolean allowModify = false;
	private Set<MinigamePlayer> viewers;
	
	/**
	 * Creates a new Menu with MenuItem based pages
	 * @param rows The number of rows to use in this Menu. This must be in the range of 1 to 5. Note that 1 extra row will (the control bar) will be added
	 * @param title The title of this menu.
	 */
	public Menu(int rows, String title) {
		this(rows, title, new MenuPageNormal(rows * 9, null));
	}
	
	/**
	 * Creates a new Menu with the specified type of page.
	 * Menus using custom pages cannot use any of the shortcut methods for adding and removing MenuItems and pages
	 * @param rows The number of rows to use in this Menu. This must be in the range of 1 to 5. Note that 1 extra row will (the control bar) will be added
	 * @param title The title of this menu.
	 * @param firstPage An instanceof MenuPage which will be the first page of this menu. Custom page types have to be manually dealt with
	 */
	public Menu(int rows, String title, MenuPage firstPage) {
		if(rows > 5)
			rows = 5;
		else if(rows < 1)
			rows = 1;
		
		this.rows = rows;
		this.name = title;
		viewers = Sets.newHashSet();
		this.firstPage = firstPage;
		firstPage.setContainer(this);
		controlSlots = new MenuItem[5];
	}
	
	/**
	 * @return Returns the title of this menu
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return Returns the first page of this menu.
	 */
	public MenuPage getFirstPage() {
		return firstPage;
	}
	
	/**
	 * @return Returns the number of pages in this menu
	 */
	public int getPageCount() {
		int count = 0;
		MenuPage page = firstPage;
		while(page != null) {
			++count;
			page = page.getNext();
		}
		
		return count;
	}
	
	/**
	 * Gets a specific page in this menu
	 * @param index The index of the page
	 * @return The page
	 * @throws IndexOutOfBoundsException thrown if the specified index does not point to a page
	 */
	public MenuPage getPage(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		int count = 0;
		MenuPage page = firstPage;
		while(page != null) {
			if (count == index) {
				return page;
			}
			++count;
			page = page.getNext();
		}
		
		throw new IndexOutOfBoundsException();
	}
	
	/**
	 * @return Returns the last page in the menu
	 */
	public MenuPage getLastPage() {
		if (firstPage == null) {
			return null;
		}
		
		MenuPage page = firstPage;
		
		while (page.getNext() != null) {
			page = page.getNext();
		}
		
		return page;
	}
	
	/**
	 * Sets a ControlBar slot.<br>
	 * The ControlBar is the bottom row of the screen and is independent of pages. There are 5 slots you can use:
	 * <pre>[ ] [0] [1] [ ] [ ] [ ] [2] [3] [4]</pre>
	 * The numbered slots are the indices of those slots. The blank slots are reserved: 
	 * The left most slot is for the back button, and the middle slots are for page navigation.
	 * @param item The MenuItem to place in this slot, or null to remove
	 * @param slot The slot from 0 to 4 inclusive, to place it at
	 */
	public void setControlItem(MenuItem item, int slot) {
		Validate.isTrue(slot >= 0 && slot < 5);
		
		controlSlots[slot] = item;
	}
	
	/**
	 * Places a MenuItem in the specified slot on the specified page.<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param item The MenuItem to place
	 * @param slot The slot on the page to use.
	 * @param page The page of the menu to use. The page <b>must</b> already exist before using this.
	 */
	public void setItem(MenuItem item, int slot, int page) {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal menuPage = (MenuPageNormal)getPage(page);
		menuPage.setItem(item, slot);
	}
	
	/**
	 * Removes a MenuItem. This will not pull menu items in to fill the space<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param slot The slot on the page to use.
	 * @param page The page of the menu to use. The page <b>must</b> exist
	 */
	public void removeItem(int slot, int page){
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal menuPage = (MenuPageNormal)getPage(page);
		menuPage.removeItem(slot);
	}
	
	/**
	 * Removes a MenuItem pulling items from the right to fill the space<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param slot The slot on the page to use.
	 * @param page The page of the menu to use. The page <b>must</b> exist
	 */
	public void removeItemFlow(int slot, int page){
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal menuPage = (MenuPageNormal)getPage(page);
		MenuPageNormal nextPage;
		int slotCount = rows * 9;
		
		menuPage.removeItem(slot);
		
		// Move items to fill space
		while (menuPage != null) {
			// Do on current page
			for (; slot < slotCount - 1; ++slot) {
				menuPage.setItem(menuPage.getItem(slot+1), slot);
				// Stop after new line
				if (menuPage.getItem(slot) instanceof MenuItemNewLine) {
					menuPage.setItem(null, slot+1);
					return;
				}
			}
			
			// Do across pages
			nextPage = menuPage.getNext();
			if (nextPage != null) {
				menuPage.setItem(nextPage.getItem(0), slot);
			} else {
				menuPage.removeItem(slot);
			}
			
			// Next page
			slot = 0;
			menuPage = nextPage;
		}
	}
	
	/**
	 * Adds a MenuItem in the next free space adding pages if needed<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param item The MenuItem to add
	 */
	public void addItem(MenuItem item) {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal menuPage = (MenuPageNormal)getLastPage();
		while (!menuPage.addItem(item)) {
			menuPage = addPage();
		}
	}
	
	/**
	 * Inserts a MenuItem before the item in the specified position pushing items to the right.<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param item The MenuItem to insert
	 * @param slot The slot on the page to use.
	 * @param page The page of the menu to use. The page <b>must</b> exist. 
	 */
	public void insertItem(MenuItem item, int slot, int page) {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		int slotCount = rows * 9;
		
		MenuPageNormal menuPage = (MenuPageNormal)getPage(page);
		MenuPageNormal nextPage;
		
		MenuItem temp = null;
		
		// Move items to fill space
		while (menuPage != null) {
			// Do on current page
			for (; slot < slotCount; ++slot) {
				temp = menuPage.getItem(slot);
				menuPage.setItem(item, slot);
				// Empty spot filled, dont continue
				if (temp == null) {
					return;
				}
				item = temp;
			}
			
			// Do across pages
			if (item != null) {
				nextPage = menuPage.getNext();
				if (nextPage == null) {
					nextPage = addPage();
				}
			} else {
				break;
			}
			
			// Next page
			slot = 0;
			menuPage = nextPage;
		}
	}
	
	/**
	 * Inserts a MenuItem before the specified MenuItem pushing items to the right.<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param item The MenuItem to insert.
	 * @param before The MenuItem to use as a marker. 
	 */
	public void addItemBefore(MenuItem item, MenuItem before) {
		Validate.notNull(before);
		insertItem(item, before.getSlot(), before.getPage());
	}
	
	/**
	 * Inserts a MenuItem after the specified MenuItem pushing items to the right.<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param item The MenuItem to insert.
	 * @param after The MenuItem to use as a marker. 
	 */
	public void addItemAfter(MenuItem item, MenuItem after) {
		Validate.notNull(after);
		int slotCount = rows * 9;
		int slot = after.getSlot();
		int page = after.getPage();
		
		++slot;
		if (slot >= slotCount) {
			slot = 0;
			++page;
		}
		
		insertItem(item, slot, page);
	}
	
	/**
	 * Adds another page to this menu<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @return The added page
	 */
	public MenuPageNormal addPage() {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal page = new MenuPageNormal(rows * 9, (MenuPageNormal)getLastPage());
		page.setContainer(this);
		return page;
	}
	
	/**
	 * This is to be used for adding new custom pages. Note that this does not actually add the page onto 
	 * the previous page (this is done in the constructor for the page). This method links the page to this menu 
	 * @param page The page to add. This must match the type of the first page
	 */
	public void addPage(MenuPage page) {
		Validate.isTrue(page.getClass().isInstance(firstPage));
		
		page.setContainer(this);
	}
	
	/**
	 * Adds all the MenuItems in the list. See {@link #addItem(MenuItem)}<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 * @param items A list of MenuItems to add
	 */
	public void addItems(List<MenuItem> items) {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		for (MenuItem item : items) {
			addItem(item);
		}
	}
	
	/**
	 * Clears the menu (except the ControlBar)
	 */
	public void clear() {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		MenuPageNormal page = (MenuPageNormal)firstPage;
		
		while(page != null) {
			page.clear();
			page = page.getNext();
		}
		
		firstPage.setNext(null);
	}
	
	/**
	 * Displays this menu to the player.
	 * @param player The player to display the menu to.
	 */
	public void displayMenu(MinigamePlayer player) {
		MenuSession session = player.getMenuSession();
		
		while(session != null && !session.current.getTrackHistory()) {
			session = session.previous;
		}
		
		session = new MenuSession(this, session);
		
		displaySession(player, session);
	}
	
	/**
	 * Displays this menu to the player at the specified page
	 * @param player The player to display the menu to
	 * @param page The page to display
	 */
	public void displayMenu(MinigamePlayer player, int page) {
		Validate.isTrue(page >= 0 && page < getPageCount());
		
		MenuSession session = player.getMenuSession();
		if (session != null) {
			if (session.current != this) {
				while(session != null && !session.current.getTrackHistory()) {
					session = session.previous;
				}
				
				session = new MenuSession(this, session);
			}
		}
		
		session.page = page;
		displaySession(player, session);
	}
	
	/**
	 * Displays a MenuSession to the player
	 * @param player The player to display to
	 * @param session The session to use. The Menu declared by the session must be this menu
	 */
	public void displaySession(MinigamePlayer player, MenuSession session) {
		Validate.isTrue(session.current == this);
		
		Inventory inv = Bukkit.createInventory(null, (rows + 1) * 9, getName());
		MenuPage page = getPage(session.page);
		
		MenuItem[] controlBar = createControlBar(session);
		session.controlBar = controlBar;
		
		displayInto(inv, page, controlBar);
		
		// Display it
		player.getPlayer().openInventory(inv);
		if (player.getMenuSession() != null && player.getMenuSession() != session) {
			player.getMenuSession().current.onCloseMenu(player);
		}
		player.setMenuSession(session);
		viewers.add(player);
	}
	
	private void displayInto(Inventory inv, MenuPage page, MenuItem[] controlBar) {
		// Page content
		page.displayIn(inv);
		
		// Add controls
		for (int i = 0; i < 9; ++i) {
			MenuItem item = controlBar[i];
			if (item != null) {
				inv.setItem(inv.getSize()-9+i, item.getItem());
			} else {
				inv.setItem(inv.getSize()-9+i, null);
			}
		}
	}
	
	private MenuItem[] createControlBar(MenuSession session) {
		MenuItem[] bar = new MenuItem[9];
		
		// Back button
		if (session.previous != null) {
			bar[0] = new MenuItemBack(session.previous);
		}
		
		// Previous page
		if (session.page != 0) {
			bar[3] = new MenuItemChangePage("Previous", session, session.page - 1);
		}
		
		// Next page
		if (session.page < getPageCount()-1) {
			bar[5] = new MenuItemChangePage("Next", session, session.page + 1);
		}
		
		// Control items
		bar[1] = controlSlots[0];
		bar[2] = controlSlots[1];
		
		bar[6] = controlSlots[2];
		bar[7] = controlSlots[3];
		bar[8] = controlSlots[4];
		
		for (MenuItem item : bar) {
			if (item != null) {
				item.onAdd(this, -1, -1);
			}
		}
		return bar;
	}
	
	/**
	 * Requests that this menu be refreshed on the next tick
	 */
	public void refreshLater() {
		Bukkit.getScheduler().runTask(Minigames.plugin, new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		});
	}
	
	/**
	 * Refreshes this menu now. Each item will be have {@link MenuItem#update()} called.
	 */
	public void refresh() {
		for (MinigamePlayer viewer : viewers) {
			MenuSession session = viewer.getMenuSession();
			InventoryView view = viewer.getPlayer().getOpenInventory();
			
			// Sanity check, make sure they are really viewing this menu
			if (view != null && session != null && session.current == this) {
				MenuPage page = getPage(session.page);
				page.update();
				session.controlBar = createControlBar(session);
				displayInto(view.getTopInventory(), page, session.controlBar);
				viewer.updateInventory();
			}
		}
	}
	
	void onCloseMenu(MinigamePlayer player) {
		viewers.remove(player);
	}
	
	/**
	 * @return Returns true if players can modify this menu. This only applies to menus with Inventory based pages  
	 */
	public boolean getAllowModify(){
		return allowModify;
	}
	
	/**
	 * Sets whether players can modified Inventory pages
	 * @param canModify true if they can
	 */
	public void setAllowModify(boolean canModify){
		allowModify = canModify;
	}
	
	/**
	 * @return Returns true (default) if, upon leaving this menu, players will be able to go back to this menu
	 */
	public boolean getTrackHistory() {
		return trackHistory;
	}
	
	/**
	 * Sets whether players can go back to this menu using automatic navigation
	 * @param track true if they can (default)
	 */
	public void setTrackHistory(boolean track) {
		trackHistory = track;
	}
	
	MenuItem getClickItem(MenuSession session, int slot) {
		if (slot >= (rows * 9)) {
			// Control slot
			return session.controlBar[slot - (rows * 9)];
		}
		
		// Other slot
		MenuPage page = getPage(session.page);
		return page.getClickItem(slot);
	}
	
	/**
	 * Gets the MenuItem in the provided location
	 * @param page The page of the item. This page <b>must</b> exist
	 * @param slot The slot on the page.
	 * @return The MenuItem or null if the slot is empty
	 */
	public MenuItem getItem(int page, int slot) {
		MenuPage menuPage = getPage(page);
		return menuPage.getClickItem(slot);
	}
	
	/**
	 * Gets a MenuItem by name. This searches all pages.
	 * @param name The name to look for
	 * @return The MenuItem or null if it was not found
	 */
	public MenuItem getItem(String name) {
		for (MenuItem item : this) {
			if (ChatColor.stripColor(item.getName()).equals(name)) {
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Provides an iterator for the MenuItems in this menu on every page, not including the ControlBar.<br>
	 * This method can <b>not</b> be used with custom MenuPage's
	 */
	public Iterator<MenuItem> iterator() {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal page = (MenuPageNormal)firstPage;
		Iterator<MenuItem> it = null;
		
		while(page != null) {
			if (it == null) {
				it = page.iterator();
			} else {
				it = Iterators.concat(it, page.iterator());
			}
			page = page.getNext();
		}
		
		return it;
	}
	
	int getSize() {
		return rows * 9;
	}
	
	/**
	 * @return Returns a list of all players currently viewing this menu
	 */
	public Set<MinigamePlayer> getViewers() {
		return viewers;
	}
}
