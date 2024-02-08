package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.*;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.triggers.MgRegTrigger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This Action trips {@link MgRegTrigger#REMOTE_TIMED} in a region or a node Applicable to nodes Uses the
 * {@link org.bukkit.scheduler.BukkitScheduler} scheduler for threading.
 *
 * @author <a href="https://github.com/Turidus/Minigames">Turidus</a>
 */
public class TimedTriggerAction extends AAction {
    private final StringFlag toTrigger = new StringFlag("None", "toTrigger");
    private final BooleanFlag isRegion = new BooleanFlag(false, "isRegion");
    private final IntegerFlag delay = new IntegerFlag(20, "delay");

    protected TimedTriggerAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_TIMEDTRIGGER_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.REMOTE;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Object to trigger", toTrigger.getFlag());
        out.put("Otherwise it is a;node", isRegion.getFlag());
        out.put("Delay in ticks", delay.getFlag());
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        execute(mgPlayer, region);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        execute(mgPlayer, node);
    }

    private void execute(MinigamePlayer player, ScriptObject obj) {
        debug(player, obj);
        if (player == null || !player.isInMinigame()) {
            return;
        }
        Minigame mg = player.getMinigame();
        if (mg == null) {
            return;
        }
        RegionModule rMod = RegionModule.getMinigameModule(mg);
        if ((isRegion.getFlag() && !rMod.hasRegion(toTrigger.getFlag())
                || (!isRegion.getFlag() && !rMod.hasNode(toTrigger.getFlag())))) {
            return;
        }
        ExecutableScriptObject toExecute = isRegion.getFlag() ? rMod.getRegion(toTrigger.getFlag()) : rMod.getNode(toTrigger.getFlag());
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> toExecute.execute(MgRegTrigger.REMOTE_TIMED, player), delay.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        toTrigger.saveValue(path, config);
        isRegion.saveValue(path, config);
        delay.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        toTrigger.loadValue(path, config);
        isRegion.loadValue(path, config);
        delay.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(toTrigger.getMenuItem("Object Name", Material.ENDER_EYE));
        m.addItem(isRegion.getMenuItem("Is Region?", Material.ENDER_PEARL));
        m.addItem(delay.getMenuItem("Delay in ticks", Material.ENDER_PEARL));
        m.displayMenu(mgPlayer);
        return true;
    }

}
