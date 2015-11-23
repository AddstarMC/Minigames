package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

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
	public void describe(Map<String, Object> out) {
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
			Node node) {
		execute(player);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region) {
		execute(player);
	}
	
	private void execute(MinigamePlayer player){
		if(player == null || !player.isInMinigame()) return;
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
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}
