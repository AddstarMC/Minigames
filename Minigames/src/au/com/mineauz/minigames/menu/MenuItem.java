package au.com.mineauz.minigames.menu;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;

/**
 * The base MenuItem type. It is encouraged that this type be extended for specific uses, 
 * however it is not strictly needed as each event has a callback which is fired upon
 * the event happening.
 * <h3>Notable children of this class:</h3>
 * <dl>
 * <dt>{@link MenuItemNewLine}</dt>
 *  <dd>A special MenuItem that is interpreted as a line break in menus. Useful for easily making menus</dd>
 * <dt>{@link MenuItemValue}</dt>
 * 	<dd>An extension of this class specifically for making MenuItems that have values that can be changed.</dd>
 * <dt>{@link MenuItemEnum}</dt>
 *  <dd>A version of MenuItemValue that deals specifically with enums</dd>
 * </dl>
 */
@SuppressWarnings("deprecation")
public class MenuItem {
	private ItemStack displayItem;
	private Menu container;
	private int slot;
	private int page;
	
	private IMenuItemClick clickCallback;
	private IMenuItemClick rightClickCallback;
	private IMenuItemClick shiftClickCallback;
	private IMenuItemClick shiftRightClickCallback;
	private IMenuItemClick doubleClickCallback;
	private IMenuItemRemove removeCallback;
	private IMenuItemClickItem clickItemCallback;
	
	public MenuItem(String name, Material displayItem){
		this(name, null, (displayItem == null ? null : displayItem.getNewData((byte)0)));
	}
	
	public MenuItem(String name, MaterialData displayItem) {
		this(name, null, displayItem);
	}
	
	public MenuItem(String name, String description, Material displayItem){
		this(name, description, (displayItem == null ? null : displayItem.getNewData((byte)0)));
	}
	
	public MenuItem(String name, String description, MaterialData displayItem) {
		Validate.notNull(name);
		if(displayItem == null) {
			displayItem = Material.STAINED_GLASS_PANE.getNewData((byte)14);
		}
		
		ItemStack display = displayItem.toItemStack(1);
		ItemMeta meta = display.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		if (description != null) {
			meta.setLore(MinigameUtils.stringToList(description));
		}
		display.setItemMeta(meta);
		
		this.displayItem = display;
	}
	
	/**
	 * Set the icon for this MenuItem.<br>
	 * If this MenuItem is already added to a menu and that menu is visible, you must call {@link Menu#refresh()} or {@link Menu#refreshLater()} to show this change
	 * @param material The material to use. Most materials work however some materials do not have an item form (eg. AIR, DOUBLE_STEP, and more) and cannot be used
	 * @return This MenuItem for chaining
	 */
	public MenuItem setIcon(Material material) {
		if (material != null) {
			setIcon(material.getNewData((byte)0));
		} else {
			setIcon((MaterialData)null);
		}
		return this;
	}
	
	/**
	 * Set the icon for this MenuItem.<br>
	 * If this MenuItem is already added to a menu and that menu is visible, you must call {@link Menu#refresh()} or {@link Menu#refreshLater()} to show this change
	 * @param material The material to use with data value. Most materials work however some materials do not have an item form (eg. AIR, DOUBLE_STEP, and more) and cannot be used
	 * @return This MenuItem for chaining
	 */
	public MenuItem setIcon(MaterialData material) {
		if(material == null) {
			material = Material.STAINED_GLASS_PANE.getNewData((byte)14);
		}
		
		ItemMeta oldMeta = displayItem.getItemMeta();
		ItemStack display = material.toItemStack(1);
		display.setItemMeta(oldMeta);
		
		this.displayItem = display;
		return this;
	}
	
	/**
	 * Sets the description of this menu item. Use ; for a line break<br>
	 * If this MenuItem is already added to a menu and that menu is visible, you must call {@link Menu#refresh()} or {@link Menu#refreshLater()} to show this change
	 * @param description The new description to use
	 * @return This MenuItem for chaining
	 */
	public final MenuItem setDescription(String description) {
		return setDescription(MinigameUtils.stringToList(description));
	}
	
	/**
	 * Sets the description of this menu item.<br>
	 * If this MenuItem is already added to a menu and that menu is visible, you must call {@link Menu#refresh()} or {@link Menu#refreshLater()} to show this change
	 * @param description A list of strings to use as the description of this item
	 * @return This MenuItem for chaining
	 */
	public MenuItem setDescription(List<String> description) {
		ItemMeta meta = displayItem.getItemMeta();
		
		meta.setLore(description);
		displayItem.setItemMeta(meta);
		
		return this;
	}
	
	/**
	 * @return Returns the current description of this MenuItem as a list. <br>
	 * This is <b>Read Only</b>. You may only change the description through {@link #setDescription(List)} or {@link #setDescription(String)}
	 */
	public final List<String> getDescription() {
		ItemMeta meta = displayItem.getItemMeta();
		if (meta.getLore() == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(meta.getLore());
		}
	}
	
