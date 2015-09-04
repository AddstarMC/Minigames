package au.com.mineauz.minigames.properties.types;

import org.bukkit.configuration.ConfigurationSection;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.properties.ConfigProperty;

public class RewardsProperty extends ConfigProperty<Rewards> {
	public RewardsProperty(Rewards value, String name) {
		super(name, value, null);
	}

	@Override
	public void save(ConfigurationSection section) {
		getValue().save(section.createSection(getName()));
	}

	@Override
	public void load(ConfigurationSection section) {
		getValue().load(section.getConfigurationSection(getName()));
	}
}
