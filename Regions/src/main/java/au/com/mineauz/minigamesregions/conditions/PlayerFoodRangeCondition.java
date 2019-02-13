package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerFoodRangeCondition extends ConditionInterface {
    private IntegerFlag min = new IntegerFlag(20, "min");
    private IntegerFlag max = new IntegerFlag(20, "max");

    @Override
    public String getName() {
        return "PLAYER_FOOD_RANGE";
    }
    
    @Override
    public String getCategory(){
        return "Player Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Food", min.getFlag() + " - " + max.getFlag());
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
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return checkCondition(player);
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        return checkCondition(player);
    }
    
    private boolean checkCondition(MinigamePlayer player) {
        if (player == null || !player.isInMinigame()) {
            return false;
        }
        
        Player p = player.getPlayer();
        int food = p.getFoodLevel();
        if (food >= min.getFlag() && food <= max.getFlag()) {
            return true;
        } else {
            return true;
        }
    }
    
    @Override
    public void saveArguments(FileConfiguration config, String path) {
        min.saveValue(path, config);
        max.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        min.loadValue(path, config);
        max.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "XP Range", player);
        m.addItem(min.getMenuItem("Min Food", Material.STONE_SLAB, 0, 20));
        m.addItem(max.getMenuItem("Max Food", Material.STONE, 0, 20));
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }
}
