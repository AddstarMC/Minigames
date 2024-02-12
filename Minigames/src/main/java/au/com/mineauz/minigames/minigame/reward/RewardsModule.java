package au.com.mineauz.minigames.minigame.reward;

import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardScheme;
import au.com.mineauz.minigames.minigame.reward.scheme.RewardSchemes;
import au.com.mineauz.minigames.minigame.reward.scheme.StandardRewardScheme;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.stats.StoredGameStats;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RewardsModule extends MinigameModule {
    private RewardScheme scheme;

    public RewardsModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);

        // Default scheme
        scheme = new StandardRewardScheme();
    }

    public static RewardsModule getModule(Minigame minigame) {
        return (RewardsModule) minigame.getModule(MgModules.REWARDS.getName());
    }


    public RewardScheme getScheme() {
        return scheme;
    }

    @SuppressWarnings("unused")
    public void setRewardScheme(RewardScheme scheme) {
        this.scheme = scheme;
    }

    public void awardPlayer(MinigamePlayer player, StoredGameStats data, Minigame minigame, boolean firstCompletion) {
        scheme.awardPlayer(player, data, minigame, firstCompletion);
    }

    public void awardPlayerOnLoss(MinigamePlayer player, StoredGameStats data, Minigame minigame) {
        scheme.awardPlayerOnLoss(player, data, minigame);
    }

    @Override
    public Map<String, Flag<?>> getConfigFlags() {
        return scheme.getFlags();
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(FileConfiguration config) {
        String name = RewardSchemes.getName(scheme.getClass());

        ConfigurationSection root = config.getConfigurationSection(getMinigame().getName());
        if (root != null) {
            root.set("reward-scheme", name);
            ConfigurationSection rewards = root.createSection("rewards");
            scheme.save(rewards);
        }
    }

    @Override
    public void load(FileConfiguration config) {
        ConfigurationSection root = config.getConfigurationSection(getMinigame().getName());
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
        MenuItemCustom launcher = new MenuItemCustom(Material.DIAMOND,
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_SETTINGS_NAME));
        launcher.setClick(() -> {
            Menu submenu = createSubMenu(menu);
            submenu.displayMenu(menu.getViewer());
            return null;
        });

        menu.addItem(launcher);
    }

    private Menu createSubMenu(final Menu parent) {
        final Menu submenu = new Menu(6,
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_SETTINGS_NAME), parent.getViewer());
        scheme.addMenuItems(submenu);

        submenu.addItem(RewardSchemes.newMenuItem(Material.PAPER,
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_SCHEME_NAME), new Callback<>() {
            @Override
            public Class<? extends RewardScheme> getValue() {
                return scheme.getClass();
            }

            @Override
            public void setValue(Class<? extends RewardScheme> value) {
                scheme = RewardSchemes.createScheme(value);
                // Update the menu
                Menu menu = createSubMenu(parent);
                menu.displayMenu(submenu.getViewer());
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
}
