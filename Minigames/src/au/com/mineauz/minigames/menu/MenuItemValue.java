package au.com.mineauz.minigames.menu;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;

/**
 * A specification of MenuItem that deals with MenuItems that have changeable values.
 * This uses {@link Callback} to set and get the actual value.
 * 
 * @param <T> The values type
 */
public abstract class MenuItemValue<T> extends MenuItem {

	private IMenuItemChange<T> changeCallback;
	private final Callback<T> valueCallback;
	private List<String> baseDescription;
	
	public MenuItemValue(String name, MaterialData displayItem, Callback<T> callback) {
		super(name, displayItem);
		baseDescription = Collections.emptyList();
		valueCallback = callback;
		updateDescription();
	}
	public MenuItemValue(String name, String description, Material displayItem, Callback<T> callback) {
		super(name, description, displayItem);
		baseDescription = getDescription();
		valueCallback = callback;
		updateDescription();
	}
	public MenuItemValue(String name, Material displayItem, Callback<T> callback) {
		super(name, null, displayItem);
		baseDescription = Collections.emptyList();
		valueCallback = callback;
		updateDescription();
	}
	public MenuItemValue(String name, String description, MaterialData displayItem, Callback<T> callback) {
		super(name, description, displayItem);
		baseDescription = getDescription();
		valueCallback = callback;
		updateDescription();
	}
	
	/**
	 * This rebuilds the description of this MenuItemValue and applies it
	 */
	protected final void updateDescription() {
		List<String> valueDesc = getValueDescription(getValue());
		super.setDescription(Lists.newArrayList(Iterators.concat(valueDesc.iterator(), baseDescription.iterator())));
	}
	
	@Override
	public MenuItem setDescription(List<String> description) {
		baseDescription = description;
		updateDescription();
		return this;
	}
	
	@Override
	protected final void onClick(MinigamePlayer player) {
		T oldValue = valueCallback.getValue();
		T newValue = increaseValue(oldValue, false);
		valueCallback.setValue(newValue);
		
		onChange(player, oldValue, newValue);
		updateDescription();
		if (changeCallback != null) {
			changeCallback.onChange(this, player, oldValue, newValue);
		}
	}
	
	@Override
	protected final void onShiftClick(MinigamePlayer player) {
		T oldValue = valueCallback.getValue();
		T newValue = increaseValue(oldValue, true);
		valueCallback.setValue(newValue);
		
		onChange(player, oldValue, newValue);
		updateDescription();
		if (changeCallback != null) {
			changeCallback.onChange(this, player, oldValue, newValue);
		}
	}
	
	@Override
	protected final void onRightClick(MinigamePlayer player) {
		T oldValue = valueCallback.getValue();
		T newValue = decreaseValue(oldValue, false);
		valueCallback.setValue(newValue);
		
		onChange(player, oldValue, newValue);
		updateDescription();
		if (changeCallback != null) {
			changeCallback.onChange(this, player, oldValue, newValue);
		}
	}
	
	@Override
	protected final void onShiftRightClick(MinigamePlayer player) {
		T oldValue = valueCallback.getValue();
		T newValue = decreaseValue(oldValue, true);
		valueCallback.setValue(newValue);
		
		onChange(player, oldValue, newValue);
		updateDescription();
		if (changeCallback != null) {
			changeCallback.onChange(this, player, oldValue, newValue);
		}
	}
	
	@Override
	protected void onDoubleClick(MinigamePlayer player) {
		if (isManualEntryAllowed()) {
			int time = getManualEntryTime();
			beginManualEntry(player, getManualEntryText() + ", the menu will automatically reopen in " + time + "s if nothing is entered.", time);
			onManualEntryStart(player);
		}
	}
	
	@Override
	protected void checkValidEntry(MinigamePlayer player, String entry) {
		try {
			T oldValue = valueCallback.getValue();
			T newValue = onManualEntryComplete(player, entry);
			valueCallback.setValue(newValue);
			
			onChange(player, oldValue, newValue);
			updateDescription();
			if (changeCallback != null) {
				changeCallback.onChange(this, player, oldValue, newValue);
			}
		} catch (IllegalArgumentException e) {
			player.sendMessage(e.getMessage(), MessageType.Error);
		}
	}
	
	/**
	 * @return Returns the current value of this MenuItem
	 */
	public final T getValue() {
		return valueCallback.getValue();
	}
	
	@Override
	public void update() {
		updateDescription();
	}
	
	/**
	 * Returns true if double clicking this MenuItem will enter manual entry mode.<br>
	 * This should be overridden in child classes to return true if manual entry is needed.
	 * @return True if double clicking enters manual entry
	 */
	protected boolean isManualEntryAllowed() { return false; }
	/**
	 * @return Returns the text that will be displayed to the player. Text informing the player of the time limit is automatically appended 
	 */
	protected String getManualEntryText() { return ""; }
	/**
	 * @return Returns the time in seconds given for manual entry. The default is 10 seconds
	 */
	protected int getManualEntryTime() { return 10; }
	
	/**
	 * Called upon launching manual entry. You may send additional information to the player at this time
	 * @param player The player entering manual entry.
	 */
	protected void onManualEntryStart(MinigamePlayer player) {}
	/**
	 * Called upon the player completing manual entry. This must either return the parsed value, 
	 * or throw an IllegalArgumentException there is an error in it.
	 * @param player The player that finished the manual entry
	 * @param raw The raw text value entered by the player
	 * @return The parsed value
	 * @throws IllegalArgumentException To be thrown if parsing fails
	 */
	protected T onManualEntryComplete(MinigamePlayer player, String raw) throws IllegalArgumentException {return null;}
	/**
	 * Called upon the value being changed
	 * @param player The player that caused the change
	 * @param previous The previous value
	 * @param current The new value
	 */
	protected void onChange(MinigamePlayer player, T previous, T current) {}
	
	/**
	 * Sets a handler for changing the value of this MenuItem
	 * @param handler The handler, null to remove
	 */
	public void setChangeHandler(IMenuItemChange<T> handler) {
		changeCallback = handler;
	}
	
	/**
	 * Called to increase the value. This must return the new or same value
	 * @param current The value before the increase
	 * @param shift True if the shift key was pressed
	 * @return The new value
	 */
	protected abstract T increaseValue(T current, boolean shift);
	/**
	 * Called to decrease the value. This must return the new or same value
	 * @param current The value before the decrease
	 * @param shift True if the shift key was pressed
	 * @return The new value
	 */
	protected abstract T decreaseValue(T current, boolean shift);
	
	/**
	 * Called to turn the value into a description that goes on the item.<br>
	 * This will not overwrite the description provided to the item upon creation or setDescription()
	 * @param value The current value
	 * @return A list containing lines to prepend to the MenuItems description
	 */
	protected abstract List<String> getValueDescription(T value);
	
	/**
	 * An interface for handling value change
	 * @param <T> The type of the value
	 */
	public interface IMenuItemChange<T> {
		public void onChange(MenuItemValue<T> menuItem, MinigamePlayer player, T previous, T current);
	}

}
