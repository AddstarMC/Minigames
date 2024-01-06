package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.config.MinigameSave;
import au.com.mineauz.minigames.config.RewardsFlag;
import au.com.mineauz.minigames.events.StartGlobalMinigameEvent;
import au.com.mineauz.minigames.events.StopGlobalMinigameEvent;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.gametypes.MinigameTypeBase;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.minigame.modules.MgModules;
import au.com.mineauz.minigames.minigame.modules.ModuleFactory;
import au.com.mineauz.minigames.minigame.modules.ResourcePackModule;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ResourcePack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import au.com.mineauz.minigames.recorder.RecorderData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MinigameManager {
    private static final Minigames PLUGIN = Minigames.getPlugin();
    private final Map<String, Minigame> minigames = new HashMap<>();
    private final Map<String, Configuration> configs = new HashMap<>();
    private final Map<MinigameType, MinigameTypeBase> minigameTypes = new HashMap<>();
    private final Map<String, PlayerLoadout> globalLoadouts = new HashMap<>();
    private final Map<String, RewardsFlag> rewardSigns = new HashMap<>();
    private final Map<Minigame, List<String>> claimedScoreSignsRed = new HashMap<>();
    private final Map<Minigame, List<String>> claimedScoreSignsBlue = new HashMap<>();
    private final Map<String, ModuleFactory> modules = new HashMap<>();
    private MinigameSave rewardSignsSave;

    public MinigameManager() {
        for (ModuleFactory moduleFactory : MgModules.values()) {
            addModule(moduleFactory);
        }
    }

    public Collection<ModuleFactory> getModules() {
        return this.modules.values();
    }

    public void addModule(final @NotNull ModuleFactory moduleFactory) {
        this.modules.put(moduleFactory.getName(), moduleFactory);
    }
    public void removeModule(final @NotNull String moduleName) {
        for (final Minigame mg : this.minigames.values()) {
            mg.removeModule(moduleName);
        }

        this.modules.remove(moduleName);
    }

    public void startGlobalMinigame(final @NotNull Minigame minigame, final @Nullable MinigamePlayer caller) {
        final boolean canStart = minigame.getMechanic().checkCanStart(minigame, caller);
        if (minigame.getType() == MinigameType.GLOBAL &&
                minigame.getMechanic().validTypes().contains(MinigameType.GLOBAL) &&
                canStart) {
            final StartGlobalMinigameEvent ev = new StartGlobalMinigameEvent(minigame, caller);
            Bukkit.getPluginManager().callEvent(ev);

            minigame.getMechanic().startMinigame(minigame, caller);
            final ResourcePackModule module = ResourcePackModule.getMinigameModule(minigame);
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
                Minigames.getCmpnntLogger().warn("The Minigame Type \"" + MinigameType.GLOBAL.getName() + "\" cannot use the selected Mechanic \"" + minigame.getMechanicName() + "\"!");
            } else {
                MinigameMessageManager.sendMgMessage(caller, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_INVALIDMECHANIC,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MECHANIC.getKey(), minigame.getMechanicName()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), MinigameType.GLOBAL.getName()));
            }
        } else if (!canStart) {
            if (caller == null) {
                Minigames.getCmpnntLogger().warn("The Game Mechanic \"" + minigame.getMechanicName() + "\" has failed to initiate!");
            } else {
                MinigameMessageManager.sendMgMessage(caller, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_MECHANICSTARTFAIL,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MECHANIC.getKey(), minigame.getMechanicName()));
            }
        }
    }

    public void stopGlobalMinigame(final Minigame minigame, final MinigamePlayer caller) {
        if (minigame.getType() == MinigameType.GLOBAL) {
            final StopGlobalMinigameEvent ev = new StopGlobalMinigameEvent(minigame, caller);
            Bukkit.getPluginManager().callEvent(ev);

            minigame.getMechanic().stopMinigame(minigame, caller);

            minigame.setEnabled(false);
            final ResourcePackModule module = ResourcePackModule.getMinigameModule(minigame);
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
        if (Minigames.getPlugin().includesPapi()) {
            Minigames.getPlugin().getPlaceHolderManager().addGameIdentifiers(game);
        }
    }

    public @Nullable Minigame getMinigame(final @NotNull String minigame) {
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

    public void removeMinigame(final @NotNull String minigame) {
        this.minigames.remove(minigame);
    }

    public void addConfigurationFile(final @NotNull String filename, final Configuration config) {
        this.configs.put(filename, config);
    }

    public Configuration getConfigurationFile(final @NotNull String filename) {
        if (this.configs.containsKey(filename)) {
            return this.configs.get(filename);
        }
        return null;
    }

    public boolean hasConfigurationFile(final @NotNull String filename) {
        return this.configs.containsKey(filename);
    }

    public void removeConfigurationFile(final @NotNull String filename) {
        this.configs.remove(filename);
    }

    public void addRegenDataToRecorder(final @NotNull Minigame minigame) {
        if (minigame.hasRegenArea() && !minigame.getRecorderData().hasCreatedRegenBlocks()) {
            final RecorderData recorderData = minigame.getRecorderData();

            recorderData.setCreatedRegenBlocks(true);

            for (MgRegion region : recorderData.getMinigame().getRegenRegions()) {
                for (int x = (int) region.getMinX(); x <= region.getMaxX(); x++) {
                    for (int y = (int) region.getMinY(); y <= region.getMaxY(); y++) {
                        for (int z = (int) region.getMinZ(); z <= region.getMaxZ(); z++) {
                            //add block
                            recorderData.addBlock(region.getWorld().getBlockAt(x, y, z), null);
                        }
                    }
                }
            }

            MinigameMessageManager.debugMessage("Block Regen Data has been created for " + minigame.getName(false));
        }
    }

    public void addMinigameType(final @NotNull MinigameTypeBase minigameType) {
        this.minigameTypes.put(minigameType.getType(), minigameType);
        MinigameMessageManager.debugMessage("Loaded " + minigameType.getType().getName() + " minigame type."); //DEBUG
    }

    public MinigameTypeBase minigameType(final @NotNull MinigameType name) {
        if (this.minigameTypes.containsKey(name)) {
            return this.minigameTypes.get(name);
        }
        return null;
    }

    public Set<MinigameType> getMinigameTypes() {
        return this.minigameTypes.keySet();
    }

    public void addGlobalLoadout(final @NotNull String name) {
        this.globalLoadouts.put(name, new PlayerLoadout(name));
    }

    public void deleteGlobalLoadout(final @NotNull String name) {
        this.globalLoadouts.remove(name);
    }

    public @NotNull Set<@NotNull String> getLoadouts() {
        return this.globalLoadouts.keySet();
    }

    public @NotNull Map<@NotNull String, @NotNull PlayerLoadout> getLoadoutMap() {
        return this.globalLoadouts;
    }

    public @Nullable PlayerLoadout getLoadout(final @NotNull String name) {
        PlayerLoadout pl = null;
        if (this.globalLoadouts.containsKey(name)) {
            pl = this.globalLoadouts.get(name);
        }
        return pl;
    }

    public boolean hasLoadouts() {
        return !this.globalLoadouts.isEmpty();
    }

    public boolean hasLoadout(final @NotNull String name) {
        return this.globalLoadouts.containsKey(name);
    }

    public void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message) { //todo move this into Message Manager
        this.sendMinigameMessage(minigame, message, MinigameMessageType.INFO);
    }

    public void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message, final @Nullable MinigameMessageType type) {
        this.sendMinigameMessage(minigame, message, type, (List<MinigamePlayer>) null);
    }

    public void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message, final @Nullable MinigameMessageType type,
                                    final @NotNull MinigamePlayer exclude) {
        this.sendMinigameMessage(minigame, message, type, Collections.singletonList(exclude));
    }

    /**
     * Sending a general Broadcast
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     * @param type     Message Type
     * @param exclude  Players, which shall not get this message
     */
    public void sendMinigameMessage(final @NotNull Minigame minigame, final @NotNull Component message, @Nullable MinigameMessageType type,
                                    final @Nullable List<@NotNull MinigamePlayer> exclude) {
        if (!minigame.getShowPlayerBroadcasts()) {
            return;
        }
        sendBroadcastMessage(minigame, message, type, exclude);
    }

    /**
     * Sending a ctf relevant message
     *
     * @param minigame The minigame in which this message shall be sent
     * @param message  The message
     * @param type     Message Type
     * @param exclude  Players, which shall not get this message
     */
    public void sendCTFMessage(final @NotNull Minigame minigame, final @NotNull Component message, @Nullable MinigameMessageType type,
                               final @Nullable List<@NotNull MinigamePlayer> exclude) {
        if (!minigame.getShowCTFBroadcasts()) {
            return;
        }
        sendBroadcastMessage(minigame, message, type, exclude);
    }

    public void sendCTFMessage(final @NotNull Minigame minigame, final @NotNull Component message, @Nullable MinigameMessageType type) {
        sendCTFMessage(minigame, message, type, null);
    }

    // This sends a message to every player which is not excluded from the exclude list
    private void sendBroadcastMessage(@NotNull Minigame minigame, final @NotNull Component message, @Nullable MinigameMessageType type, @Nullable List<@NotNull MinigamePlayer> exclude) {
        if (type == null) {
            type = MinigameMessageType.INFO;
        }

        final Set<MinigamePlayer> playersSendTo = new HashSet<>();
        playersSendTo.addAll(minigame.getPlayers());
        playersSendTo.addAll(minigame.getSpectators());
        if (exclude != null) {
            playersSendTo.removeAll(exclude);
        }

        for (final MinigamePlayer player : playersSendTo) {
            MinigameMessageManager.sendMessage(player, type, message);
        }
    }

    public void addRewardSign(final @NotNull Location loc) {
        final RewardsFlag flag = new RewardsFlag(new Rewards(), MinigameUtils.createLocationID(loc));
        this.rewardSigns.put(MinigameUtils.createLocationID(loc), flag);
    }

    public @Nullable Rewards getRewardSign(final @NotNull Location loc) {
        return this.rewardSigns.get(MinigameUtils.createLocationID(loc)).getFlag();
    }

    public boolean hasRewardSign(final @NotNull Location loc) {
        return this.rewardSigns.containsKey(MinigameUtils.createLocationID(loc));
    }

    public void removeRewardSign(final @NotNull Location loc) {
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

    public void saveRewardSign(final @NotNull String id, final boolean save) {
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

    public boolean hasClaimedScore(final @NotNull Minigame mg, final Location loc, final int team) {
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

    public boolean minigameMechanicCheck(final @NotNull Minigame minigame, final @NotNull MinigamePlayer mgPlayer) {
        return minigame.getMechanic() == null || minigame.getMechanic().checkCanStart(minigame, mgPlayer);
    }

    public boolean minigameStartStateCheck(final @NotNull Minigame minigame, final @NotNull MinigamePlayer mgPlayer) {
        if (!minigame.isEnabled() && !mgPlayer.getPlayer().hasPermission("minigame.join.disabled")) { //todo Permission Manager
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTENABLED);
            return false;
        } else if (!this.minigameMechanicCheck(minigame, mgPlayer)) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_MECHANICSTARTFAIL,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MECHANIC.getKey(), minigame.getMechanicName()));
            return false;
        } else if (minigame.getState() == MinigameState.REGENERATING) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_REGENERATING);
            return false;
        } else if (minigame.getState() == MinigameState.STARTED && !minigame.canLateJoin()) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_STARTED);
            return false;
        }
        return true;
    }

    public boolean minigameStartSetupCheck(final Minigame minigame, final MinigamePlayer mgPlayer) {
        if (minigame.getEndLocation() == null) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOEND);
            return false;
        } else if (minigame.getQuitLocation() == null) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOQUIT);
            return false;
        } else if (minigame.getType() == null || this.minigameType(minigame.getType()).cannotStart(minigame, mgPlayer)) { //type specific reasons we cannot start.
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_INVALIDTYPE);
            return false;
        } else if (!minigame.getMechanic().validTypes().contains(minigame.getType())) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_INVALIDTYPE);
            return false;
        } else if (minigame.getStartLocations().isEmpty() ||
                minigame.isTeamGame() && !TeamsModule.getMinigameModule(minigame).hasTeamStartLocations()) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOSTART);
            return false;
        }
        return true;
    }

    public boolean teleportPlayerOnJoin(final @NotNull Minigame minigame, final @NotNull MinigamePlayer mgPlayer) {
        if (this.minigameType(minigame.getType()) == null) {
            Minigames.getCmpnntLogger().warn("The Minigame \"" + minigame.getName(true) + "\" failed the start-up checks for its Type");
        }
        return this.minigameType(minigame.getType()).teleportOnJoin(mgPlayer, minigame);
    }

}
