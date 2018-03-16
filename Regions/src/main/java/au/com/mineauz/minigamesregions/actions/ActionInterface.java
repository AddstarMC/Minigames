package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.script.ScriptObject;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public abstract class ActionInterface {
	public abstract String getName();
	public abstract String getCategory();
	public abstract void describe(Map<String, Object> out);
	public abstract boolean useInRegions();
	public abstract boolean useInNodes();
	public abstract void executeRegionAction(MinigamePlayer player, Region region);
	public abstract void executeNodeAction(MinigamePlayer player, Node node);
	public abstract void saveArguments(FileConfiguration config, String path);
	public abstract void loadArguments(FileConfiguration config, String path);
	public abstract boolean displayMenu(MinigamePlayer player, Menu previous);
	public void debug(MinigamePlayer p, ScriptObject obj){
		if (Minigames.plugin.isDebugging()){
			Minigames.plugin.getLogger().info("Debug: Execute on Obj:" + String.valueOf(obj) + " as Action: " + String.valueOf(this) + " Player: " +String.valueOf(p));
		}
	}

	void setWinnersLosers(MinigamePlayer winner){
		if(winner.getMinigame().getType() != MinigameType.SINGLEPLAYER){
			List<MinigamePlayer> w;
			List<MinigamePlayer> l;
			if(winner.getMinigame().isTeamGame()){
				w = new ArrayList<>(winner.getTeam().getPlayers());
				l = new ArrayList<>(winner.getMinigame().getPlayers().size() - winner.getTeam().getPlayers().size());
				for(Team t : TeamsModule.getMinigameModule(winner.getMinigame()).getTeams()){
					if(t != winner.getTeam())
						l.addAll(t.getPlayers());
				}
			}
			else{
				w = new ArrayList<>(1);
				l = new ArrayList<>(winner.getMinigame().getPlayers().size());
				w.add(winner);
				l.addAll(winner.getMinigame().getPlayers());
				l.remove(winner);
			}
			Minigames.plugin.pdata.endMinigame(winner.getMinigame(), w, l);
		} else{
			Minigames.plugin.pdata.endMinigame(winner);
		}
	}

}
