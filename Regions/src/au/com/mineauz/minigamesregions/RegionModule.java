package au.com.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigamesregions.menuitems.MenuItemNode;
import au.com.mineauz.minigamesregions.menuitems.MenuItemRegion;

public class RegionModule extends MinigameModule {
	
	private Map<String, Region> regions = new HashMap<String, Region>();
	private Map<String, Node> nodes = new HashMap<String, Node>();
	
	public RegionModule(Minigame mgm){
		super(mgm);
	}
	
	@Override
	public String getName(){
		return "Regions";
	}
	
	@Override
	public ConfigPropertyContainer getProperties() {
		return null;
	}

	@Override
	public boolean useSeparateConfig(){
		return true;
	}

	@Override
	public void save(ConfigurationSection root) {
		ConfigurationSection regionSection = root.createSection("regions");
		for (Region region : regions.values()) {
			region.save(regionSection.createSection(region.getName()));
		}
		
		ConfigurationSection nodeSection = root.createSection("nodes");
		for (Node node : nodes.values()) {
			node.save(nodeSection.createSection(node.getName()));
		}
	}

	@Override
	public void load(ConfigurationSection root) {
		ConfigurationSection regionSection = root.getConfigurationSection("regions");
		if (regionSection != null) {
			for (String name : regionSection.getKeys(false)) {
				Region region = new Region(name);
				region.load(regionSection.getConfigurationSection(name));
				regions.put(name, region);
			}
		}
		
		ConfigurationSection nodeSection = root.getConfigurationSection("nodes");
		if (nodeSection != null) {
			for (String name : nodeSection.getKeys(false)) {
				Node node = new Node(name);
				node.load(nodeSection.getConfigurationSection(name));
				nodes.put(name, node);
			}
		}
	}
	
	@Deprecated
	public static RegionModule getMinigameModule(Minigame minigame){
		return (RegionModule) minigame.getModule(RegionModule.class);
	}
	
	public boolean hasRegion(String name){
		if(!regions.containsKey(name)){
			for(String n : regions.keySet()){
				if(n.equalsIgnoreCase(name))
					return true;
			}
			return false;
		}
		return true;
	}
	
	public void addRegion(String name, Region region){
		if(!hasRegion(name))
			regions.put(name, region);
	}
	
	public Region getRegion(String name){
		if(!hasRegion(name)){
			for(String n : regions.keySet()){
				if(n.equalsIgnoreCase(name))
					return regions.get(n);
			}
			return null;
		}
		return regions.get(name);
	}
	
	public List<Region> getRegions(){
		return new ArrayList<Region>(regions.values());
	}
	
	public void removeRegion(String name){
		if(hasRegion(name)){
			regions.get(name).removeTickTask();
			regions.remove(name);
		}
		else{
			for(String n : regions.keySet()){
				if(n.equalsIgnoreCase(name)){
					regions.get(n).removeTickTask();
					regions.remove(n);
					break;
				}
			}
		}
	}
	
	public boolean hasNode(String name){
		if(!nodes.containsKey(name)){
			for(String n : nodes.keySet()){
				if(n.equalsIgnoreCase(name))
					return true;
			}
			return false;
		}
		return true;
	}
	
	public void addNode(String name, Node node){
		if(!hasNode(name))
			nodes.put(name, node);
	}
	
	public Node getNode(String name){
		if(!hasNode(name)){
			for(String n : nodes.keySet()){
				if(n.equalsIgnoreCase(name))
					return nodes.get(n);
			}
			return null;
		}
		return nodes.get(name);
	}
	
	public List<Node> getNodes(){
		return new ArrayList<Node>(nodes.values());
	}
	
	public void removeNode(String name){
		if(hasNode(name)){
			nodes.remove(name);
		}
		else{
			for(String n : nodes.keySet()){
				if(n.equalsIgnoreCase(name)){
					nodes.remove(n);
					break;
				}
			}
		}
	}
	
	@Override
	public Menu createSettingsMenu() {
		Menu rm = new Menu(6, "Regions and Nodes");
		List<MenuItem> items = new ArrayList<MenuItem>(regions.size());
		for(String name : regions.keySet()){
			MenuItemRegion mir = new MenuItemRegion(name, Material.CHEST, regions.get(name), this);
			items.add(mir);
		}
		items.add(new MenuItemNewLine());
		for(String name : nodes.keySet()){
			MenuItemNode min = new MenuItemNode(name, Material.CHEST, nodes.get(name), this);
			items.add(min);
		}
		rm.addItems(items);
		return rm;
	}
	
	
	@Override
	public void addEditMenuOptions(Menu menu) {
		menu.addItem(new MenuItemSubMenu("Regions and Nodes", Material.DIAMOND_BLOCK, createSettingsMenu()));
	}
}
