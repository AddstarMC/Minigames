package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemValue;
import au.com.mineauz.minigames.menu.MenuItemValue.IMenuItemChange;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SpawnEntityAction extends ActionInterface {
	
	private final StringProperty type = new StringProperty("ZOMBIE", "type"); // TODO: CHange to EntityType
	private Map<String, Property<?>> settings = new HashMap<String, Property<?>>(); // FIXME: This is not saved anywhere
	
	public SpawnEntityAction(){
		properties.addProperty(type);
		addBaseSettings();
	}
	
	private void addBaseSettings(){
		settings.put("velocityx", Properties.create(0.0d));
		settings.put("velocityy", Properties.create(0.0d));
		settings.put("velocityz", Properties.create(0.0d));
	}
	
	public <T> Property<T> getSetting(String name) {
		return (Property<T>)settings.get(name.toLowerCase());
	}
	
	public <T> Property<T> getSetting(String name, Class<T> type) {
		return (Property<T>)settings.get(name.toLowerCase());
	}
	
	public void addProperty(String name, Property<?> property) {
		settings.put(name.toLowerCase(), property);
	}
	
	public boolean hasSetting(String name) {
		return settings.containsKey(name.toLowerCase());
	}

	@Override
	public String getName() {
		return "SPAWN_ENTITY";
	}

	@Override
	public String getCategory() {
		return "World Actions";
	}

	@Override
	public boolean useInRegions() {
		return false;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeRegionAction(MinigamePlayer player,
			Region region) {
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		if(player == null || !player.isInMinigame()) return;
		final Entity ent = node.getLocation().getWorld().spawnEntity(node.getLocation(), EntityType.valueOf(type.getValue()));
		
		final double vx = getSetting("velocityx", Double.class).getValue();
		final double vy = getSetting("velocityy", Double.class).getValue();
		final double vz = getSetting("velocityz", Double.class).getValue();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				ent.setVelocity(new Vector(vx, vy, vz));
			}
		});
		
		if(ent instanceof LivingEntity){
			LivingEntity lent = (LivingEntity) ent;
			if (hasSetting("displayname")) {
				lent.setCustomName(getSetting("displayname", String.class).getValue());
				lent.setCustomNameVisible(getSetting("displaynamevisible", Boolean.class).getValue());
			}
		}
		
		ent.setMetadata("MinigameEntity", new FixedMetadataValue(Minigames.plugin, true));
		player.getMinigame().getBlockRecorder().addEntity(ent, player, true);
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Spawn Entity");
		List<String> options = new ArrayList<String>();
		for(EntityType type : EntityType.values()){
			if(type != EntityType.ITEM_FRAME && type != EntityType.LEASH_HITCH && type != EntityType.PLAYER && 
					type != EntityType.COMPLEX_PART && type != EntityType.WEATHER && type != EntityType.LIGHTNING &&
					type != EntityType.PAINTING && type != EntityType.UNKNOWN &&
					type != EntityType.DROPPED_ITEM)
				options.add(MinigameUtils.capitalize(type.toString().replace("_", " ")));
		}
		
		MenuItemList typeItem = new MenuItemList("Entity Type", Material.SKULL_ITEM, type, options);
		typeItem.setChangeHandler(new IMenuItemChange<String>() {
			@Override
			public void onChange(MenuItemValue<String> menuItem, MinigamePlayer player, String previous, String current) {
				settings.clear();
				addBaseSettings();
			}
		});
		
		m.addItem(new MenuItemDecimal("X Velocity", Material.ARROW, getSetting("velocityx", Double.class), 0.5, 1, Double.MIN_VALUE, Double.MAX_VALUE));
		m.addItem(new MenuItemDecimal("Y Velocity", Material.ARROW, getSetting("velocityy", Double.class), 0.5, 1, Double.MIN_VALUE, Double.MAX_VALUE));
		m.addItem(new MenuItemDecimal("Z Velocity", Material.ARROW, getSetting("velocityz", Double.class), 0.5, 1, Double.MIN_VALUE, Double.MAX_VALUE));
		
		m.addItem(new MenuItemNewLine());
		
		final MenuItem cus = new MenuItem("Entity Settings", Material.CHEST);
		cus.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				if(type.getValue().equals("ZOMBIE")){
					Menu eSet = new Menu(3, "Settings");
					eSet.clear();
					livingEntitySettings(eSet);
					eSet.displayMenu(player);
				}
			}
		});
		m.addItem(cus);
		
		m.displayMenu(player);
		return true;
	}
	
	private void livingEntitySettings(Menu eSet){
		addProperty("displayname", Properties.create(""));
		addProperty("displaynamevisible", Properties.create(false));
		
		eSet.addItem(new MenuItemString("Display Name", Material.NAME_TAG, getSetting("displayname", String.class)));
		eSet.addItem(new MenuItemBoolean("Display Name Visible", Material.ENDER_PEARL, getSetting("displaynamevisible", Boolean.class)));
	}
	
	public void zombieSettings(Menu eSet){
		
	}
}
