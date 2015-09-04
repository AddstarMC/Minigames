package au.com.mineauz.minigames.config;

import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class TeamSetFlag extends ConfigProperty<Map<TeamColor, Team>> {
	private final Minigame mgm;
	
	public TeamSetFlag(Map<TeamColor, Team> value, String name, Minigame mgm) {
		super(name, value);
		this.mgm = mgm;
	}

	@Override
	public void save(ConfigurationSection section) {
		for (Team t : getValue().values()) {
			// TODO: This should just save it directly
			TeamFlag tf = new TeamFlag(null, t.getColor().toString(), mgm);
			tf.setValue(t);
			tf.save(section);
		}
	}

	@Override
	public void load(ConfigurationSection section) {
		for(String key : section.getKeys(false)) {
			TeamFlag tf = new TeamFlag(null, key, mgm);
			tf.load(section);
			getValue().put(tf.getValue().getColor(), tf.getValue());
			
			// TODO: This setup should not be here
			String sbTeam = tf.getValue().getColor().toString().toLowerCase();
			mgm.getScoreboardManager().registerNewTeam(sbTeam);
			mgm.getScoreboardManager().getTeam(sbTeam).setPrefix(tf.getValue().getColor().getColor().toString());
			mgm.getScoreboardManager().getTeam(sbTeam).setAllowFriendlyFire(false);
			mgm.getScoreboardManager().getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
			mgm.getScoreboardManager().getTeam(sbTeam).setNameTagVisibility(tf.getValue().getNameTagVisibility());
		}
	}
}
