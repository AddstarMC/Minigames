package au.com.mineauz.minigames.degeneration;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.degeneration.DegenerationStage.StartType;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;

public class DegenerationModule extends MinigameModule {
	private final List<DegenerationStage> stages;
	private final Map<String, Location[]> areas;
	
	// Runtime
	private BukkitTask runTask;
	
	private List<DegenerationStage> remainingStages;
	private Map<DegenerationStage, DegenState> activeStages;
	
	private Map<DegenerationStage, DegenerationStage> waiting;
	
	public DegenerationModule(Minigame minigame) {
		super(minigame);
		stages = Lists.newArrayList();
		areas = Maps.newHashMap();
	}
	
	@Override
	public String getName() {
		return "Degeneration";
	}
	
	@Override
	public boolean useSeparateConfig() {
		return false;
	}
	
	@Override
	public ConfigPropertyContainer getProperties() {
		return null;
	}
	
	@Override
	public void addEditMenuOptions(Menu menu) {
		MenuItem button = new MenuItem("Degeneration Settings", Material.TNT);
		button.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				showSettingsMenu(player);
			}
		});
		
		menu.addItem(button);
	}
	
	public void showSettingsMenu(MinigamePlayer player) {
		Menu degenMenu = new Menu(5, "Degeneration Settings");
		
		menuAddStages(degenMenu);
		
		degenMenu.setControlItem(new MenuItemAddStage(this), 4);
		
		degenMenu.displayMenu(player);
	}
	
	private void menuAddStages(Menu menu) {
		for (DegenerationStage stage : stages) {
			menu.addItem(new MenuItemStage(stage, this));
		}
	}
	
	
	public List<DegenerationStage> getStages() {
		return stages;
	}
	
	public Location[] getArea(String name) {
		return areas.get(name.toLowerCase());
	}
	
	public void setArea(String name, Location point1, Location point2) {
		areas.put(name.toLowerCase(), MinigameUtils.getMinMaxSelection(point1, point2));
	}
	
	public Set<String> getAreas() {
		return Collections.unmodifiableSet(areas.keySet());
	}
	
	@Override
	public void save(FileConfiguration config) {
		ConfigurationSection root = config.getConfigurationSection(getMinigame().getName(false));
		if (root == null) {
			root = config.createSection(getMinigame().getName(false));
		}
		save(root.createSection("degen"));
	}
	
	public void save(ConfigurationSection section) {
		// Save the stages
		int index = 0;
		for (DegenerationStage stage : stages) {
			stage.save(section.createSection("stages." + index));
			++index;
		}
		
		// Save the areas
		for (Entry<String, Location[]> area : areas.entrySet()) {
			ConfigurationSection areaSection = section.createSection("areas." + area.getKey());
			MinigameUtils.saveShortLocation(areaSection.createSection("min"), area.getValue()[0]);
			MinigameUtils.saveShortLocation(areaSection.createSection("max"), area.getValue()[1]);
		}
	}
	
	@Override
	public void load(FileConfiguration config) {
		ConfigurationSection root = config.getConfigurationSection(getMinigame().getName(false));
		if (root.contains("degen")) {
			load(root.getConfigurationSection("degen"));
		}
	}
	
	public void load(ConfigurationSection section) {
		// Load the stages
		stages.clear();
		ConfigurationSection stagesSection = section.getConfigurationSection("stages");
		if (stagesSection != null) {
			for (String key : stagesSection.getKeys(false)) {
				DegenerationStage stage = new DegenerationStage();
				stage.load(stagesSection.getConfigurationSection(key));
				
				stages.add(stage);
			}
		}
		
		// Load the areas
		ConfigurationSection areasSection = section.getConfigurationSection("areas");
		if (areasSection != null) {
			for (String key : areasSection.getKeys(false)) {
				ConfigurationSection areaSection = areasSection.getConfigurationSection(key);
				Location[] locations = new Location[2];
				locations[0] = MinigameUtils.loadShortLocation(areaSection.getConfigurationSection("min"));
				locations[1] = MinigameUtils.loadShortLocation(areaSection.getConfigurationSection("max"));
				
				areas.put(key, locations);
			}
		}
	}
	
	public void start() {
		System.out.println("Starting degeneration module");
		remainingStages = Lists.newArrayList(stages);
		activeStages = Maps.newIdentityHashMap();
		waiting = Maps.newIdentityHashMap();
		
		// Generate map of all degenerators that are dependant on just one other
		DegenerationStage last = null;
		for (DegenerationStage stage : stages) {
			if (stage.getStartType() == StartType.AfterLast && last != null) {
				waiting.put(stage, last);
			}
			last = stage;
		}
		
		runTask = Bukkit.getScheduler().runTaskTimer(Minigames.plugin, new DegenTask(), 20, 20);
	}
	
	public void stop() {
		System.out.println("Stopping degeneration module");
		if (runTask != null) {
			runTask.cancel();
			runTask = null;
			
			// Cleanup
			activeStages.clear();
			remainingStages.clear();
			waiting.clear();
		}
	}
	
	private boolean isDone() {
		return (remainingStages.isEmpty() && activeStages.isEmpty());
	}
	
	private void activateNext() {
		Iterator<DegenerationStage> it = remainingStages.iterator();
		
		while (it.hasNext()) {
			DegenerationStage stage = it.next();
			
			boolean add = false;
			// Immediate mode ones just activate immediately
			if (stage.getStartType() == StartType.Immediate) {
				add = true;
			// After all mode ones need no other active degeneraters
			} else if (stage.getStartType() == StartType.AfterAll) {
				if (activeStages.isEmpty()) {
					add = true;
				} else {
					break;
				}
			// After Last mode ones need their dependant to be finished
			} else {
				DegenerationStage dependant = waiting.get(stage);
				if (!activeStages.containsKey(dependant)) {
					add = true;
				} else {
					break;
				}
			}
			
			if (add) {
				DegenState state = new DegenState(stage);
				activeStages.put(stage, state);
				System.out.println("Activating stage " + stage);
				it.remove();
			} else {
				break;
			}
		}
	}
	
	private void doDegenStep() {
		Iterator<Entry<DegenerationStage, DegenState>> it = activeStages.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<DegenerationStage, DegenState> entry = it.next();
			
			DegenerationStage stage = entry.getKey();
			DegenState state = entry.getValue();
			
			if (state.counter >= state.nextTrigger) {
				System.out.println("Triggering stage " + stage);
				state.nextTrigger = state.counter + stage.getInterval();
				
				if (!state.degenerator.isFinished()) {
					Iterable<Block> blocks = state.degenerator.next();
					degenerateBlocks(blocks);
				} else {
					System.out.println("Degen finished. stage " + stage);
					it.remove();
				}
			}
			
			if (stage.getMaxRuntime() != 0 && state.counter >= stage.getMaxRuntime()) {
				System.out.println("Degen out of time. stage " + stage);
				// This one is done
				it.remove();
			}
			++state.counter;
		}
	}
	
	private void degenerateBlocks(Iterable<Block> blocks) {
		for (Block block : blocks) {
			if (block.isEmpty()) {
				continue;
			}
			
			getMinigame().getBlockRecorder().addBlock(block, null);
			block.setType(Material.AIR);
		}
	}
	
	private class DegenTask implements Runnable {
		@Override
		public void run() {
			if (isDone()) {
				stop();
			}
			
			// Activate next degenerators if possible
			activateNext();
			
			doDegenStep();
		}
	}
	
	private class DegenState {
		public int counter;
		public int nextTrigger;
		
		public final Degenerator degenerator;
		
		public DegenState(DegenerationStage stage) {
			counter = 0;
			nextTrigger = stage.getDelay();
			
			degenerator = Degenerators.create(stage.getDegeneratorType(), stage.getMinCorner(), stage.getMaxCorner());
		}
	}
	
	public Menu createAreaPickMenu(final Callback<String> callback) {
		Menu menu = new Menu(5, "Choose Area");
		
		if (areas.isEmpty()) {
			menu.addItem(new MenuItem("There are no areas defined", "Use the 'Degeneration Area';mode of the minigame tool;to create an area.", Material.BARRIER));
		} else {
			for (final String area : areas.keySet()) {
				Location[] box = areas.get(area);
				String description = ChatColor.GREEN + String.format("(%d,%d,%d) - (%d,%d,%d)", box[0].getBlockX(), box[0].getBlockY(), box[0].getBlockZ(), box[1].getBlockX(), box[1].getBlockY(), box[1].getBlockZ());
				MenuItem item = new MenuItem(WordUtils.capitalizeFully(area), description, Material.FENCE);
				item.setClickHandler(new IMenuItemClick() {
					@Override
					public void onClick(MenuItem menuItem, MinigamePlayer player) {
						player.showPreviousMenu();
						callback.setValue(area);
					}
				});
				
				menu.addItem(item);
			}
			
			menu.setControlItem(new MenuItem("Tip", ChatColor.YELLOW + "Use the 'Degeneration Area';mode of the minigame tool;to create and modify areas.", Material.STAINED_GLASS_PANE), 4);
		}
		
		return menu;
	}
}
