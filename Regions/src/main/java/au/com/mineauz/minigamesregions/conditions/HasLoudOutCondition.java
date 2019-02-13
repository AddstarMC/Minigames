package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 4/05/2017.
 */
public class HasLoudOutCondition extends ConditionInterface {

    private StringFlag loadOutName = new StringFlag("default", "loadout");

    @Override
    public String getName() {
        return "HAS_LOADOUT";
    }

    @Override
    public String getCategory() {
        return  "Player Conditions";
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
        if(player == null || !player.isInMinigame()) return false;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
        if(lmod.hasLoadout(loadOutName.getFlag())) {
            return player.getLoadout().getName(false).equals(lmod.getLoadout(loadOutName.getFlag()).getName(false));
        }
        return false;
    }


    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        if(player == null || !player.isInMinigame()) return false;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
        if(lmod.hasLoadout(loadOutName.getFlag())) {
            return player.getLoadout().getName(false).equals(lmod.getLoadout(loadOutName.getFlag()).getName(false));
        }
        return false;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        loadOutName.saveValue(path, config);
        saveInvert(config, path);

    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        loadOutName.loadValue(path, config);
        loadInvert(config, path);

    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Equip Loadout", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        m.addItem(new MenuItemString("Loadout Name", Material.DIAMOND_SWORD, new Callback<String>() {

            @Override
            public void setValue(String value) {
                loadOutName.setFlag(value);
            }

            @Override
            public String getValue() {
                return loadOutName.getFlag();
            }
        }));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public void describe(Map<String, Object> out) {
            out.put("Loadout",loadOutName.getFlag());
        }

}

