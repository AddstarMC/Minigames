package au.com.mineauz.minigames.config;

import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class TeamFlag extends ConfigProperty<Team> {
	
	private final Minigame mgm;
	
	public TeamFlag(Team value, String name, Minigame mgm) {
		super(name, value);
		this.mgm = mgm;
	}

	@Override
	public void save(ConfigurationSection root) {
		ConfigurationSection section = root.createSection(getName());
		Team team = getValue();
		
		section.set("displayName", team.getDisplayName());
		if(!team.getStartLocations().isEmpty()) {
			for(int i = 0; i < team.getStartLocations().size(); i++) {
				MinigameUtils.saveLocation(section.createSection("startpos." + i), team.getStartLocations().get(i));
			}
		}
		
		team.getProperties().saveAll(section);
	}
	
	@Override
	public void load(ConfigurationSection root) {
		ConfigurationSection section = root.getConfigurationSection(getName());
		
		Team team = new Team(TeamColor.valueOf(getName()), mgm);
		team.setDisplayName(section.getString("displayName"));
		if(section.contains("startpos")) {
			ConfigurationSection startPositions = section.getConfigurationSection("startpos");
			for (String key : startPositions.getKeys(false)) {
				team.addStartLocation(MinigameUtils.loadLocation(startPositions.getConfigurationSection(key)));
			}
		}
		
		team.getProperties().loadAll(section);
		
		setValue(team);
	}
}
