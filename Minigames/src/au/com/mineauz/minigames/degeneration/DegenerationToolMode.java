package au.com.mineauz.minigames.degeneration;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;

public class DegenerationToolMode implements ToolMode {
	@Override
	public String getName() {
		return "DEGEN_AREA";
	}

	@Override
	public String getDisplayName() {
		return "Degeneration Area";
	}

	@Override
	public String getDescription() {
		return "Selects the degeneration;area with right click;finalise with left";
	}

	@Override
	public Material getIcon() {
		return Material.LAVA_BUCKET;
	}

	@Override
	public void onSetMode(MinigamePlayer player, final MinigameTool tool) {
		tool.addSetting("Area Name", "DegenArea");
		
		Menu menu = new Menu(2, "Area Selection");
		menu.addItem(new MenuItemString("Name", Material.PAPER, tool.getSettingProperty("Area Name")));
		
		if (tool.getMinigame() != null) {
			DegenerationModule module = tool.getMinigame().getModule(DegenerationModule.class);
			
			Menu areaMenu = new Menu(5, "Areas");
			
			for (final String area : module.getAreas()) {
				final MenuItem item = new MenuItem(area, Material.FENCE);
				
				item.setClickHandler(new IMenuItemClick() {
					@Override
					public void onClick(MenuItem menuItem, MinigamePlayer player) {
						tool.changeSetting("Area Name", area);
						
						player.showPreviousMenu();
					}
				});
				
				areaMenu.addItem(item);
			}
			
			menu.addItem(new MenuItemSubMenu("Edit Area", Material.CHEST, areaMenu));
		}
		
		menu.displayMenu(player);
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
	}

	@Override
	public void onLeftClick(final MinigamePlayer player, final Minigame minigame, Team team, PlayerInteractEvent event) {
		if (player.hasSelection()) {
			final DegenerationModule module = minigame.getModule(DegenerationModule.class);
			if (module == null) {
				player.sendMessage("This game does not support degeneration", MessageType.Error);
				return;
			}
			
			String name = MinigameUtils.getMinigameTool(player).getSetting("Area Name");
			
			boolean updated = (module.getArea(name) != null);
			module.setArea(name, player.getSelectionPoints()[0], player.getSelectionPoints()[1]);
			player.clearSelection();
			
			if (updated) {
				player.sendMessage("Updated degeneration area " + name + " in " + minigame, MessageType.Normal);
			} else {
				player.sendMessage("Added degeneration area " + name + " in " + minigame, MessageType.Normal);
			}
		} else {
			player.sendMessage("You need to select a region with right click first!", MessageType.Error);
		}
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			player.addSelectionPoint(event.getClickedBlock().getLocation());
			if (player.hasSelection()) {
				player.sendMessage("Selection complete, finalise with left click.", MessageType.Normal);
			}
		}
	}

	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		// TODO Auto-generated method stub
		
	}

}
