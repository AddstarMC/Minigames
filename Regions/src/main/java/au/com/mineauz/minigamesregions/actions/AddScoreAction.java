package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
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

public class AddScoreAction extends ScoreAction {
    private final IntegerFlag amount = new IntegerFlag(1, "amount");

    protected AddScoreAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_SCOREADD_NAME);
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
                                  @NotNull Node base) {
        executeAction(mgPlayer, base);
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region base) {
        executeAction(mgPlayer, base);


    }

    private void executeAction(MinigamePlayer player, ScriptObject base) {
        debug(player, base);
        debug(player, base);
        if (player == null || !player.isInMinigame()) return;
        player.addScore(amount.getFlag());
        player.getMinigame().setScore(player, player.getScore());
        checkScore(player);

    }


    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        amount.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        amount.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemInteger(Material.ENDER_PEARL, "Add Score Amount", new Callback<>() {

            @Override
            public Integer getValue() {
                return amount.getFlag();
            }

            @Override
            public void setValue(Integer value) {
                amount.setFlag(value);
            }
        }, null, null));
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.displayMenu(mgPlayer);
        return true;
    }

}
