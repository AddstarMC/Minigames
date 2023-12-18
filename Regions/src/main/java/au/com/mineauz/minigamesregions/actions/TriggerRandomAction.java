package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TriggerRandomAction extends AbstractAction {

    private final IntegerFlag timesTriggered = new IntegerFlag(1, "timesTriggered");
    private final BooleanFlag randomPerTrigger = new BooleanFlag(false, "randomPerTrigger");

    @Override
    public String getName() {
        return "TRIGGER_RANDOM";
    }

    @Override
    public String getCategory() {
        return "Region/Node Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Trigger Count", timesTriggered.getFlag());
        out.put("Allow same", randomPerTrigger.getFlag());
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
        debug(mgPlayer, region);
        List<RegionExecutor> exs = new ArrayList<>();
        for (RegionExecutor ex : region.getExecutors()) {
            if (ex.getTrigger().getName().equalsIgnoreCase("RANDOM"))
                exs.add(ex);
        }
        Collections.shuffle(exs);
        if (timesTriggered.getFlag() == 1) {
            if (region.checkConditions(exs.get(0), mgPlayer) && exs.get(0).canBeTriggered(mgPlayer))
                region.execute(exs.get(0), mgPlayer);
        } else {
            for (int i = 0; i < timesTriggered.getFlag(); i++) {
                if (!randomPerTrigger.getFlag()) {
                    if (i == timesTriggered.getFlag()) break;
                    if (region.checkConditions(exs.get(i), mgPlayer) && exs.get(i).canBeTriggered(mgPlayer))
                        region.execute(exs.get(i), mgPlayer);
                } else {
                    if (region.checkConditions(exs.get(0), mgPlayer) && exs.get(0).canBeTriggered(mgPlayer))
                        region.execute(exs.get(0), mgPlayer);
                    Collections.shuffle(exs);
                }
            }
        }
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        List<NodeExecutor> exs = new ArrayList<>();
        for (NodeExecutor ex : node.getExecutors()) {
            if (ex.getTrigger().getName().equalsIgnoreCase("RANDOM"))
                exs.add(ex);
        }
        Collections.shuffle(exs);
        if (timesTriggered.getFlag() == 1) {
            if (node.checkConditions(exs.get(0), mgPlayer) && exs.get(0).canBeTriggered(mgPlayer))
                node.execute(exs.get(0), mgPlayer);
        } else {
            for (int i = 0; i < timesTriggered.getFlag(); i++) {
                if (!randomPerTrigger.getFlag()) {
                    if (i == timesTriggered.getFlag()) break;
                    if (node.checkConditions(exs.get(i), mgPlayer) && exs.get(i).canBeTriggered(mgPlayer))
                        node.execute(exs.get(i), mgPlayer);
                } else {
                    if (node.checkConditions(exs.get(0), mgPlayer) && exs.get(0).canBeTriggered(mgPlayer))
                        node.execute(exs.get(0), mgPlayer);
                    Collections.shuffle(exs);
                }
            }
        }
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        timesTriggered.saveValue(path, config);
        randomPerTrigger.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        timesTriggered.loadValue(path, config);
        randomPerTrigger.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Trigger Random", mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(timesTriggered.getMenuItem("Times to Trigger Random", Material.COMMAND_BLOCK, 1, null));
        m.addItem(randomPerTrigger.getMenuItem("Allow Same Executor", Material.ENDER_PEARL,
                List.of("Should there be a chance", "that the same executor", "can be triggered more?")));
        m.displayMenu(mgPlayer);
        return true;
    }

}
