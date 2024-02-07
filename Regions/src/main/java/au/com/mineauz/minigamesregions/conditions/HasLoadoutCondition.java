package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HasLoadoutCondition extends ACondition {
    private final StringFlag loadOutName = new StringFlag("default", "loadout");

    protected HasLoadoutCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_HASLOADOUT_NAME);
    }

    @Override
    public String getCategory() {
        return "Player ConditionRegistry";
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
        if (player == null || !player.isInMinigame()) return false;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
        if (lmod.hasLoadout(loadOutName.getFlag())) {
            return player.getLoadout().getName().equals(lmod.getLoadout(loadOutName.getFlag()).getName(false));
        }
        return false;
    }


    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        if (player == null || !player.isInMinigame()) return false;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(player.getMinigame());
        if (lmod.hasLoadout(loadOutName.getFlag())) {
            return player.getLoadout().getName().equals(lmod.getLoadout(loadOutName.getFlag()).getName(false));
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
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
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
    public void describe(Map<String, Object> out) {
        out.put("Loadout", loadOutName.getFlag());
    }

    @Override
    public boolean onPlayerApplicable() {
        return true;
    }
}

