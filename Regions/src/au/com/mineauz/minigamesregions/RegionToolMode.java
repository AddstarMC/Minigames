package au.com.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
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

public class RegionToolMode implements ToolMode {

	@Override
	public String getName() {
		return "REGION";
	}

	@Override
	public String getDisplayName() {
		return "Region Selection";
	}

	@Override
	public String getDescription() {
		return "Selects an area;for a region.;Create via left click";
	}

	@Override
	public Material getIcon() {
		return Material.DIAMOND_BLOCK;
	}

	@Override
	public void onSetMode(final MinigamePlayer player, MinigameTool tool) {
		tool.addSetting("Region", "None");
		final Menu m = new Menu(2, "Region Selection");
		
		final MinigameTool ftool = tool;
		m.addItem(new MenuItemString("Region Name", Material.PAPER, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				ftool.changeSetting("Region", value);
			}
			
			@Override
			public String getValue() {
				return ftool.getSetting("Region");
			}
		}));
		
		if (tool.getMinigame() != null) {
			// Node selection menu
			RegionModule module = tool.getMinigame().getModule(RegionModule.class);
			
			Menu regionMenu = new Menu(6, "Regions");
			List<MenuItem> items = new ArrayList<MenuItem>();
			
			for(final Region region : module.getRegions()){
				final MenuItem item = new MenuItem(region.getName(), Material.CHEST);
				
				// Set the node and go back to the main menu
				item.setClickHandler(new IMenuItemClick() {
					@Override
					public void onClick(MenuItem menuItem, MinigamePlayer player) {
						ftool.changeSetting("Region", region.getName());
						
						player.showPreviousMenu();
					}
				});
				
				items.add(item);
			}
			
			regionMenu.addItems(items);
			
			m.addItem(new MenuItemSubMenu("Edit Region", Material.CHEST, regionMenu));
		}
		m.displayMenu(player);
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
		tool.removeSetting("Region");
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(player.hasSelection()){
			String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
			RegionModule module = minigame.getModule(RegionModule.class);
			Region region = module.getRegion(name);
			
			if(region == null) {
				module.addRegion(name, new Region(name, player.getSelectionPoints()[0], player.getSelectionPoints()[1]));
				player.sendMessage("Created a new region in " + minigame + " called " + name, MessageType.Normal);
				player.clearSelection();
			}
			else{
				region.updateRegion(player.getSelectionPoints()[0], player.getSelectionPoints()[1]);
				Main.getPlugin().getDisplayManager().update(region);
				player.sendMessage("Updated region " + name + " in " + minigame, MessageType.Normal);
			}
		}
		else{
			player.sendMessage("You need to select a region with right click first!", MessageType.Error);
		}
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			player.addSelectionPoint(event.getClickedBlock().getLocation());
			if(player.hasSelection()){
				player.sendMessage("Selection complete, finalise with left click.", MessageType.Normal);
			}
		}
	}

	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		RegionModule mod = minigame.getModule(RegionModule.class);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
		if(mod.hasRegion(name)){
			Main.getPlugin().getDisplayManager().show(mod.getRegion(name), player);
			player.sendMessage("Selected the " + name + " region in " + minigame);
		}
		else{
			player.sendMessage("No region created by the name '" + name + "'", MessageType.Error);
		}
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		RegionModule mod = minigame.getModule(RegionModule.class);
		String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
		if(mod.hasRegion(name)){
			Main.getPlugin().getDisplayManager().hide(mod.getRegion(name), player);
			player.clearSelection();
			player.sendMessage("Deselected the region");
		}
		else{
			player.sendMessage("No region created by the name '" + name + "'", MessageType.Error);
		}
	}

}
