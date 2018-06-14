package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import com.google.common.base.Joiner;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHasItemCondition extends ConditionInterface {
	
	private StringFlag type = new StringFlag("STONE", "type");
	private BooleanFlag useData = new BooleanFlag(false, "usedata");
	private IntegerFlag data = new IntegerFlag(0, "data");
	private StringFlag where = new StringFlag("ANYWHERE", "where");
	private IntegerFlag slot = new IntegerFlag(0, "slot");
	
	private BooleanFlag matchName = new BooleanFlag(false, "matchName");
	private BooleanFlag matchLore = new BooleanFlag(false, "matchLore");
	
	private StringFlag name = new StringFlag(null, "name");
	private StringFlag lore = new StringFlag(null, "lore");

	@Override
	public String getName() {
		return "PLAYER_HAS_ITEM";
	}

	@Override
	public String getCategory() {
		return "Player Conditions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
		if (useData.getFlag()) {
			out.put("Item", type.getFlag() + ":" + data.getFlag());
		} else {
			out.put("Item", type.getFlag() + ":all");
		}
		
		out.put("Where", where.getFlag());
		out.put("Slot", slot.getFlag());
		
		if (matchName.getFlag()) {
			out.put("Name", name.getFlag());
		}
		
		if (matchLore.getFlag()) {
			out.put("Lore", lore.getFlag());
		}
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		return check(player);
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return check(player);
	}
	
	private boolean check(MinigamePlayer player) {
		PositionType checkType = PositionType.valueOf(where.getFlag().toUpperCase());
		if (checkType == null) {
			checkType = PositionType.ANYWHERE;
		}
		
		PlayerInventory inventory = player.getPlayer().getInventory();
		ItemStack[] searchItems;
		int startSlot;
		int endSlot;
		
		if (checkType == PositionType.ARMOR) {
			searchItems = inventory.getArmorContents();
			startSlot = 0;
			endSlot = searchItems.length;
		} else {
			searchItems = inventory.getContents();
			
			if (checkType == PositionType.HOTBAR) {
				startSlot = 0;
				endSlot = 9;
			} else if (checkType == PositionType.MAIN) {
				startSlot = 9;
				endSlot = 36;
			} else if (checkType == PositionType.SLOT) {
				startSlot = slot.getFlag();
				endSlot = startSlot + 1;
			} else {
				startSlot = 0;
				endSlot = searchItems.length;
			}
		}
		
		Material material = Material.getMaterial(type.getFlag());
		
		Pattern namePattern = null;
		Pattern lorePattern = null;
		
		if (matchName.getFlag()) {
			namePattern = createNamePattern();
		}
		
		if (matchLore.getFlag()) {
			lorePattern = createLorePattern();
		}
		
		for (int i = startSlot; i < endSlot && i < searchItems.length; ++i) {
			ItemStack itemInSlot = searchItems[i];
			if (itemInSlot == null) {
				continue;
			}
			
			if (itemInSlot.getType() == material) {
				if (useData.getFlag() && itemInSlot.getDurability() != data.getFlag()) {
					continue;
				}
				
				ItemMeta meta = itemInSlot.getItemMeta();
				
				if (matchName.getFlag()) {
					Matcher m = namePattern.matcher(meta.getDisplayName());
					if (!m.matches()) {
						continue;
					}
				}
				
				if (matchLore.getFlag()) {
					if (meta.getLore() != null) {
						Matcher m = lorePattern.matcher(Joiner.on('\n').join(meta.getLore()));
						if (!m.matches()) {
							continue;
						}
					} else {
						// Only an unset lore pattern can match this
						if (lore.getFlag() != null) {
							continue;
						}
					}
				}
				
				// This item completely matches
				return true;
			}
		}
		
		return false;
	}
	
	private Pattern createNamePattern() {
		String name = this.name.getFlag();
		if (name == null) {
			return Pattern.compile(".*");
		}
		
		StringBuffer buffer = new StringBuffer();
		int start = 0;
		int index = 0;
		
		while (true) {
			index = name.indexOf('%', start);
			// End of input, append the rest
			if (index == -1) {
				buffer.append(Pattern.quote(name.substring(start)));
				break;
			}
			
			// Append the start
			buffer.append(Pattern.quote(name.substring(start, index)));
			
			// Append the wildcard code
			buffer.append(".*?");
			
			// Move to next position
			start = index + 1;
		}
		
		return Pattern.compile(buffer.toString());
	}
	
	private Pattern createLorePattern() {
		String lore = this.lore.getFlag();
		if (lore == null) {
			return Pattern.compile(".*");
		}
		
		lore = lore.replace(';', '\n');
		
		StringBuffer buffer = new StringBuffer();
		int start = 0;
		int index = 0;
		
		while (true) {
			index = lore.indexOf('%', start);
			// End of input, append the rest
			if (index == -1) {
				buffer.append(Pattern.quote(lore.substring(start)));
				break;
			}
			
			// Append the start
			buffer.append(Pattern.quote(lore.substring(start, index)));
			
			// Append the wildcard code
			buffer.append(".*?");
			
			// Move to next position
			start = index + 1;
		}
		
		return Pattern.compile(buffer.toString());
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		type.saveValue(path, config);
		useData.saveValue(path, config);
		data.saveValue(path, config);
		where.saveValue(path, config);
		slot.saveValue(path, config);
		
		matchName.saveValue(path, config);
		matchLore.saveValue(path, config);
		name.saveValue(path, config);
		lore.saveValue(path, config);
		saveInvert(config, path);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		type.loadValue(path, config);
		useData.loadValue(path, config);
		data.loadValue(path, config);
		where.loadValue(path, config);
		slot.loadValue(path, config);
		
		matchName.loadValue(path, config);
		matchLore.loadValue(path, config);
		name.loadValue(path, config);
		lore.loadValue(path, config);
		loadInvert(config, path);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Player Has Item", player);
		m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
		final MinigamePlayer fply = player;
		m.addItem(new MenuItemString("Item", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.getMaterial(value.toUpperCase()) != null)
					type.setFlag(value.toUpperCase());
				else
                    fply.sendMessage("Invalid Item!", MinigameMessageType.ERROR);
			}
			
			@Override
			public String getValue() {
				return type.getFlag();
			}
		}));
		m.addItem(useData.getMenuItem("Match Item Data", Material.ENDER_PEARL));
		m.addItem(data.getMenuItem("Data Value", Material.ENDER_EYE, 0, null));
		m.addItem(new MenuItemList("Search Where", Material.COMPASS, new Callback<String>() {
			@Override
			public void setValue(String value) {
				where.setFlag(value.toUpperCase());
			}
			
			@Override
			public String getValue() {
				return WordUtils.capitalizeFully(where.getFlag());
			}
		}, Arrays.asList("Anywhere", "Hotbar", "Main", "Armor", "Slot")));
		m.addItem(slot.getMenuItem("Slot", Material.DIAMOND, 0, 35));
		m.addItem(new MenuItemNewLine());
		
		m.addItem(matchName.getMenuItem("Match Display Name", Material.NAME_TAG));
		MenuItemString menuItem = (MenuItemString)name.getMenuItem("Display Name", Material.NAME_TAG, MinigameUtils.stringToList("The name to match.;Use % to do a wildcard match"));
		menuItem.setAllowNull(true);
		m.addItem(menuItem);
		
		m.addItem(matchLore.getMenuItem("Match Lore", Material.BOOK));
		menuItem = (MenuItemString)lore.getMenuItem("Lore", Material.BOOK, MinigameUtils.stringToList("The lore to match. Separate;with semi-colons;for new lines.;Use % to do a wildcard match"));
		menuItem.setAllowNull(true);
		m.addItem(menuItem);
		
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}
	
	private enum PositionType {
		ANYWHERE,
		HOTBAR,
		MAIN,
		ARMOR,
		SLOT
	}

}
