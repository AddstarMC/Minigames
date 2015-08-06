package au.com.mineauz.minigames;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import au.com.mineauz.minigames.Metrics.Graph;
import au.com.mineauz.minigames.backend.BackendManager;
import au.com.mineauz.minigames.blockRecorder.BasicRecorder;
import au.com.mineauz.minigames.commands.CommandDispatcher;
import au.com.mineauz.minigames.display.DisplayManager;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MultiplayerType;
import au.com.mineauz.minigames.gametypes.SingleplayerType;
import au.com.mineauz.minigames.mechanics.TreasureHuntMechanic;
import au.com.mineauz.minigames.menu.MenuListener;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;
import au.com.mineauz.minigames.signs.SignBase;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatValueField;
import au.com.mineauz.minigames.stats.StoredGameStats;

public class Minigames extends JavaPlugin{
	public PlayerData pdata;
	public MinigameData mdata;
	public DisplayManager display;
	public ModuleManager modules;
	
	public static Minigames plugin;
    private static Economy econ = null;
	private static SignBase minigameSigns;
	private FileConfiguration lang = null;
	private FileConfiguration defLang = null;
	public boolean thrownError = false;
	private boolean debug = false;
	
	private long lastUpdateCheck = 0;
	
	private BackendManager backend;
	
