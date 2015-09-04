package au.com.mineauz.minigames.menu;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.properties.ObservableValue;

public class MenuItemDecimal extends MenuItemValue<Double> {
	public static final DecimalFormat format = new DecimalFormat("#.##");
	
	private double lowerInc;
	private double upperInc;
	private Double min;
	private Double max;
	
	public MenuItemDecimal(String name, MaterialData displayItem, ObservableValue<Double> callback, double lowerInc, double upperInc, double min, double max) {
		super(name, displayItem, callback);
		this.lowerInc = lowerInc;
		this.upperInc = upperInc;
		this.min = min;
		this.max = max;
	}
	public MenuItemDecimal(String name, String description, Material displayItem, ObservableValue<Double> callback, double lowerInc, double upperInc, double min, double max) {
		super(name, description, displayItem, callback);
		this.lowerInc = lowerInc;
		this.upperInc = upperInc;
		this.min = min;
		this.max = max;
	}
	public MenuItemDecimal(String name, Material displayItem, ObservableValue<Double> callback, double lowerInc, double upperInc, double min, double max) {
		super(name, null, displayItem, callback);
		this.lowerInc = lowerInc;
		this.upperInc = upperInc;
		this.min = min;
		this.max = max;
	}
	public MenuItemDecimal(String name, String description, MaterialData displayItem, ObservableValue<Double> callback, double lowerInc, double upperInc, double min, double max) {
		super(name, description, displayItem, callback);
		this.lowerInc = lowerInc;
		this.upperInc = upperInc;
		this.min = min;
		this.max = max;
	}
	
	@Override
	protected List<String> getValueDescription(Double value) {
		return Arrays.asList(ChatColor.GREEN + format.format(value));
	}
	
	@Override
	protected Double increaseValue(Double current, boolean shift) {
		double value = current + (shift ? upperInc : lowerInc);
		if (value > max) {
			value = max;
		}
		
		return value;
	}
	
	@Override
	protected Double decreaseValue(Double current, boolean shift) {
		double value = current - (shift ? upperInc : lowerInc);
		if (value > min) {
			value = min;
		}
		
		return value;
	}
	
	@Override
	protected boolean isManualEntryAllowed() {
		return true;
	}
	
	@Override
	protected String getManualEntryText() {
		return "Enter decimal value into chat for " + getName();
	}
	
	@Override
	protected int getManualEntryTime() {
		return 15;
	}
	
	@Override
	protected void onManualEntryStart(MinigamePlayer player) {
		String min;
		String max;
		if(this.min == Double.MIN_VALUE) {
			min = "N/A";
		} else {
			min = format.format(this.min);
		}
		if(this.max == Double.MAX_VALUE) {
			max = "N/A";
		} else {
			max = format.format(this.max);
		}
		
		player.sendMessage("Min: " + min + ", Max: " + max);
	}
	
	@Override
	protected Double onManualEntryComplete(MinigamePlayer player, String raw) throws IllegalArgumentException {
		try {
			double value = Double.parseDouble(raw);
			value = Math.min(Math.max(value, min), max);
			return value;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid value entry!");
		}
	}
}
