package au.com.mineauz.minigames;

import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.config.RewardsFlag;
import au.com.mineauz.minigames.events.StartGlobalMinigameEvent;
import au.com.mineauz.minigames.events.StopGlobalMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MinigameTypeBase;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.modules.*;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MinigameManager {
    private Map<String, Minigame> minigames = new HashMap<>();
    private Map<String, Configuration> configs = new HashMap<>();
    private Map<MinigameType, MinigameTypeBase> minigameTypes = new HashMap<>();
    private Map<String, PlayerLoadout> globalLoadouts = new HashMap<>();
    private Map<String, RewardsFlag> rewardSigns = new HashMap<>();
    private static Minigames plugin = Minigames.getPlugin();
    private MinigameSave rewardSignsSave = null;
    private Map<Minigame, List<String>> claimedScoreSignsRed = new HashMap<>();
    private Map<Minigame, List<String>> claimedScoreSignsBlue = new HashMap<>();

    private List<Class<? extends MinigameModule>> modules = new ArrayList<>();

    public MinigameManager() {
        
        modules.add(LoadoutModule.class);
        modules.add(LobbySettingsModule.class);
        modules.add(TeamsModule.class);
        modules.add(WeatherTimeModule.class);
        modules.add(TreasureHuntModule.class);
        modules.add(InfectionModule.class);
        modules.add(GameOverModule.class);
        modules.add(JuggernautModule.class);
        modules.add(RewardsModule.class);
        modules.add(CTFModule.class);
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
                caller.sendMessage(MinigameUtils.getLang("minigame.error.invalidMechanic"), MinigameMessageType.ERROR);
        }
        else if(!canStart){
            if(caller == null)
                Bukkit.getLogger().info(MinigameUtils.getLang("minigame.error.mechanicStartFail"));
            else
                caller.sendMessage(MinigameUtils.getLang("minigame.error.mechanicStartFail"), MinigameMessageType.ERROR);
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
                if(mg.equalsIgnoreCase(minigame) || mg.toLowerCase().startsWith(minigame.toLowerCase())){
                    hasmg = true;
                    break;
                }
            }
        }
        return hasmg;
    }
    
    public void removeMinigame(String minigame){
        minigames.remove(minigame);
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
        configs.remove(filename);
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

    public void addBlockRecorderData(Minigame minigame) {
        if (minigame.getBlockRecorder().hasRegenArea() && !minigame.getBlockRecorder().hasCreatedRegenBlocks()) {
            RecorderData d = minigame.getBlockRecorder();
            d.setCreatedRegenBlocks(true);
            Location cur = new Location(minigame.getRegenArea1().getWorld(), 0, 0, 0);
            for (double y = d.getRegenMinY(); y <= d.getRegenMaxY(); y++) {
                cur.setY(y);
                for (double x = d.getRegenMinX(); x <= d.getRegenMaxX(); x++) {
                    cur.setX(x);
                    for (double z = d.getRegenMinZ(); z <= d.getRegenMaxZ(); z++) {
                        cur.setZ(z);
                        d.addBlock(cur.getBlock(), null);
                    }
                }
            }
        }
        
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
//        Minigames.log.info("Loaded " + minigameType.getType().getName() + " minigame type."); //DEBUG
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
        List<String> list = new ArrayList<>();
        for(MinigameType type : getMinigameTypes()){
            list.add(type.getName());
        }
        return list;
    }
    
    public void addLoadout(String name){
        globalLoadouts.put(name, new PlayerLoadout(name));
    }
    
    public void deleteLoadout(String name){
        globalLoadouts.remove(name);
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
        return !globalLoadouts.isEmpty();
    }
    
    public boolean hasLoadout(String name){
        return globalLoadouts.containsKey(name);
    }

    public void sendMinigameMessage(Minigame minigame, String message) {
        sendMinigameMessage(minigame, message, MinigameMessageType.INFO);
    }

    public void sendMinigameMessage(Minigame minigame, String message, MinigameMessageType type) {
        sendMinigameMessage(minigame, message, type, (List<MinigamePlayer>) null);
    }

    public void sendMinigameMessage(Minigame minigame, String message, MinigameMessageType type, MinigamePlayer exclude) {
        sendMinigameMessage(minigame, message, type, Collections.singletonList(exclude));
    }

    public void sendMinigameMessage(Minigame minigame, String message, MinigameMessageType type, List<MinigamePlayer> exclude) {
        if(!minigame.getShowPlayerBroadcasts())return;
        String finalMessage = "";
        if (type == null) type = MinigameMessageType.INFO;
        switch (type) {
            case ERROR:
                finalMessage = ChatColor.RED + "[Minigames] " + ChatColor.WHITE;
                break;
            case INFO:
            default:
                finalMessage = ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE;
                break;
        }
        finalMessage += message;
        List<MinigamePlayer> sendto = new ArrayList<>();
        Collections.copy(minigame.getPlayers(), sendto);
        sendto.addAll(minigame.getSpectators());
        if (exclude != null) {
            sendto.removeAll(exclude);
        }
        for (MinigamePlayer pl : sendto) {
            pl.sendInfoMessage(finalMessage);
        }
    }
    
    public void addRewardSign(Location loc){
        RewardsFlag flag = new RewardsFlag(new Rewards(), MinigameUtils.createLocationID(loc));
        rewardSigns.put(MinigameUtils.createLocationID(loc), flag);
    }
    
    public Rewards getRewardSign(Location loc){
        return rewardSigns.get(MinigameUtils.createLocationID(loc)).getFlag();
    }
    
    public boolean hasRewardSign(Location loc){
        return rewardSigns.containsKey(MinigameUtils.createLocationID(loc));
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
        RewardsFlag reward = rewardSigns.get(id);
        if(rewardSignsSave == null)
            loadRewardSignsFile();
        FileConfiguration cfg = rewardSignsSave.getConfig();
        cfg.set(id, null);
        reward.saveValue("", cfg);
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
            RewardsFlag rew = new RewardsFlag(new Rewards(), id);
            rew.loadValue("", cfg);
            
            rewardSigns.put(id, rew);
        }
    }
    
    public boolean hasClaimedScore(Minigame mg, Location loc, int team){
        String id = MinigameUtils.createLocationID(loc);
        if(team == 0){
            return claimedScoreSignsRed.containsKey(mg) && claimedScoreSignsRed.get(mg).contains(id);
        }
        else{
            return claimedScoreSignsBlue.containsKey(mg) && claimedScoreSignsBlue.get(mg).contains(id);
        }
    }
    
    public void addClaimedScore(Minigame mg, Location loc, int team){
        String id = MinigameUtils.createLocationID(loc);
        if(team == 0){
            if(!claimedScoreSignsRed.containsKey(mg))
                claimedScoreSignsRed.put(mg, new ArrayList<>());
            claimedScoreSignsRed.get(mg).add(id);
        }
        else{
            if(!claimedScoreSignsBlue.containsKey(mg))
                claimedScoreSignsBlue.put(mg, new ArrayList<>());
            claimedScoreSignsBlue.get(mg).add(id);
        }
    }
    
    public void clearClaimedScore(Minigame mg){
        claimedScoreSignsRed.remove(mg);
        claimedScoreSignsBlue.remove(mg);
    }

    public boolean minigameMechanicCheck(Minigame minigame, MinigamePlayer player) {
        return minigame.getMechanic() == null || minigame.getMechanic().checkCanStart(minigame, player);
    }

    public boolean minigameStartStateCheck(Minigame minigame, MinigamePlayer player) {
        if (!minigame.isEnabled() && !player.getPlayer().hasPermission("minigame.join.disabled")) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.notEnabled"), MinigameMessageType.ERROR);
            return false;
        } else if (!minigameMechanicCheck(minigame, player)) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.mechanicStartFail"), MinigameMessageType.ERROR);
            return false;
        } else if (minigame.getState() == MinigameState.REGENERATING) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.regenerating"), MinigameMessageType.ERROR);
            return false;
        } else if (minigame.getState() == MinigameState.STARTED && !minigame.canLateJoin()) {
            player.sendMessage(MinigameUtils.getLang("minigame.started"), MinigameMessageType.ERROR);
            return false;
        }
        return true;
    }

    public boolean minigameStartSetupCheck(Minigame minigame, MinigamePlayer player) {
        if (minigame.getEndPosition() == null) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noEnd"), MinigameMessageType.ERROR);
            return false;
        } else if (minigame.getQuitPosition() == null) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), MinigameMessageType.ERROR);
            return false;
        } else if ((minigame.getType() == null) || minigameType(minigame.getType()).cannotStart(minigame, player)) { //type specific reasons we cannot start.
            player.sendMessage(MinigameUtils.getLang("minigame.error.invalidType"), MinigameMessageType.ERROR);
            return false;
        } else if (!minigame.getMechanic().validTypes().contains(minigame.getType())) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.invalidType"), MinigameMessageType.ERROR);
            return false;
        } else if (minigame.getStartLocations().size() <= 0 ||
                (minigame.isTeamGame() && !TeamsModule.getMinigameModule(minigame).hasTeamStartLocations())) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noStart"), MinigameMessageType.ERROR);
            return false;
        }
        return true;
    }

    public boolean teleportPlayerOnJoin(Minigame minigame, MinigamePlayer player) {
        return minigameType(minigame.getType()).teleportOnJoin(player, minigame);
    }

}
