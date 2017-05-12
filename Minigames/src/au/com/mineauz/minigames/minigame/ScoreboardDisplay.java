package au.com.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemScoreboardSave;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatSettings;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredStat;

public class ScoreboardDisplay {
	public static final int defaultWidth = 3;
	public static final int defaultHeight = 3;
	private Location rootBlock;
	private MinigameStat stat;
	private StatValueField field;
	private ScoreboardOrder order;
	private Minigame minigame;
	private int width;
	private int height;
	private BlockFace facing;
	
	private StatSettings settings;
	
	private List<StoredStat> stats;
	
	private boolean needsLoad;
	
	public ScoreboardDisplay(Minigame minigame, int width, int height, Location rootBlock, BlockFace facing) {
		this.minigame = minigame;
		this.width = width;
		this.height = height;
		this.rootBlock = rootBlock;
		this.facing = facing;
		
		// Default values
		stat = MinigameStats.Wins;
		field = StatValueField.Total;
		order = ScoreboardOrder.DESCENDING;
		
		stats = Lists.newArrayListWithCapacity(width * height * 2);
		needsLoad = true;
	}

	public Location getRoot() {
		return rootBlock;
	}

	public MinigameStat getStat() {
		return stat;
	}
	
	public StatValueField getField() {
		return field;
	}
	
	public void setStat(MinigameStat stat, StatValueField field) {
		this.stat = stat;
		this.field = field;
	}

	public ScoreboardOrder getOrder() {
		return order;
	}

