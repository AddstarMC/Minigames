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
	
	public Menu(int rows, String title) {
		this(rows, title, new MenuPageNormal(rows * 9, null));
	}
	
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
	
	public String getName(){
		return name;
	}
	
	public MenuPage getFirstPage() {
		return firstPage;
	}
	
	public int getPageCount() {
		int count = 0;
		MenuPage page = firstPage;
		while(page != null) {
			++count;
			page = page.getNext();
		}
		
		return count;
	}
	
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
	
	public void setControlItem(MenuItem item, int slot) {
		Validate.isTrue(slot >= 0 && slot < 5);
		
		controlSlots[slot] = item;
	}
	
	public void setItem(MenuItem item, int slot, int page) {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal menuPage = (MenuPageNormal)getPage(page);
		menuPage.setItem(item, slot);
	}
	
	public void removeItem(int slot, int page){
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal menuPage = (MenuPageNormal)getPage(page);
		menuPage.removeItem(slot);
	}
	
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
	
	public void addItem(MenuItem item) {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal menuPage = (MenuPageNormal)getLastPage();
		while (!menuPage.addItem(item)) {
			menuPage = addPage();
		}
	}
	
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
	
	public void addItemBefore(MenuItem item, MenuItem before) {
		Validate.notNull(before);
		insertItem(item, before.getSlot(), before.getPage());
	}
	
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
	
	public MenuPageNormal addPage() {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		MenuPageNormal page = new MenuPageNormal(rows * 9, (MenuPageNormal)getLastPage());
		page.setContainer(this);
		return page;
	}
	
	public void addPage(MenuPage page) {
		Validate.isTrue(page.getClass().isInstance(firstPage));
		
		page.setContainer(this);
	}
	
	public void addItems(List<MenuItem> items) {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		
		for (MenuItem item : items) {
			addItem(item);
		}
	}
	
	public void clear() {
		Validate.isTrue(firstPage instanceof MenuPageNormal);
		MenuPageNormal page = (MenuPageNormal)firstPage;
		
		while(page != null) {
			page.clear();
			page = page.getNext();
		}
		
		firstPage.setNext(null);
	}
	
	public void displayMenu(MinigamePlayer player) {
		MenuSession session = player.getMenuSession();
		
		while(session != null && !session.current.getTrackHistory()) {
			session = session.previous;
		}
		
		session = new MenuSession(this, session);
		
		displaySession(player, session);
	}
	
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
	
	public void refreshLater() {
		Bukkit.getScheduler().runTask(Minigames.plugin, new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		});
	}
	
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
	
	public boolean getAllowModify(){
		return allowModify;
	}
	
	public void setAllowModify(boolean canModify){
		allowModify = canModify;
	}
	
	public boolean getTrackHistory() {
		return trackHistory;
	}
	
	public void setTrackHistory(boolean track) {
		trackHistory = track;
	}
	
	public MenuItem getClickItem(MenuSession session, int slot) {
		if (slot >= (rows * 9)) {
			// Control slot
			return session.controlBar[slot - (rows * 9)];
		}
		
		// Other slot
		MenuPage page = getPage(session.page);
		return page.getClickItem(slot);
	}
	
	public MenuItem getItem(int page, int slot) {
		MenuPage menuPage = getPage(page);
		return menuPage.getClickItem(slot);
	}
	
	public MenuItem getItem(String name) {
		for (MenuItem item : this) {
			if (ChatColor.stripColor(item.getName()).equals(name)) {
				return item;
			}
		}
		
		return null;
	}
	
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
	
	public int getSize() {
		return rows * 9;
	}
	
	public Set<MinigamePlayer> getViewers() {
		return viewers;
	}
}
