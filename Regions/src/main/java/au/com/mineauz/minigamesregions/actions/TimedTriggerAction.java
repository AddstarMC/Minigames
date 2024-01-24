package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.*;
import au.com.mineauz.minigamesregions.triggers.TimedRemoteTrigger;
import au.com.mineauz.minigamesregions.triggers.Triggers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This Action trips {@link TimedRemoteTrigger} in a region or a node Applicable to nodes Uses the
 * {@link org.bukkit.scheduler.BukkitScheduler} scheduler for threading.
 *
 * @author <a href="https://github.com/Turidus/Minigames">Turidus</a>
 */
public class TimedTriggerAction extends AbstractAction {

    private final StringFlag toTrigger = new StringFlag("None", "toTrigger");
    private final BooleanFlag isRegion = new BooleanFlag(false, "isRegion");
    private final IntegerFlag delay = new IntegerFlag(20, "delay");

    @Override
    public @NotNull String getName() {
        return "TIMED_TRIGGER";
    }

    @Override
    public @NotNull String getCategory() {
        return "Remote Trigger Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
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
    public void executeRegionAction(MinigamePlayer player, @NotNull Region region) {
        execute(player, region);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, @NotNull Node node) {
        execute(player, node);
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
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                toExecute.execute(Triggers.getTrigger("TIMED_REMOTE"), player);
            }
        }, delay.getFlag());
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        toTrigger.saveValue(path, config);
        isRegion.saveValue(path, config);
        delay.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        toTrigger.loadValue(path, config);
        isRegion.loadValue(path, config);
        delay.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Timed Trigger", player);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(toTrigger.getMenuItem("Object Name", Material.ENDER_EYE));
        m.addItem(isRegion.getMenuItem("Is Region?", Material.ENDER_PEARL));
        m.addItem(delay.getMenuItem("Delay in ticks", Material.ENDER_PEARL));
        m.displayMenu(player);
        return true;
    }

}