	public void setOrder(ScoreboardOrder order) {
		this.order = order;
		stats.clear();
		needsLoad = true;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Minigame getMinigame() {
		return minigame;
	}
	
	public BlockFace getFacing() {
		return facing;
	}
	
	public boolean needsLoad() {
		return needsLoad;
	}
	
	private List<Block> getSignBlocks(boolean onlySigns) {
		// Find the horizontal direction (going across the the signs, left to right) 
		BlockFace horizontal;
		
		switch(facing) {
		case NORTH:
			horizontal = BlockFace.WEST;
			break;
		case SOUTH:
			horizontal = BlockFace.EAST;
			break;
		case WEST:
			horizontal = BlockFace.SOUTH;
			break;
		case EAST:
			horizontal = BlockFace.NORTH;
			break;
		default:
			throw new AssertionError("Invalid facing " + facing);
		}
		
		List<Block> blocks = Lists.newArrayListWithCapacity(width * height);
		
		// Find the corner that is the top left part of the scoreboard
		Location min = rootBlock.clone();
		min.add(-horizontal.getModX() * (width / 2), -1, -horizontal.getModZ() * (width / 2));

		// Grab each sign of the scoreboards in order 
		Block block = min.getBlock();
		
		for(int y = 0; y < height; ++y) {
			Block start = block;
			for(int x = 0; x < width; ++x) {
				// Only add signs
				if (block.getType() == Material.WALL_SIGN || (!onlySigns && block.getType() == Material.AIR)) {
					blocks.add(block);
				}
				
				block = block.getRelative(horizontal);
			}
			block = start.getRelative(BlockFace.DOWN);
		}
		
		return blocks;
	}
	
	/**
	 * Updates all signs with the current values of the stats
	 */
	public void updateSigns() {
		settings = minigame.getSettings(stat);
		
		placeRootSign();
		
		List<Block> signs = getSignBlocks(true);
		
		int nextIndex = 0;
		for (Block sign : signs) {
			if (nextIndex <= stats.size() - 2) {
				updateSign(sign, nextIndex + 1, stats.get(nextIndex++), stats.get(nextIndex++));
			} else if (nextIndex <= stats.size() - 1) {
				updateSign(sign, nextIndex + 1, stats.get(nextIndex++));
			} else {
				clearSign(sign);
			}
		}
	}
	
	private void updateSign(Block block, int place, StoredStat... stats) {
		Preconditions.checkArgument(stats.length >= 1 && stats.length <= 2);
		
		Sign sign = (Sign)block.getState();
		sign.setLine(0, MinigameUtils.limitIgnoreCodes(ChatColor.GREEN + String.valueOf(place) + ". " + ChatColor.BLACK + stats[0].getPlayerName(), 15));
		sign.setLine(1, MinigameUtils.limitIgnoreCodes(ChatColor.BLUE + stat.displayValueSign(stats[0].getValue(), settings), 15));
		
		if (stats.length == 2) {
			++place;
			sign.setLine(2, MinigameUtils.limitIgnoreCodes(ChatColor.GREEN + String.valueOf(place) + ". " + ChatColor.BLACK + stats[1].getPlayerName(), 15));
			sign.setLine(3, MinigameUtils.limitIgnoreCodes(ChatColor.BLUE + stat.displayValueSign(stats[1].getValue(), settings), 15));
		} else {
			sign.setLine(2, "");
			sign.setLine(3, "");
		}
		
		sign.update();
	}
	
	public void displayMenu(MinigamePlayer player){
		final Menu setupMenu = new Menu(3, "Setup Scoreboard", player);
		
		StatSettings settings = minigame.getSettings(stat);
		final MenuItemCustom statisticChoice = new MenuItemCustom("Statistic", Material.BOOK_AND_QUILL);
		statisticChoice.setDescription(Arrays.asList(ChatColor.GREEN + settings.getDisplayName()));
		
		final MenuItemCustom fieldChoice = new MenuItemCustom("Statistic Field", Material.PAPER);
		fieldChoice.setDescription(Arrays.asList(ChatColor.GREEN + field.getTitle()));
		
		statisticChoice.setClick(new InteractionInterface() {
			@Override
			public Object interact(Object object) {
				Menu childMenu = MinigameStats.createStatSelectMenu(setupMenu, new Callback<MinigameStat>() {
					@Override
					public MinigameStat getValue() {
						throw new UnsupportedOperationException();
					}
					
					@Override
					public void setValue(MinigameStat value) {
						stat = value;
						StatSettings settings = minigame.getSettings(stat);
						statisticChoice.setDescription(Arrays.asList(ChatColor.GREEN + settings.getDisplayName()));
						
						// Check that the field is valid
						StatValueField first = null;
						boolean valid = false;
						for (StatValueField sfield : settings.getFormat().getFields()) {
							if (first == null) {
								first = sfield;
							}
							
							if (sfield == field) {
								valid = true;
								break;
							}
						}
						
						// Update the field
						if (!valid) {
							field = first;
							fieldChoice.setDescription(Arrays.asList(ChatColor.GREEN + value.toString()));
						}
					}
				});
				
				childMenu.displayMenu(setupMenu.getViewer());
				return null;
			}
		});
		
		fieldChoice.setClick(new InteractionInterface() {
			@Override
			public Object interact(Object object) {
				StatSettings settings = minigame.getSettings(stat);
				Menu childMenu = MinigameStats.createStatFieldSelectMenu(setupMenu, settings.getFormat(), new Callback<StatValueField>() {
					@Override
					public StatValueField getValue() {
						throw new UnsupportedOperationException();
					}
					
					@Override
					public void setValue(StatValueField value) {
						field = value;
						fieldChoice.setDescription(Arrays.asList(ChatColor.GREEN + value.getTitle()));
					}
				});
				
				childMenu.displayMenu(setupMenu.getViewer());
				return null;
			}
		});
		
		setupMenu.addItem(statisticChoice);
		setupMenu.addItem(fieldChoice);
		
		List<String> sbotypes = new ArrayList<String>();
		for(ScoreboardOrder o : ScoreboardOrder.values()){
			sbotypes.add(o.toString().toLowerCase());
		}
		setupMenu.addItem(new MenuItemList("Scoreboard Order", Material.ENDER_PEARL, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				order = ScoreboardOrder.valueOf(value.toUpperCase());
			}
			
			@Override
			public String getValue() {
				return order.toString().toLowerCase();
			}
		}, sbotypes));
		setupMenu.addItem(new MenuItemScoreboardSave("Create Scoreboard", Material.REDSTONE_TORCH_ON, this), setupMenu.getSize() - 1);
		setupMenu.displayMenu(player);
	}
	
	
	
	private void clearSign(Block block) {
		Sign sign = (Sign)block.getState();
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}

	public void deleteSigns() {
		List<Block> blocks = getSignBlocks(true);
		
		for (Block block : blocks) {
			block.setType(Material.AIR);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void placeSigns() {
		List<Block> blocks = getSignBlocks(false);
		
		for (Block block : blocks) {
			block.setType(Material.WALL_SIGN);
			
			org.bukkit.material.Sign signMat = new org.bukkit.material.Sign(Material.WALL_SIGN);
			signMat.setFacingDirection(facing);
			block.setData(signMat.getData());
		}
	}
	
	public void placeRootSign() {
		// For external calls
		if (settings == null) {
			settings = minigame.getSettings(stat);
		}
		
		Block root = rootBlock.getBlock();
		Sign sign = (Sign)root.getState();
		
		sign.setLine(0, ChatColor.BLUE + minigame.getName(true));
		sign.setLine(1, ChatColor.GREEN + settings.getDisplayName());
		sign.setLine(2, ChatColor.GREEN + field.getTitle());
		sign.setLine(3, "(" + WordUtils.capitalize(order.toString()) + ")");
		sign.update();
		
		sign.setMetadata("MGScoreboardSign", new FixedMetadataValue(Minigames.plugin, true));
		sign.setMetadata("Minigame", new FixedMetadataValue(Minigames.plugin, minigame));
	}
	
	public void save(ConfigurationSection section) {
		section.set("height", height);
		section.set("width", width);
		section.set("dir", facing.name());
		section.set("stat", stat.getName());
		section.set("field", field.name());
		section.set("order", order.name());
		MinigameUtils.saveShortLocation(section.createSection("location"), rootBlock);
	}
	
	@SuppressWarnings("deprecation")
	public static ScoreboardDisplay load(Minigame minigame, ConfigurationSection section) {
		int width = section.getInt("width");
		int height = section.getInt("height");
		Location location = MinigameUtils.loadShortLocation(section.getConfigurationSection("location"));
		BlockFace facing = BlockFace.valueOf(section.getString("dir"));
		
		// from invalid world
		if (location == null) {
			return null;
		}
		
		ScoreboardDisplay display = new ScoreboardDisplay(minigame, width, height, location, facing);
		display.setOrder(ScoreboardOrder.valueOf(section.getString("order")));
		
		// Convert type to new stat
		if (section.contains("type")) {
			ScoreboardType type = ScoreboardType.valueOf(section.getString("type"));
			
			switch (type) {
			case BEST_KILLS:
				display.setStat(MinigameStats.Kills, StatValueField.Max);
				break;
			case BEST_SCORE:
				display.setStat(MinigameStats.Score, StatValueField.Max);
				break;
			case COMPLETIONS:
				display.setStat(MinigameStats.Wins, StatValueField.Total);
				break;
			case FAILURES:
				display.setStat(MinigameStats.Losses, StatValueField.Total);
				break;
			case LEAST_DEATHS:
				display.setStat(MinigameStats.Deaths, StatValueField.Min);
				break;
			case LEAST_REVERTS:
				display.setStat(MinigameStats.Reverts, StatValueField.Min);
				break;
			case LEAST_TIME:
				display.setStat(MinigameStats.CompletionTime, StatValueField.Min);
				break;
			case TOTAL_DEATHS:
				display.setStat(MinigameStats.Deaths, StatValueField.Total);
				break;
			case TOTAL_KILLS:
				display.setStat(MinigameStats.Kills, StatValueField.Total);
				break;
			case TOTAL_REVERTS:
				display.setStat(MinigameStats.Reverts, StatValueField.Total);
				break;
			case TOTAL_SCORE:
				display.setStat(MinigameStats.Score, StatValueField.Total);
				break;
			case TOTAL_TIME:
				display.setStat(MinigameStats.CompletionTime, StatValueField.Total);
				break;
			default:
				break;
			}
			
			section.set("type", null);
		// Load stat
		} else {
			MinigameStat stat = MinigameStats.getStat(section.getString("stat", "wins"));
			StatValueField field = StatValueField.valueOf(section.getString("field", "Total"));
			display.setStat(stat, field);
		}
		
		Block block = location.getBlock();
		block.setMetadata("MGScoreboardSign", new FixedMetadataValue(Minigames.plugin, true));
		block.setMetadata("Minigame", new FixedMetadataValue(Minigames.plugin, minigame));
		
		return display;
	}
	
	public void reload() {
		needsLoad = false;
		ListenableFuture<List<StoredStat>> future = Minigames.plugin.getBackend().loadStats(minigame, stat, field, order, 0, width * height * 2);
		Minigames.plugin.getBackend().addServerThreadCallback(future, getUpdateCallback());
	}
	
	// The update callback to be provided to the future. MUST be executed on the bukkit server thread
	private FutureCallback<List<StoredStat>> getUpdateCallback() {
		return new FutureCallback<List<StoredStat>>() {
			@Override
			public void onSuccess(List<StoredStat> result) {
				stats = result;
				needsLoad = false;
				updateSigns();
			}
			
			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
				stats = Collections.emptyList();
				needsLoad = true;
			}
		};
	}
}
