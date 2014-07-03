package au.com.mineauz.minigames.mechanics;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.event.Listener;

import au.com.mineauz.minigames.MinigameData;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerData;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameModule;
import au.com.mineauz.minigames.minigame.Team;
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
					for(Team t : TeamsModule.getMinigameModule(minigame).getTeams()){
						if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() && 
								(t.getMaxPlayers() == 0 || t.getPlayers().size() != t.getMaxPlayers())))
							smt = t;
					}
					if(smt == null){
						pdata.quitMinigame(ply, false);
						ply.sendMessage(MinigameUtils.getLang("minigame.full"), "error");
					}
					smt.addPlayer(ply);
					ply.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									ply.getName(), smt.getChatColor() + smt.getDisplayName()), null, ply);
				}
			}
			
			while(!sorted){
				Team smt = null;
				Team lgt = null;
				for(Team t : TeamsModule.getMinigameModule(minigame).getTeams()){
					if(smt == null || (t.getPlayers().size() < smt.getPlayers().size() - 1 && !t.isFull()))
						smt = t;
					if((lgt == null || (t.getPlayers().size() > lgt.getPlayers().size() && !t.isFull())) && t != smt)
						lgt = t;
				}
				if(smt != null && lgt != null && lgt.getPlayers().size() - smt.getPlayers().size() > 1){
					MinigamePlayer pl = lgt.getPlayers().get(0);
					MultiplayerType.switchTeam(minigame, pl, smt);
					pl.sendMessage(MinigameUtils.formStr("player.team.autobalance.plyMsg", smt.getChatColor() + smt.getDisplayName()), null);
					mdata.sendMinigameMessage(minigame, 
							MinigameUtils.formStr("player.team.autobalance.minigameMsg", 
									pl.getName(), smt.getChatColor() + smt.getDisplayName()), null, pl);
				}
				else{
					sorted = true;
				}
			}
		}
	}
	
	/**
	 * Returns the module that is assigned to this mechanic, or null if none is assigned.
	 * @return The module that has been assigned
	 */
	public abstract MinigameModule displaySettings(Minigame minigame);
}
