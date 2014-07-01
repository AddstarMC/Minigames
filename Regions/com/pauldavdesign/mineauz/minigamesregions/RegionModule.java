package com.pauldavdesign.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.config.Flag;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemNewLine;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;
import com.pauldavdesign.mineauz.minigamesregions.actions.ActionInterface;
import com.pauldavdesign.mineauz.minigamesregions.actions.Actions;
import com.pauldavdesign.mineauz.minigamesregions.conditions.ConditionInterface;
import com.pauldavdesign.mineauz.minigamesregions.conditions.Conditions;
import com.pauldavdesign.mineauz.minigamesregions.menuitems.MenuItemNode;
import com.pauldavdesign.mineauz.minigamesregions.menuitems.MenuItemRegion;

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
	public Map<String, Flag<?>> getFlags() {
		return null;
	}
	
	@Override
	public boolean useSeparateConfig(){
		return true;
	}

	@Override
	public void save(FileConfiguration config) {
		Set<String> rs = regions.keySet();
		for(String name : rs){
			Region r = regions.get(name);
			Map<String, Object> sloc = MinigameUtils.serializeLocation(r.getFirstPoint());
			for(String i : sloc.keySet()){
				if(!i.equals("yaw") && !i.equals("pitch"))
					config.set(getMinigame() + ".regions." + name + ".point1." + i, sloc.get(i));
			}
			sloc = MinigameUtils.serializeLocation(r.getSecondPoint());
			for(String i : sloc.keySet()){
				if(!i.equals("yaw") && !i.equals("pitch"))
					config.set(getMinigame() + ".regions." + name + ".point2." + i, sloc.get(i));
			}
			
			if(r.getTickDelay() != 20){
				config.set(getMinigame() + ".regions." + name + ".tickDelay", r.getTickDelay());
			}
			
			int c = 0;
			for(RegionExecutor ex : r.getExecutors()){
				String path = getMinigame() + ".regions." + name + ".executors." + c;
				config.set(path + ".trigger", ex.getTrigger().toString());
				List<String> acts = new ArrayList<String>();
				for(ActionInterface act : ex.getActions()){
					acts.add(act.getName());
				}
				config.set(path + ".actions", acts);
				if(!ex.getConditions().isEmpty()){
					List<String> conditions = new ArrayList<String>(ex.getConditions().size());
					for(ConditionInterface con : ex.getConditions()){
						conditions.add(con.getName());
					}
					config.set(path + ".conditions", conditions);
				}
				if(!ex.getArguments().isEmpty()){
					for(ActionInterface act : ex.getActions()){
						act.saveArguments(ex.getArguments(), config, path + ".arguments");
					}
					for(ConditionInterface con : ex.getConditions()){
						con.saveArguments(ex.getArguments(), config, path + ".arguments");
					}
				}
				c++;
			}
		}
		
		Set<String> ns = nodes.keySet();
		for(String name : ns){
			Node n = nodes.get(name);
			Map<String, Object> sloc = MinigameUtils.serializeLocation(n.getLocation());
			for(String i : sloc.keySet()){
				if(!i.equals("yaw") && !i.equals("pitch"))
					config.set(getMinigame() + ".nodes." + name + ".point." + i, sloc.get(i));
			}
			
			int c = 0;
			for(NodeExecutor ex : n.getExecutors()){
				String path = getMinigame() + ".nodes." + name + ".executors." + c;
				config.set(path + ".trigger", ex.getTrigger().toString());
				List<String> acts = new ArrayList<String>();
				for(ActionInterface act : ex.getActions()){
					acts.add(act.getName());
				}
				config.set(path + ".actions", acts);
				if(!ex.getConditions().isEmpty()){
					List<String> conditions = new ArrayList<String>(ex.getConditions().size());
					for(ConditionInterface con : ex.getConditions()){
						conditions.add(con.getName());
					}
					config.set(path + ".conditions", conditions);
				}
				if(!ex.getArguments().isEmpty()){
					for(ActionInterface act : ex.getActions()){
						act.saveArguments(ex.getArguments(), config, path + ".arguments");
					}
					for(ConditionInterface con : ex.getConditions()){
						con.saveArguments(ex.getArguments(), config, path + ".arguments");
					}
				}
				c++;
			}
		}
	}

	@Override
	public void load(FileConfiguration config) {
		if(config.contains(getMinigame() + ".regions")){
			Set<String> rs = config.getConfigurationSection(getMinigame() + ".regions").getKeys(false);
			for(String name : rs){
				String cloc1 = getMinigame() + ".regions." + name + ".point1.";
				String cloc2 = getMinigame() + ".regions." + name + ".point2.";
				World w1 = Minigames.plugin.getServer().getWorld(config.getString(cloc1 + "world"));
				World w2 = Minigames.plugin.getServer().getWorld(config.getString(cloc2 + "world"));
				double x1 = config.getDouble(cloc1 + "x");
				double x2 = config.getDouble(cloc2 + "x");
				double y1 = config.getDouble(cloc1 + "y");
				double y2 = config.getDouble(cloc2 + "y");
				double z1 = config.getDouble(cloc1 + "z");
				double z2 = config.getDouble(cloc2 + "z");
				Location loc1 = new Location(w1, x1, y1, z1);
				Location loc2 = new Location(w2, x2, y2, z2);
				
				regions.put(name, new Region(name, loc1, loc2));
				Region r = regions.get(name);
				if(config.contains(getMinigame() + ".regions." + name + ".tickDelay")){
					r.changeTickDelay(config.getLong(getMinigame() + ".regions." + name + ".tickDelay"));
				}
				if(config.contains(getMinigame() + ".regions." + name + ".executors")){
					Set<String> ex = config.getConfigurationSection(getMinigame() + ".regions." + name + ".executors").getKeys(false);
					for(String i : ex){
						String path = getMinigame() + ".regions." + name + ".executors." + i;
						RegionExecutor rex = new RegionExecutor(RegionTrigger.valueOf(config.getString(path + ".trigger")));
						if(config.contains(path + ".actions")){
							for(String action : config.getStringList(path + ".actions")){
								rex.addAction(Actions.getActionByName(action));
							}
						}
						if(config.contains(path + ".conditions")){
							for(String con : config.getStringList(path + ".conditions")){
								rex.addCondition(Conditions.getConditionByName(con));
							}
						}
						if(config.contains(path + ".arguments")){
							for(ActionInterface act : rex.getActions()){
								rex.addArguments(act.loadArguments(config, path + ".arguments"));
							}
							for(ConditionInterface con : rex.getConditions()){
								rex.addArguments(con.loadArguments(config, path + ".arguments"));
							}
						}
						r.addExecutor(rex);
					}
				}
			}
		}
		
		if(config.contains(getMinigame() + ".nodes")){
			Set<String> rs = config.getConfigurationSection(getMinigame() + ".nodes").getKeys(false);
			for(String name : rs){
				String cloc1 = getMinigame() + ".nodes." + name + ".point.";
				World w1 = Minigames.plugin.getServer().getWorld(config.getString(cloc1 + "world"));
				double x1 = config.getDouble(cloc1 + "x");
				double y1 = config.getDouble(cloc1 + "y");
				double z1 = config.getDouble(cloc1 + "z");
				Location loc1 = new Location(w1, x1, y1, z1);
				
				nodes.put(name, new Node(name, loc1));
				Node n = nodes.get(name);
				if(config.contains(getMinigame() + ".nodes." + name + ".executors")){
					Set<String> ex = config.getConfigurationSection(getMinigame() + ".nodes." + name + ".executors").getKeys(false);
					for(String i : ex){
						String path = getMinigame() + ".nodes." + name + ".executors." + i;
						NodeExecutor rex = new NodeExecutor(NodeTrigger.valueOf(config.getString(path + ".trigger")));
						if(config.contains(path + ".actions")){
							for(String action : config.getStringList(path + ".actions")){
								rex.addAction(Actions.getActionByName(action));
							}
						}
						if(config.contains(path + ".conditions")){
							for(String con : config.getStringList(path + ".conditions")){
								rex.addCondition(Conditions.getConditionByName(con));
							}
						}
						if(config.contains(path + ".arguments")){
							for(ActionInterface act : rex.getActions()){
								rex.addArguments(act.loadArguments(config, path + ".arguments"));
							}
							for(ConditionInterface con : rex.getConditions()){
								rex.addArguments(con.loadArguments(config, path + ".arguments"));
							}
						}
						n.addExecutor(rex);
					}
				}
			}
		}
	}
	
	public static RegionModule getMinigameModule(Minigame minigame){
		return (RegionModule) minigame.getModule("Regions");
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
	
	public void displayMenu(MinigamePlayer viewer){
		Menu rm = new Menu(6, "Regions", viewer);
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
		rm.displayMenu(viewer);
	}
	
	
	
	@Override
	public void addMenuOptions(Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getMenuOptions(Menu previous) {
		return false;
	}
}
