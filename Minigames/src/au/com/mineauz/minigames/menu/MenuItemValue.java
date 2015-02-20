package au.com.mineauz.minigames.menu;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigamePlayer;

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
			player.sendMessage(e.getMessage(), "error");
		}
	}
	
	public final T getValue() {
		return valueCallback.getValue();
	}
	
	@Override
	public void update() {
		updateDescription();
	}
	
	protected boolean isManualEntryAllowed() { return false; }
	protected String getManualEntryText() { return ""; }
	protected int getManualEntryTime() { return 10; }
	
	protected void onManualEntryStart(MinigamePlayer player) {}
	protected T onManualEntryComplete(MinigamePlayer player, String raw) throws IllegalArgumentException {return null;}
	protected void onChange(MinigamePlayer player, T previous, T current) {}
	
	public void setChangeHandler(IMenuItemChange<T> handler) {
		changeCallback = handler;
	}
	
	protected abstract T increaseValue(T current, boolean shift);
	protected abstract T decreaseValue(T current, boolean shift);
	
	protected abstract List<String> getValueDescription(T value);
	
	public interface IMenuItemChange<T> {
		public void onChange(MenuItemValue<T> menuItem, MinigamePlayer player, T previous, T current);
	}

}
