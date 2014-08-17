package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class BarrierAction extends ActionInterface{

	@Override
	public String getName() {
		return "BARRIER";
	}

	@Override
	public String getCategory() {
		return "World Actions";
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
			Node node, Event event) {
		
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region,
			Event event) {
		if(player == null || !player.isInMinigame()) return;
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
			if(region.getPlayers().contains(player)){
				if(isMinX)
					o.setX(o.getX() - 0.5);
				else
					o.setX(o.getX() + 0.5);
			}
			else{
				if(isMinX)
					o.setX(o.getX() + 0.5);
				else
					o.setX(o.getX() - 0.5);
			}
		}
		else if(yval < xval && yval < zval){
			if(region.getPlayers().contains(player)){
				if(isMinY)
					o.setY(o.getY() - 0.5);
				else
					o.setY(o.getY() + 0.5);
			}
			else{
				if(isMinY)
					o.setY(o.getY() + 0.5);
				else
					o.setY(o.getY() - 0.5);
			}
		}
		else if(zval < xval && zval < yval){
			if(region.getPlayers().contains(player)){
				if(isMinZ)
					o.setZ(o.getZ() - 0.5);
				else
					o.setZ(o.getZ() + 0.5);
			}
			else{
				if(isMinZ)
					o.setZ(o.getZ() + 0.5);
				else
					o.setZ(o.getZ() - 0.5);
			}
		}
		player.teleport(o);
		if(region.getPlayers().contains(player))
			region.removePlayer(player);
		else
			region.addPlayer(player);
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		// None
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		//None
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}
}
