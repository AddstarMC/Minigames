package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReequipLoadoutAction extends AAction {

    protected ReequipLoadoutAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_REEQUIPLOADOUT_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.MINIGAME;
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
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        mgPlayer.getLoadout().equipLoadout(mgPlayer);
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        mgPlayer.getLoadout().equipLoadout(mgPlayer);
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        // None
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        // None
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        // None
        return false;
    }
}
