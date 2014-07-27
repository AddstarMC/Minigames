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

public class AddTeamScoreAction implements ActionInterface {

	@Override
	public String getName() {
		return "ADD_TEAM_SCORE";
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
		if(player.getTeam() != null && args.get("a_addteamscorename").equals("NONE")){
			player.getTeam().addScore((int)args.get("a_addteamscore"));
		}
		else if(!args.get("a_addteamscorename").equals("NONE")){
			TeamsModule tm = TeamsModule.getMinigameModule(player.getMinigame());
			if(tm.hasTeam(TeamColor.valueOf((String)args.get("a_addteamscorename")))){
				tm.getTeam(TeamColor.valueOf((String)args.get("a_addteamscorename"))).addScore((int)args.get("a_addteamscore"));
			}
		}
	}

	@Override
	public Map<String, Object> getRequiredArguments() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_addteamscore", 1);
		args.put("a_addteamscorename", "NONE");
		return args;
	}

	@Override
	public void saveArguments(Map<String, Object> args,
			FileConfiguration config, String path) {
		config.set(path + ".a_addteamscore", args.get("a_addteamscore"));
		config.set(path + ".a_addteamscorename", args.get("a_addteamscorename"));
	}

	@Override
	public Map<String, Object> loadArguments(FileConfiguration config,
			String path) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("a_addteamscore", config.getInt(path + ".a_addteamscore"));
		args.put("a_addteamscorename", config.getString(path + ".a_addteamscorename"));
		return args;
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Map<String, Object> args,
			Menu previous) {
		Menu m = new Menu(3, "Add Team Score", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final Map<String, Object> fargs = args;
		m.addItem(new MenuItemInteger("Add Score Amount", Material.DOUBLE_STEP, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				fargs.put("a_addteamscore", value);
			}
			
			@Override
			public Integer getValue() {
				return (Integer)fargs.get("a_addteamscore");
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
				fargs.put("a_addteamscorename", value.toUpperCase());
			}

			@Override
			public String getValue() {
				return MinigameUtils.capitalize((String) fargs.get("a_addteamscorename"));
			}
		}, teams));
		m.displayMenu(player);
		return true;
	}

}
