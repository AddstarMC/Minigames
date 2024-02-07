package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.config.TimeFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public class TreasureHuntModule extends MinigameModule {
    private final StringFlag location = new StringFlag(null, "location");
    private final IntegerFlag maxRadius = new IntegerFlag(1000, "maxradius");
    private final IntegerFlag maxHeight = new IntegerFlag(20, "maxheight");
    private final IntegerFlag minTreasure = new IntegerFlag(0, "mintreasure");
    private final IntegerFlag maxTreasure = new IntegerFlag(8, "maxtreasure");
    private final TimeFlag treasureWaitTime = new TimeFlag(Minigames.getPlugin().getConfig().getLong("treasurehunt.waittime"), "treasurehuntwait");
    private final TimeFlag hintWaitTime = new TimeFlag(500L, "hintWaitTime");
    private final ArrayList<Component> curHints = new ArrayList<>();
    private final Map<UUID, Long> hintUse = new HashMap<>();
    //Unsaved Data
    private Location treasureLocation = null;
    private boolean treasureFound = false;

    public TreasureHuntModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    public static @Nullable TreasureHuntModule getMinigameModule(@NotNull Minigame mgm) {
        return ((TreasureHuntModule) mgm.getModule(MgModules.TREASURE_HUNT.getName()));
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> flags = new HashMap<>();
        flags.put(location.getName(), location);
        flags.put(maxRadius.getName(), maxRadius);
        flags.put(maxHeight.getName(), maxHeight);
        flags.put(minTreasure.getName(), minTreasure);
        flags.put(maxTreasure.getName(), maxTreasure);
        flags.put(treasureWaitTime.getName(), treasureWaitTime);
        flags.put(hintWaitTime.getName(), hintWaitTime);
        return flags;
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(FileConfiguration config) {
    }

    @Override
    public void load(FileConfiguration config) {
    }

    @Override
    public void addEditMenuOptions(Menu menu) {

    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        Menu treasureHunt = new Menu(6, getMinigame().getName(false), previous.getViewer());

        List<MenuItem> itemsTreasureHunt = new ArrayList<>(5);
        itemsTreasureHunt.add(location.getMenuItem("Location Name", Material.WHITE_BED, List.of("Name to appear when", "treasure spawns")));
        itemsTreasureHunt.add(maxRadius.getMenuItem("Max. Radius", Material.ENDER_PEARL, 10, null));
        List<String> maxHeightDes = new ArrayList<>();
        maxHeightDes.add("Max. height of where a");
        maxHeightDes.add("chest can generate.");
        maxHeightDes.add("Can still move above to");
        maxHeightDes.add("avoid terrain");
        itemsTreasureHunt.add(maxHeight.getMenuItem("Max. Height", Material.BEACON, maxHeightDes, 1, 256));
        List<String> minDes = new ArrayList<>();
        minDes.add("Minimum items to");
        minDes.add("spawn in chest.");
        itemsTreasureHunt.add(minTreasure.getMenuItem("Min. Items", Material.STONE_SLAB, minDes, 0, 27));
        List<String> maxDes = new ArrayList<>();
        maxDes.add("Maximum items to");
        maxDes.add("spawn in chest.");
        itemsTreasureHunt.add(maxTreasure.getMenuItem("Max. Items", Material.STONE, maxDes, 0, 27));
        itemsTreasureHunt.add(treasureWaitTime.getMenuItem("Restart Delay", Material.CLOCK, 0L, null));
        itemsTreasureHunt.add(hintWaitTime.getMenuItem("Hint Usage Delay", Material.CLOCK, 0L, null));
        treasureHunt.addItems(itemsTreasureHunt);
        treasureHunt.addItem(new MenuItemBack(previous), treasureHunt.getSize() - 9);
        treasureHunt.displayMenu(treasureHunt.getViewer());
        return true;
    }

    public int getMaxRadius() {
        return maxRadius.getFlag();
    }

    public void setMaxRadius(int maxRadius) {
        this.maxRadius.setFlag(maxRadius);
    }

    public int getMaxHeight() {
        return maxHeight.getFlag();
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight.setFlag(maxHeight);
    }

    public String getLocation() {
        return location.getFlag();
    }

    public void setLocation(String location) {
        this.location.setFlag(location);
    }

    public int getMinTreasure() {
        return minTreasure.getFlag();
    }

    public void setMinTreasure(int minTreasure) {
        this.minTreasure.setFlag(minTreasure);
    }

    public int getMaxTreasure() {
        return maxTreasure.getFlag();
    }

    public void setMaxTreasure(int maxTreasure) {
        this.maxTreasure.setFlag(maxTreasure);
    }

    public Location getTreasureLocation() {
        return treasureLocation.clone();
    }

    public void setTreasureLocation(Location loc) {
        treasureLocation = loc;
    }

    public boolean hasTreasureLocation() {
        return treasureLocation != null;
    }

    public boolean isTreasureFound() {
        return treasureFound;
    }

    public void setTreasureFound(boolean bool) {
        treasureFound = bool;
    }

    public List<Component> getCurrentHints() {
        return curHints;
    }

    public void addHint(Component hint) {
        curHints.add(hint.color(NamedTextColor.GRAY));
    }

    public void clearHints() {
        curHints.clear();
    }

    public long getTreasureWaitTime() {
        return treasureWaitTime.getFlag();
    }

    public void setTreasureWaitTime(long time) {
        treasureWaitTime.setFlag(time);
    }

    public long getLastHintUse(MinigamePlayer player) {
        if (!hintUse.containsKey(player.getUUID()))
            return -1L;
        return hintUse.get(player.getUUID());
    }

    public boolean canUseHint(MinigamePlayer player) {
        if (hintUse.containsKey(player.getUUID())) {
            long curtime = System.currentTimeMillis();
            long lastuse = curtime - hintUse.get(player.getUUID());
            return lastuse >= getHintDelay() * 1000L;
        }
        return true;
    }

    public void addHintUse(MinigamePlayer player) {
        hintUse.put(player.getUUID(), System.currentTimeMillis());
    }

    public void clearHintUsage() {
        hintUse.clear();
    }

    public void getHints(MinigamePlayer mgPlayer) {
        if (!hasTreasureLocation()) return;
        Location block = getTreasureLocation();
        if (mgPlayer.getPlayer().getWorld().getName().equals(getTreasureLocation().getWorld().getName())) {
            Location ploc = mgPlayer.getLocation();
            double distance = ploc.distance(block);
            int maxradius = getMaxRadius();
            if (canUseHint(mgPlayer)) {
                if (distance > maxradius) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_DISTANCE6);
                } else if (distance > (double) maxradius / 2) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_DISTANCE5);
                } else if (distance > (double) maxradius / 4) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_DISTANCE4);
                } else if (distance > 50) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_DISTANCE3);
                } else if (distance > 20) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_DISTANCE2);
                } else if (distance < 20) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_DISTANCE1);
                }
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_TIMELEFT,
                        Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(getMinigame().getMinigameTimer().getTimeLeft()))));

                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_GLOBALHINTS);
                if (getCurrentHints().isEmpty()) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_NOHINT);
                } else {
                    for (Component globalHint : getCurrentHints()) {
                        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, globalHint);
                    }
                }

                addHintUse(mgPlayer);
            } else {
                int nextUse = (300000 - (int) (System.currentTimeMillis() - getLastHintUse(mgPlayer))) / 1000;

                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_NOUSE,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), getMinigame().getName(true)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.TIME.getKey(), String.valueOf(nextUse)));

                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_TIMELEFT,
                        Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(getMinigame().getMinigameTimer().getTimeLeft()))));
            }
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_TREASUREHUNT_PLAYERSPECIFICHINT_WRONGWORLD,
                    Placeholder.unparsed(MinigamePlaceHolderKey.WORLD.getKey(), block.getWorld().getName()));
        }
    }

    public long getHintDelay() {
        return hintWaitTime.getFlag();
    }

    public void setHintDelay(long time) {
        hintWaitTime.setFlag(time);
    }
}
