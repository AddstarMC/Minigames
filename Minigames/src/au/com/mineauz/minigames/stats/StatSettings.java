package au.com.mineauz.minigames.stats;

import au.com.mineauz.minigames.properties.AbstractProperty;
import au.com.mineauz.minigames.properties.Property;

/**
 * Represents per minigame settings for a stat
 */
public class StatSettings {
	private final MinigameStat stat;
	private StatFormat format;
	private Property<StatFormat> formatProperty;
	private String displayName;
	private Property<String> displayNameProperty;
	
	public StatSettings(MinigameStat stat, StatFormat format, String displayName) {
		this.stat = stat;
		this.format = format;
		this.displayName = displayName;
		
		formatProperty = new AbstractProperty<StatFormat>() {
			@Override
			public StatFormat getValue() {
				return getFormat();
			}
			
			@Override
			public void setValue(StatFormat value) {
				StatSettings.this.format = value;
				super.setValue(value);
			}
		};
		
		displayNameProperty = new AbstractProperty<String>() {
			@Override
			public String getValue() {
				return getDisplayName();
			}
			
			@Override
			public void setValue(String value) {
				StatSettings.this.displayName = value;
				super.setValue(value);
			}
		};
	}
	
	public StatSettings(MinigameStat stat) {
		this(stat, null, null);
	}
	
	/**
	 * @return Returns the stat
	 */
	public MinigameStat getStat() {
		return stat;
	}
	
	/**
	 * @return Returns the current format of this stat for this minigame
	 */
	public StatFormat getFormat() {
		if (format == null) {
			return stat.getFormat();
		} else {
			return format;
		}
	}
	
	/**
	 * Sets the format of this stat for this minigame.
	 * @param format The new format to display. Setting to null will reset the format
	 */
	public void setFormat(StatFormat format) {
		this.format = format;
	}
	
	public Property<StatFormat> format() {
		return formatProperty;
	}
	
	/**
	 * @return Returns the current display name of this stat
	 */
	public String getDisplayName() {
		if (displayName == null) {
			return stat.getDisplayName();
		} else {
			return displayName;
		}
	}
	
	/**
	 * Sets the display name of this stat for this minigame
	 * @param displayName The new name of this stat. Setting to null will reset the name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Property<String> displayName() {
		return displayNameProperty;
	}
}
