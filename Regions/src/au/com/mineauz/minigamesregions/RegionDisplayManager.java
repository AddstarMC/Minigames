package au.com.mineauz.minigamesregions;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.display.IDisplayObject;
import au.com.mineauz.minigames.minigame.Minigame;

public class RegionDisplayManager {
	private Map<Player, Map<Region, IDisplayObject>> regionDisplays;
	private Map<Player, Map<Node, IDisplayObject>> nodeDisplays;
	
	public RegionDisplayManager() {
		regionDisplays = Maps.newHashMap();
		nodeDisplays = Maps.newHashMap();
	}
	
	public void show(Region region, MinigamePlayer player) {
		Map<Region, IDisplayObject> regions = regionDisplays.get(player.getPlayer());
		if (regions == null) {
			regions = Maps.newIdentityHashMap();
			regionDisplays.put(player.getPlayer(), regions);
		}
		
		Location[] corners = MinigameUtils.getMinMaxSelection(region.getFirstPoint(), region.getSecondPoint());
		
		IDisplayObject display = Minigames.plugin.display.displayCuboid(player.getPlayer(), corners[0], corners[1].add(1, 1, 1));
		display.show();
		regions.put(region, display);
	}
	
	public void show(Node node, MinigamePlayer player) {
		Map<Node, IDisplayObject> nodes = nodeDisplays.get(player.getPlayer());
		if (nodes == null) {
			nodes = Maps.newIdentityHashMap();
			nodeDisplays.put(player.getPlayer(), nodes);
		}
		
		IDisplayObject display = Minigames.plugin.display.displayPoint(player.getPlayer(), node.getLocation(), true);
		display.show();
		nodes.put(node, display);
	}
	
	public void hide(Region region, MinigamePlayer player) {
		Map<Region, IDisplayObject> regions = regionDisplays.get(player.getPlayer());
		if (regions == null) {
			return;
		}
		
		IDisplayObject display = regions.remove(region);
		if (display != null) {
			display.remove();
		}
	}
	
	public void hide(Node node, MinigamePlayer player) {
		Map<Node, IDisplayObject> nodes = nodeDisplays.get(player.getPlayer());
		if (nodes == null) {
			return;
		}
		
		IDisplayObject display = nodes.remove(node);
		if (display != null) {
			display.remove();
		}
	}
	
	public void showAll(Minigame minigame, MinigamePlayer player) {
		RegionModule module = RegionModule.getMinigameModule(minigame);
		for (Region region : module.getRegions()) {
			show(region, player);
		}
		
		for (Node node : module.getNodes()) {
			show(node, player);
		}
	}
	
	public void hideAll(Minigame minigame, MinigamePlayer player) {
		RegionModule module = RegionModule.getMinigameModule(minigame);
		for (Region region : module.getRegions()) {
			hide(region, player);
		}
		
		for (Node node : module.getNodes()) {
			hide(node, player);
		}
	}
	
	public void hideAll(Player player) {
		Map<Region, IDisplayObject> regions = regionDisplays.remove(player);
		if (regions != null) {
			for (IDisplayObject display : regions.values()) {
				display.remove();
			}
		}
		
		Map<Node, IDisplayObject> nodes = nodeDisplays.remove(player);
		if (nodes != null) {
			for (IDisplayObject display : nodes.values()) {
				display.remove();
			}
		}
	}
}
