package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardScheme;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardSchemes;
import au.com.mineauz.minigames.minigame.reward.scheme.StandardRewardScheme;
import au.com.mineauz.minigames.stats.StoredGameStats;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

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
    public Map<String, Flag<?>> getFlags() {
        return scheme.getFlags();
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(FileConfiguration config) {
        String name = RewardSchemes.getName(scheme.getClass());
        
        ConfigurationSection root = config.getConfigurationSection(getMinigame().getName(false));
        root.set("reward-scheme", name);
        
        ConfigurationSection rewards = root.createSection("rewards");
        scheme.save(rewards);
    }

    @Override
    public void load(FileConfiguration config) {
        ConfigurationSection root = config.getConfigurationSection(getMinigame().getName(false));
        if (root != null) {
            String name = root.getString("reward-scheme", "standard");

            scheme = RewardSchemes.createScheme(name);
            if (scheme == null) {
                scheme = new StandardRewardScheme();
            }

            ConfigurationSection rewards = root.getConfigurationSection("rewards");
            scheme.load(rewards);
        }
    }

    @Override
    public void addEditMenuOptions(final Menu menu) {
        MenuItemCustom launcher = new MenuItemCustom("Reward Settings", Material.DIAMOND);
        launcher.setClick(object -> {
            Menu submenu = createSubMenu(menu);
            submenu.displayMenu(menu.getViewer());
            return null;
        });
        
        menu.addItem(launcher);
    }
    
    private Menu createSubMenu(final Menu parent) {
        final Menu submenu = new Menu(6, "Reward Settings", parent.getViewer());
        scheme.addMenuItems(submenu);
        
        submenu.addItem(RewardSchemes.newMenuItem("Reward Scheme", Material.PAPER, new Callback<Class<? extends RewardScheme>>() {
            @Override
            public void setValue(Class<? extends RewardScheme> value) {
                scheme = RewardSchemes.createScheme(value);
                // Update the menu
                Menu menu = createSubMenu(parent);
                menu.displayMenu(submenu.getViewer());
            }
            
            @Override
            public Class<? extends RewardScheme> getValue() {
                return scheme.getClass();
            }
        }), submenu.getSize() - 1);
        
        submenu.addItem(new MenuItemBack(parent), submenu.getSize() - 9);
        return submenu;
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
