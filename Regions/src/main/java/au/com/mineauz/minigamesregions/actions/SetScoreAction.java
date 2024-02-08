package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SetScoreAction extends ScoreAction {
    private final IntegerFlag amount = new IntegerFlag(1, "amount");

    protected SetScoreAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_SETSCORE_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.MINIGAME;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Score", amount.getFlag());
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
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        mgPlayer.setScore(amount.getFlag());
        mgPlayer.getMinigame().setScore(mgPlayer, mgPlayer.getScore());
        checkScore(mgPlayer);
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        mgPlayer.setScore(amount.getFlag());
        mgPlayer.getMinigame().setScore(mgPlayer, mgPlayer.getScore());
        checkScore(mgPlayer);
    }


    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        amount.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        amount.saveValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu menu = new Menu(3, getDisplayname(), mgPlayer);
        menu.addItem(amount.getMenuItem(Material.ENDER_PEARL, "Set Score Amount", null, null));
        menu.addItem(new MenuItemBack(previous), menu.getSize() - 9);
        menu.displayMenu(mgPlayer);
        return true;
    }

}
