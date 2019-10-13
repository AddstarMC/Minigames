package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemSaveMinigame;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import au.com.mineauz.minigamesregions.menuitems.MenuItemNode;
import au.com.mineauz.minigamesregions.menuitems.MenuItemRegion;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RegionNodeEditToolMode implements ToolMode {

    @Override
    public String getName() {
        return "REGION_AND_NODE_EDITOR";
    }

    @Override
    public String getDisplayName() {
        return "Region and Node editor";
    }

    @Override
    public String getDescription() {
        return "Allows you to simply;edit regions and nodes;with right click";
    }

    @Override
    public Material getIcon() {
        return Material.WRITABLE_BOOK;
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
        if (tool.getMinigame() != null) {
            Main.getPlugin().getDisplayManager().hideAll(player.getPlayer());
            Main.getPlugin().getDisplayManager().showAll(tool.getMinigame(), player);
        }
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
        if (tool.getMinigame() != null) {
            Main.getPlugin().getDisplayManager().hideAll(player.getPlayer());
        }
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        Vector origin = event.getPlayer().getEyeLocation().toVector();
        Vector direction = event.getPlayer().getEyeLocation().getDirection().normalize();
        
        // Prepare region and node bounds for efficiency
        RegionModule module = RegionModule.getMinigameModule(minigame);
        Map<Region, Vector[]> regionBBs = Maps.newIdentityHashMap();
        for (Region region : module.getRegions()) {
            Vector point1 = region.getFirstPoint().toVector();
            Vector point2 = region.getSecondPoint().toVector();
            
            regionBBs.put(region, new Vector[] {Vector.getMinimum(point1, point2), Vector.getMaximum(point1, point2).add(new Vector(1,1,1))});
        }
        
        Map<Node, Vector> nodeLocs = Maps.newIdentityHashMap();
        for (Node node : module.getNodes()) {
            nodeLocs.put(node, node.getLocation().toVector());
        }
        
        Set<Object> hits = Sets.newIdentityHashSet();
        
        // Raytrace the view vector
        for (double dist = 0; dist < 10; dist += 0.25) {
            Vector pos = origin.clone().add(direction.clone().multiply(dist));
            
            for (Entry<Region, Vector[]> region : regionBBs.entrySet()) {
                if (pos.isInAABB(region.getValue()[0], region.getValue()[1])) {
                    hits.add(region.getKey());
                }
            }
            
            for (Entry<Node, Vector> node : nodeLocs.entrySet()) {
                if (pos.isInSphere(node.getValue(), 0.4)) {
                    hits.add(node.getKey());
                }
            }
        }
        
        // Tracing done, now show the results
        if (hits.size() == 1) {
            openMenu(player, minigame, Iterables.getFirst(hits, null));
        } else if (!hits.isEmpty()) {
            openChooseMenu(player, module, hits);
        }
    }
    
    @Override
    public void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event) {
    
    }
    
    @Override
    public void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event) {
    
    }
    
    private void openMenu(MinigamePlayer player, Minigame minigame, Object hit) {
        Menu menu = null;
        if (hit instanceof Region) {
            player.sendMessage("Editing region " + ((Region)hit).getName(), MinigameMessageType.INFO);
            menu = MenuItemRegion.createMenu(player, null, (Region)hit);
        } else if (hit instanceof Node) {
            player.sendMessage("Editing node " + ((Node)hit).getName(), MinigameMessageType.INFO);
            menu = MenuItemNode.createMenu(player, null, (Node)hit);
        }
        
        menu.addItem(new MenuItemSaveMinigame("Save", MenuUtility.getSaveMaterial(), minigame), menu.getSize() - 9);
        
        menu.displayMenu(player);
    }
    
    private void openChooseMenu(MinigamePlayer player, RegionModule module, Set<Object> objects) {
        Menu menu = new Menu(3, "Choose Region or Node", player);
        
        StringBuilder options = new StringBuilder();
        for(Object object : objects) {
            if (options.length() != 0) {
                options.append(", ");
            }
            
            if (object instanceof Region) {
                options.append(((Region)object).getName());
                MenuItemRegion item = new MenuItemRegion(((Region)object).getName(), Material.CHEST, (Region)object, module);
                menu.addItem(item);
            } else if (object instanceof Node) {
                options.append(((Node)object).getName());
                MenuItemNode item = new MenuItemNode(((Node)object).getName(), Material.STONE_BUTTON, (Node)object, module);
                menu.addItem(item);
            }
        }
        
        menu.addItem(new MenuItemSaveMinigame("Save", MenuUtility.getSaveMaterial(), module.getMinigame()), menu.getSize() - 9);
        
        menu.displayMenu(player);
        
        player.sendMessage("Multiple regions/nodes selected: " + options.toString(), MinigameMessageType.INFO);
    }

    @Override
    public void select(MinigamePlayer player, Minigame minigame, Team team) {
        Main.getPlugin().getDisplayManager().showAll(minigame, player);
    }

    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        Main.getPlugin().getDisplayManager().hideAll(player.getPlayer());
    }
}
