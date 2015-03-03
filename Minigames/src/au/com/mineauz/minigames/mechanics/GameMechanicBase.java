package au.com.mineauz.minigames.mechanics;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.Listener;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigameData;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerData;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

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
	 * Adds the modules this mechanic requires to the minigame 
	 */
	public abstract void addRequiredModules(Minigame minigame);
	
	/**
	 * Checks if a mechanic is allowed to start with the current settings. Should throw 
	 * IllegalStateException to give error message
	 * @param minigame The Minigame in which settings to check
	 * @return True if the minigame can be started. This should only be false if no explanation is needed
	 * @throws IllegalStateException To be thrown if the game cannot be started and needs to be explained.
	 */
	public abstract boolean checkCanStart(Minigame minigame) throws IllegalStateException;
	
	/**
	 * In the case of a Minigame having teams, this should be used to balance players
	 * to a specific team, usual games is evenly distributed, in the case of Infection,
	 * only a specific percentage is assigned to one team by default. The default function
	 * will assign teams automatically unless overridden.
	 * @param players The players to be balanced to a team
	 * @param minigame The minigame in which the balancing occours
	 */
	public void balanceTeam(List<MinigamePlayer> players, Minigame minigame){
		if(minigame.isTeamGame()){
			boolean sorted = false;
			for(MinigamePlayer ply : players){
				if(ply.getTeam() == null){
					Team smt = null;
					for(Team t : minigame.getModule(TeamsModule.class).getTeams()){
						if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() && 
								(t.getMaxPlayers() == 0 || t.getPlayers().size() != t.getMaxPlayers())))
							smt = t;
					}
					if(smt == null){
						pdata.quitMinigame(ply, false);
						ply.sendMessage(MinigameUtils.getLang("minigame.full"), MessageType.Error);
					}
					smt.addPlayer(ply);
					ply.sendMessage(String.format(smt.getAutobalanceMessage(), smt.getChatColor() + smt.getDisplayName()), MessageType.Normal);
					minigame.broadcastExcept(String.format(smt.getGameAutobalanceMessage(), 
						ply.getName(), smt.getChatColor() + smt.getDisplayName()), MessageType.Normal, ply);
				}
			}
			
			while(!sorted){
				Team smt = null;
				Team lgt = null;
				for(Team t : minigame.getModule(TeamsModule.class).getTeams()){
					if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() - 1 && !t.isFull()))
						smt = t;
					if((lgt == null || (t.getPlayers().size() > lgt.getPlayers().size() && !t.isFull())) && t != smt)
						lgt = t;
				}
				if(smt != null && lgt != null && lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MinigamePlayer pl = lgt.getPlayers().get(0);
					MultiplayerType.switchTeam(minigame, pl, smt);
					pl.sendMessage(String.format(smt.getAutobalanceMessage(), smt.getChatColor() + smt.getDisplayName()), MessageType.Normal);
					minigame.broadcastExcept(String.format(smt.getGameAutobalanceMessage(), 
						pl.getName(), smt.getChatColor() + smt.getDisplayName()), MessageType.Normal, pl);
				}
				else{
					sorted = true;
				}
			}
		}
	}
	
	/**
	 * Returns the module that is assigned to this mechanic, or null if none is assigned. This is to open the settings menu
	 * for the GameMechanic if clicked in the edit menu.
	 * @return The module that has been assigned
	 */
	public abstract MinigameModule displaySettings(Minigame minigame);
	
	/**
	 * Called when a global Minigame has been started.
	 * @param minigame
	 * @param caller The player who initiated the global Minigame or null if not by a player.
	 */
	public abstract void startMinigame(Minigame minigame, MinigamePlayer caller);
	
	/**
	 * Called when a global Minigame has been stopped.
	 * @param minigame
	 * @param caller The player who stopped the global Minigame or null if not by a player.
	 */
	public abstract void stopMinigame(Minigame minigame, MinigamePlayer caller);
	
	/**
	 * Called when a player joins a Minigame. Called after the player has completely joined the game.
	 * @param minigame
	 * @param player
	 */
	public abstract void joinMinigame(Minigame minigame, MinigamePlayer player);
	
	/**
	 * Called when a player quits a Minigame or is forced to quit by the Minigame. Called as the quit function has started.
	 * @param minigame
	 * @param player
	 * @param forced
	 */
	public abstract void quitMinigame(Minigame minigame, MinigamePlayer player, boolean forced);
	
	/**
	 * Called when a player (or group of players) wins a Minigame. Called as the end function has been started, so winners and
	 * losers can still be modified.
	 * @param minigame
	 * @param winners
	 * @param losers
	 */
	public abstract void endMinigame(Minigame minigame, List<MinigamePlayer> winners, List<MinigamePlayer> losers);
}
