package au.com.mineauz.minigames.signs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Directional;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.ScoreboardDisplay;

public class ScoreboardSign implements MinigameSign{
	
	private Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Scoreboard";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.scoreboard";
	}

	@Override
	public String getCreatePermissionMessage() {
		return "You do not have permission to create a Minigame scoreboard sign!";
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.scoreboard";
	}

	@Override
	public String getUsePermissionMessage() {
		return "You do not have permission to set up a Minigame scoreboard sign!";
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		Sign sign = (Sign)event.getBlock().getState();
		if(sign.getType() != Material.WALL_SIGN){
			event.getPlayer().sendMessage(ChatColor.RED + "Scoreboards must be placed on a wall!");
			return false;
		}
		
		BlockFace dir = ((Directional)sign.getData()).getFacing();
		
		if(plugin.mdata.hasMinigame(event.getLine(2))){
			if(event.getLine(3).isEmpty() || event.getLine(3).matches("[0-9]+x[0-9]+")){
				int len;
				int hei;
				if(!event.getLine(3).isEmpty()){
					len = Integer.parseInt(event.getLine(3).split("x")[0]);
					hei = Integer.parseInt(event.getLine(3).split("x")[1]);
				}
				else{
					len = 3;
					hei = 3;
				}
				
				if(len % 2 == 0){
					event.getPlayer().sendMessage(ChatColor.RED + "Length must not be an even number!");
					return false;
				}
				Location cur = event.getBlock().getLocation().clone();
				cur.setY(cur.getY() - hei);
				int ord;
				int ory = cur.getBlockY();
				if(dir == BlockFace.EAST || dir == BlockFace.WEST){
					cur.setZ(cur.getZ() - Math.floor(len / 2));
					ord = cur.getBlockZ();
				}
				else{
					cur.setX(cur.getX() - Math.floor(len / 2));
					ord = cur.getBlockX();
				}
				
				for(int y = 0; y < hei; y++){
					cur.setY(ory + y);
					for(int i = 0; i < len; i++){
						if(dir == BlockFace.EAST || dir == BlockFace.WEST){
							cur.setZ(ord + i);
						}
						else{
							cur.setX(ord + i);
						}
						if(cur.getBlock().getType() == Material.AIR){
							cur.getBlock().setType(Material.WALL_SIGN);
							
							if(cur.getBlock().getState() instanceof Sign){
								Sign nsign = (Sign)cur.getBlock().getState();
								org.bukkit.material.Sign mt = (org.bukkit.material.Sign) nsign.getData();
								mt.setFacingDirection(dir);
								nsign.update();
							}
						}
						else{
							event.getPlayer().sendMessage(ChatColor.RED + "Block in the way!");
							return false;
						}
					}
				}
				event.setLine(1, ChatColor.GREEN + "Scoreboard");
				event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName(false));
				return true;
			}
		}
		else{
			event.getPlayer().sendMessage(ChatColor.RED + "No Minigame found by the name " + event.getLine(2));
		}
		return false;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		Minigame mgm = plugin.mdata.getMinigame(sign.getLine(2));
		int width = 3;
		int height = 3;
		if(sign.getLine(3).matches("[0-9]+x[0-9]")){
			width = Integer.parseInt(sign.getLine(3).split("x")[0]);
			height = Integer.parseInt(sign.getLine(3).split("x")[1]);
		}
		if(mgm != null){
			ScoreboardDisplay disp = new ScoreboardDisplay(mgm, width, height, sign.getLocation(), ((org.bukkit.material.Sign)sign.getData()).getFacing());
			disp.displayMenu(player);
		}
		return false;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		Minigame mg = plugin.mdata.getMinigame(sign.getBlock().getMetadata("Minigame").get(0).asString());
		if(mg != null){
			mg.getScoreboardData().removeDisplay(MinigameUtils.createLocationID(sign.getBlock().getLocation()));
		}
	}

}
