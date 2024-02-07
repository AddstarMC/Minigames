package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EquipLoadoutAction extends AbstractAction {

    private final StringFlag loadout = new StringFlag("default", "loadout");
    private final BooleanFlag equipOnTrigger = new BooleanFlag(false, "equipOnTrigger");

    @Override
    public String getName() {
        return "EQUIP_LOADOUT";
    }

    @Override
    public String getCategory() {
        return "Minigame Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Loadout", loadout.getFlag());
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
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(mgPlayer.getMinigame());
        if (lmod.hasLoadout(loadout.getFlag())) {
            PlayerLoadout pLoadOut = lmod.getLoadout(loadout.getFlag());
            mgPlayer.setLoadout(pLoadOut);
            pLoadOut.equipLoadout(mgPlayer);
        }
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        LoadoutModule lmod = LoadoutModule.getMinigameModule(mgPlayer.getMinigame());
        if (lmod.hasLoadout(loadout.getFlag())) {
            PlayerLoadout pLoadOut = lmod.getLoadout(loadout.getFlag());
            mgPlayer.setLoadout(pLoadOut);
            pLoadOut.equipLoadout(mgPlayer);
        }
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        loadout.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        loadout.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Equip Loadout", mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(new MenuItemString("Loadout Name", Material.DIAMOND_SWORD, new Callback<>() {

            @Override
            public String getValue() {
                return loadout.getFlag();
            }

            @Override
            public void setValue(String value) {
                loadout.setFlag(value);
            }


        }));
        List<String> equipDesc = new ArrayList<>();
        equipDesc.add("This will force the loadout to equip as soon as the Action is triggered...");
        m.addItem(new MenuItemBoolean("Equip on Trigger", equipDesc, Material.PAPER, new Callback<>() {
            @Override
            public Boolean getValue() {
                return equipOnTrigger.getFlag();
            }

            @Override
            public void setValue(Boolean value) {
                equipOnTrigger.setFlag(value);
            }


        }));
        m.displayMenu(mgPlayer);
        return true;
    }

}
