package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.FloatFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class PlayerXPRangeCondition extends ConditionInterface {
    private FloatFlag min = new FloatFlag(1.0f, "min");
    private FloatFlag max = new FloatFlag(1.0f, "max");

    @Override
    public String getName() {
        return "PLAYER_XP_RANGE";
    }
    
    @Override
    public String getCategory(){
        return "Player Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Xp", min.getFlag() + " - " + max.getFlag());
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
        float xp = p.getLevel() + p.getExp();
        if (xp >= min.getFlag() && xp <= max.getFlag()) {
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
        m.addItem(min.getMenuItem("Min XP", Material.STONE_SLAB, 0.5, 1, 0.0, null));
        m.addItem(max.getMenuItem("Max XP", Material.STONE, 0.5, 1, 0.0, null));
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }
}
