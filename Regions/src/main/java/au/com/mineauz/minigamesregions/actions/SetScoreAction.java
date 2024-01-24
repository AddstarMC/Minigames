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
    public void executeNodeAction(MinigamePlayer player,
                                  @NotNull Node node) {
        if (player == null || !player.isInMinigame()) return;
        player.setScore(amount.getFlag());
        player.getMinigame().setScore(player, player.getScore());
        checkScore(player);
    }

    @Override
    public void executeRegionAction(MinigamePlayer player, @NotNull Region region) {
        if (player == null || !player.isInMinigame()) return;
        player.setScore(amount.getFlag());
        player.getMinigame().setScore(player, player.getScore());
        checkScore(player);
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
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Set Score", player);
        m.addItem(amount.getMenuItem("Set Score Amount", Material.ENDER_PEARL, null, null));
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.displayMenu(player);
        return true;
    }

}
