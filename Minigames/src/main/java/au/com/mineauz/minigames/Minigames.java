package au.com.mineauz.minigames;

import au.com.mineauz.minigames.backend.BackendManager;
import au.com.mineauz.minigames.blockRecorder.BasicRecorder;
import au.com.mineauz.minigames.commands.CommandDispatcher;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.gametypes.SingleplayerType;
import au.com.mineauz.minigames.mechanics.TreasureHuntMechanic;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;
import au.com.mineauz.minigames.signs.SignBase;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Minigames extends JavaPlugin{
	static Logger log = Logger.getLogger("Minecraft");
	private static Minigames plugin;
	private MinigamePlayerManager playerManager;
	public DisplayManager display;
	private MinigameManager minigameManager;
    private static Economy econ = null;
	private static SignBase minigameSigns;
	private FileConfiguration lang = null;
	private FileConfiguration defLang = null;
	private boolean debug = false;

	private long lastUpdateCheck = 0;
	
	private BackendManager backend;
	
	public static Minigames getPlugin() {
		return plugin;
	}
	
	public void onDisable() {
		if (getPlugin() == null) {
			return;
		}
		PluginDescriptionFile desc = this.getDescription();
		
		for (Player p : getServer().getOnlinePlayers()) {
			if (getPlayerManager().getMinigamePlayer(p).isInMinigame()) {
				getPlayerManager().quitMinigame(getPlayerManager().getMinigamePlayer(p), true);
			}
		}
		for (Minigame minigame : getMinigameManager().getAllMinigames().values()) {
			if (minigame.getType() == MinigameType.GLOBAL &&
					minigame.getMechanicName().equals("treasure_hunt") &&
					minigame.isEnabled()) {
				if (minigame.getMinigameTimer() != null)
					minigame.getMinigameTimer().stopTimer();
				TreasureHuntMechanic.removeTreasure(minigame);
			}
		}
		for (Minigame mg : getMinigameManager().getAllMinigames().values()) {
			mg.saveMinigame();
		}
		
		backend.shutdown();
		getPlayerManager().saveDeniedCommands();
		
		MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
		if (getMinigameManager().hasLoadouts()) {
			for (String loadout : getMinigameManager().getLoadouts()) {
				for (Integer slot : getMinigameManager().getLoadout(loadout).getItems()) {
					globalLoadouts.getConfig().set(loadout + "." + slot, getMinigameManager().getLoadout(loadout).getItem(slot));
				}
				if (!getMinigameManager().getLoadout(loadout).getAllPotionEffects().isEmpty()) {
					for (PotionEffect eff : getMinigameManager().getLoadout(loadout).getAllPotionEffects()) {
						globalLoadouts.getConfig().set(loadout + ".potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
						globalLoadouts.getConfig().set(loadout + ".potions." + eff.getType().getName() + ".dur", eff.getDuration());
					}
				} else {
					globalLoadouts.getConfig().set(loadout + ".potions", null);
				}
				if (getMinigameManager().getLoadout(loadout).getUsePermissions()) {
					globalLoadouts.getConfig().set(loadout + ".usepermissions", true);
				} else {
					globalLoadouts.getConfig().set(loadout + ".usepermissions", null);
				}
			}
		} else {
			globalLoadouts.getConfig().set("globalloadouts", null);
		}
		globalLoadouts.saveConfig();
		getMinigameManager().saveRewardSigns();
		log.info(desc.getName() + " successfully disabled.");
		
	}

	public void onEnable(){
		try {
			plugin = this;
			PluginDescriptionFile desc = this.getDescription();
			
			MinigameSave sv = new MinigameSave("lang/" + getConfig().getString("lang"));
			lang = sv.getConfig();
			loadLang();
			lang.setDefaults(defLang);
			getLogger().info("Using lang " + getConfig().getString("lang"));
            loadPresets();
            setupMinigames();
			if(!setupEconomy()){
		        getLogger().info("No Vault plugin found! You may only reward items.");
			 }
			
			backend = new BackendManager(getLogger());
			if (!backend.initialize(getConfig())) {
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			
			getConfig().options().copyDefaults(true);
			saveConfig();
			//		playerManager.loadDCPlayers();
			getPlayerManager().loadDeniedCommands();
			
			MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
			Set<String> keys = globalLoadouts.getConfig().getKeys(false);
			for(String loadout : keys){
				getMinigameManager().addLoadout(loadout);
				Set<String> items = globalLoadouts.getConfig().getConfigurationSection(loadout).getKeys(false);
	//			for(int i = 0; i < items.size(); i++){
	//				if(globalLoadouts.getConfig().contains(loadout + "." + i))
				//					minigameManager.getLoadout(loadout).addItemToLoadout(globalLoadouts.getConfig().getItemStack(loadout + "." + i));
	//			}
				for(String slot : items){
                    if (slot.matches("[-]?[0-9]+"))
						getMinigameManager().getLoadout(loadout).addItem(globalLoadouts.getConfig().getItemStack(loadout + "." + slot), Integer.parseInt(slot));
				}
				if(globalLoadouts.getConfig().contains(loadout + ".potions")){
					Set<String> pots = globalLoadouts.getConfig().getConfigurationSection(loadout + ".potions").getKeys(false);
					for(String eff : pots){
						if(PotionEffectType.getByName(eff) != null){
							PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
									globalLoadouts.getConfig().getInt(loadout + ".potions." + eff + ".dur"),
									globalLoadouts.getConfig().getInt(loadout + ".potions." + eff + ".amp"));
							getMinigameManager().getLoadout(loadout).addPotionEffect(effect);
						}
					}
				}
				if(globalLoadouts.getConfig().contains(loadout + ".usepermissions")){
					getMinigameManager().getLoadout(loadout).setUsePermissions(globalLoadouts.getConfig().getBoolean(loadout + ".usepermissions"));
				}
			}
			
			minigameSigns = new SignBase();
			getMinigameManager().loadRewardSigns();
			
			CommandDispatcher disp = new CommandDispatcher();
			getCommand("minigame").setExecutor(disp);
			getCommand("minigame").setTabCompleter(disp);
			
			for(Player player : getServer().getOnlinePlayers()){
				getPlayerManager().addMinigamePlayer(player);
			}
			
			
	
			log.info(desc.getName() + " successfully enabled.");
		} catch (Throwable e) {
			plugin = null;
			getLogger().log(Level.SEVERE, "Failed to enable Minigames " + getDescription().getVersion() + ": ", e);
			getPluginLoader().disablePlugin(this);
		}
	}
    private void loadPresets(){
        String prespath = getDataFolder() + "/presets/";
        String[] presets = {"spleef", "lms", "ctf", "infection"};
        File pres;
        for(String preset : presets){
            pres = new File(prespath + preset + ".yml");
            if(!pres.exists()){
                saveResource("presets/" + preset + ".yml", false);
            }
        }
    }
	private void setupMinigames(){

        minigameManager = new MinigameManager();
        playerManager = new MinigamePlayerManager();
        display = new DisplayManager();

        getMinigameManager().addMinigameType(new SingleplayerType());
        getMinigameManager().addMinigameType(new MultiplayerType());

        MinigameSave completion = new MinigameSave("completion");
        getMinigameManager().addConfigurationFile("completion", completion.getConfig());

        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getPluginManager().registerEvents(new BasicRecorder(), this);

        try{
            this.getConfig().load(this.getDataFolder() + "/config.yml");
            List<String> mgs = new ArrayList<>();
            if(getConfig().contains("minigames")){
                mgs = getConfig().getStringList("minigames");
            }
            debug = getConfig().getBoolean("debug", false);
            final List<String> allMGS = new ArrayList<String>(mgs);

            if(!mgs.isEmpty()){
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {

                    @Override
                    public void run() {
                        for(String minigame : allMGS){
                            final Minigame game = new Minigame(minigame);
                            try{
                                game.loadMinigame();
                                getMinigameManager().addMinigame(game);
                            }
                            catch(Exception e){
                                getLogger().severe(ChatColor.RED.toString() + "Failed to load \"" + minigame +"\"! The configuration file may be corrupt or missing!");
                                e.printStackTrace();
                            }
                        }
                    }
                }, 1L);
            }
        }
        catch(FileNotFoundException ex){
            log.info("Failed to load config, creating one.");
            try{
                this.getConfig().save(this.getDataFolder() + "/config.yml");
            }
            catch(IOException e){
                log.log(Level.SEVERE, "Could not save config.yml!");
                e.printStackTrace();
            }
        }
        catch(Exception e){
            log.log(Level.SEVERE, "Failed to load config!");
            e.printStackTrace();
        }
    }
	
	private boolean setupEconomy(){
        if(getServer().getPluginManager().getPlugin("Vault") == null){
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null){
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public boolean hasEconomy(){
		return econ != null;
	}
	
	public Economy getEconomy(){
		return econ;
	}

	/**
	 * use {@link #getPlayerManager()}
	 *
	 * @return MinigamePlayeManager
	 */
	@Deprecated
	public MinigamePlayerManager getPlayerData() {
		return getPlayerManager();
	}

	public void newPlayerManager() {
		playerManager = new MinigamePlayerManager();
	}

	/**
	 * use {@link #getMinigameManager()}
	 *
	 * @return MinigameManager
	 */
	@Deprecated
	public MinigameManager getMinigameData() {
		return getMinigameManager();
	}


	public void newMinigameManager(){
		minigameManager = new MinigameManager();
	}
	
	public BackendManager getBackend() {
		return backend;
	}
	@Deprecated
	public long getLastUpdateCheck(){
		return lastUpdateCheck;
	}
	@Deprecated
	public void setLastUpdateCheck(long time){
		lastUpdateCheck = time;
	}
	
	public SignBase getMinigameSigns(){
		return minigameSigns;
	}
	
	
	public FileConfiguration getLang(){
		return lang;
	}
	
	private void loadLang(){
		InputStream is = getClassLoader().getResourceAsStream("lang/en_AU.yml");
		OutputStream os = null;
		try {
			os = new FileOutputStream(getDataFolder() + "/lang/en_AU.yml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		byte[] buffer = new byte[4096];
		int length;
		try {
			while ((length = is.read(buffer)) > 0) {
			    os.write(buffer, 0, length);
			}
			
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MinigameSave svb = new MinigameSave("lang/en_AU");
		defLang = svb.getConfig();
	}
	
	public void queueStatSave(final StoredGameStats saveData, final boolean winner) {
		MinigameUtils.debugMessage("Scheduling SQL data save for " + saveData);
		
		ListenableFuture<Long> winCountFuture = backend.loadSingleStat(saveData.getMinigame(), MinigameStats.Wins, StatValueField.Total, saveData.getPlayer().getUUID());
		backend.saveStats(saveData);
		
		backend.addServerThreadCallback(winCountFuture, new FutureCallback<Long>() {
			@Override
			public void onFailure(Throwable t) {
			}
			
			@Override
			public void onSuccess(Long winCount) {
				Minigame minigame = saveData.getMinigame();
				MinigamePlayer player = saveData.getPlayer();
				
				// Do rewards
				if (winner) {
					RewardsModule.getModule(minigame).awardPlayer(player, saveData, minigame, winCount == 0);
				} else {
					//TODO: RewardsModule reward on loss
				}
			}
		});
	}
	
	public void toggleDebug(){
		debug = !debug;
		backend.toggleDebug();
		if(backend.isDebugging() && !isDebugging())backend.toggleDebug();
	}
	
	public boolean isDebugging(){
		return debug;
	}
	
	public MinigamePlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public MinigameManager getMinigameManager() {
		return minigameManager;
	}
}
