package au.com.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
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
		final Menu m = new Menu(2, "Node Selection");
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
			RegionModule module = tool.getMinigame().getModule(RegionModule.class);
			
			Menu nodeMenu = new Menu(6, "Nodes");
			List<MenuItem> items = new ArrayList<MenuItem>();
			
			for(final Node node : module.getNodes()){
				final MenuItem item = new MenuItem(node.getName(), Material.STONE_BUTTON);
				
				// Set the node and go back to the main menu
				item.setClickHandler(new IMenuItemClick() {
					@Override
					public void onClick(MenuItem menuItem, MinigamePlayer player) {
						ftool.changeSetting("Node", node.getName());
						player.showPreviousMenu();
					}
				});
				
				items.add(item);
			}
			
			nodeMenu.addItems(items);
			
			m.addItem(new MenuItemSubMenu("Edit Node", Material.STONE_BUTTON, nodeMenu));
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
			RegionModule mod = minigame.getModule(RegionModule.class);
			String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
			
			Location loc = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
			Node node = mod.getNode(name);
			if (node == null) {
				node = new Node(name, loc);
				mod.addNode(name, node);
				player.sendMessage("Added new node to " + minigame + " called " + name, MessageType.Normal);
			} else {
				node.setLocation(loc);
				player.sendMessage("Edited node " + name + " in " + minigame, MessageType.Normal);
				Main.getPlugin().getDisplayManager().update(node);
			}
		}
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
		RegionModule mod = minigame.getModule(RegionModule.class);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
		
		Node node = mod.getNode(name);
		if (node == null) {
			node = new Node(name, player.getLocation());
			mod.addNode(name, node);
			player.sendMessage("Added new node to " + minigame + " called " + name, MessageType.Normal);
		} else {
			node.setLocation(player.getLocation());
			player.sendMessage("Edited node " + name + " in " + minigame, MessageType.Normal);
			Main.getPlugin().getDisplayManager().update(node);
		}
	}

	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		RegionModule mod = minigame.getModule(RegionModule.class);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
		if(mod.hasNode(name)){
			Main.getPlugin().getDisplayManager().show(mod.getNode(name), player);
			player.sendMessage("Selected node '" + name + "' visually.", MessageType.Normal);
		}
		else{
			player.sendMessage("No node exists by the name '" + name + "'", MessageType.Error);
		}
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		RegionModule mod = minigame.getModule(RegionModule.class);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Node");
		if(mod.hasNode(name)){
			Main.getPlugin().getDisplayManager().hide(mod.getNode(name), player);
			player.sendMessage("Deselected node '" + name + "'", MessageType.Normal);
		}
		else{
			player.sendMessage("No node exists by the name '" + name + "'", MessageType.Error);
		}
	}

}