	/**
	 * @return Returns the name of this MenuItem
	 */
	public final String getName() {
		return displayItem.getItemMeta().getDisplayName().substring(2);
	}
	
	/**
	 * Sets the name of this item<br>
	 * If this MenuItem is already added to a menu and that menu is visible, you must call {@link Menu#refresh()} or {@link Menu#refreshLater()} to show this change
	 * @param name The new name to display
	 */
	public final void setName(String name) {
		ItemMeta meta = displayItem.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		displayItem.setItemMeta(meta);
	}
	
	/**
	 * @return Returns the final display item. This includes the description, name, and material
	 */
	public final ItemStack getItem() {
		return displayItem;
	}
	
	/**
	 * Set the icon for this MenuItem using an ItemStack.<br>
	 * If this MenuItem is already added to a menu and that menu is visible, you must call {@link Menu#refresh()} or {@link Menu#refreshLater()} to show this change
	 * @param item An ItemStack to display. Everything except a the display name, and lore are kept.
	 * @return This MenuItem for chaining
	 */
	public MenuItem setItem(ItemStack item) {
		ItemMeta ometa = displayItem.getItemMeta();
		displayItem = item.clone();
		ItemMeta nmeta = displayItem.getItemMeta();
		nmeta.setDisplayName(ometa.getDisplayName());
		nmeta.setLore(nmeta.getLore());
		displayItem.setItemMeta(nmeta);
		
		return this;
	}
	
	/**
	 * Called when this item is left clicked with an empty cursor
	 * @param player The player that clicked it
	 */
	protected void onClick(MinigamePlayer player) {}
	
