package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class HasFlagCondition extends ConditionInterface {
    private StringFlag flagName = new StringFlag("flag", "flag");

    @Override
    public String getName() {
        return "HAS_FLAG";
    }

    @Override
    public String getCategory() {
        return "Player Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Flag", flagName.getFlag());
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
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        return checkCondition(player);
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return checkCondition(player);
    }
    
    private boolean checkCondition(MinigamePlayer player) {
        if (player == null) {
            return false;
        }
        return player.hasFlag(flagName.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        flagName.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        flagName.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Has Flag", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        m.addItem(flagName.getMenuItem("Flag Name", Material.NAME_TAG));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

}