	public void onEnable(){
		try {
			plugin = this;
			PluginDescriptionFile desc = this.getDescription();
			
			MinigameSave sv = new MinigameSave("lang/" + getConfig().getString("lang"));
			lang = sv.getConfig();
			loadLang();
			lang.setDefaults(defLang);
			
			getLogger().info("Using lang " + getConfig().getString("lang"));
			
			String prespath = getDataFolder() + "/presets/";
			String[] presets = {"spleef", "lms", "ctf", "infection"};
			File pres;
			for(String preset : presets){
				pres = new File(prespath + preset + ".yml");
				if(!pres.exists()){
					saveResource("presets/" + preset + ".yml", false);
				}
			}
			
			mdata = new MinigameData();
			pdata = new PlayerData();
			display = new DisplayManager();
			modules = new ModuleManager(this);
			
			mdata.addMinigameType(new SingleplayerType());
	//		mdata.addMinigameType(new FreeForAllType());
	//		mdata.addMinigameType(new TeamsType());
			mdata.addMinigameType(new MultiplayerType());
			
			MinigameSave completion = new MinigameSave("completion");
			mdata.addConfigurationFile("completion", completion.getConfig());
			
			getServer().getPluginManager().registerEvents(new Events(), this);
			getServer().getPluginManager().registerEvents(new MenuListener(), this);
			getServer().getPluginManager().registerEvents(new BasicRecorder(), this);
			
			try{
				this.getConfig().load(this.getDataFolder() + "/config.yml");
				List<String> mgs = new ArrayList<String>();
				if(getConfig().contains("minigames")){
					mgs = getConfig().getStringList("minigames");
				}
				final List<String> allMGS = new ArrayList<String>();
				allMGS.addAll(mgs);
				
				if(!mgs.isEmpty()){
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							for(String minigame : allMGS){
								final Minigame game = new Minigame(minigame);
								try{
									game.loadMinigame();
									mdata.addMinigame(game);
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
				getLogger().info("Failed to load config, creating one.");
				try{
					this.getConfig().save(this.getDataFolder() + "/config.yml");
				} 
				catch(IOException e){
					getLogger().severe("Could not save config.yml!");
					e.printStackTrace();
				}
			}
			catch(Exception e){
				getLogger().severe("Failed to load config!");
				e.printStackTrace();
			}
			
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
			
			Calendar cal = Calendar.getInstance();
			if(cal.get(Calendar.DAY_OF_MONTH) == 21 && cal.get(Calendar.MONTH) == 8 ||
					cal.get(Calendar.DAY_OF_MONTH) == 25 && cal.get(Calendar.MONTH) == 11 ||
					cal.get(Calendar.DAY_OF_MONTH) == 1 && cal.get(Calendar.MONTH) == 0){
				getLogger().info(ChatColor.GREEN.name() + "Party Mode enabled!");
				pdata.setPartyMode(true);
			}
			
	//		pdata.loadDCPlayers();
			pdata.loadDeniedCommands();
			
			MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
			Set<String> keys = globalLoadouts.getConfig().getKeys(false);
			for(String loadout : keys){
				mdata.addLoadout(loadout);
				Set<String> items = globalLoadouts.getConfig().getConfigurationSection(loadout).getKeys(false);
	//			for(int i = 0; i < items.size(); i++){
	//				if(globalLoadouts.getConfig().contains(loadout + "." + i))
	//					mdata.getLoadout(loadout).addItemToLoadout(globalLoadouts.getConfig().getItemStack(loadout + "." + i));
	//			}
				for(String slot : items){
					if(slot.matches("[0-9]+"))
						mdata.getLoadout(loadout).addItem(globalLoadouts.getConfig().getItemStack(loadout + "." + slot), Integer.parseInt(slot));
				}
				if(globalLoadouts.getConfig().contains(loadout + ".potions")){
					Set<String> pots = globalLoadouts.getConfig().getConfigurationSection(loadout + ".potions").getKeys(false);
					for(String eff : pots){
						if(PotionEffectType.getByName(eff) != null){
							PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
									globalLoadouts.getConfig().getInt(loadout + ".potions." + eff + ".dur"),
									globalLoadouts.getConfig().getInt(loadout + ".potions." + eff + ".amp"));
							mdata.getLoadout(loadout).addPotionEffect(effect);
						}
					}
				}
				if(globalLoadouts.getConfig().contains(loadout + ".usepermissions")){
					mdata.getLoadout(loadout).setUsePermissions(globalLoadouts.getConfig().getBoolean(loadout + ".usepermissions"));
				}
			}
			
			minigameSigns = new SignBase();
			mdata.loadRewardSigns();
			
			CommandDispatcher disp = new CommandDispatcher();
			getCommand("minigame").setExecutor(disp);
			getCommand("minigame").setTabCompleter(disp);
			
			for(Player player : getServer().getOnlinePlayers()){
				pdata.addMinigamePlayer(player);
			}
			
			initMetrics();
	
			getLogger().info(desc.getName() + " successfully enabled.");
		} catch (Throwable e) {
			plugin = null;
			getLogger().log(Level.SEVERE, "Failed to enable Minigames " + getDescription().getVersion() + ": ", e);
			getPluginLoader().disablePlugin(this);
		}
	}

	public void onDisable(){
		if (plugin == null) {
			return;
		}
		
		PluginDescriptionFile desc = this.getDescription();
		getLogger().info(desc.getName() + " successfully disabled.");
		
		for(Player p : getServer().getOnlinePlayers()){
			if(pdata.getMinigamePlayer(p).isInMinigame()){
				pdata.quitMinigame(pdata.getMinigamePlayer(p), true);
			}
		}
		for(Minigame minigame : mdata.getAllMinigames().values()){
			if(minigame.getType() == MinigameType.GLOBAL && 
					minigame.getMechanicName().equals("treasure_hunt") && 
					minigame.isEnabled()){
				if(minigame.getMinigameTimer() != null)
					minigame.getMinigameTimer().stopTimer();
				TreasureHuntMechanic.removeTreasure(minigame);
			}
		}
		for(Minigame mg : mdata.getAllMinigames().values()){
			mg.saveMinigame();
		}
		
		backend.shutdown();
		
//		pdata.saveDCPlayers();
		pdata.saveDeniedCommands();
		
		MinigameSave globalLoadouts = new MinigameSave("globalLoadouts");
		if(mdata.hasLoadouts()){
			for(String loadout : mdata.getLoadouts()){
//				for(int i = 0; i < mdata.getLoadout(loadout).getItems().size(); i++){
//					globalLoadouts.getConfig().set(loadout + "." + i, mdata.getLoadout(loadout).getItems().get(i));
//				}
				for(Integer slot : mdata.getLoadout(loadout).getItems()){
					globalLoadouts.getConfig().set(loadout + "." + slot, mdata.getLoadout(loadout).getItem(slot));
				}
				if(!mdata.getLoadout(loadout).getAllPotionEffects().isEmpty()){
					for(PotionEffect eff : mdata.getLoadout(loadout).getAllPotionEffects()){
						globalLoadouts.getConfig().set(loadout + ".potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
						globalLoadouts.getConfig().set(loadout + ".potions." + eff.getType().getName() + ".dur", eff.getDuration());
					}
				}
				else{
					globalLoadouts.getConfig().set(loadout + ".potions", null);
				}
				if(mdata.getLoadout(loadout).getUsePermissions()){
					globalLoadouts.getConfig().set(loadout + ".usepermissions", true);
				}
				else{
					globalLoadouts.getConfig().set(loadout + ".usepermissions", null);
				}
			}
		}
		else{
			globalLoadouts.getConfig().set("globalloadouts", null);
		}
		globalLoadouts.saveConfig();
		mdata.saveRewardSigns();
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
		if(econ != null){
			return true;
		}
		return false;
	}
	
	public Economy getEconomy(){
		return econ;
	}
	
	public PlayerData getPlayerData(){
		return pdata;
	}
	
	public void newPlayerData(){
		pdata = new PlayerData();
	}
	
	public MinigameData getMinigameData(){
		return mdata;
	}
	
	public void newMinigameData(){
		mdata = new MinigameData();
	}
	
	public BackendManager getBackend() {
		return backend;
	}
	
	public long getLastUpdateCheck(){
		return lastUpdateCheck;
	}
	
	public void setLastUpdateCheck(long time){
		lastUpdateCheck = time;
	}
	
	public SignBase getMinigameSigns(){
		return minigameSigns;
	}
	
	private void initMetrics(){
		try {
		    Metrics metrics = new Metrics(this);
		    
		    Graph playerGraph = metrics.createGraph("Players Playing Minigames");
		    playerGraph.addPlotter(new Metrics.Plotter("Singleplayer") {
				
				@Override
				public int getValue() {
					int count = 0;
					for(MinigamePlayer pl : pdata.getAllMinigamePlayers()){
						if(pl.isInMinigame() && pl.getMinigame().getType() == MinigameType.SINGLEPLAYER){
							count++;
						}
					}
					return count;
				}
			});
		    
		    playerGraph.addPlotter(new Metrics.Plotter("Free For All") {
				
				@Override
				public int getValue() {
					int count = 0;
					for(MinigamePlayer pl : pdata.getAllMinigamePlayers()){
						if(pl.isInMinigame() && !pl.getMinigame().isTeamGame()){
							count++;
						}
					}
					return count;
				}
			});
		    
		    playerGraph.addPlotter(new Metrics.Plotter("Teams") {
				
				@Override
				public int getValue() {
					int count = 0;
					for(MinigamePlayer pl : pdata.getAllMinigamePlayers()){
						if(pl.isInMinigame() && pl.getMinigame().isTeamGame()){
							count++;
						}
					}
					return count;
				}
			});
		    
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
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
					minigame.getModule(RewardsModule.class).awardPlayer(player, saveData, minigame, winCount == 0);
				} else {
					//TODO: RewardsModule reward on loss
				}
			}
		});
	}
	
	public void toggleDebug(){
		if(debug)
			debug = false;
		else
			debug = true;
	}
	
	public boolean isDebugging(){
		return debug;
	}
}
