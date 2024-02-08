package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ResetTriggerCountAction extends AAction {

    protected ResetTriggerCountAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_RESETTRIGGERCOUNT_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.REGION_NODE;
    }

    @Override
    public void describe(Map<String, Object> out) {
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
        for (RegionExecutor ex : region.getExecutors())
            ex.setTriggerCount(0);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        for (NodeExecutor ex : node.getExecutors())
            ex.setTriggerCount(0);
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        return false;
    }

}
