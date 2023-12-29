package au.com.mineauz.minigames;

import au.com.mineauz.minigames.backend.BackendManager;
import au.com.mineauz.minigames.commands.CommandDispatcher;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.gametypes.SingleplayerType;
import au.com.mineauz.minigames.managers.*;
import au.com.mineauz.minigames.mechanics.TreasureHuntMechanic;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ResourcePack;
import au.com.mineauz.minigames.recorder.BasicRecorder;
import au.com.mineauz.minigames.signs.SignBase;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SimpleBarChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class Minigames extends JavaPlugin {
    private static ComponentLogger componentLogger = null;
    private static final Pattern COMPILE = Pattern.compile("[-]?[0-9]+");
    private static Minigames plugin;
    private static Economy econ;
    private static SignBase minigameSigns;
    private static ComparableVersion VERSION;
    private static ComparableVersion PAPER_VERSION;
    private final StartUpLogHandler startUpHandler;
    public DisplayManager display;
    private ResourcePackManager resourceManager;
    private MinigamePlayerManager playerManager;
    private MinigameManager minigameManager;
    private PlaceHolderManager placeHolderManager;
    private boolean debug;
    private boolean hasPAPI = false;
    private long lastUpdateCheck;
    private BackendManager backend;
    private Metrics metrics;

    public Minigames() {
        super();
        startUpHandler = new StartUpLogHandler();
    }

    public static ComparableVersion getVERSION() {
        return VERSION;
    }

    public static Minigames getPlugin() {
        return plugin;
    }

    public static ComponentLogger getCmpnntLogger() {
        if (Minigames.componentLogger == null) {
            Minigames.componentLogger = Minigames.getPlugin().getComponentLogger();
        }

        return Minigames.componentLogger;
    }

    public String getStartupLog() {
        return startUpHandler.getNormalLog();
    }

    public String getStartupExceptionLog() {
        return startUpHandler.getExceptionLog();
    }

    public PlaceHolderManager getPlaceHolderManager() {
        return placeHolderManager;
    }

    public void onDisable() {
        if (getPlugin() == null) {
            this.getComponentLogger().info("Minigames is disabled");
            return;
        }
        final PluginDescriptionFile desc = this.getDescription();

        for (final Player p : this.getServer().getOnlinePlayers()) {
            if (this.playerManager.getMinigamePlayer(p).isInMinigame()) {
                this.playerManager.quitMinigame(this.playerManager.getMinigamePlayer(p), true);
            }
        }
        for (final Minigame minigame : this.minigameManager.getAllMinigames().values()) {
            if (minigame.getType() == MinigameType.GLOBAL &&
                    "treasure_hunt".equals(minigame.getMechanicName())
                    && minigame.isEnabled()) {
                if (minigame.getMinigameTimer() != null) {
                    minigame.getMinigameTimer().stopTimer();
                }
                TreasureHuntMechanic.removeTreasure(minigame);
            }
        }
        for (final Minigame mg : this.minigameManager.getAllMinigames().values()) {
            mg.saveMinigame();
        }

        this.backend.shutdown();
        this.playerManager.saveDeniedCommands();

        final MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
        if (this.minigameManager.hasLoadouts()) {
            for (final String loadout : this.minigameManager.getLoadouts()) {
                for (final Integer slot : this.minigameManager.getLoadout(loadout).getItems()) {
                    globalLoadouts.getConfig().set(loadout + '.' + slot, this.minigameManager.getLoadout(loadout).getItem(slot));
                }
                if (!this.minigameManager.getLoadout(loadout).getAllPotionEffects().isEmpty()) {
                    for (final PotionEffect eff : this.minigameManager.getLoadout(loadout).getAllPotionEffects()) {
                        globalLoadouts.getConfig().set(loadout + ".potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
                        globalLoadouts.getConfig().set(loadout + ".potions." + eff.getType().getName() + ".dur", eff.getDuration());
                    }
                } else {
                    globalLoadouts.getConfig().set(loadout + ".potions", null);
                }
                if (this.minigameManager.getLoadout(loadout).getUsePermissions()) {
                    globalLoadouts.getConfig().set(loadout + ".usepermissions", true);
                } else {
                    globalLoadouts.getConfig().set(loadout + ".usepermissions", null);
                }
            }
        } else {
            globalLoadouts.getConfig().set("globalloadouts", null);
        }
        globalLoadouts.saveConfig();
        this.minigameManager.saveRewardSigns();
        resourceManager.saveResources();
        getCmpnntLogger().info(desc.getName() + " successfully disabled.");
    }

    public void onEnable() {
        this.getLogger().addHandler(startUpHandler);
        ComponentLogger logger = this.getComponentLogger();
        try {
            plugin = this;
            switch (this.checkVersion()) {
                case -1:
                    logger.warn("This version of Minigames (" + VERSION.getCanonical() + ") is designed for Paper Version: " + PAPER_VERSION.getCanonical());
                    logger.warn("Your version is newer: " + Bukkit.getBukkitVersion());
                    logger.warn("Please check for an updated");

                    break;
                case 0:
                    break;
                case 1:
                    if (!this.getConfig().getBoolean("forceload", true)) {
                        logger.warn("This version of Minigames (" + VERSION.getCanonical() + ") " +
                                "is designed for Bukkit Version: " + PAPER_VERSION.getCanonical());
                        logger.warn("Your version is " + Bukkit.getVersion());
                        logger.warn(" Bypass this by setting forceload: true in the config");

                        logger.warn("DISABLING MINIGAMES....");
                        plugin = null;
                        this.onDisable();
                        return;
                    } else {
                        logger.warn("Version incompatible - Force Loading Minigames.");
                        logger.warn("This version of Minigames (" + VERSION.getCanonical() + ") " +
                                "is designed for Bukkit Version: " + PAPER_VERSION.getCanonical());
                        logger.warn("Your version is " + Bukkit.getBukkitVersion());
                    }
            }
            final PluginDescriptionFile desc = this.getDescription();
            ConfigurationSerialization.registerClass(ResourcePack.class);
            MinigameMessageManager.registerCoreLanguage();
            this.checkVersion();
            this.loadPresets();
            this.setupMinigames();
            if (!this.setupEconomy()) {
                this.getLogger().info("No Vault plugin found! You may only reward items.");
            }
            this.backend = new BackendManager(this.getComponentLogger());
            if (!this.backend.initialize(this.getConfig())) {
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
            //        playerManager.loadDCPlayers();
            this.playerManager.loadDeniedCommands();
            setupLoadOuts();
            minigameSigns = new SignBase();
            this.minigameManager.loadRewardSigns();

            final CommandDispatcher disp = new CommandDispatcher();
            PluginCommand command = this.getCommand("minigame");
            if (command == null) {
                throw (new Throwable("Could not find command `minigame`"));
            }
            command.setExecutor(disp);
            command.setTabCompleter(disp);
            for (final Player player : this.getServer().getOnlinePlayers()) {
                this.playerManager.addMinigamePlayer(player);
            }
            try {
                this.initMetrics();
            } catch (final IllegalStateException | NoClassDefFoundError | ExceptionInInitializerError e) {
                logger.info("Metrics will not be available(enabled debug for more details): " + e.getMessage());
                if (this.debug) {
                    logger.info("", e);
                }
            }

            logger.info(desc.getName() + " successfully enabled.");
            this.hookPlaceHolderApi();
        } catch (final Throwable e) {
            plugin = null;
            logger.error("Failed to enable Minigames " + this.getDescription().getVersion() + ": ", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        this.getLogger().removeHandler(startUpHandler);
    }

    private void setupLoadOuts() {
        final MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
        final Set<String> keys = globalLoadouts.getConfig().getKeys(false);
        for (final String loadout : keys) {
            this.minigameManager.addLoadout(loadout);
            ConfigurationSection loadOutSection = globalLoadouts.getConfig().getConfigurationSection(loadout);
            if (loadOutSection != null) {
                final Set<String> items = loadOutSection.getKeys(false);
                for (final String slot : items) {
                    if (COMPILE.matcher(slot).matches()) {
                        this.minigameManager.getLoadout(loadout).addItem(globalLoadouts.getConfig().getItemStack(loadout + '.' + slot), Integer.parseInt(slot));
                    }
                }
            }
            if (globalLoadouts.getConfig().contains(loadout + ".potions")) {
                ConfigurationSection potionLoadOutSection = globalLoadouts.getConfig().getConfigurationSection(loadout + ".potions");
                if (potionLoadOutSection != null) {
                    final Set<String> pots = potionLoadOutSection.getKeys(false);
                    for (final String eff : pots) {
                        PotionEffectType type = PotionEffectType.getByName(eff);
                        if (type != null) {
                            final PotionEffect effect = new PotionEffect(type,
                                    globalLoadouts.getConfig().getInt(loadout + ".potions." + eff + ".dur"),
                                    globalLoadouts.getConfig().getInt(loadout + ".potions." + eff + ".amp"));
                            this.minigameManager.getLoadout(loadout).addPotionEffect(effect);
                        }
                    }
                }
            }
            if (globalLoadouts.getConfig().contains(loadout + ".usepermissions")) {
                this.minigameManager.getLoadout(loadout).setUsePermissions(globalLoadouts.getConfig().getBoolean(loadout + ".usepermissions"));
            }
        }
    }

    private void loadPresets() {
        final String prespath = this.getDataFolder() + "/presets/";
        final String[] presets = {"spleef", "lms", "ctf", "infection"};
        File pres;
        for (final String preset : presets) {
            pres = new File(prespath + preset + ".yml");
            if (!pres.exists()) {
                this.saveResource("presets/" + preset + ".yml", false);
            }
        }
    }

    private void setupMinigames() {
        this.minigameManager = new MinigameManager();
        this.playerManager = new MinigamePlayerManager();
        this.display = new DisplayManager();

        this.resourceManager = new ResourcePackManager();
        final MinigameSave resources = new MinigameSave("resources");
        this.minigameManager.addConfigurationFile("resources", resources.getConfig());
        this.resourceManager.initialize(resources);
        this.minigameManager.addMinigameType(new SingleplayerType());
        this.minigameManager.addMinigameType(new MultiplayerType());

        final MinigameSave completion = new MinigameSave("completion");
        this.minigameManager.addConfigurationFile("completion", completion.getConfig());

        this.getServer().getPluginManager().registerEvents(new Events(), this);
        //always active recorder, don't get confused with RegenRecorder, that is only active, if the minigame has a regen area
        this.getServer().getPluginManager().registerEvents(new BasicRecorder(), this);

        try {
            this.getConfig().load(this.getDataFolder() + "/config.yml");
            List<String> mgs = new ArrayList<>();
            if (this.getConfig().contains("minigames")) {
                mgs = this.getConfig().getStringList("minigames");
            }
            this.debug = this.getConfig().getBoolean("debug", false);
            final List<String> allMGS = new ArrayList<>(mgs);

            if (!mgs.isEmpty()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    for (final String minigame : allMGS) {
                        final Minigame game = new Minigame(minigame);
                        try {
                            game.loadMinigame();
                            this.minigameManager.addMinigame(game);
                        } catch (final Exception e) {
                            this.getLogger().severe(ChatColor.RED + "Failed to load \"" + minigame + "\"! The configuration file may be corrupt or missing!");
                            getCmpnntLogger().error("", e);
                        }
                    }
                }, 1L);
            }
        } catch (final FileNotFoundException ex) {
            this.getComponentLogger().info("Failed to load config, creating one.");
            try {
                this.getConfig().save(this.getDataFolder() + "/config.yml");
            } catch (final IOException e) {
                this.getComponentLogger().error("Could not save config.yml!", e);
            }
        } catch (final Exception e) {
            this.getComponentLogger().error("Failed to load config!", e);
        }

    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private void hookPlaceHolderApi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            hasPAPI = true;
            this.getComponentLogger().info("--------------------");
            this.getComponentLogger().info("Hooking PlaceHolder API");
            placeHolderManager = new PlaceHolderManager(this);
            placeHolderManager.register();
            this.getComponentLogger().info("Adding Placeholders for " + getMinigameManager().getAllMinigames().size() + " games");
            for (Map.Entry<String, Minigame> game : getMinigameManager().getAllMinigames().entrySet()) {
                this.getComponentLogger().trace("Adding Placeholders for " + game.getKey());
                placeHolderManager.addGameIdentifiers(game.getValue());
            }
            this.getComponentLogger().info("PlaceHolders: " + placeHolderManager.getRegisteredPlaceHolders().toString());
            this.getComponentLogger().info("--------------------");
        }
    }

    public boolean hasEconomy() {
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    private int checkVersion() {
        final InputStream stream = this.getResource("minigame.properties");
        final Properties p = new Properties();
        try {
            p.load(stream);
        } catch (final NullPointerException | IOException e) {
            this.getLogger().warning(e.getMessage());
        } finally {
            //noinspection UnstableApiUsage
            Closeables.closeQuietly(stream);
        }

        if (p.containsKey("version")) {
            VERSION = new ComparableVersion(p.getProperty("version"));
            PAPER_VERSION = new ComparableVersion(p.getProperty("paper_version"));
            final ComparableVersion serverversion = new ComparableVersion(this.getServer().getBukkitVersion());
            return PAPER_VERSION.compareTo(serverversion);
        } else {
            return 1;
        }
    }

    /**
     * use {@link #getPlayerManager()}
     *
     * @return MinigamePlayeManager
     */
    @Deprecated
    public MinigamePlayerManager getPlayerData() {
        return this.playerManager;
    }

    /**
     * use {@link #minigameManager}
     *
     * @return MinigameManager
     */
    @Deprecated
    public MinigameManager getMinigameData() {
        return this.minigameManager;
    }

    public BackendManager getBackend() {
        return this.backend;
    }

    @Deprecated
    public long getLastUpdateCheck() {
        return this.lastUpdateCheck;
    }

    @Deprecated
    public void setLastUpdateCheck(final long time) {
        this.lastUpdateCheck = time;
    }

    /**
     * @return Signs
     */
    @SuppressWarnings("unused")
    public SignBase getMinigameSigns() {
        return minigameSigns;
    }

    private void initMetrics() {
        this.metrics = new Metrics(this, 1190);
        final MultiLineChart chart = new MultiLineChart("Players_in_Minigames", () -> {
            final Map<String, Integer> result = new HashMap<>();
            result.put("Total_Players", this.playerManager.getAllMinigamePlayers().size());
            for (final MinigamePlayer pl : this.playerManager.getAllMinigamePlayers()) {
                if (pl.isInMinigame()) {
                    int count = result.getOrDefault(pl.getMinigame().getType().getName(), 0);
                    result.put(pl.getMinigame().getType().getName(), count + 1);
                }
            }
            return result;
        });
        final SimpleBarChart barChart = new SimpleBarChart("Modules_v_Servers", () -> {
            final Map<String, Integer> result = new HashMap<>();
            for (final Class<? extends MinigameModule> module : this.minigameManager.getModules()) {
                result.put(module.getCanonicalName(), 1);
            }
            return result;
        });
        this.metrics.addCustomChart(chart);
        this.metrics.addCustomChart(barChart);
    }

    public void addMetric(final CustomChart chart) {
        this.metrics.addCustomChart(chart);
    }

    public void queueStatSave(final StoredGameStats saveData, final boolean winner) {
        MinigameMessageManager.debugMessage("Scheduling SQL data save for " + saveData);

        final ListenableFuture<Long> winCountFuture = this.backend.loadSingleStat(saveData.getMinigame(), MinigameStats.Wins, StatValueField.Total, saveData.getPlayer().getUUID());
        this.backend.saveStats(saveData);

        this.backend.addServerThreadCallback(winCountFuture, new FutureCallback<>() {
            @Override
            public void onFailure(final @NotNull Throwable t) {
            }

            @Override
            public void onSuccess(final Long winCount) {
                final Minigame minigame = saveData.getMinigame();
                final MinigamePlayer player = saveData.getPlayer();

                // Do rewards
                if (winner) {
                    RewardsModule.getModule(minigame).awardPlayer(player, saveData, minigame, winCount == 0);
                } else {
                    RewardsModule.getModule(minigame).awardPlayerOnLoss(player, saveData, minigame);
                }
            }
        });
    }

    public void toggleDebug() {
        this.debug = !this.debug;
        this.backend.toggleDebug();
        if (this.backend.isDebugging() && !this.debug) {
            this.backend.toggleDebug();
        }
    }

    public boolean isDebugging() {
        return this.debug;
    }

    public MinigamePlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public MinigameManager getMinigameManager() {
        return this.minigameManager;
    }

    public ResourcePackManager getResourceManager() {
        return this.resourceManager;
    }

    public boolean includesPapi() {
        return hasPAPI;
    }
}
