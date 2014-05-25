package com.pauldavdesign.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;
import com.pauldavdesign.mineauz.minigamesregions.Node;
import com.pauldavdesign.mineauz.minigamesregions.Region;

public class EndAction implements ActionInterface {

	@Override
	public String getName() {
		return "END";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		execute(player, args);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Map<String, Object> args, Region region, Event event) {
		execute(player, args);
	}
	
	private void execute(MinigamePlayer player, Map<String, Object> args){
		if(!player.isInMinigame()) return;
		if(player.getMinigame().getType() != MinigameType.SINGLEPLAYER){
			List<MinigamePlayer> w = null;
			List<MinigamePlayer> l = null;
			if(player.getMinigame().isTeamGame()){
				w = new ArrayList<MinigamePlayer>(player.getTeam().getPlayers());
				l = new ArrayList<MinigamePlayer>(player.getMinigame().getPlayers().size() - player.getTeam().getPlayers().size());
				for(Team t : TeamsModule.getMinigameModule(player.getMinigame()).getTeams()){
					if(t != player.getTeam())
						l.addAll(t.getPlayers());
				}
			}
			else{
				w = new ArrayList<MinigamePlayer>(1);
				l = new ArrayList<MinigamePlayer>(player.getMinigame().getPlayers().size());
				w.add(player);
				l.addAll(player.getMinigame().getPlayers());
				l.remove(player);
			}
			Minigames.plugin.pdata.endMinigame(player.getMinigame(), w, l);
		}
		else{
			Minigames.plugin.pdata.endMinigame(player);
		}
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		return null;
	}

	@Override
	public void saveArguments(Map<String, Object> args, FileConfiguration config, String path) {
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config, String path) {
		return null;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args, Menu previous) {
		return false;
	}

}
