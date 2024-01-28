package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HasLoadoutCondition extends ConditionInterface {
    private final StringFlag loadOutName = new StringFlag("default", "loadout");

    @Override
    public String getName() {
        return "HAS_LOADOUT";
    }

    @Override
    public String getCategory() {
        return "Player Conditions";
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
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        if (player == null || !player.isInMinigame()) return false;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
        if (lmod.hasLoadout(loadOutName.getFlag())) {
            return player.getLoadout().getName(false).equals(lmod.getLoadout(loadOutName.getFlag()).getName(false));
        }
        return false;
    }


    @Override
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        if (player == null || !player.isInMinigame()) return false;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
        if (lmod.hasLoadout(loadOutName.getFlag())) {
            return player.getLoadout().getName(false).equals(lmod.getLoadout(loadOutName.getFlag()).getName(false));
        }
        return false;
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        loadOutName.saveValue(path, config);
        saveInvert(config, path);

    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        loadOutName.loadValue(path, config);
        loadInvert(config, path);

    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Equip Loadout", player);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        m.addItem(new MenuItemString("Loadout Name", Material.DIAMOND_SWORD, new Callback<>() {

            @Override
            public String getValue() {
                return loadOutName.getFlag();
            }

            @Override
            public void setValue(String value) {
                loadOutName.setFlag(value);
            }
        }));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
        out.put("Loadout", loadOutName.getFlag());
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }
}

