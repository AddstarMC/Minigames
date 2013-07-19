package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.List;

import org.bukkit.event.Listener;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;

public abstract class ScoreType implements Listener{
	public static Minigames plugin;
	public PlayerData pdata;
	public MinigameData mdata;
	
	public ScoreType(){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
		mdata = plugin.mdata;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public abstract String getType();
	
	public abstract void balanceTeam(List<MinigamePlayer> players, Minigame minigame);
}
