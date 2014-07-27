package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SetTeamScoreAction implements ActionInterface {

	@Override
	public String getName() {
		return "SET_TEAM_SCORE";
	}

	@Override
	public String getCategory() {
		return "Team Actions";
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
	public void executeRegionAction(MinigamePlayer player,
			Map<String, Object> args, Region region, Event event) {
		executeAction(player, args);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Map<String, Object> args, Node node, Event event) {
		executeAction(player, args);
	}
	
	private void executeAction(MinigamePlayer player, Map<String, Object> args){
		if(player == null || !player.isInMinigame()) return;
		if(player.getTeam() != null && args.get("a_setteamscorename").equals("NONE")){
			player.getTeam().setScore((int)args.get("a_setteamscore"));
		}
		else if(!args.get("a_setteamscorename").equals("NONE")){
			TeamsModule tm = TeamsModule.getMinigameModule(player.getMinigame());
			if(tm.hasTeam(TeamColor.valueOf((String)args.get("a_setteamscorename")))){
				tm.getTeam(TeamColor.valueOf((String)args.get("a_setteamscorename"))).setScore((int)args.get("a_setteamscore"));
			}
		}
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_setteamscore", 1);
		args.put("a_setteamscorename", "NONE");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_setteamscore", args.get("a_setteamscore"));
		config.set(path + ".a_setteamscorename", args.get("a_setteamscorename"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_setteamscore", config.getInt(path + ".a_setteamscore"));
		args.put("a_setteamscorename", config.getString(path + ".a_setteamscorename"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Set Team Score", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Set Score Amount", Material.DOUBLE_STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("a_setteamscore", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("a_setteamscore");
			}
		}, null, null));
		
		List<String> teams = new ArrayList<String>();
		teams.add("None");
		for(TeamColor team : TeamColor.values()){
			teams.add(MinigameUtils.capitalize(team.toString()));
		}
		m.addItem(new MenuItemList("Specific Team", MinigameUtils.stringToList("If 'None', the players;team will be used"), Material.PAPER, new Callback<String>() {

			@Override
			public void setValue(String value) {
				fargs.put("a_setteamscorename", value.toUpperCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize((String) fargs.get("a_setteamscorename"));
			}
		}, teams));
		m.displayMenu(player);
		return true;
	}

}
