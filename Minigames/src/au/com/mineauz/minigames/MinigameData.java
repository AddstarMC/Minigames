package au.com.mineauz.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.events.StartGlobalMinigameEvent;
import au.com.mineauz.minigames.events.StopGlobalMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MinigameTypeBase;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import au.com.mineauz.minigames.minigame.modules.WeatherTimeModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardItem;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MinigameData {
	private Map<String, Minigame> minigames = new HashMap<String, Minigame>();
	private Map<String, Configuration> configs = new HashMap<String, Configuration>();
	private Map<MinigameType, MinigameTypeBase> minigameTypes = new HashMap<MinigameType, MinigameTypeBase>();
	private Map<String, PlayerLoadout> globalLoadouts = new HashMap<String, PlayerLoadout>();
	private Map<String, Rewards> rewardSigns = new HashMap<String, Rewards>();
	private static Minigames plugin = Minigames.plugin;
	private MinigameSave rewardSignsSave = null;
	private Map<Minigame, List<String>> claimedScoreSignsRed = new HashMap<Minigame, List<String>>();
	private Map<Minigame, List<String>> claimedScoreSignsBlue = new HashMap<Minigame, List<String>>();
	
	private List<Class<? extends MinigameModule>> modules = new ArrayList<Class<? extends MinigameModule>>();
	
	public MinigameData(){
		
		modules.add(LoadoutModule.class);
		modules.add(LobbySettingsModule.class);
		modules.add(TeamsModule.class);
		modules.add(WeatherTimeModule.class);
		modules.add(TreasureHuntModule.class);
	}
	
	public List<Class<? extends MinigameModule>> getModules(){
		return modules;
	}
	
	public void addModule(Class<? extends MinigameModule> module){
		modules.add(module);
	}
	
	public void removeModule(String moduleName, Class<? extends MinigameModule> module){
		for(Minigame mg : getAllMinigames().values()){
			mg.removeModule(moduleName);
		}
		
		modules.remove(module);
	}
	
	public void startGlobalMinigame(Minigame minigame, MinigamePlayer caller){
		boolean canStart = minigame.getMechanic().checkCanStart(minigame, caller);
		if(minigame.getType() == MinigameType.GLOBAL && 
				minigame.getMechanic().validTypes().contains(MinigameType.GLOBAL) &&
				canStart){
			StartGlobalMinigameEvent ev = new StartGlobalMinigameEvent(minigame, caller);
			Bukkit.getPluginManager().callEvent(ev);
			
			minigame.getMechanic().startMinigame(minigame, caller);
			
			minigame.setEnabled(true);
			minigame.saveMinigame();
		}
		else if(!minigame.getMechanic().validTypes().contains(MinigameType.GLOBAL)){
			if(caller == null)
				Bukkit.getLogger().info(MinigameUtils.getLang("minigame.error.invalidMechanic"));
			else
				caller.sendMessage(MinigameUtils.getLang("minigame.error.invalidMechanic"), "error");
		}
		else if(!canStart){
			if(caller == null)
				Bukkit.getLogger().info(MinigameUtils.getLang("minigame.error.mechanicStartFail"));
			else
				caller.sendMessage(MinigameUtils.getLang("minigame.error.mechanicStartFail"), "error");
		}
	}
	
	public void stopGlobalMinigame(Minigame minigame, MinigamePlayer caller){
		if(minigame.getType() == MinigameType.GLOBAL){
			StopGlobalMinigameEvent ev = new StopGlobalMinigameEvent(minigame, caller);
			Bukkit.getPluginManager().callEvent(ev);
			
			minigame.getMechanic().stopMinigame(minigame, caller);

			minigame.setEnabled(false);
			minigame.saveMinigame();
		}
	}
	
	public void addMinigame(Minigame game){
		minigames.put(game.getName(false), game);
	}
	
	public Minigame getMinigame(String minigame){
		if(minigames.containsKey(minigame)){
			return minigames.get(minigame);
		}
		
		for(String mg : minigames.keySet()){
			if(minigame.equalsIgnoreCase(mg) || mg.startsWith(minigame)){
				return minigames.get(mg);
			}
		}
		
		return null;
	}
	
	public Map<String, Minigame> getAllMinigames(){
		return minigames;
	}
	
	public boolean hasMinigame(String minigame){
		boolean hasmg = minigames.containsKey(minigame);
		if(!hasmg){
			for(String mg : minigames.keySet()){
				if(mg.equalsIgnoreCase(minigame) || mg.startsWith(minigame)){
					hasmg = true;
					break;
				}
			}
		}
		return hasmg;
	}
	
	public void removeMinigame(String minigame){
		if(minigames.containsKey(minigame)){
			minigames.remove(minigame);
		}
	}
	
	public void addConfigurationFile(String filename, Configuration config){
		configs.put(filename, config);
	}
	
	public Configuration getConfigurationFile(String filename){
		if(configs.containsKey(filename)){
			return configs.get(filename);
		}
		return null;
	}
	
	public boolean hasConfigurationFile(String filename){
		return configs.containsKey(filename);
	}
	
	public void removeConfigurationFile(String filename){
		if(configs.containsKey(filename)){
			configs.remove(filename);
		}
	}
	
	public Location minigameLocations(String minigame, String type, Configuration save) {
		Double locx = (Double) save.get(minigame + "." + type + ".x");
		Double locy = (Double) save.get(minigame + "." + type + ".y");
		Double locz = (Double) save.get(minigame + "." + type + ".z");
		Float yaw = new Float(save.get(minigame + "." + type + ".yaw").toString());
		Float pitch = new Float(save.get(minigame + "." + type + ".pitch").toString());
		String world = (String) save.get(minigame + "." + type + ".world");
		
		Location loc = new Location(plugin.getServer().getWorld(world), locx, locy, locz, yaw, pitch);
		return loc;
	}
	
	public Location minigameLocationsShort(String minigame, String type, Configuration save) {
		Double locx = (Double) save.get(minigame + "." + type + ".x");
		Double locy = (Double) save.get(minigame + "." + type + ".y");
		Double locz = (Double) save.get(minigame + "." + type + ".z");
		String world = (String) save.get(minigame + "." + type + ".world");
		
		Location loc = new Location(plugin.getServer().getWorld(world), locx, locy, locz);
		return loc;
	}
	
	public void minigameSetLocations(String minigame, Location loc, String type, FileConfiguration save){
		save.set(minigame + "." + type + "." + ".x", loc.getX());
		save.set(minigame + "." + type + "." + ".y", loc.getY());
		save.set(minigame + "." + type + "." + ".z", loc.getZ());
		save.set(minigame + "." + type + "." + ".yaw", loc.getYaw());
		save.set(minigame + "." + type + "." + ".pitch", loc.getPitch());
		save.set(minigame + "." + type + "." + ".world", loc.getWorld().getName());
	}
	
	public void minigameSetLocationsShort(String minigame, Location loc, String type, FileConfiguration save){
		save.set(minigame + "." + type + "." + ".x", loc.getX());
		save.set(minigame + "." + type + "." + ".y", loc.getY());
		save.set(minigame + "." + type + "." + ".z", loc.getZ());
		save.set(minigame + "." + type + "." + ".world", loc.getWorld().getName());
	}
	
	void addMinigameType(MinigameTypeBase minigameType){
		minigameTypes.put(minigameType.getType(), minigameType);
//		Minigames.log.info("Loaded " + minigameType.getType().getName() + " minigame type."); //DEBUG
	}
	
	public MinigameTypeBase minigameType(MinigameType name){
		if(minigameTypes.containsKey(name)){
			return minigameTypes.get(name);
		}
		return null;
	}
	
	public Set<MinigameType> getMinigameTypes(){
		return minigameTypes.keySet();
	}
	
	public List<String> getMinigameTypesList(){
		List<String> list = new ArrayList<String>();
		for(MinigameType type : getMinigameTypes()){
			list.add(type.getName());
		}
		return list;
	}
	
	public void addLoadout(String name){
		globalLoadouts.put(name, new PlayerLoadout(name));
	}
	
	public void deleteLoadout(String name){
		if(globalLoadouts.containsKey(name)){
			globalLoadouts.remove(name);
		}
	}
	
	public Set<String> getLoadouts(){
		return globalLoadouts.keySet();
	}
	
	public Map<String, PlayerLoadout> getLoadoutMap(){
		return globalLoadouts;
	}
	
	public PlayerLoadout getLoadout(String name){
		PlayerLoadout pl = null;
		if(globalLoadouts.containsKey(name)){
			pl = globalLoadouts.get(name);
		}
		return pl;
	}
	
	public boolean hasLoadouts(){
		if(globalLoadouts.isEmpty()){
			return false;
		}
		return true;
	}
	
	public boolean hasLoadout(String name){
		return globalLoadouts.containsKey(name);
	}
	
	public void sendMinigameMessage(Minigame minigame, String message, String type, MinigamePlayer exclude){
		String finalMessage = "";
		if(type == null){
			type = "info";
		}
		if(type.equals("error")){
			finalMessage = ChatColor.RED + "[Minigames] " + ChatColor.WHITE;
		}
		else{
			finalMessage = ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE;
		}
		
		finalMessage += message;
		for(MinigamePlayer pl : minigame.getPlayers()){
			if(exclude == null || exclude != pl)
				pl.sendMessage(finalMessage);
		}
		for(MinigamePlayer pl : minigame.getSpectators()){
			if(exclude == null || exclude != pl)
				pl.sendMessage(finalMessage);
		}
	}
	
	public void addRewardSign(Location loc){
		rewardSigns.put(MinigameUtils.createLocationID(loc), new Rewards());
	}
	
	public Rewards getRewardSign(Location loc){
		return rewardSigns.get(MinigameUtils.createLocationID(loc));
	}
	
	public boolean hasRewardSign(Location loc){
		if(rewardSigns.containsKey(MinigameUtils.createLocationID(loc)))
			return true;
		return false;
	}
	
	public void removeRewardSign(Location loc){
		String locid = MinigameUtils.createLocationID(loc);
		if(rewardSigns.containsKey(locid)){
			rewardSigns.remove(locid);
			if(rewardSignsSave == null)
				loadRewardSignsFile();
			rewardSignsSave.getConfig().set(locid, null);
			rewardSignsSave.saveConfig();
			rewardSignsSave = null;
		}
	}
	
	public void saveRewardSigns(){
		for(String rew : rewardSigns.keySet()){
			saveRewardSign(rew, false);
		}
		if(rewardSignsSave != null){
			rewardSignsSave.saveConfig();
			rewardSignsSave = null;
		}
	}
	
	public void saveRewardSign(String id, boolean save){
		Rewards reward = rewardSigns.get(id);
		if(rewardSignsSave == null)
			loadRewardSignsFile();
		FileConfiguration cfg = rewardSignsSave.getConfig();
		int count = 0;
		cfg.set(id, null);
		for(RewardItem item : reward.getRewards()){
			if(item.getItem() != null){
				cfg.set(id + "." + count + ".item", item.getItem());
				cfg.set(id + "." + count + ".rarity", item.getRarity().toString());
			}
			else if(item.getMoney() != 0){
				cfg.set(id + "." + count + ".money", item.getMoney());
				cfg.set(id + "." + count + ".rarity", item.getRarity().toString());
			}
			count++;
		}
		for(RewardGroup group : reward.getGroups()){
			count = 0;
			for(RewardItem item : group.getItems()){
				if(item.getItem() != null){
					cfg.set(id + "." + group.getName() + "." + count + ".item", item.getItem());
				}
				else if(item.getMoney() != 0){
					cfg.set(id + "." + group.getName() + "." + count + ".money", item.getMoney());
				}
				count++;
			}
			cfg.set(id + "." + group.getName() + ".rarity", group.getRarity().toString());
		}
		if(save){
			rewardSignsSave.saveConfig();
			rewardSignsSave = null;
		}
	}
	
	public void loadRewardSignsFile(){
		rewardSignsSave = new MinigameSave("rewardSigns");
	}
	
	public void loadRewardSigns(){
		if(rewardSignsSave == null)
			loadRewardSignsFile();
		
		FileConfiguration cfg = rewardSignsSave.getConfig();
		Set<String> keys = cfg.getKeys(false);
		for(String id : keys){
			Rewards rew = new Rewards();
			Set<String> items = cfg.getConfigurationSection(id).getKeys(false);
			for(String item : items){
				if(item.matches("[0-9]+")){
					RewardRarity rar = RewardRarity.valueOf(cfg.getString(id + "." + item + ".rarity"));
					if(cfg.contains(id + "." + item + ".item"))
						rew.addItem(cfg.getItemStack(id + "." + item + ".item") , rar);
					else
						rew.addMoney(cfg.getDouble(id + "." + item + ".money"), rar);
				}
				else{
					RewardGroup gr = rew.addGroup(item, RewardRarity.valueOf(cfg.getString(id + "." + item + ".rarity")));
					Set<String> gItems = cfg.getConfigurationSection(id + "." + item).getKeys(false);
					for(String gItem : gItems){
						if(gItem.matches("[0-9]+")){
							RewardRarity rar = RewardRarity.NORMAL;
							if(cfg.contains(id + "." + item + "." + gItem + ".item"))
								gr.addItem(new RewardItem(cfg.getItemStack(id + "." + item + "." + gItem + ".item") , rar));
							else
								gr.addItem(new RewardItem(cfg.getDouble(id + "." + item + "." + gItem + ".money"), rar));
						}
					}
				}
			}
			rewardSigns.put(id, rew);
		}
	}
	
	public boolean hasClaimedScore(Minigame mg, Location loc, int team){
		String id = MinigameUtils.createLocationID(loc);
		if(team == 0){
			if(claimedScoreSignsRed.containsKey(mg) && claimedScoreSignsRed.get(mg).contains(id))
				return true;
		}
		else{
			if(claimedScoreSignsBlue.containsKey(mg) && claimedScoreSignsBlue.get(mg).contains(id))
				return true;
		}
		return false;
	}
	
	public void addClaimedScore(Minigame mg, Location loc, int team){
		String id = MinigameUtils.createLocationID(loc);
		if(team == 0){
			if(!claimedScoreSignsRed.containsKey(mg))
				claimedScoreSignsRed.put(mg, new ArrayList<String>());
			claimedScoreSignsRed.get(mg).add(id);
		}
		else{
			if(!claimedScoreSignsBlue.containsKey(mg))
				claimedScoreSignsBlue.put(mg, new ArrayList<String>());
			claimedScoreSignsBlue.get(mg).add(id);
		}
	}
	
	public void clearClaimedScore(Minigame mg){
		if(claimedScoreSignsRed.containsKey(mg))
			claimedScoreSignsRed.remove(mg);
		if(claimedScoreSignsBlue.containsKey(mg))
			claimedScoreSignsBlue.remove(mg);
	}
}
