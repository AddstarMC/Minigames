package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import com.google.common.collect.Maps;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDisplayLoadout;
import au.com.mineauz.minigames.menu.MenuItemLoadoutAdd;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamSelection;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.types.LoadoutSetProperty;

public class LoadoutModule extends MinigameModule {

	private final ConfigPropertyContainer properties = new ConfigPropertyContainer();
	private Map<String, PlayerLoadout> extraLoadouts = new HashMap<String, PlayerLoadout>();
	private final LoadoutSetProperty loadoutsFlag = new LoadoutSetProperty(extraLoadouts, "loadouts");
	private static Map<Class<? extends LoadoutAddon>, LoadoutAddon<?>> addons = Maps.newHashMap();
	
	public LoadoutModule(Minigame mgm) {
		super(mgm);
		PlayerLoadout def = new PlayerLoadout("default");
		def.setDeleteable(false);
		extraLoadouts.put("default", def);
		
		properties.addProperty(loadoutsFlag);
	}

	@Override
	public String getName() {
		return "Loadouts";
	}
	
	@Override
	public ConfigPropertyContainer getProperties() {
		return properties;
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}
	
	@Deprecated
	public static LoadoutModule getMinigameModule(Minigame minigame){
		return (LoadoutModule) minigame.getModule(LoadoutModule.class);
	}
	
	public void addLoadout(String name){
		extraLoadouts.put(name, new PlayerLoadout(name));
	}
	
	public void deleteLoadout(String name){
		if(extraLoadouts.containsKey(name)){
			extraLoadouts.remove(name);
		}
	}
	
	public Set<String> getLoadouts(){
		return extraLoadouts.keySet();
	}
	
	public Map<String, PlayerLoadout> getLoadoutMap(){
		return extraLoadouts;
	}
	
	public PlayerLoadout getLoadout(String name){
		PlayerLoadout pl = null;
		if(extraLoadouts.containsKey(name)){
			pl = extraLoadouts.get(name);
		}
		else{
			for(String loadout : extraLoadouts.keySet()){
				if(loadout.equalsIgnoreCase(name)){
					pl = extraLoadouts.get(loadout);
					break;
				}
			}
		}
		return pl;
	}
	
	public boolean hasLoadouts(){
		if(extraLoadouts.isEmpty()){
			return false;
		}
		return true;
	}
	
	public boolean hasLoadout(String name){
		if(!name.equalsIgnoreCase("default")){
			if(extraLoadouts.containsKey(name))
				return extraLoadouts.containsKey(name);
			else{
				for(String loadout : extraLoadouts.keySet()){
					if(loadout.equalsIgnoreCase(name))
						return true;
				}
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	@Override
	public Menu createSettingsMenu() {
		Menu menu = new Menu(5, "Loadouts");
		
		for(String ld : getLoadouts()){
			Material item = Material.THIN_GLASS;
			if(getLoadout(ld).getItems().size() != 0){
				item = getLoadout(ld).getItem((Integer)getLoadout(ld).getItems().toArray()[0]).getType();
			}
			if(getLoadout(ld).isDeleteable())
				menu.addItem(new MenuItemDisplayLoadout(ld, "Shift + Right Click to Delete", item, getLoadout(ld), getMinigame()));
			else
				menu.addItem(new MenuItemDisplayLoadout(ld, item, getLoadout(ld), getMinigame()));
		}
		menu.setControlItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, getLoadoutMap(), getMinigame()), 4);
		return menu;
	}
	
	/**
	 * Registers a loadout addon. This addon will be available for all loadouts on all games.
	 * @param plugin The plugin registering the addon
	 * @param addon The addon to register
	 */
	public static void registerAddon(Plugin plugin, LoadoutAddon<?> addon) {
		addons.put(addon.getClass(), addon);
	}
	
	/**
	 * Unregisters a previously registered addon
	 * @param addon The addon to unregister
	 */
	public static void unregisterAddon(Class<? extends LoadoutAddon<?>> addon) {
		addons.remove(addon);
	}
	
	/**
	 * Retrieves a registered addon
	 * @param addonClass The addon class to get the addon for
	 * @return The addon or null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends LoadoutAddon<?>> T getAddon(Class<T> addonClass) {
		return (T)addons.get(addonClass);
	}
	
	public static void addAddonMenuItems(Menu menu, PlayerLoadout loadout) {
		for (LoadoutAddon<?> addon : addons.values()) {
			addon.addMenuOptions(menu, loadout);
		}
	}
	
	public void displaySelectionMenu(MinigamePlayer player, final boolean equip){
		Menu m = new Menu(6, "Select Loadout");
		
		for(PlayerLoadout loadout : extraLoadouts.values()){
			if(loadout.isDisplayedInMenu()){
				if(!loadout.getUsePermissions() || player.getPlayer().hasPermission("minigame.lonulladout." + loadout.getName(false).toLowerCase())){
					if(!player.getMinigame().isTeamGame() || loadout.getTeamColor() == TeamSelection.NONE || 
							player.getTeam().getColor() == loadout.getTeamColor().getTeam()){
						MenuItem c = new MenuItem(loadout.getName(true), Material.GLASS);
						if(!loadout.getItems().isEmpty())
							c.setItem(loadout.getItem(new ArrayList<Integer>(loadout.getItems()).get(0)));
						final PlayerLoadout floadout2 = loadout;
						c.setClickHandler(new IMenuItemClick() {
							@Override
							public void onClick(MenuItem menuItem, MinigamePlayer player) {
								player.setLoadout(floadout2);
								player.getPlayer().closeInventory();
								if(!equip)
									player.sendMessage(MinigameUtils.getLang("player.loadout.nextSpawn"), MessageType.Normal);
								else{
									player.sendMessage(MinigameUtils.formStr("player.loadout.equipped", floadout2.getName(true)), MessageType.Normal);
									floadout2.equiptLoadout(player);
								}
							}
						});
						m.addItem(c);
					}
				}
			}
		}
		m.displayMenu(player);
	}

	/**
	 * Represents a custom loadout element.
	 * This can be used to add things like disguises
	 * or commands.
	 * 
	 * @param <T> The value type for this loadout addon.arg1
	 */
	public static interface LoadoutAddon<T> {
		public String getName();
		public void addMenuOptions(Menu menu, PlayerLoadout loadout);
		
		public void save(ConfigurationSection section, T value);
		public T load(ConfigurationSection section);
		
		public void applyLoadout(MinigamePlayer player, T value);
		public void clearLoadout(MinigamePlayer player, T value);
	}
}
