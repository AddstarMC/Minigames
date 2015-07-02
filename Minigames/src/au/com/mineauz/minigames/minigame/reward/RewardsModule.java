package au.com.mineauz.minigames.minigame.reward;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardScheme;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardSchemes;
import au.com.mineauz.minigames.minigame.reward.scheme.StandardRewardScheme;

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
	
	public void awardPlayer(MinigamePlayer player, Minigame minigame, boolean firstCompletion) {
		scheme.awardPlayer(player, minigame, firstCompletion);
	}

	@Override
	public Map<String, Flag<?>> getFlags() {
		return scheme.getFlags();
	}

	@Override
	public boolean useSeparateConfig() {
		return scheme.useSeparateConfig();
	}

	@Override
	public void save(FileConfiguration config) {
		String name = RewardSchemes.getName(scheme.getClass());
		config.set(getMinigame().getName(false) + ".reward-scheme", name);
	}

	@Override
	public void load(FileConfiguration config) {
		String name = config.getString(getMinigame().getName(false) + ".reward-scheme", "standard");
		
		scheme = RewardSchemes.createScheme(name);
		if (scheme == null) {
			scheme = new StandardRewardScheme();
		}
	}

	@Override
	public void addEditMenuOptions(Menu menu) {
		final Menu submenu = new Menu(6, "Reward Settings", menu.getViewer());
		buildMenu(submenu, menu);
		
		// Add to actual menu
		menu.addItem(new MenuItemPage("Reward Settings", Material.DIAMOND, submenu));
	}
	
	private void buildMenu(final Menu submenu, final Menu parent) {
		scheme.addMenuItems(submenu);
		
		submenu.addItem(RewardSchemes.newMenuItem("Reward Scheme", Material.PAPER, new Callback<Class<? extends RewardScheme>>() {
			@Override
			public void setValue(Class<? extends RewardScheme> value) {
				scheme = RewardSchemes.createScheme(value);
				// Update the menu
				submenu.clearMenu();
				buildMenu(submenu, parent);
			}
			
			@Override
			public Class<? extends RewardScheme> getValue() {
				return scheme.getClass();
			}
		}), submenu.getSize() - 1);
		submenu.addItem(new MenuItemBack(parent), submenu.getSize() - 9);
	}

	@Override
	public boolean displayMechanicSettings(Menu previous) {
		// Not used
		return false;
	}
	
	public static RewardsModule getModule(Minigame minigame) {
		return (RewardsModule)minigame.getModule(Name);
	}
}
