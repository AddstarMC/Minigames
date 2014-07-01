package com.pauldavdesign.mineauz.minigames.mechanics;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.Listener;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.PlayerData;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;

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
	
	/**
	 * Gets the mechanics name.
	 * @return The name of the Mechanic
	 */
	public abstract String getMechanic();
	
	/**
	 * Gives the valid types for this game mechanic
	 * @return All valid game types.
	 */
	public abstract EnumSet<MinigameType> validTypes();
	
	/**
	 * Checks if a mechanic is allowed to start with the current settings. Caller 
	 * can be sent message, but can also be null, in which case, should be sent
	 * to the console.
	 * @param minigame The Minigame in which settings to check
	 * @param caller The Player (or Null) to send the error messages to
	 * @return true if all checks pass.
	 */
	public abstract boolean checkCanStart(Minigame minigame, MinigamePlayer caller);
	
	/**
	 * In the case of a Minigame having teams, this should be used to balance players
	 * to a specific team, usual games is evenly distributed, in the case of Infection,
	 * only a specific percentage is assigned to one team by default.
	 * @param players The players to be balanced to a team
	 * @param minigame The minigame in which the balancing occours
	 */
	public abstract void balanceTeam(List<MinigamePlayer> players, Minigame minigame);
	
	/**
	 * Returns the module that is assigned to this mechanic, or null if none is assigned.
	 * @return The module that has been assigned
	 */
	public abstract MinigameModule displaySettings(Minigame minigame);
}
