package au.com.mineauz.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Maps;

import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule.LoadoutAddon;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemPotion;
import au.com.mineauz.minigames.menu.MenuItemPotionAdd;
import au.com.mineauz.minigames.menu.MenuItemSaveLoadout;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuPageInventory;
import au.com.mineauz.minigames.minigame.TeamSelection;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;

public class PlayerLoadout {
	private Map<Integer, ItemStack> itemSlot = new HashMap<Integer, ItemStack>();
	private List<PotionEffect> potions = new ArrayList<PotionEffect>();
	private Property<String> loadoutName = Properties.create("default");
	private Property<Boolean> usePermission = Properties.create(false);
	private Property<Boolean> fallDamage = Properties.create(true);
	private Property<Boolean> hunger = Properties.create(false);
	private Property<Integer> level = Properties.create(-1);
	private Property<String> displayname = Properties.create(null);
	private Property<Boolean> lockInventory = Properties.create(false);
	private Property<Boolean> lockArmour = Properties.create(false);
	private Property<TeamSelection> team = Properties.create(null);
	private Property<Boolean> displayInMenu = Properties.create(true);
	
	private boolean deleteable = true;

	private Map<Class<? extends LoadoutAddon>, Object> addonValues = Maps.newHashMap();
	
	public PlayerLoadout(String name) {
		loadoutName.setValue(name);
		for(TeamSelection col : TeamSelection.values()) {
			if(name.toUpperCase().equals(col.toString())){
				team.setValue(col);
				break;
			}
		}
	}
	
	public void setDisplayName(String name) {
		displayname.setValue(name);
	}
	
	public String getDisplayName() {
		return displayname.getValue();
	}
	
	public Property<String> displayName() {
		return displayname;
	}
	
	public void setUsePermissions(boolean bool) {
		usePermission.setValue(bool);
	}
	
	public boolean getUsePermissions() {
		return usePermission.getValue();
	}
	
	public Property<Boolean> usePermissions() {
		return usePermission;
	}
	
	public String getName(boolean useDisplay) {
		if(!useDisplay || getDisplayName() == null)
			return loadoutName.getValue();
		return getDisplayName();
	}
	
	public void addItem(ItemStack item, int slot) {
		itemSlot.put(slot, item);
	}
	
	public void addPotionEffect(PotionEffect effect) {
		for (PotionEffect pot : potions) {
			if (effect.getType().equals(pot.getType())) {
				potions.remove(pot);
				break;
			}
		}
		potions.add(effect);
	}
	
	public void removePotionEffect(PotionEffect effect) {
		if (potions.contains(effect)) {
			potions.remove(effect);
		} else {
			for (PotionEffect pot : potions) {
				if (pot.getType().equals(effect.getType())) {
					potions.remove(pot);
					break;
				}
			}
		}
	}
	
	public List<PotionEffect> getAllPotionEffects() {
		return potions;
	}
	
