package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class RandomChanceCondition extends ConditionInterface {
    
    private IntegerFlag chance = new IntegerFlag(50, "chance");

    @Override
    public String getName() {
        return "RANDOM_CHANCE";
    }
    
    @Override
    public String getCategory(){
        return "Misc Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Chance", chance.getFlag() + "%");
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
        return check();
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return check();
    }
    
    private boolean check(){
        double chance = this.chance.getFlag().doubleValue() / 100d;
        Random rand = new Random();
        return rand.nextDouble() <= chance;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        chance.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        chance.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Random Chance", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        m.addItem(chance.getMenuItem("Percentage Chance", Material.ENDER_EYE, 1, 99));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

}
