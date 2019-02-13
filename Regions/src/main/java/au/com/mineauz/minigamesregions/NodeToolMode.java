package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class NodeToolMode implements ToolMode {

    @Override
    public String getName() {
        return "NODE";
    }

    @Override
    public String getDisplayName() {
        return "Node Creation";
    }

    @Override
    public String getDescription() {
        return "Creates a node where;you are standing;on right click";
    }

    @Override
    public Material getIcon() {
        return Material.STONE_BUTTON;
    }

    @Override
    public void onSetMode(final MinigamePlayer player, MinigameTool tool) {
        tool.addSetting("Node", "None");
        final Menu m = new Menu(2, "Node Selection", player);
        if(player.isInMenu()){
            m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), player.getMenu()), m.getSize() - 9);
        }
        final MinigameTool ftool = tool;
        m.addItem(new MenuItemString("Node Name", Material.PAPER, new Callback<String>() {
            
            @Override
            public void setValue(String value) {
                ftool.changeSetting("Node", value);
            }
            
            @Override
            public String getValue() {
                return ftool.getSetting("Node");
            }
        }));
        
        if (tool.getMinigame() != null) {
            // Node selection menu
            RegionModule module = RegionModule.getMinigameModule(tool.getMinigame());
            
            Menu nodeMenu = new Menu(6, "Nodes", player);
            List<MenuItem> items = new ArrayList<>();
            
            for(final Node node : module.getNodes()){
                MenuItemCustom item = new MenuItemCustom(node.getName(), Material.STONE_BUTTON);
                
                // Set the node and go back to the main menu
                item.setClick(object -> {
                    ftool.changeSetting("Node", node.getName());
                    m.displayMenu(player);

                    return object;
                });
                
                items.add(item);
            }
            
            nodeMenu.addItems(items);
            nodeMenu.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), m), nodeMenu.getSize() - 9);
            
            m.addItem(new MenuItemPage("Edit Node", Material.STONE_BUTTON, nodeMenu));
        }
        m.displayMenu(player);
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
        tool.removeSetting("Node");
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            RegionModule mod = RegionModule.getMinigameModule(minigame);
            String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
            
            Location loc = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
            Node node = mod.getNode(name);
            if (node == null) {
                node = new Node(name, loc);
                mod.addNode(name, node);
                player.sendInfoMessage("Added new node to " + minigame + " called " + name);
            } else {
                node.setLocation(loc);
                player.sendInfoMessage("Edited node " + name + " in " + minigame);
                Main.getPlugin().getDisplayManager().update(node);
            }
        }
    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
        
        Node node = mod.getNode(name);
        if (node == null) {
            node = new Node(name, player.getLocation());
            mod.addNode(name, node);
            player.sendInfoMessage("Added new node to " + minigame + " called " + name);
        } else {
            node.setLocation(player.getLocation());
            player.sendInfoMessage("Edited node " + name + " in " + minigame);
            Main.getPlugin().getDisplayManager().update(node);
        }
    }
    
    @Override
    public void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event) {
    
    }
    
    @Override
    public void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event) {
    
    }
    
    @Override
    public void select(MinigamePlayer player, Minigame minigame, Team team) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
        if(mod.hasNode(name)){
            Main.getPlugin().getDisplayManager().show(mod.getNode(name), player);
            player.sendInfoMessage("Selected node '" + name + "' visually.");
        }
        else{
            player.sendMessage("No node exists by the name '" + name + "'", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
        if(mod.hasNode(name)){
            Main.getPlugin().getDisplayManager().hide(mod.getNode(name), player);
            player.sendInfoMessage("Deselected node '" + name + "'");
        }
        else{
            player.sendMessage("No node exists by the name '" + name + "'", MinigameMessageType.ERROR);
        }
    }

}
