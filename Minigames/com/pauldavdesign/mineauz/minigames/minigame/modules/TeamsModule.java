package com.pauldavdesign.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Callback;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemAddTeam;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemList;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemNewLine;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemTeam;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;
import com.pauldavdesign.mineauz.minigames.minigame.MinigameModule;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;

public class TeamsModule implements MinigameModule {
	private Map<TeamColor, Team> teams = new HashMap<TeamColor, Team>();
	private Team defaultWinner = null;

	@Override
	public String getName() {
		return "Teams";
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void save(Minigame minigame, FileConfiguration config) {
		for(Team team : teams.values()){
			config.set(minigame + ".teams." + team.getColor().toString() + ".displayName", team.getDisplayName());
			if(!team.getStartLocations().isEmpty()){
				for(int i = 0; i < team.getStartLocations().size(); i++){
					Minigames.plugin.mdata.minigameSetLocations(minigame.getName(false), team.getStartLocations().get(i), 
							"teams." + team.getColor().toString() + ".startpos." + i, config);
				}
			}
		}
		
		if(getDefaultWinner() != null){
			config.set(minigame + ".defaultwinner", getDefaultWinner().getColor().toString());
		}
	}

	@Override
	public void load(Minigame minigame, FileConfiguration config) {

		if(config.contains(minigame + ".teams")){
			Set<String> teams = config.getConfigurationSection(minigame + ".teams").getKeys(false);
			for(String team : teams){
				Team t = addTeam(minigame, TeamColor.valueOf(team), config.getString(minigame + ".teams." + team + ".displayName"));
				if(config.contains(minigame + ".teams." + team + ".startPos")){
					Set<String> locations = config.getConfigurationSection(minigame + ".teams." + team + ".startPos").getKeys(false);
					for(String loc : locations){
						t.addStartLocation(Minigames.plugin.mdata.minigameLocations(minigame.getName(false), 
								"teams." + team + ".startPos." + loc, config));
					}
				}
			}
		}
		if(config.contains(minigame + ".startposred")){ //TODO: Remove after 1.7
			if(!hasTeam(TeamColor.RED))
				addTeam(minigame, TeamColor.RED);
			Set<String> locs = config.getConfigurationSection(minigame + ".startposred").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				getTeam(TeamColor.RED).addStartLocation(Minigames.plugin.mdata.minigameLocations(minigame.getName(false), 
						"startposred." + String.valueOf(i), config));
			}
		}
		if(config.contains(minigame + ".startposblue")){ //TODO: Remove after 1.7
			if(!hasTeam(TeamColor.BLUE))
				addTeam(minigame, TeamColor.BLUE);
			Set<String> locs = config.getConfigurationSection(minigame + ".startposblue").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				getTeam(TeamColor.BLUE).addStartLocation(Minigames.plugin.mdata.minigameLocations(minigame.getName(false), 
						"startposblue." + String.valueOf(i), config));
			}
		}
		
		if(config.contains(minigame + ".defaultwinner")){
			setDefaultWinner(getTeam(TeamColor.matchColor(config.getString(minigame + ".defaultwinner"))));
		}
	}
	
	public static TeamsModule getMinigameModule(Minigame minigame){
		return (TeamsModule) minigame.getModule("Teams");
	}
	
	public Team getTeam(TeamColor color){
		return teams.get(color);
	}
	
	public List<Team> getTeams(){
		return new ArrayList<Team>(teams.values());
	}
	
	public Team addTeam(Minigame mgm, TeamColor color){
		return addTeam(mgm, color, "");
	}
	
	public Team addTeam(Minigame mgm, TeamColor color, String name){
		if(!teams.containsKey(color)){
			teams.put(color, new Team(color, mgm));
			String sbTeam = color.toString().toLowerCase();
			mgm.getScoreboardManager().registerNewTeam(sbTeam);
			mgm.getScoreboardManager().getTeam(sbTeam).setPrefix(color.getColor().toString());
			mgm.getScoreboardManager().getTeam(sbTeam).setAllowFriendlyFire(false);
			mgm.getScoreboardManager().getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
		}
		if(!name.equals(""))
			teams.get(color).setDisplayName(name);
		return teams.get(color);
	}
	
	public void addTeam(Minigame mgm, TeamColor color, Team team){
		teams.put(color, team);
		String sbTeam = color.toString().toLowerCase();
		mgm.getScoreboardManager().registerNewTeam(sbTeam);
		mgm.getScoreboardManager().getTeam(sbTeam).setPrefix(color.getColor().toString());
		mgm.getScoreboardManager().getTeam(sbTeam).setAllowFriendlyFire(false);
		mgm.getScoreboardManager().getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
	}
	
	public boolean hasTeam(TeamColor color){
		if(teams.containsKey(color))
			return true;
		return false;
	}
	
	public void removeTeam(Minigame mgm, TeamColor color){
		if(teams.containsKey(color)){
			teams.remove(color);
			mgm.getScoreboardManager().getTeam(color.toString().toLowerCase()).unregister();
		}
	}
	
	public boolean hasTeamStartLocations(){
		for(Team t : teams.values()){
			if(!t.hasStartLocations())
				return false;
		}
		return true;
	}
	
	public Callback<String> getDefaultWinnerCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				if(!value.equals("None"))
					defaultWinner = getTeam(TeamColor.matchColor(value.replace(" ", "_")));
				else
					defaultWinner = null;
			}

			@Override
			public String getValue() {
				if(defaultWinner != null)
					return MinigameUtils.capitalize(defaultWinner.getColor().toString().replace("_", " "));
				return "None";
			}
		};
	}

	public void setDefaultWinner(Team defaultWinner) {
		this.defaultWinner = defaultWinner;
	}
	
	public Team getDefaultWinner() {
		return defaultWinner;
	}
	
	public void clearTeams(){
		teams.clear();
		defaultWinner = null;
	}

	@Override
	public void addMenuOptions(Menu menu, Minigame minigame) {
		Menu m = new Menu(6, "Teams", menu.getViewer());
		m.setPreviousPage(menu);
		List<MenuItem> items = new ArrayList<MenuItem>();
		List<String> teams = new ArrayList<String>(this.teams.size() + 1);
		for(TeamColor t : this.teams.keySet()){
			teams.add(MinigameUtils.capitalize(t.toString().replace("_", " ")));
		}
		teams.add("None");
		items.add(new MenuItemList("Default Winning Team", Material.PAPER, getDefaultWinnerCallback(), teams));
		items.add(new MenuItemNewLine());
		for(Team t : this.teams.values()){
			items.add(new MenuItemTeam(t.getChatColor() + t.getDisplayName(), t));
		}
		
		m.addItem(new MenuItemAddTeam("Add Team", minigame), m.getSize() - 1);
		
		m.addItems(items);
		
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, menu), m.getSize() - 9);
		
		MenuItemPage p = new MenuItemPage("Team Options", Material.CHEST, m);
		menu.addItem(p);
	}
}
