package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.display.IDisplayObject;
import au.com.mineauz.minigames.minigame.Minigame;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class RegionDisplayManager {
    private final Map<Player, Map<Region, IDisplayObject>> regionDisplays;
    private final Map<Player, Map<Node, IDisplayObject>> nodeDisplays;
    
    private final SetMultimap<Object, MinigamePlayer> activeWatchers;
    private final Map<Object, ArmorStand> nameDisplay;
    
    public RegionDisplayManager() {
        regionDisplays = Maps.newHashMap();
        nodeDisplays = Maps.newHashMap();
        
        activeWatchers = HashMultimap.create();
        nameDisplay = Maps.newIdentityHashMap();
    }
    
    private void showInfo(Region region, MinigamePlayer player) {
        activeWatchers.put(region, player);
        
        ArmorStand stand = nameDisplay.get(region);
        if (stand == null) {
            Location midPoint = region.getFirstPoint().clone().add(region.getSecondPoint()).add(1,1,1).multiply(0.5).subtract(0, 1.4, 0);
            stand = region.getFirstPoint().getWorld().spawn(midPoint, ArmorStand.class);
            stand.setGravity(false);
            stand.setSmall(true);
            stand.setVisible(false);
            stand.setCustomNameVisible(true);
            
            nameDisplay.put(region, stand);
        }
        
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.BLUE);
        info.append("Region: ");
        info.append(ChatColor.WHITE);
        info.append(region.getName());
        info.append("Enabled: "+ region.getEnabled());
        stand.setCustomName(info.toString());
    }
    
    private void showInfo(Node node, MinigamePlayer player) {
        activeWatchers.put(node, player);
        
        ArmorStand stand = nameDisplay.get(node);
        if (stand == null) {
            stand = node.getLocation().getWorld().spawn(node.getLocation().clone().subtract(0, 0.75, 0), ArmorStand.class);
            stand.setGravity(false);
            stand.setSmall(true);
            stand.setVisible(false);
            stand.setCustomNameVisible(true);
            
            nameDisplay.put(node, stand);
        }
        
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.RED);
        info.append("Node: ");
        info.append(ChatColor.WHITE);
        info.append(node.getName());
        
        // TODO: Add more info
        stand.setCustomName(info.toString());
    }
    
    private void hideInfo(Object object, MinigamePlayer player) {
        activeWatchers.remove(object, player);
        if (activeWatchers.get(object).isEmpty()) {
            ArmorStand stand = nameDisplay.remove(object);
            if (stand != null)
                stand.remove();
        }
    }
    
    public void show(Region region, MinigamePlayer player) {
        Map<Region, IDisplayObject> regions = regionDisplays.computeIfAbsent(player.getPlayer(), k -> Maps.newIdentityHashMap());

        Location[] corners = MinigameUtils.getMinMaxSelection(region.getFirstPoint(), region.getSecondPoint());
        
        IDisplayObject display = Minigames.getPlugin().display.displayCuboid(player.getPlayer(), corners[0], corners[1].add(1, 1, 1));
        display.show();
        regions.put(region, display);
        
        showInfo(region, player);
    }
    
    public void show(Node node, MinigamePlayer player) {
        Map<Node, IDisplayObject> nodes = nodeDisplays.computeIfAbsent(player.getPlayer(), k -> Maps.newIdentityHashMap());

        IDisplayObject display = Minigames.getPlugin().display.displayPoint(player.getPlayer(), node.getLocation(), true);
        display.show();
        nodes.put(node, display);
        
        showInfo(node, player);
    }
    
    public void hide(Region region, MinigamePlayer player) {
        Map<Region, IDisplayObject> regions = regionDisplays.get(player.getPlayer());
        if (regions == null) {
            return;
        }
        
        IDisplayObject display = regions.remove(region);
        if (display != null) {
            display.remove();
        }
        
        hideInfo(region, player);
    }
    
    public void hide(Node node, MinigamePlayer player) {
        Map<Node, IDisplayObject> nodes = nodeDisplays.get(player.getPlayer());
        if (nodes == null) {
            return;
        }
        
        IDisplayObject display = nodes.remove(node);
        if (display != null) {
            display.remove();
        }
        
        hideInfo(node, player);
    }
    
    public void showAll(Minigame minigame, MinigamePlayer player) {
        RegionModule module = RegionModule.getMinigameModule(minigame);
        for (Region region : module.getRegions()) {
            show(region, player);
        }
        
        for (Node node : module.getNodes()) {
            show(node, player);
        }
    }
    
    public void hideAll(Minigame minigame, MinigamePlayer player) {
        RegionModule module = RegionModule.getMinigameModule(minigame);
        for (Region region : module.getRegions()) {
            hide(region, player);
        }
        
        for (Node node : module.getNodes()) {
            hide(node, player);
        }
    }
    
    public void hideAll(Player player) {
        MinigamePlayer mplayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
        Map<Region, IDisplayObject> regions = regionDisplays.remove(player);
        if (regions != null) {
            for (IDisplayObject display : regions.values()) {
                display.remove();
            }
            
            for (Region region : regions.keySet()) {
                hideInfo(region, mplayer);
            }
        }
        
        Map<Node, IDisplayObject> nodes = nodeDisplays.remove(player);
        if (nodes != null) {
            for (IDisplayObject display : nodes.values()) {
                display.remove();
            }
            
            for (Node node : nodes.keySet()) {
                hideInfo(node, mplayer);
            }
        }
    }
    
    public void shutdown() {
        for (ArmorStand stand : nameDisplay.values()) {
            stand.remove();
        }
    }
    
    public void update(Node node) {
        Set<MinigamePlayer> watchers = Sets.newHashSet(activeWatchers.get(node));
        
        ArmorStand stand = nameDisplay.remove(node);
        if (stand != null)
            stand.remove();
        
        for (MinigamePlayer player : watchers) {
            hide(node, player);
            show(node, player);
        }
    }
    
    public void update(Region region) {
        Set<MinigamePlayer> watchers = Sets.newHashSet(activeWatchers.get(region));
        
        ArmorStand stand = nameDisplay.remove(region);
        if (stand != null)
            stand.remove();
        
        for (MinigamePlayer player : watchers) {
            hide(region, player);
            show(region, player);
        }
    }
}
