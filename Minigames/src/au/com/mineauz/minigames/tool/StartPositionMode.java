package au.com.mineauz.minigames.tool;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public class StartPositionMode implements ToolMode{

	@Override
	public String getName() {
		return "START";
	}

	@Override
	public String getDisplayName() {
		return "Start Positions";
	}

	@Override
	public String getDescription() {
		return "Sets the starting;positions for a team;or player";
	}
	
	@Override
	public Material getIcon(){
		return Material.SKULL_ITEM;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			int x = event.getClickedBlock().getLocation().getBlockX();
			int y = event.getClickedBlock().getLocation().getBlockY();
			int z = event.getClickedBlock().getLocation().getBlockZ();
			String world = event.getClickedBlock().getLocation().getWorld().getName();
			
			int nx;
			int ny;
			int nz;
			String nworld;
			Location delLoc = null;
			if(team != null){
				if(team.hasStartLocations()){
					for(Location loc : team.getStartLocations()){
						nx = loc.getBlockX();
						ny = loc.getBlockY();
						nz = loc.getBlockZ();
						nworld = loc.getWorld().getName();
						
						if(x == nx && y == ny && z == nz && world.equals(nworld)){
							delLoc = loc;
							break;
						}
					}
				}
				if(delLoc != null){
					team.getStartLocations().remove(delLoc);
					player.sendMessage("Removed selected " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE + 
							" start location.", null);
				}
				else{
					player.sendMessage("Could not find a " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE +
							" start location at that point.", "error");
				}
			}
			else{
				for(Location loc : minigame.getStartLocations()){
					nx = loc.getBlockX();
					ny = loc.getBlockY();
					nz = loc.getBlockZ();
					nworld = loc.getWorld().getName();
					
					if(x == nx && y == ny && z == nz && world.equals(nworld)){
						delLoc = loc;
						break;
					}
				}
				if(delLoc != null){
					minigame.getStartLocations().remove(delLoc);
					player.sendMessage("Removed selected start location.", null);
				}
				else
					player.sendMessage("Could not find a start location at that point.", "error");
			}
		}
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
		if(team == null){
			minigame.addStartLocation(player.getLocation());
			player.sendMessage("Added start location for " + minigame, null);
		}
		else{
			team.addStartLocation(player.getLocation());
			player.sendMessage("Added " + team.getChatColor() + 
					team.getDisplayName() + ChatColor.WHITE + " start location to " + minigame, null);
		}
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		if(team != null){
			for(Location loc : team.getStartLocations()){
				player.getPlayer().sendBlockChange(loc, Material.SKULL, (byte)1);
			}
			player.sendMessage("Selected " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE + 
					" start points in " + minigame, null);
		}
		else{
			for(Location loc : minigame.getStartLocations()){
				player.getPlayer().sendBlockChange(loc, Material.SKULL, (byte)1);
			}
			player.sendMessage("Selected start points in " + minigame, null);
		}
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		if(team != null){
			for(Location loc : team.getStartLocations()){
				player.getPlayer().sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
			}
			player.sendMessage("Deselected " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE + 
					" start points in " + minigame, null);
		}
		else{
			for(Location loc : minigame.getStartLocations()){
				player.getPlayer().sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
			}
			player.sendMessage("Deselected start points in " + minigame, null);
		}
	}

	@Override
	public void onSetMode(MinigamePlayer player, MinigameTool tool) {
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
	}

}
