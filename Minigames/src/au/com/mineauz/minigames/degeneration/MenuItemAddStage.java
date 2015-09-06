package au.com.mineauz.minigames.degeneration;

import org.bukkit.Location;
import org.bukkit.Material;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;

class MenuItemAddStage extends MenuItem {
	private final DegenerationModule module;
	
	public MenuItemAddStage(DegenerationModule module) {
		super ("Add Stage", Material.ITEM_FRAME);
		
		this.module = module;
	}
	
	@Override
	protected void onClick(MinigamePlayer player) {
		Callback<String> areaCallback = new Callback<String>() {
			@Override
			public String getValue() {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void setValue(String value) {
				Location[] area = module.getArea(value);
				DegenerationStage stage = new DegenerationStage(area[0], area[1]);
				
				module.getStages().add(stage);
				getContainer().addItem(new MenuItemStage(stage, module));
				getContainer().refresh();
			}
		};
		
		Menu pickMenu = module.createAreaPickMenu(areaCallback);
		pickMenu.displayMenu(player);
	}
}
