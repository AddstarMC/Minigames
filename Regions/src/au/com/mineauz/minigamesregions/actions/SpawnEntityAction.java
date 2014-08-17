package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SpawnEntityAction extends ActionInterface {
	
	private StringFlag type = new StringFlag("ZOMBIE", "type");

	@Override
	public String getName() {
		return "SPAWN_ENTITY";
	}

	@Override
	public String getCategory() {
		return "World Actions";
	}

	@Override
	public boolean useInRegions() {
		return false;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeRegionAction(MinigamePlayer player,
			Region region, Event event) {
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node,
			Event event) {
		if(player == null || !player.isInMinigame()) return;
		Entity ent = node.getLocation().getWorld().spawnEntity(node.getLocation(), EntityType.valueOf(type.getFlag()));
		ent.setMetadata("MinigameEntity", new FixedMetadataValue(Minigames.plugin, true));
		player.getMinigame().getBlockRecorder().addEntity(ent, player, true);
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		type.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		type.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Spawn Entity", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		List<String> options = new ArrayList<String>();
		for(EntityType type : EntityType.values()){
			if(type != EntityType.ITEM_FRAME && type != EntityType.LEASH_HITCH && type != EntityType.PLAYER && 
					type != EntityType.COMPLEX_PART && type != EntityType.WEATHER && type != EntityType.LIGHTNING &&
					type != EntityType.PAINTING && type != EntityType.UNKNOWN)
				options.add(MinigameUtils.capitalize(type.toString().replace("_", " ")));
		}
		m.addItem(new MenuItemList("Entity Type", Material.SKULL_ITEM, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				type.setFlag(value.toUpperCase().replace(" ", "_"));
			}
			
			@Override
			public String getValue() {
				return MinigameUtils.capitalize(type.getFlag().replace("_", " "));
			}
		}, options));
		m.displayMenu(player);
		return true;
	}
}
