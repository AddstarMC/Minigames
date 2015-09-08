package au.com.mineauz.minigames.minigame.reward;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardScheme;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardSchemes;
import au.com.mineauz.minigames.minigame.reward.scheme.StandardRewardScheme;
import au.com.mineauz.minigames.properties.AbstractProperty;
import au.com.mineauz.minigames.properties.ChangeListener;
import au.com.mineauz.minigames.properties.ConfigPropertyContainer;
import au.com.mineauz.minigames.properties.ObservableValue;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigames.stats.StoredGameStats;

public class RewardsModule extends MinigameModule {
	public static final String Name = "rewards";
	private RewardScheme scheme;
	
	public RewardsModule(Minigame minigame) {
		super(minigame);
		
		// Default scheme
		scheme = new StandardRewardScheme();
	}

	@Override
	public String getName() {
		return Name;
	}
	
	public RewardScheme getScheme() {
		return scheme;
	}
	
	public void setRewardScheme(RewardScheme scheme) {
		this.scheme = scheme;
	}
	
	public void awardPlayer(MinigamePlayer player, StoredGameStats data, Minigame minigame, boolean firstCompletion) {
		scheme.awardPlayer(player, data, minigame, firstCompletion);
	}

	@Override
	public ConfigPropertyContainer getProperties() {
		return scheme.getProperties();
	}

	@Override
	public boolean useSeparateConfig() {
		return false;
	}

	@Override
	public void save(ConfigurationSection root) {
		String name = RewardSchemes.getName(scheme.getClass());
		
		root.set("reward-scheme", name);
		
		ConfigurationSection rewards = root.createSection("rewards");
		scheme.save(rewards);
	}

	@Override
	public void load(ConfigurationSection root) {
		String name = root.getString("reward-scheme", "standard");
		
		scheme = RewardSchemes.createScheme(name);
		if (scheme == null) {
			scheme = new StandardRewardScheme();
		}
		
		ConfigurationSection rewards = root.getConfigurationSection("rewards");
		scheme.load(rewards);
	}

	@Override
	public void addEditMenuOptions(final Menu menu) {
		MenuItem launcher = new MenuItem("Reward Settings", Material.DIAMOND) {
			@Override
			protected void onClick(MinigamePlayer player) {
				Menu submenu = createSubMenu(menu);
				submenu.displayMenu(player);
			}
		};
		
		menu.addItem(launcher);
	}
	
	private Menu createSubMenu(final Menu parent) {
		final Menu submenu = new Menu(5, "Reward Settings");
		scheme.addMenuItems(submenu);
		
		Property<Class<? extends RewardScheme>> schemeProp = new AbstractProperty<Class<? extends RewardScheme>>() {
			@Override
			public Class<? extends RewardScheme> getValue() {
				return scheme.getClass();
			}
			
			@Override
			public void setValue(Class<? extends RewardScheme> value) {
				scheme = RewardSchemes.createScheme(value);
				super.setValue(value);
			}
		};
		
		schemeProp.addListener(new ChangeListener<Class<? extends RewardScheme>>() {
			@Override
			public void onValueChange(ObservableValue<? extends Class<? extends RewardScheme>> observable, Class<? extends RewardScheme> oldValue, Class<? extends RewardScheme> newValue) {
				// Update the menu
				submenu.clear();
				scheme.addMenuItems(submenu);
				submenu.refresh();
			}
		});
		
		submenu.setControlItem(RewardSchemes.newMenuItem("Reward Scheme", Material.PAPER, schemeProp), 4);
		
		return submenu;
	}
}
