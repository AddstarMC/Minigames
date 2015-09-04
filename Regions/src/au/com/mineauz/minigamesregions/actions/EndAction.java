package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigamesregions.TriggerArea;

public class EndAction extends ActionInterface {

	@Override
	public String getName() {
		return "END";
	}

	@Override
	public String getCategory() {
		return "Minigame Actions";
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
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		if(player.getMinigame().getType() != MinigameType.SINGLEPLAYER){
			List<MinigamePlayer> w = null;
			List<MinigamePlayer> l = null;
			if(player.getMinigame().isTeamGame()){
				w = new ArrayList<MinigamePlayer>(player.getTeam().getPlayers());
				l = new ArrayList<MinigamePlayer>(player.getMinigame().getPlayers().size() - player.getTeam().getPlayers().size());
				for(Team t : player.getMinigame().getModule(TeamsModule.class).getTeams()){
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
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
