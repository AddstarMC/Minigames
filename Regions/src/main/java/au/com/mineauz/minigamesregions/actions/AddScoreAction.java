package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class AddScoreAction extends ScoreAction {

    private final IntegerFlag amount = new IntegerFlag(1, "amount");

    @Override
    public String getName() {
        return "ADD_SCORE";
    }

    @Override
    public String getCategory() {
        return "Minigame Actions";
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
    public void executeNodeAction(MinigamePlayer player,
                                  Node base) {
        executeAction(player, base);
    }

    @Override
    public void executeRegionAction(MinigamePlayer player, Region base) {
        executeAction(player, base);


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
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Add Score", player);
        m.addItem(new MenuItemInteger("Add Score Amount", Material.ENDER_PEARL, new Callback<>() {

            @Override
            public Integer getValue() {
                return amount.getFlag();
            }

            @Override
            public void setValue(Integer value) {
                amount.setFlag(value);
            }
        }, null, null));
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.displayMenu(player);
        return true;
    }

}
