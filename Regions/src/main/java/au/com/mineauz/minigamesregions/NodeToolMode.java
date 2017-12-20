package au.com.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;

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
			m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, player.getMenu()), m.getSize() - 9);
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
			nodeMenu.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, m), nodeMenu.getSize() - 9);
			
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
				player.sendMessage("Added new node to " + minigame + " called " + name, null);
			} else {
				node.setLocation(loc);
				player.sendMessage("Edited node " + name + " in " + minigame, null);
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
			player.sendMessage("Added new node to " + minigame + " called " + name, null);
		} else {
			node.setLocation(player.getLocation());
			player.sendMessage("Edited node " + name + " in " + minigame, null);
			Main.getPlugin().getDisplayManager().update(node);
		}
	}

	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		RegionModule mod = RegionModule.getMinigameModule(minigame);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
		if(mod.hasNode(name)){
			Main.getPlugin().getDisplayManager().show(mod.getNode(name), player);
			player.sendMessage("Selected node '" + name + "' visually.", null);
		}
		else{
			player.sendMessage("No node exists by the name '" + name + "'", "error");
		}
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		RegionModule mod = RegionModule.getMinigameModule(minigame);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
		if(mod.hasNode(name)){
			Main.getPlugin().getDisplayManager().hide(mod.getNode(name), player);
			player.sendMessage("Deselected node '" + name + "'", null);
		}
		else{
			player.sendMessage("No node exists by the name '" + name + "'", "error");
		}
	}

}