	public final ItemStack handleClick(MinigamePlayer player) {
		onClick(player);
		
		if (clickCallback != null) {
			clickCallback.onClick(this, player);
		}
		
		if (container != null) {
			return getItem();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets a handler for left clicking this MenuItem
	 * @param handler The handler, null to remove
	 */
	public final void setClickHandler(IMenuItemClick handler) {
		clickCallback = handler;
	}

	/**
	 * Called when this item is clicked with an item in the cursor
	 * @param player The player that clicked it
	 * @param item The ItemStack that was used
	 */
	protected void onClickWithItem(MinigamePlayer player, ItemStack item) {}
	
	public final ItemStack handleClickWithItem(MinigamePlayer player, ItemStack item) {
		onClickWithItem(player, item);
		
		if (clickItemCallback != null) {
			clickItemCallback.onClickWithItem(this, player, item);
		}
		
		if (container != null) {
			return getItem();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets a handler for clicking this MenuItem with an ItemStack
	 * @param handler The handler, null to remove
	 */
	public final void setClickWithItemHandler(IMenuItemClickItem handler) {
		clickItemCallback = handler;
	}
	
	/**
	 * Called when this item is right clicked with an empty cursor
	 * @param player The player that clicked it
	 */
	protected void onRightClick(MinigamePlayer player) {}
	
	public final ItemStack handleRightClick(MinigamePlayer player) {
		onRightClick(player);
		
		if (rightClickCallback != null) {
			rightClickCallback.onClick(this, player);
		}
		
		if (container != null) {
			return getItem();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets a handler for right clicking this MenuItem
	 * @param handler The handler, null to remove
	 */
	public final void setRightClickHandler(IMenuItemClick handler) {
		rightClickCallback = handler;
	}
	
	/**
	 * Called when this item is shift + left clicked with an empty cursor
	 * @param player The player that clicked it
	 */
	protected void onShiftClick(MinigamePlayer player) {}
	
	public final ItemStack handleShiftClick(MinigamePlayer player) {
		onShiftClick(player);
		
		if (shiftClickCallback != null) {
			shiftClickCallback.onClick(this, player);
		}
		
		if (container != null) {
			return getItem();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets a handler for shift + left clicking this MenuItem
	 * @param handler The handler, null to remove
	 */
	public final void setShiftClickHandler(IMenuItemClick handler) {
		shiftClickCallback = handler;
	}
	
	/**
	 * Called when this item is shift + right clicked with an empty cursor
	 * @param player The player that clicked it
	 */
	protected void onShiftRightClick(MinigamePlayer player) {}
	
	public final ItemStack handleShiftRightClick(MinigamePlayer player) {
		onShiftRightClick(player);
		
		if (shiftRightClickCallback != null) {
			shiftRightClickCallback.onClick(this, player);
		}
		
		return getItem();
	}
	
	/**
	 * Sets a handler for shift + right clicking this MenuItem
	 * @param handler The handler, null to remove
	 */
	public final void setShiftRightClickHandler(IMenuItemClick handler) {
		shiftRightClickCallback = handler;
	}
	
	/**
	 * Called when this item is double left clicked with an empty cursor
	 * @param player The player that clicked it
	 */
	protected void onDoubleClick(MinigamePlayer player) {}
	
	public final ItemStack handleDoubleClick(MinigamePlayer player) {
		onDoubleClick(player);
		
		if (doubleClickCallback != null) {
			doubleClickCallback.onClick(this, player);
		}
		
		if (container != null) {
			return getItem();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets a handler for double left clicking this MenuItem
	 * @param handler The handler, null to remove
	 */
	public final void setDoubleClickHandler(IMenuItemClick handler) {
		doubleClickCallback = handler;
	}
	
	/**
	 * Called upon completion of manual entry. Use this to validate and handle the entered value
	 * @param player The player
	 * @param entry The value they entered
	 */
	protected void checkValidEntry(MinigamePlayer player, String entry) {}
	
	/**
	 * @return Returns the {@link Menu} this MenuItem is part of, or null
	 */
	public final Menu getContainer() {
		return container;
	}
	
	void onAdd(Menu container, int page, int slot) {
		this.container = container;
		this.page = page;
		this.slot = slot;
	}
	
	/**
	 * @return Returns the slot number in the {@link #getContainer()} where this MenuItem is displayed<br>
	 * If this item is in the control bar of a Menu, this is unused
	 */
	public final int getSlot() {
		return slot;
	}
	
	/**
	 * @return Returns the page number in the {@link #getContainer()} where this MenuItem is displayed<br>
	 * If this item is in the control bar of a Menu, this is unused
	 */
	public final int getPage() {
		return page;
	}
	
	/**
	 * Removes this item from {@link #getContainer()} and refreshes the container<br> 
	 * This remove will move items from the right to fill the gap left by this item
	 */
	public final void remove() {
		container.removeItemFlow(slot, page);
		container.refreshLater();
		container = null;
		
		onRemove();
		if (removeCallback != null) {
			removeCallback.onRemove(this);
		}
	}
	
	/**
	 * Removes this item from {@link #getContainer()} and refreshes the container<br> 
	 * This remove will leave a gap where this item was
	 */
	public final void removeStatic() {
		container.removeItem(slot, page);
		container.refreshLater();
		container = null;
		
		onRemove();
		if (removeCallback != null) {
			removeCallback.onRemove(this);
		}
	}
	
	/**
	 * Called upon removal of this item
	 */
	protected void onRemove() {}
	
	/**
	 * Sets a handler to detect removal of this item
	 * @param handler The handler, null to remove
	 */
	public final void setRemoveHandler(IMenuItemRemove handler) {
		removeCallback = handler;
	}
	
	/**
	 * Call to update this item. Sub classes should override this to update icons, descriptions, etc. whenever the {@link #getContainer()} is refreshed
	 */
	public void update() {}
	
	/**
	 * Enters the player into manual entry mode. In this mode the menu is hidden and the player is prompted to enter a value into chat.<br>
	 * {@link #checkValidEntry(MinigamePlayer, String)} is called when the player has entered a value. 
	 * If the player fails to enter a value in the specified time, the menu will be reopened.
	 * @param player The player that will be doing the manual entry
	 * @param message A message to display to this player. This can be anything you like, you may also send a message to the player 
	 * 				  after this if you need to add more information.
	 * @param time The time in seconds to delay reopening the menu
	 */
	public final void beginManualEntry(MinigamePlayer player, String message, int time) {
		player.setNoClose(true);
		player.getPlayer().closeInventory();
		player.sendMessage(message, MessageType.Normal);
		player.startManualEntry(this, time);
	}
	
	final void completeManualEntry(MinigamePlayer player, String value) {
		player.cancelMenuReopen();
		player.setNoClose(false);
		getContainer().displaySession(player, player.getMenuSession());
		checkValidEntry(player, value);
	}
	
	/**
	 * An interface for handling normal clicks
	 */
	public interface IMenuItemClick {
		/**
		 * @param menuItem The MenuItem that was clicked
		 * @param player The player that clicked it
		 */
		public void onClick(MenuItem menuItem, MinigamePlayer player);
	}
	
	/**
	 * An interface for handling clicks with an ItemStack
	 */
	public interface IMenuItemClickItem {
		/**
		 * @param menuItem The MenuItem that was clicked
		 * @param player The player that clicked it
		 * @param item The ItemStack that was used on the MenuItem
		 */
		public void onClickWithItem(MenuItem menuItem, MinigamePlayer player, ItemStack item);
	}
	
	/**
	 * An interface for handling removal of MenuItems
	 */
	public interface IMenuItemRemove {
		/**
		 * @param menuItem The MenuItem that was removed
		 */
		public void onRemove(MenuItem menuItem);
	}
}
