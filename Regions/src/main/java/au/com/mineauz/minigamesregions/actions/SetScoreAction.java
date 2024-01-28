package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SetScoreAction extends ScoreAction {
    private final IntegerFlag amount = new IntegerFlag(1, "amount");

    @Override
    public @NotNull String getName() {
        return "SET_SCORE";
    }

    @Override
    public @NotNull String getCategory() {
        return "Minigame Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
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
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        amount.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        amount.saveValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Set Score", mgPlayer);
        m.addItem(amount.getMenuItem("Set Score Amount", Material.ENDER_PEARL, null, null));
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.displayMenu(mgPlayer);
        return true;
    }

}
