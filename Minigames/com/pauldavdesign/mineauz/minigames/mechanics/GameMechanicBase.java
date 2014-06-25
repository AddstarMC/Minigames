package com.pauldavdesign.mineauz.minigames.mechanics;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.Listener;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public abstract class GameMechanicBase implements Listener{
	public static Minigames plugin;
	public PlayerData pdata;
	public MinigameData mdata;
	
	public GameMechanicBase(){
		plugin = Minigames.plugin;
		pdata = plugin.pdata;
		mdata = plugin.mdata;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public abstract String getMechanic();
	
	public abstract EnumSet<MinigameType> validTypes();
	
	public abstract void balanceTeam(List<MinigamePlayer> players, Minigame minigame);
	
	public abstract boolean displaySettings(Menu menu);
}
