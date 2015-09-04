package au.com.mineauz.minigames.tool;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.menu.MenuItemToolMode;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamSelection;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.properties.ChangeListener;
import au.com.mineauz.minigames.properties.ObservableValue;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;

public class MinigameTool {
	private ItemStack tool;
	private Minigame minigame = null;
	private ToolMode mode = null;
	private Property<TeamSelection> team = Properties.create(TeamSelection.NONE);
	private Map<String, Property<String>> properties = Maps.newHashMap();
	
	private final ChangeListener<String> propertyListener = new ChangeListener<String>() {
		@Override
		public void onValueChange(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			updateItem();
		}
	};
	
	public MinigameTool(ItemStack tool){
		this.tool = tool;
		ItemMeta meta = tool.getItemMeta();
		if(meta.getLore() != null){
			String mg = ChatColor.stripColor(meta.getLore().get(0)).replace("Minigame: ", "");
			if(Minigames.plugin.mdata.hasMinigame(mg))
				minigame = Minigames.plugin.mdata.getMinigame(mg);
			
			String md = ChatColor.stripColor(meta.getLore().get(1)).replace("Mode: ", "").replace(" ", "_");
			mode = ToolModes.getToolMode(md);
			
			TeamSelection selection = TeamSelection.valueOf(ChatColor.stripColor(meta.getLore().get(2).replace("Team: ", "")).toUpperCase());
			if (selection == null) {
				selection = TeamSelection.NONE;
			}
			
			team.setValue(selection);
			loadSettings(meta.getLore());
		}
		else{
			meta.setDisplayName(ChatColor.GREEN + "Minigame Tool");
			meta.setLore(generateLore());
			tool.setItemMeta(meta);
		}
	}
	
	private void loadSettings(List<String> lore) {
		for (String line : lore) {
			line = ChatColor.stripColor(line);
			if (line.startsWith("Minigame") || line.startsWith("Mode") || line.startsWith("Team")) {
				continue;
			}
			
			String[] parts = line.split(":");
			if (parts.length != 2) {
				continue;
			}
			
			String name = parts[0].trim();
			String value = parts[1].trim();
			
			Property<String> prop = Properties.create(value);
			prop.addListener(propertyListener);
			properties.put(name, prop);
		}
	}
	
	private List<String> generateLore() {
		List<String> lore = Lists.newArrayList();
		lore.add(ChatColor.AQUA + "Minigame: " + ChatColor.WHITE + "None");
		lore.add(ChatColor.AQUA + "Mode: " + ChatColor.WHITE + "None");
		lore.add(ChatColor.AQUA + "Team: " + ChatColor.WHITE + "None");
		
		for (Entry<String, Property<String>> entry : properties.entrySet()) {
			if (entry.getValue().getValue() == null) {
				continue;
			}
			
			lore.add(ChatColor.AQUA + entry.getKey() + ": " + ChatColor.WHITE + entry.getValue().getValue());
		}
		
		return lore;
	}
	
	public void updateItem() {
		ItemMeta meta = tool.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Minigame Tool");
		meta.setLore(generateLore());
		tool.setItemMeta(meta);
	}
	
	public ItemStack getTool(){
		return tool;
	}
	
	public void setMinigame(Minigame minigame){
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(0, ChatColor.AQUA + "Minigame: " + ChatColor.WHITE + minigame.getName(false));
		meta.setLore(lore);
		tool.setItemMeta(meta);
		this.minigame = minigame;
	}
	
	public void setMode(ToolMode mode){
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(1, ChatColor.AQUA + "Mode: " + ChatColor.WHITE + MinigameUtils.capitalize(mode.getName().replace("_", " ")));
		meta.setLore(lore);
		tool.setItemMeta(meta);
		this.mode = mode;
	}
	
	public ToolMode getMode(){
		return mode;
	}
	
	public Minigame getMinigame(){
		return minigame;
	}
	
	public void setTeam(TeamSelection team) {
		ItemMeta meta = tool.getItemMeta();
		List<String> lore = meta.getLore();
		
		ChatColor color = ChatColor.WHITE;
		if (team.getTeam() != null) {
			color = team.getTeam().getColor();
		}
		
		lore.set(2, ChatColor.AQUA + "Team: " + color + WordUtils.capitalizeFully(team.name()));
			
		meta.setLore(lore);
		tool.setItemMeta(meta);
		this.team.setValue(team);
	}
	
	public TeamSelection getTeam() {
		return team.getValue();
	}
	
	public Property<TeamSelection> team() {
		return team;
	}
	
	public void addSetting(String name, String setting) {
		Property<String> prop = Properties.create(setting);
		prop.addListener(propertyListener);
		properties.put(name, prop);
		updateItem();
	}
	
	public void changeSetting(String name, String setting) {
		Property<String> prop = properties.get(name);
		if (prop != null) {
			prop.setValue(setting);
		} else {
			addSetting(name, setting);
		}
	}
	
	public String getSetting(String name) {
		Property<String> prop = properties.get(name);
		if (prop != null) {
			return prop.getValue();
		} else {
			return "None";
		}
	}
	
	public Property<String> getSettingProperty(String name) {
		return properties.get(name);
	}
	
	public void removeSetting(String name) {
		Property<String> prop = properties.remove(name);
		if (prop != null) {
			prop.removeListener(propertyListener);
		}
		
		updateItem();
	}
	
	public void openMenu(MinigamePlayer player){
		Menu men = new Menu(2, "Set Tool Mode");
		
		final MenuItem miselect = new MenuItem("Select", "Selects and area;or points visually", Material.DIAMOND_BLOCK);
		final MenuItem mideselect = new MenuItem("Deselect", "Deselects an;area or points", Material.GLASS);
		miselect.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				if(mode != null){
					mode.select(player, minigame, minigame.getModule(TeamsModule.class).getTeam(team.getValue().getTeam()));
				}
			}
		});
		mideselect.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				if(mode != null){
					mode.deselect(player, minigame, minigame.getModule(TeamsModule.class).getTeam(team.getValue().getTeam()));
				}
			}
		});
		
		men.setControlItem(mideselect, 4);
		men.setControlItem(miselect, 3);
		
		men.setControlItem(new MenuItemEnum<TeamSelection>("Team", Material.PAPER, team, TeamSelection.class), 2);
		
		for(ToolMode m : ToolModes.getToolModes()){
			men.addItem(new MenuItemToolMode(m.getDisplayName(), m.getDescription(), m.getIcon(), m));
		}
		
		men.displayMenu(player);
	}
}
