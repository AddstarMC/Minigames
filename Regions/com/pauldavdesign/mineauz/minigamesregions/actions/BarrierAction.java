package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class BarrierAction implements ActionInterface{

	@Override
	public String getName() {
		return "BARRIER";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return false;
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args,
			Region region, Event event) {
		Location o = player.getLocation().clone();
		Location[] locs = {region.getFirstPoint(), region.getSecondPoint()};
		double xdis1 = Math.abs(o.getX() - locs[0].getX());
		double ydis1 = Math.abs(o.getY() - locs[0].getY());
		double zdis1 = Math.abs(o.getZ() - locs[0].getZ());
		double xdis2 = Math.abs(o.getX() - (locs[1].getX() + 1));
		double ydis2 = Math.abs(o.getY() - (locs[1].getY() + 1));
		double zdis2 = Math.abs(o.getZ() - (locs[1].getZ() + 1));
		boolean isMinX = false;
		boolean isMinY = false;
		boolean isMinZ = false;
		double xval = 0;
		double yval = 0;
		double zval = 0;
		if(xdis1 < xdis2){
			isMinX = true;
			xval = xdis1;
		}
		else
			xval = xdis2;
		if(ydis1 < ydis2){
			isMinY = true;
			yval = ydis1;
		}
		else
			yval = ydis2;
		if(zdis1 < zdis2){
			isMinZ = true;
			zval = zdis1;
		}
		else
			zval = zdis2;
		if(xval < yval && xval < zval){
			if(isMinX)
				o.setX(o.getX() - 0.5);
			else
				o.setX(o.getX() + 0.5);
		}
		else if(yval < xval && yval < zval){
			if(isMinY)
				o.setY(o.getY() - 0.5);
			else
				o.setY(o.getY() + 0.5);
		}
		else if(zval < xval && zval < yval){
			if(isMinZ)
				o.setZ(o.getZ() - 0.5);
			else
				o.setZ(o.getZ() + 0.5);
		}
		player.teleport(o);
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		return null;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		// None
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		return null;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		return false;
	}

}
