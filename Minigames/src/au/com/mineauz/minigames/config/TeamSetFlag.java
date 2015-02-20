package au.com.mineauz.minigames.config;

import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;

public class TeamSetFlag extends Flag<Map<TeamColor, Team>>{
	
	private final Minigame mgm;
	
	public TeamSetFlag(Map<TeamColor, Team> value, String name, Minigame mgm){
		setFlag(value);
		setDefaultFlag(value);
		setName(name);
		this.mgm = mgm;
	}

	@Override
	public void saveValue(String path, FileConfiguration config) {
		for(Team t : getFlag().values()){
			TeamFlag tf = new TeamFlag(null, t.getColor().toString(), mgm);
			tf.setFlag(t);
			tf.saveValue(path + "." + getName(), config);
		}
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		Set<String> teams = config.getConfigurationSection(path + "." + getName()).getKeys(false);
		for(String t : teams){
			TeamFlag tf = new TeamFlag(null, t, mgm);
			tf.loadValue(path + "." + getName(), config);
			getFlag().put(tf.getFlag().getColor(), tf.getFlag());
			String sbTeam = tf.getFlag().getColor().toString().toLowerCase();
			mgm.getScoreboardManager().registerNewTeam(sbTeam);
			mgm.getScoreboardManager().getTeam(sbTeam).setPrefix(tf.getFlag().getColor().getColor().toString());
			mgm.getScoreboardManager().getTeam(sbTeam).setAllowFriendlyFire(false);
			mgm.getScoreboardManager().getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
			mgm.getScoreboardManager().getTeam(sbTeam).setNameTagVisibility(tf.getFlag().getNameTagVisibility());
		}
	}
}
