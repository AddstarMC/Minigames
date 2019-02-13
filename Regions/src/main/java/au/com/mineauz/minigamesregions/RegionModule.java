package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.actions.Actions;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;
import au.com.mineauz.minigamesregions.conditions.Conditions;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.menuitems.MenuItemNode;
import au.com.mineauz.minigamesregions.menuitems.MenuItemRegion;
import au.com.mineauz.minigamesregions.triggers.Triggers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class RegionModule extends MinigameModule {

    private Map<String, Region> regions = new HashMap<>();
    private Map<String, Node> nodes = new HashMap<>();
    
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
                if(ex.getTrigger() !=null ) {
                    config.set(path + ".trigger", ex.getTrigger().getName());
                }
                int acc = 0;
                for(ActionInterface act : ex.getActions()){
                    config.set(path + ".actions." + acc + ".type", act.getName());
                    act.saveArguments(config, path + ".actions." + acc + ".arguments");
                    acc++;
                }
                
                acc = 0;
                for(ConditionInterface con : ex.getConditions()){
                    config.set(path + ".conditions." + acc + ".type", con.getName());
                    con.saveArguments(config, path + ".conditions." + acc + ".arguments");
                    acc++;
                }
                
                if(ex.isTriggerPerPlayer())
                    config.set(path + ".isTriggeredPerPlayer", ex.isTriggerPerPlayer());
                if(ex.getTriggerCount() != 0)
                    config.set(path + ".triggerCount", ex.getTriggerCount());
                c++;
            }
        }
        
        Set<String> ns = nodes.keySet();
        for(String name : ns){
            Node n = nodes.get(name);
            Map<String, Object> sloc = MinigameUtils.serializeLocation(n.getLocation());
            for(String i : sloc.keySet()){
                config.set(getMinigame() + ".nodes." + name + ".point." + i, sloc.get(i));
            }
            
            int c = 0;
            for(NodeExecutor ex : n.getExecutors()){
                String path = getMinigame() + ".nodes." + name + ".executors." + c;
                config.set(path + ".trigger", ex.getTrigger().getName());
                
                int acc = 0;
                for(ActionInterface act : ex.getActions()){
                    config.set(path + ".actions." + acc + ".type", act.getName());
                    act.saveArguments(config, path + ".actions." + acc + ".arguments");
                    acc++;
                }
                
                acc = 0;
                for(ConditionInterface con : ex.getConditions()){
                    config.set(path + ".conditions." + acc + ".type", con.getName());
                    con.saveArguments(config, path + ".conditions." + acc + ".arguments");
                    acc++;
                }
                
                if(ex.isTriggerPerPlayer())
                    config.set(path + ".isTriggeredPerPlayer", ex.isTriggerPerPlayer());
                if(ex.getTriggerCount() != 0)
                    config.set(path + ".triggerCount", ex.getTriggerCount());
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
                World w1 = Minigames.getPlugin().getServer().getWorld(config.getString(cloc1 + "world"));
                World w2 = Minigames.getPlugin().getServer().getWorld(config.getString(cloc2 + "world"));
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
                        RegionExecutor rex = new RegionExecutor(Triggers.getTrigger(config.getString(path + ".trigger")));
                        
                        if(config.contains(path + ".actions")){
                            for(String a : config.getConfigurationSection(path + ".actions").getKeys(false)){
                                ActionInterface ai = Actions.getActionByName(config.getString(path + ".actions." + a + ".type"));
                                if (ai != null) {
                                    ai.loadArguments(config, path + ".actions." + a + ".arguments");
                                    rex.addAction(ai);
                                }
                            }
                        }
                        if(config.contains(path + ".conditions")){
                            for(String c : config.getConfigurationSection(path + ".conditions").getKeys(false)){
                                ConditionInterface ci = Conditions.getConditionByName(config.getString(path + ".conditions." + c + ".type"));
                                if (ci != null) {
                                    ci.loadArguments(config, path + ".conditions." + c + ".arguments");
                                    rex.addCondition(ci);
                                }
                            }
                        }
                        
                        if(config.contains(path + ".isTriggeredPerPlayer"))
                            rex.setTriggerPerPlayer(config.getBoolean(path + ".isTriggeredPerPlayer"));
                        if(config.contains(path + ".triggerCount"))
                            rex.setTriggerCount(config.getInt(path + ".triggerCount"));
                        r.addExecutor(rex);
                    }
                }
            }
        }
        
        if(config.contains(getMinigame() + ".nodes")){
            Set<String> rs = config.getConfigurationSection(getMinigame() + ".nodes").getKeys(false);
            for(String name : rs){
                String cloc1 = getMinigame() + ".nodes." + name + ".point.";
                World w1 = Minigames.getPlugin().getServer().getWorld(config.getString(cloc1 + "world"));
                double x1 = config.getDouble(cloc1 + "x");
                double y1 = config.getDouble(cloc1 + "y");
                double z1 = config.getDouble(cloc1 + "z");
                float yaw = 0f;
                float pitch = 0f;
                if(config.contains(cloc1 + "yaw")){ //TODO: Remove check after next dev build
                    yaw = ((Double) config.getDouble(cloc1 + "yaw")).floatValue();
                    pitch = ((Double) config.getDouble(cloc1 + "pitch")).floatValue();
                }
                Location loc1 = new Location(w1, x1, y1, z1, yaw, pitch);
                
                nodes.put(name, new Node(name, loc1));
                Node n = nodes.get(name);
                if(config.contains(getMinigame() + ".nodes." + name + ".executors")){
                    Set<String> ex = config.getConfigurationSection(getMinigame() + ".nodes." + name + ".executors").getKeys(false);
                    for(String i : ex){
                        String path = getMinigame() + ".nodes." + name + ".executors." + i;
                        NodeExecutor rex = new NodeExecutor(Triggers.getTrigger(config.getString(path + ".trigger")));

                        if(config.contains(path + ".actions")){
                            for(String a : config.getConfigurationSection(path + ".actions").getKeys(false)){
                                ActionInterface ai = Actions.getActionByName(config.getString(path + ".actions." + a + ".type"));
                                ai.loadArguments(config, path + ".actions." + a + ".arguments");
                                rex.addAction(ai);
                            }
                        }
                        if(config.contains(path + ".conditions")){
                            for(String c : config.getConfigurationSection(path + ".conditions").getKeys(false)){
                                ConditionInterface ci = Conditions.getConditionByName(config.getString(path + ".conditions." + c + ".type"));
                                ci.loadArguments(config, path + ".conditions." + c + ".arguments");
                                rex.addCondition(ci);
                            }
                        }
                        
                        if(config.contains(path + ".isTriggeredPerPlayer"))
                            rex.setTriggerPerPlayer(config.getBoolean(path + ".isTriggeredPerPlayer"));
                        if(config.contains(path + ".triggerCount"))
                            rex.setTriggerCount(config.getInt(path + ".triggerCount"));
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
        return new ArrayList<>(regions.values());
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
        return new ArrayList<>(nodes.values());
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
    
    public void displayMenu(MinigamePlayer viewer, Menu previous){
        Menu rm = new Menu(6, "Regions and Nodes", viewer);
        List<MenuItem> items = new ArrayList<>(regions.size());
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
        if(previous != null)
            rm.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), rm.getSize() - 9);
        rm.displayMenu(viewer);
    }
    
    
    
    @Override
    public void addEditMenuOptions(Menu menu) {
        final MenuItemCustom c = new MenuItemCustom("Regions and Nodes", Material.DIAMOND_BLOCK);
        final Menu fmenu = menu;
        c.setClick(object -> {
            displayMenu(c.getContainer().getViewer(), fmenu);
            return null;
        });
        menu.addItem(c);
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }
}