	public void equiptLoadout(MinigamePlayer player) {
		player.getPlayer().getInventory().clear();
		player.getPlayer().getInventory().setHelmet(null);
		player.getPlayer().getInventory().setChestplate(null);
		player.getPlayer().getInventory().setLeggings(null);
		player.getPlayer().getInventory().setBoots(null);
		for (PotionEffect potion : player.getPlayer().getActivePotionEffects()) {
			player.getPlayer().removePotionEffect(potion.getType());
		}
		if (!itemSlot.isEmpty()) {
			for (Integer slot : itemSlot.keySet()){
				if (slot < 100)
					player.getPlayer().getInventory().setItem(slot, getItem(slot));
				else if(slot == 100)
					player.getPlayer().getInventory().setBoots(getItem(slot));
				else if(slot == 101)
					player.getPlayer().getInventory().setLeggings(getItem(slot));
				else if(slot == 102)
					player.getPlayer().getInventory().setChestplate(getItem(slot));
				else if(slot == 103)
					player.getPlayer().getInventory().setHelmet(getItem(slot));
			}
			player.updateInventory();
		}
		
		final MinigamePlayer fplayer = player;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				fplayer.getPlayer().addPotionEffects(potions);
			}
		});
		
		for (Entry<Class<? extends LoadoutAddon>, Object> addonValue : addonValues.entrySet()) {
			LoadoutAddon<Object> addon = LoadoutModule.getAddon(addonValue.getKey());
			if (addon != null) {
				addon.applyLoadout(player, addonValue.getValue());
			}
		}
		
		if(level.getValue() != -1)
			player.getPlayer().setLevel(level.getValue());
	}
	
	public void removeLoadout(MinigamePlayer player) {
		for (Entry<Class<? extends LoadoutAddon>, Object> addonValue : addonValues.entrySet()) {
			LoadoutAddon<Object> addon = LoadoutModule.getAddon(addonValue.getKey());
			if (addon != null) {
				addon.clearLoadout(player, addonValue.getValue());
			}
		}
	}
	
	public Set<Integer> getItems() {
		return itemSlot.keySet();
	}
	
	public ItemStack getItem(int slot) {
		return itemSlot.get(slot);
	}
	
	public void clearLoadout() {
		itemSlot.clear();
	}
	
	public boolean hasFallDamage() {
		return fallDamage.getValue();
	}
	
	public void setHasFallDamage(boolean bool) {
		fallDamage.setValue(bool);
	}
	
	public Property<Boolean> fallDamage() {
		return fallDamage;
	}
	
	public boolean hasHunger() {
		return hunger.getValue();
	}
	
	public void setHasHunger(boolean bool) {
		hunger.setValue(bool);
	}
	
	public Property<Boolean> hunger() {
		return hunger;
	}
	
	public int getLevel() {
		return level.getValue();
	}
	
	public void setLevel(int level) {
		this.level.setValue(level);
	}
	
	public Property<Integer> level() {
		return level;
	}
	
	public boolean isDeleteable() {
		return deleteable;
	}
	
	public void setDeleteable(boolean value) {
		deleteable = value;
	}
	
	public boolean isInventoryLocked() {
		return lockInventory.getValue();
	}
	
	public void setInventoryLocked(boolean locked) {
		lockInventory.setValue(locked);
	}
	
	public Property<Boolean> inventoryLocked() {
		return lockInventory;
	}
	
	public boolean isArmourLocked() {
		return lockArmour.getValue();
	}
	
	public void setArmourLocked(boolean locked) {
		lockArmour.setValue(locked);
	}
	
	public Property<Boolean> armourLocked() {
		return lockArmour;
	}
	
	public TeamSelection getTeamColor() {
		return team.getValue();
	}
	
	public void setTeamColor(TeamSelection color) {
		team.setValue(color);
	}
	
	public Property<TeamSelection> teamColor() {
		return team;
	}
	
	public boolean isDisplayedInMenu() {
		return displayInMenu.getValue();
	}
	
	public void setDisplayInMenu(boolean bool) {
		displayInMenu.setValue(bool);
	}
	
	public Property<Boolean> displayInMenu() {
		return displayInMenu;
	}
	
	public Menu createLoadoutMenu() {
		Menu loadoutMenu = new Menu(5, loadoutName.getValue(), new MenuPageInventory(45, null));
		Menu loadoutSettings = new Menu(5, loadoutName.getValue());
		
		if(!loadoutName.getValue().equals("default"))
			loadoutSettings.addItem(new MenuItemBoolean("Use Permissions", "Permission:;minigame.loadout." + loadoutName.getValue().toLowerCase(), 
					Material.GOLD_INGOT, usePermission));
		MenuItemString disName = new MenuItemString("Display Name", Material.PAPER, displayname);
		disName.setAllowNull(true);
		loadoutSettings.addItem(disName);
		loadoutSettings.addItem(new MenuItemBoolean("Allow Fall Damage", Material.LEATHER_BOOTS, fallDamage));
		loadoutSettings.addItem(new MenuItemBoolean("Allow Hunger", Material.APPLE, hunger));
		loadoutSettings.addItem(new MenuItemInteger("XP Level", "Use -1 to not;use loadout levels", Material.EXP_BOTTLE, level, -1, Integer.MAX_VALUE));
		loadoutSettings.addItem(new MenuItemBoolean("Lock Inventory", Material.DIAMOND_SWORD, lockInventory));
		loadoutSettings.addItem(new MenuItemBoolean("Lock Armour", Material.DIAMOND_CHESTPLATE, lockArmour));
		loadoutSettings.addItem(new MenuItemBoolean("Display in Loadout Menu", Material.THIN_GLASS, displayInMenu));
		loadoutSettings.addItem(new MenuItemEnum<TeamSelection>("Lock to Team", Material.LEATHER_CHESTPLATE, team, TeamSelection.class));
		
		LoadoutModule.addAddonMenuItems(loadoutSettings, this);
		
		Menu potionMenu = new Menu(5, loadoutName.getValue());
		
		potionMenu.setControlItem(new MenuItemPotionAdd("Add Potion", Material.ITEM_FRAME, this), 4);
		
		List<MenuItem> potions = new ArrayList<MenuItem>();
		
		for (PotionEffect eff : getAllPotionEffects()) {
			potions.add(new MenuItemPotion(MinigameUtils.capitalize(eff.getType().getName().replace("_", " ")), "Shift + Right Click to Delete", Material.POTION, eff, this));
		}
		potionMenu.addItems(potions);
		
		loadoutMenu.setAllowModify(true);
		
		MenuPageInventory inventory = (MenuPageInventory)loadoutMenu.getFirstPage();
		loadoutMenu.setControlItem(new MenuItemSaveLoadout("Loadout Settings", Material.CHEST, this, loadoutSettings), 2);
		loadoutMenu.setControlItem(new MenuItemSaveLoadout("Edit Potion Effects", Material.POTION, this, potionMenu), 3);
		loadoutMenu.setControlItem(new MenuItemSaveLoadout("Save Loadout", Material.REDSTONE_TORCH_ON, this), 4);
		
		for (Integer item : getItems()) {
			if(item < 100)
				inventory.setSlot(getItem(item), item);
			else if(item == 100)
				inventory.setSlot(getItem(item), 39);
			else if(item == 101)
				inventory.setSlot(getItem(item), 38);
			else if(item == 102)
				inventory.setSlot(getItem(item), 37);
			else if(item == 103)
				inventory.setSlot(getItem(item), 36);
		}
		
		return loadoutMenu;
	}
	
	/**
	 * Sets an addons value in this loadout
	 * @param addon The addon
	 * @param value The value to use
	 */
	public <T> void setAddonValue(Class<? extends LoadoutAddon<T>> addon, T value) {
		addonValues.put(addon, value);
	}
	
	/**
	 * Gets an addons value in this loadout
	 * @param addon The addon
	 * @return The value of the addon, or null
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAddonValue(Class<? extends LoadoutAddon<T>> addon) {
		return (T)addonValues.get(addon);
	}
	
	public void save(ConfigurationSection section) {
		for(Integer slot : getItems())
			section.set("items." + slot, getItem(slot));
		
		for(PotionEffect eff : getAllPotionEffects()) {
			section.set("potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
			section.set("potions." + eff.getType().getName() + ".dur", eff.getDuration());
		}
		
		if(getUsePermissions())
			section.set("usepermissions", true);
		
		if(!hasFallDamage())
			section.set("falldamage", hasFallDamage());
		
		if(hasHunger())
			section.set("hunger", hasHunger());
		
		if(getDisplayName() != null)
			section.set("displayName", getDisplayName());
		
		if(isArmourLocked())
			section.set("armourLocked", isArmourLocked());
		
		if(isInventoryLocked())
			section.set("inventoryLocked", isInventoryLocked());
		
		if(getTeamColor() != null)
			section.set("team", getTeamColor().toString());
		
		if(!isDisplayedInMenu())
			section.set("displayInMenu", isDisplayedInMenu());
		
		for (Entry<Class<? extends LoadoutAddon>, Object> addonValue : addonValues.entrySet()) {
			ConfigurationSection subSection = section.createSection("addons." + addonValue.getKey().getName().replace('.', '-'));
			LoadoutAddon<Object> addon = LoadoutModule.getAddon(addonValue.getKey());
			addon.save(subSection, addonValue.getValue());
		}
	}
	
	public void load(ConfigurationSection section) {
		if (section.contains("items")) {
			ConfigurationSection itemSection = section.getConfigurationSection("items");
			for(String key : itemSection.getKeys(false)) {
				if(key.matches("[0-9]+")) {
					addItem(itemSection.getItemStack(key), Integer.parseInt(key));
				}
			}
		}
		
		if (section.contains("potions")) {
			ConfigurationSection potionSection = section.getConfigurationSection("potions");
			for (String effectName : potionSection.getKeys(false)) {
				if(PotionEffectType.getByName(effectName) == null) {
					continue;
				}
				
				PotionEffect effect = new PotionEffect(PotionEffectType.getByName(effectName),
					potionSection.getInt(effectName + ".dur"),
					potionSection.getInt(effectName + ".amp")
					);
				
				addPotionEffect(effect);
			}
		}
		
		if(section.contains("usepermissions"))
			setUsePermissions(section.getBoolean("usepermissions"));
		
		if(section.contains("falldamage"))
			setHasFallDamage(section.getBoolean("falldamage"));
		
		if(section.contains("hunger"))
			setHasHunger(section.getBoolean("hunger"));
		
		if(section.contains("displayName"))
			setDisplayName(section.getString("displayName"));
		
		if(section.contains("lockInventory"))
			setInventoryLocked(section.getBoolean("lockInventory"));
		
		if(section.contains("lockArmour"))
			setArmourLocked(section.getBoolean("lockArmour"));
		
		if(section.contains("team"))
			setTeamColor(TeamSelection.from(section.getString("team")));
		
		if(section.contains("displayInMenu"))
			setDisplayInMenu(section.getBoolean("displayInMenu"));
		
		if (section.contains("addons")) {
			ConfigurationSection addonSection = section.getConfigurationSection("addons");
			
			for (String addonKey : addonSection.getKeys(false)) {
				try {
					// First determine the class
					Class<?> rawClass = Class.forName(addonKey.replace('-', '.'));
					if (LoadoutAddon.class.isAssignableFrom(rawClass)) {
						Class<? extends LoadoutAddon> clazz = rawClass.asSubclass(LoadoutAddon.class);
						
						// Now we can load the value
						LoadoutAddon<Object> addon = LoadoutModule.getAddon(clazz);
						if (addon != null) {
							Object value = addon.load(addonSection.getConfigurationSection(addonKey));
							addonValues.put(clazz, value);
						}
					}
				} catch (ClassNotFoundException e) {
					// Ignore it
				}
			}
		}
	}
}
