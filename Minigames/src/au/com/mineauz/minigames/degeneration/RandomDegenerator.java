package au.com.mineauz.minigames.degeneration;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigames.properties.types.IntegerProperty;

import com.google.common.collect.Lists;

public class RandomDegenerator extends Degenerator {
	private final Random random;
	
	private List<Block> blocks;
	
	public RandomDegenerator(Location min, Location max) {
		super(min, max);
		random = new Random();
	}
	
	@Override
	public String getName() {
		return "random";
	}
	
	@Override
	public String getDescription() {
		return "Removes a random selection of blocks from the region each step";
	}

	@Override
	public Iterable<Block> next(DegeneratorSettings rawSettings) {
		Settings settings = (Settings)rawSettings;
		
		// Generate the random block list
		if (blocks == null) {
			blocks = Lists.newArrayList();
			
			for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
				for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
						blocks.add(min.getWorld().getBlockAt(x, y, z));
					}
				}
			}
			
			Collections.shuffle(blocks, random);
		}
		
		List<Block> chosenBlocks = Lists.newArrayList();
		int toGet = Math.min(blocks.size(), settings.getRandomBlocks(random));
		
		for (int i = 0; i < toGet; ++i) {
			chosenBlocks.add(blocks.remove(blocks.size()-1));
		}
		
		return chosenBlocks;
	}

	@Override
	public boolean isFinished() {
		return (blocks != null && blocks.isEmpty());
	}
	
	public static class Settings extends DegeneratorSettings {
		private final IntegerProperty min;
		private final IntegerProperty max;
		
		public Settings() {
			min = new IntegerProperty(2, "min-removed");
			max = new IntegerProperty(6, "max-removed");
			
			properties.addProperty(min);
			properties.addProperty(max);
		}
		
		@Override
		public void addMenuItems(Menu menu) {
			menu.addItem(new MenuItemInteger("Minimum Blocks Per Step", Material.STEP, min, 0, Integer.MAX_VALUE));
			menu.addItem(new MenuItemInteger("Maximum Blocks Per Step", Material.STEP, max, 0, Integer.MAX_VALUE));
		}
		
		public int getMinBlocks() {
			return min.getValue();
		}
		
		public void setMinBlocks(int min) {
			this.min.setValue(min);
		}
		
		public Property<Integer> minBlocks() {
			return min;
		}
		
		public int getMaxBlocks() {
			return max.getValue();
		}
		
		public void setMaxBlocks(int max) {
			this.max.setValue(max);
		}
		
		public Property<Integer> maxBlocks() {
			return max;
		}
		
		int getRandomBlocks(Random random) {
			if (max.getValue() > min.getValue()) {
				return random.nextInt(max.getValue() - min.getValue()) + min.getValue();
			} else {
				return min.getValue();
			}
		}
	}

}
