package com.pauldavdesign.mineauz.minigames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UpdateChecker extends Thread{
	
	private Player ply;
	
	public UpdateChecker(Player player){
		ply = player;
	}
	
	@Override
	public void run(){
		URL update;
		
		try {
			update = new URL("https://api.curseforge.com/servermods/files?projectIds=42036");
			BufferedReader read = new BufferedReader(new InputStreamReader(update.openStream()));
			
			String res = read.readLine();
			
			JSONArray arr = (JSONArray) JSONValue.parse(res);
			
			if(!arr.isEmpty()){
				JSONObject latest = (JSONObject) arr.get(arr.size() - 1);
				String version = (String) latest.get("name");
				version = version.replaceAll("[a-zA-Z-]", "");
				if(!Minigames.plugin.getDescription().getVersion().equals(version)){
					String gameV = (String) latest.get("gameVersion");
					String type = (String) latest.get("releaseType");
					String url = (String) latest.get("downloadUrl");
					
					ply.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.formStr("minigame.update.msg", type, version, gameV));
					ply.sendMessage(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
