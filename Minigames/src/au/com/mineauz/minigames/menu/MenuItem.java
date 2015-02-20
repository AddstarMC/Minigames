package au.com.mineauz.minigames.menu;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;

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
	
	public MenuItem setIcon(Material material) {
		if (material != null) {
			setIcon(material.getNewData((byte)0));
		} else {
			setIcon((MaterialData)null);
		}
		return this;
	}
	
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
	
	public final MenuItem setDescription(String description) {
		return setDescription(MinigameUtils.stringToList(description));
	}
	
	public MenuItem setDescription(List<String> description) {
		ItemMeta meta = displayItem.getItemMeta();
		
		meta.setLore(description);
		displayItem.setItemMeta(meta);
		
		return this;
	}
	
	public final List<String> getDescription() {
		ItemMeta meta = displayItem.getItemMeta();
		if (meta.getLore() == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(meta.getLore());
		}
	}
	
	public final String getName() {
		return displayItem.getItemMeta().getDisplayName().substring(2);
	}
	
	public final void setName(String name) {
		ItemMeta meta = displayItem.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		displayItem.setItemMeta(meta);
	}
	
	public final ItemStack getItem() {
		return displayItem;
	}
	
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
	
	public final void setClickHandler(IMenuItemClick handler) {
		clickCallback = handler;
	}

	/**
	 * Called when this item is clicked with an item in the cursor
	 * @param player The player that clicked it
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
	
	public final void setClickWithItemHandler(IMenuItemClickItem handler) {
		clickItemCallback = handler;
	}
	
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
	
	public final void setRightClickHandler(IMenuItemClick handler) {
		rightClickCallback = handler;
	}
	
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
	
	public final void setShiftClickHandler(IMenuItemClick handler) {
		shiftClickCallback = handler;
	}
	
	protected void onShiftRightClick(MinigamePlayer player) {}
	
	public final ItemStack handleShiftRightClick(MinigamePlayer player) {
		onShiftRightClick(player);
		
		if (shiftRightClickCallback != null) {
			shiftRightClickCallback.onClick(this, player);
		}
		
		return getItem();
	}
	
	public final void setShiftRightClickHandler(IMenuItemClick handler) {
		shiftRightClickCallback = handler;
	}
	
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
	
	public final void setDoubleClickHandler(IMenuItemClick handler) {
		doubleClickCallback = handler;
	}
	
	protected void checkValidEntry(MinigamePlayer player, String entry) {}
	
	public final Menu getContainer() {
		return container;
	}
	
	void onAdd(Menu container, int page, int slot) {
		this.container = container;
		this.page = page;
		this.slot = slot;
	}
	
	public final int getSlot() {
		return slot;
	}
	
	public final int getPage() {
		return page;
	}
	
	public final void remove() {
		container.removeItemFlow(slot, page);
		container.refreshLater();
		container = null;
		
		onRemove();
		if (removeCallback != null) {
			removeCallback.onRemove(this);
		}
	}
	
	public final void removeStatic() {
		container.removeItem(slot, page);
		container.refreshLater();
		container = null;
		
		onRemove();
		if (removeCallback != null) {
			removeCallback.onRemove(this);
		}
	}
	
	protected void onRemove() {}
	public final void setRemoveHandler(IMenuItemRemove handler) {
		removeCallback = handler;
	}
	
	public void update() {}
	
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
	
	public interface IMenuItemClick {
		public void onClick(MenuItem menuItem, MinigamePlayer player);
	}
	
	public interface IMenuItemClickItem {
		public void onClickWithItem(MenuItem menuItem, MinigamePlayer player, ItemStack item);
	}
	
	public interface IMenuItemRemove {
		public void onRemove(MenuItem menuItem);
	}
}
