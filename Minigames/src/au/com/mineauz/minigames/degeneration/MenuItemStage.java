package au.com.mineauz.minigames.degeneration;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.degeneration.DegenerationStage.StartType;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.properties.ChangeListener;
import au.com.mineauz.minigames.properties.ObservableValue;

class MenuItemStage extends MenuItem implements ChangeListener<Object> {
	private final DegenerationModule module;
	private final DegenerationStage stage;
	
	public MenuItemStage(DegenerationStage stage, DegenerationModule module) {
		super("Stage", Material.CHEST);

		this.module = module;
		this.stage = stage;
		
		stage.degeneratorType().addListener(this);
		stage.delay().addListener(this);
		stage.interval().addListener(this);
		stage.maxRuntime().addListener(this);
		stage.startType().addListener(this);
		
		updateDescription();
	}
	
	private void updateDescription() {
		List<String> description = Lists.newArrayList();
		
		String startDesc;
		switch (stage.getStartType()) {
		default:
		case Immediate:
			startDesc = "Immediately";
			break;
		case AfterAll:
			startDesc = "After All";
			break;
		case AfterLast:
			startDesc = "After Last";
			break;
		}
		description.add(ChatColor.YELLOW + "Starts: " + ChatColor.GRAY + startDesc);
		description.add(ChatColor.YELLOW + "Delay: " + ChatColor.GRAY + stage.getDelay() + "s");
		description.add(ChatColor.YELLOW + "Interval: " + ChatColor.GRAY + stage.getInterval() + "s");
		if (stage.getMaxRuntime() == 0) {
			description.add(ChatColor.YELLOW + "Max Time: " + ChatColor.GRAY + "No Limit");
		} else {
			description.add(ChatColor.YELLOW + "Max Time: " + ChatColor.GRAY + stage.getMaxRuntime() + "s");
		}
		
		description.add(ChatColor.YELLOW + "Type: " + ChatColor.GRAY + WordUtils.capitalizeFully(stage.getDegeneratorType()));
		
		description.add("Left click to open");
		description.add("Shift + Right click to remove");
		
		setDescription(description);
	}
	
	@Override
	public void update() {
		super.update();
		updateDescription();
	}
	
	@Override
	public void onValueChange(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
		updateDescription();
	}
	
	private String formatArea(Location min, Location max) {
		return String.format("(%d,%d,%d) - (%d,%d,%d)", min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
	}
	
	private void populateMenu(final Menu menu) {
		menu.addItem(new MenuItemEnum<StartType>("Starts When", Material.DIODE, stage.startType(), StartType.class));
		menu.addItem(new MenuItemTime("Start Delay", Material.WATCH, stage.delay(), 0, Integer.MAX_VALUE));
		menu.addItem(new MenuItemTime("Interval", Material.WATCH, stage.interval(), 1, Integer.MAX_VALUE));
		menu.addItem(new MenuItemTime("Max Time", "Maximum time the;degenerator will run for.;A value of 0 means no limit", Material.WATCH, stage.maxRuntime(), 0, Integer.MAX_VALUE));
		menu.addItem(new MenuItemNewLine());
		
		final MenuItem changeAreaItem = new MenuItem("Change Area", Material.FENCE);
		changeAreaItem.setDescription(ChatColor.GREEN + formatArea(stage.getMinCorner(), stage.getMaxCorner()));
		
		changeAreaItem.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				Callback<String> callback = new Callback<String>() {
					@Override
					public String getValue() {
						throw new UnsupportedOperationException();
					}
					
					@Override
					public void setValue(String value) {
						Location[] area = module.getArea(value);
						stage.setRegion(area[0], area[1]);
						changeAreaItem.setDescription(ChatColor.GREEN + formatArea(stage.getMinCorner(), stage.getMaxCorner()));
					}
				};
				
				Menu areaMenu = module.createAreaPickMenu(callback);
				areaMenu.displayMenu(player);
			}
		});
		
		menu.addItem(changeAreaItem);
		
		final MenuItem item = new MenuItem("Degen Type", ChatColor.GREEN + WordUtils.capitalizeFully(stage.getDegeneratorType()), Material.PAPER);
		stage.degeneratorType().addListener(new ChangeListener<String>() {
			@Override
			public void onValueChange(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// Delay to prevent CME
				Bukkit.getScheduler().runTask(Minigames.plugin, new Runnable() {
					@Override
					public void run() {
						menu.clear();
						populateMenu(menu);
						menu.refresh();
					}
				});
			}
		});
		
		item.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				Menu selectMenu = Degenerators.createSelectionMenu(stage.degeneratorType());
				selectMenu.displayMenu(player);
			}
		});
		menu.addItem(item);
		
		menu.addItem(new MenuItemNewLine());
		
		DegeneratorSettings settings = stage.getDegenSettings();
		if (settings != null) {
			settings.addMenuItems(menu);
		}
	}
	
	@Override
	protected void onClick(MinigamePlayer player) {
		Menu menu = new Menu(5, "Edit Degeneration Stage");
		populateMenu(menu);
		menu.displayMenu(player);
	}
	
	@Override
	protected void onShiftRightClick(MinigamePlayer player) {
		module.getStages().remove(stage);
		remove();
	}
}
