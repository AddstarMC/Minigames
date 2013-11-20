package com.pauldavdesign.mineauz.minigames.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigameSave;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;

public class ScoreboardData {
	public Map<String, ScoreboardPlayer> scoreboards = new HashMap<String, ScoreboardPlayer>();
	public Map<String, ScoreboardDisplay> displays = new HashMap<String, ScoreboardDisplay>();
	
	public void addPlayer(ScoreboardPlayer player){
		scoreboards.put(player.getPlayerName(), player);
	}
	
	public ScoreboardPlayer getPlayer(String name){
		return scoreboards.get(name);
	}
	
	public boolean hasPlayer(String name){
		return scoreboards.containsKey(name);
	}
	
	public List<ScoreboardPlayer> getPlayers(){
		return new ArrayList<ScoreboardPlayer>(scoreboards.values());
	}
	
	public ScoreboardDisplay getDisplay(String locID){
		if(displays.containsKey(locID)){
			return displays.get(locID);
		}
		return null;
	}
	
	public void addDisplay(ScoreboardDisplay display){
		displays.put(MinigameUtils.createLocationID(display.getLocation()), display);
	}
	
	public void removeDisplay(String locID){
		displays.remove(locID);
	}
	
	public void updateDisplays(){
		if(!displays.isEmpty()){
			for(ScoreboardDisplay dis : displays.values()){
				dis.updateStats();
			}
		}
	}
	
	public void saveDisplays(MinigameSave save, String name){
		int inc = 0;
		FileConfiguration con = save.getConfig();
		con.set(name + ".scoreboards", null);
		for(ScoreboardDisplay dis : displays.values()){
			String loc = dis.getMinigame().getName() + ".scoreboards." + inc + ".";
			con.set(loc + "height", dis.getHeight());
			con.set(loc + "width", dis.getWidth());
			con.set(loc + "dir", dis.getDirection().toString());
			con.set(loc + "type", dis.getType().toString());
			con.set(loc + "order", dis.getOrder().toString());
			Minigames.plugin.mdata.minigameSetLocationsShort(dis.getMinigame().getName(), dis.getLocation(), "scoreboards." + inc + ".location", save.getConfig());
			inc++;
		}
	}
	
	public void loadDisplays(MinigameSave save, Minigame mgm){
		FileConfiguration con = save.getConfig();
		if(!save.getConfig().contains(mgm.getName() + ".scoreboards")) return;
		
		Set<String> keys = save.getConfig().getConfigurationSection(mgm.getName() + ".scoreboards").getKeys(false);
		for(String key : keys){
			String loc = mgm.getName() + ".scoreboards." + key + ".";
			ScoreboardDisplay dis = 
					new ScoreboardDisplay(mgm, 
							con.getInt(loc + "width"), 
							con.getInt(loc + "height"), 
							Minigames.plugin.mdata.minigameLocationsShort(mgm.getName(), "scoreboards." + key + ".location", save.getConfig()), 
							BlockFace.valueOf(con.getString(loc + "dir")));
			dis.setOrd(ScoreboardOrder.valueOf(con.getString(loc + "order")));
			dis.setType(ScoreboardType.valueOf(con.getString(loc + "type")));
			addDisplay(dis);
		}
	}
}
