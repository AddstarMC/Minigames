package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.blockRecorder.RecorderData;
import au.com.mineauz.minigames.config.MinigameSave;
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
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ResourcePack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MinigameManager {
    private static final Minigames PLUGIN = Minigames.getPlugin();
    private final Map<String, Minigame> minigames = new HashMap<>();
    private final Map<String, Configuration> configs = new HashMap<>();
    private final Map<MinigameType, MinigameTypeBase> minigameTypes = new HashMap<>();
    private final Map<String, PlayerLoadout> globalLoadouts = new HashMap<>();
    private final Map<String, RewardsFlag> rewardSigns = new HashMap<>();
    private MinigameSave rewardSignsSave;
    private final Map<Minigame, List<String>> claimedScoreSignsRed = new HashMap<>();
    private final Map<Minigame, List<String>> claimedScoreSignsBlue = new HashMap<>();

    private final List<Class<? extends MinigameModule>> modules = new ArrayList<>();

    public MinigameManager() {

        this.modules.add(LoadoutModule.class);
        this.modules.add(LobbySettingsModule.class);
        this.modules.add(TeamsModule.class);
        this.modules.add(WeatherTimeModule.class);
        this.modules.add(TreasureHuntModule.class);
        this.modules.add(InfectionModule.class);
        this.modules.add(GameOverModule.class);
        this.modules.add(JuggernautModule.class);
        this.modules.add(RewardsModule.class);
        this.modules.add(CTFModule.class);
        this.modules.add(ResourcePackModule.class);
    }

    public List<Class<? extends MinigameModule>> getModules() {
        return this.modules;
    }

    public void addModule(final Class<? extends MinigameModule> module) {
        this.modules.add(module);
    }

    public void removeModule(final String moduleName, final Class<? extends MinigameModule> module) {
        for (final Minigame mg : this.minigames.values()) {
            mg.removeModule(moduleName);
        }

        this.modules.remove(module);
    }

    public void startGlobalMinigame(final Minigame minigame, final MinigamePlayer caller) {
        final boolean canStart = minigame.getMechanic().checkCanStart(minigame, caller);
        if (minigame.getType() == MinigameType.GLOBAL &&
                minigame.getMechanic().validTypes().contains(MinigameType.GLOBAL) &&
                canStart) {
            final StartGlobalMinigameEvent ev = new StartGlobalMinigameEvent(minigame, caller);
            Bukkit.getPluginManager().callEvent(ev);

            minigame.getMechanic().startMinigame(minigame, caller);
            final ResourcePackModule module = (ResourcePackModule) minigame.getModule("ResourcePack");
            if (module != null) {
                if (module.isEnabled()) {
                    final String name = module.getResourcePackName();
                    final ResourcePack pack = PLUGIN.getResourceManager().getResourcePack(name);
                    if (pack.isValid()) {
                        for (final MinigamePlayer player : minigame.getPlayers()) {
                            player.applyResourcePack(pack);
                        }
                    }
                }
            }
            minigame.setEnabled(true);
            minigame.saveMinigame();
        } else if (!minigame.getMechanic().validTypes().contains(MinigameType.GLOBAL)) {
            if (caller == null) {
                Bukkit.getLogger().info(MinigameUtils.getLang("minigame.error.invalidMechanic"));
            } else {
                caller.sendMessage(MinigameUtils.getLang("minigame.error.invalidMechanic"), MinigameMessageType.ERROR);
            }
        } else if (!canStart) {
            if (caller == null) {
                Bukkit.getLogger().info(MinigameUtils.getLang("minigame.error.mechanicStartFail"));
            } else {
                caller.sendMessage(MinigameUtils.getLang("minigame.error.mechanicStartFail"), MinigameMessageType.ERROR);
            }
        }
    }

    public void stopGlobalMinigame(final Minigame minigame, final MinigamePlayer caller) {
        if (minigame.getType() == MinigameType.GLOBAL) {
            final StopGlobalMinigameEvent ev = new StopGlobalMinigameEvent(minigame, caller);
            Bukkit.getPluginManager().callEvent(ev);

            minigame.getMechanic().stopMinigame(minigame, caller);

            minigame.setEnabled(false);
            final ResourcePackModule module = (ResourcePackModule) minigame.getModule("ResourcePack");
            if (module != null) {
                if (module.isEnabled()) {
                    final ResourcePack pack = PLUGIN.getResourceManager().getResourcePack("empty");
                    if (pack.isValid()) {
                        for (final MinigamePlayer player : minigame.getPlayers()) {
                            player.applyResourcePack(pack);
                        }
                    }
                }
            }
            minigame.saveMinigame();
        }
    }

    public void addMinigame(final Minigame game) {
        this.minigames.put(game.getName(false), game);
        if(Minigames.getPlugin().includesPapi()){
            Minigames.getPlugin().getPlaceHolderManager().addGameIdentifiers(game);
        }

    }

    public Minigame getMinigame(final String minigame) {
        if (this.minigames.containsKey(minigame)) {
            return this.minigames.get(minigame);
        }

        for (final Map.Entry<String, Minigame> stringMinigameEntry : this.minigames.entrySet()) {
            if (minigame.equalsIgnoreCase(stringMinigameEntry.getKey()) || stringMinigameEntry.getKey().startsWith(minigame)) {
                return stringMinigameEntry.getValue();
            }
        }

        return null;
    }

    public Map<String, Minigame> getAllMinigames() {
        return this.minigames;
    }

    public boolean hasMinigame(final String minigame) {
        boolean hasmg = this.minigames.containsKey(minigame);
        if (!hasmg) {
            for (final String mg : this.minigames.keySet()) {
                if (mg.equalsIgnoreCase(minigame) || mg.toLowerCase().startsWith(minigame.toLowerCase())) {
                    hasmg = true;
                    break;
                }
            }
        }
        return hasmg;
    }

    public void removeMinigame(final String minigame) {
        this.minigames.remove(minigame);
    }

    public void addConfigurationFile(final String filename, final Configuration config) {
        this.configs.put(filename, config);
    }

    public Configuration getConfigurationFile(final String filename) {
        if (this.configs.containsKey(filename)) {
            return this.configs.get(filename);
        }
        return null;
    }

    public boolean hasConfigurationFile(final String filename) {
        return this.configs.containsKey(filename);
    }

    public void removeConfigurationFile(final String filename) {
        this.configs.remove(filename);
    }

    public Location minigameLocations(final String minigame, final String type, final Configuration save) {
        final Double locx = (Double) save.get(minigame + '.' + type + ".x");
        final Double locy = (Double) save.get(minigame + '.' + type + ".y");
        final Double locz = (Double) save.get(minigame + '.' + type + ".z");
        final Float yaw = new Float(save.get(minigame + '.' + type + ".yaw",0F).toString());
        final Float pitch = new Float(save.get(minigame + '.' + type + ".pitch",0F).toString());
        final String world = (String) save.get(minigame + '.' + type + ".world");
        return  new Location(PLUGIN.getServer().getWorld(world), locx, locy, locz, yaw, pitch);
    }

    public void addBlockRecorderData(final Minigame minigame) {
        if (minigame.getBlockRecorder().hasRegenArea() && !minigame.getBlockRecorder().hasCreatedRegenBlocks()) {
            final RecorderData d = minigame.getBlockRecorder();
            d.setCreatedRegenBlocks(true);
            final Location cur = new Location(minigame.getRegenArea1().getWorld(), 0, 0, 0);
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
            Minigames.debugMessage("Block Regen Data has been created for "+minigame.getName(false));
        }

    }

    public void addMinigameType(final MinigameTypeBase minigameType) {
        this.minigameTypes.put(minigameType.getType(), minigameType);
        Minigames.debugMessage("Loaded " + minigameType.getType().getName() + " minigame type."); //DEBUG
    }

    public MinigameTypeBase minigameType(final MinigameType name) {
        if (this.minigameTypes.containsKey(name)) {
            return this.minigameTypes.get(name);
        }
        return null;
    }

    public Set<MinigameType> getMinigameTypes() {
        return this.minigameTypes.keySet();
    }


    public void addLoadout(final String name) {
        this.globalLoadouts.put(name, new PlayerLoadout(name));
    }

    public void deleteLoadout(final String name) {
        this.globalLoadouts.remove(name);
    }

    public Set<String> getLoadouts() {
        return this.globalLoadouts.keySet();
    }

    public Map<String, PlayerLoadout> getLoadoutMap() {
        return this.globalLoadouts;
    }

    public PlayerLoadout getLoadout(final String name) {
        PlayerLoadout pl = null;
        if (this.globalLoadouts.containsKey(name)) {
            pl = this.globalLoadouts.get(name);
        }
        return pl;
    }

    public boolean hasLoadouts() {
        return !this.globalLoadouts.isEmpty();
    }

    public boolean hasLoadout(final String name) {
        return this.globalLoadouts.containsKey(name);
    }

    public void sendMinigameMessage(final Minigame minigame, final String message) {
        this.sendMinigameMessage(minigame, message, MinigameMessageType.INFO);
    }

    public void sendMinigameMessage(final Minigame minigame, final String message, final MinigameMessageType type) {
        this.sendMinigameMessage(minigame, message, type, (List<MinigamePlayer>) null);
    }

    public void sendMinigameMessage(final Minigame minigame, final String message, final MinigameMessageType type,
                                    final MinigamePlayer exclude) {
        this.sendMinigameMessage(minigame, message, type, Collections.singletonList(exclude));
    }

    public void sendMinigameMessage(final Minigame minigame, final String message, MinigameMessageType type,
                                    final List<MinigamePlayer> exclude) {
        if (!minigame.getShowPlayerBroadcasts()) {
            return;
        }
        String finalMessage;
        if (type == null) {
            type = MinigameMessageType.INFO;
        }
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
        final List<MinigamePlayer> sendto = new ArrayList<>();
        Collections.copy(minigame.getPlayers(), sendto);
        sendto.addAll(minigame.getSpectators());
        if (exclude != null) {
            sendto.removeAll(exclude);
        }
        for (final MinigamePlayer pl : sendto) {
            pl.sendInfoMessage(finalMessage);
        }
    }

    public void addRewardSign(final Location loc) {
        final RewardsFlag flag = new RewardsFlag(new Rewards(), MinigameUtils.createLocationID(loc));
        this.rewardSigns.put(MinigameUtils.createLocationID(loc), flag);
    }

    public Rewards getRewardSign(final Location loc) {
        return this.rewardSigns.get(MinigameUtils.createLocationID(loc)).getFlag();
    }

    public boolean hasRewardSign(final Location loc) {
        return this.rewardSigns.containsKey(MinigameUtils.createLocationID(loc));
    }

    public void removeRewardSign(final Location loc) {
        final String locid = MinigameUtils.createLocationID(loc);
        if (this.rewardSigns.containsKey(locid)) {
            this.rewardSigns.remove(locid);
            if (this.rewardSignsSave == null) {
                this.loadRewardSignsFile();
            }
            this.rewardSignsSave.getConfig().set(locid, null);
            this.rewardSignsSave.saveConfig();
            this.rewardSignsSave = null;
        }
    }

    public void saveRewardSigns() {
        for (final String rew : this.rewardSigns.keySet()) {
            this.saveRewardSign(rew, false);
        }
        if (this.rewardSignsSave != null) {
            this.rewardSignsSave.saveConfig();
            this.rewardSignsSave = null;
        }
    }

    public void saveRewardSign(final String id, final boolean save) {
        final RewardsFlag reward = this.rewardSigns.get(id);
        if (this.rewardSignsSave == null) {
            this.loadRewardSignsFile();
        }
        final FileConfiguration cfg = this.rewardSignsSave.getConfig();
        cfg.set(id, null);
        reward.saveValue("", cfg);
        if (save) {
            this.rewardSignsSave.saveConfig();
            this.rewardSignsSave = null;
        }
    }

    public void loadRewardSignsFile() {
        this.rewardSignsSave = new MinigameSave("rewardSigns");
    }

    public void loadRewardSigns() {
        if (this.rewardSignsSave == null) {
            this.loadRewardSignsFile();
        }
        final FileConfiguration cfg = this.rewardSignsSave.getConfig();
        final Set<String> keys = cfg.getKeys(false);
        for (final String id : keys) {
            final RewardsFlag rew = new RewardsFlag(new Rewards(), id);
            rew.loadValue("", cfg);

            this.rewardSigns.put(id, rew);
        }
    }

    public boolean hasClaimedScore(final Minigame mg, final Location loc, final int team) {
        final String id = MinigameUtils.createLocationID(loc);
        if (team == 0) {
            return this.claimedScoreSignsRed.containsKey(mg) && this.claimedScoreSignsRed.get(mg).contains(id);
        } else {
            return this.claimedScoreSignsBlue.containsKey(mg) && this.claimedScoreSignsBlue.get(mg).contains(id);
        }
    }

    public void addClaimedScore(final Minigame mg, final Location loc, final int team) {
        final String id = MinigameUtils.createLocationID(loc);
        if (team == 0) {
            if (!this.claimedScoreSignsRed.containsKey(mg)) {
                this.claimedScoreSignsRed.put(mg, new ArrayList<>());
            }
            this.claimedScoreSignsRed.get(mg).add(id);
        } else {
            if (!this.claimedScoreSignsBlue.containsKey(mg)) {
                this.claimedScoreSignsBlue.put(mg, new ArrayList<>());
            }
            this.claimedScoreSignsBlue.get(mg).add(id);
        }
    }

    public void clearClaimedScore(final Minigame mg) {
        this.claimedScoreSignsRed.remove(mg);
        this.claimedScoreSignsBlue.remove(mg);
    }

    public boolean minigameMechanicCheck(final Minigame minigame, final MinigamePlayer player) {
        return minigame.getMechanic() == null || minigame.getMechanic().checkCanStart(minigame, player);
    }

    public boolean minigameStartStateCheck(final Minigame minigame, final MinigamePlayer player) {
        if (!minigame.isEnabled() && !player.getPlayer().hasPermission("minigame.join.disabled")) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.notEnabled"), MinigameMessageType.ERROR);
            return false;
        } else if (!this.minigameMechanicCheck(minigame, player)) {
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

    public boolean minigameStartSetupCheck(final Minigame minigame, final MinigamePlayer player) {
        if (minigame.getEndPosition() == null) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noEnd"), MinigameMessageType.ERROR);
            return false;
        } else if (minigame.getQuitPosition() == null) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noQuit"), MinigameMessageType.ERROR);
            return false;
        } else if (minigame.getType() == null || this.minigameType(minigame.getType()).cannotStart(minigame, player)) { //type specific reasons we cannot start.
            player.sendMessage(MinigameUtils.getLang("minigame.error.invalidType"), MinigameMessageType.ERROR);
            return false;
        } else if (!minigame.getMechanic().validTypes().contains(minigame.getType())) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.invalidType"), MinigameMessageType.ERROR);
            return false;
        } else if (minigame.getStartLocations().size() <= 0 ||
                minigame.isTeamGame() && !TeamsModule.getMinigameModule(minigame).hasTeamStartLocations()) {
            player.sendMessage(MinigameUtils.getLang("minigame.error.noStart"), MinigameMessageType.ERROR);
            return false;
        }
        return true;
    }

    public boolean teleportPlayerOnJoin(@NotNull final Minigame minigame, final MinigamePlayer player) {
        if(this.minigameType(minigame.getType()) == null) {
            Minigames.log().warning(MinigameUtils.formStr("error.invalidType") + " : "+minigame.getName(true));
        }
        return this.minigameType(minigame.getType()).teleportOnJoin(player, minigame);
    }

}
