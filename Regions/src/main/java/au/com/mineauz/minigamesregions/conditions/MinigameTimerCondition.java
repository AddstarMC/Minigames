package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class MinigameTimerCondition extends ConditionInterface{
    
    private IntegerFlag minTime = new IntegerFlag(5, "minTime");
    private IntegerFlag maxTime = new IntegerFlag(10, "maxTime");

    @Override
    public String getName() {
        return "MINIGAME_TIMER";
    }

    @Override
    public String getCategory() {
        return "Minigame Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Time", MinigameUtils.convertTime(minTime.getFlag(), true) + " - " + MinigameUtils.convertTime(maxTime.getFlag(), true));
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
        return check(player.getMinigame());
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return check(player.getMinigame());
    }
    private boolean check(Minigame mg){
        int timeLeft = mg.getMinigameTimer().getTimeLeft();
        int min = minTime.getFlag();
        int max = maxTime.getFlag();
        debug(mg);
        return timeLeft >= min &&
                timeLeft <= max;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        minTime.saveValue(path, config);
        maxTime.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        minTime.loadValue(path, config);
        maxTime.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Minigame Timer", player);
        
        m.addItem(new MenuItemTime("Min Time", Material.CLOCK, minTime.getCallback(), 0, null));
        m.addItem(new MenuItemTime("Max Time", Material.CLOCK, maxTime.getCallback(), 0, null));
        
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

}
