package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnEntityAction extends AAction {
    private final StringFlag type = new StringFlag("ZOMBIE", "type");
    private final Map<String, String> settings = new HashMap<>();

    protected SpawnEntityAction(@NotNull String name) {
        super(name);
        addBaseSettings();
    }

    private void addBaseSettings() {
        settings.put("velocityx", "0");
        settings.put("velocityy", "0");
        settings.put("velocityz", "0");
    }

    @Override
    public @NotNull String getName() {
        return "SPAWN_ENTITY";
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_SPAWNENTITY_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.WORLD;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Type", type.getFlag());
        out.put("Velocity", settings.get("velocityx") + "," + settings.get("velocityy") + "," + settings.get("velocityz"));
    }

    @Override
    public boolean useInRegions() {
        return false;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer,
                                    @NotNull Region region) {
        debug(mgPlayer, region);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        if (mgPlayer == null || !mgPlayer.isInMinigame()) return;
        final Entity ent = node.getLocation().getWorld().spawnEntity(node.getLocation(), EntityType.valueOf(type.getFlag()));

        final double vx = Double.parseDouble(settings.get("velocityx"));
        final double vy = Double.parseDouble(settings.get("velocityy"));
        final double vz = Double.parseDouble(settings.get("velocityz"));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> ent.setVelocity(new Vector(vx, vy, vz)));

        if (ent instanceof LivingEntity lent) {
            if (settings.containsKey("displayname")) {
                lent.setCustomName(settings.get("displayname"));
                lent.setCustomNameVisible(Boolean.getBoolean(settings.get("displaynamevisible")));
            }
        }

        ent.setMetadata("MinigameEntity", new FixedMetadataValue(Minigames.getPlugin(), true));
        mgPlayer.getMinigame().getRecorderData().addEntity(ent, mgPlayer, true);
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        type.saveValue(path, config);

    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        type.loadValue(path, config);

    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        List<String> options = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (type != EntityType.ITEM_FRAME && type != EntityType.LEASH_HITCH && type != EntityType.PLAYER &&
                    type != EntityType.LIGHTNING && type != EntityType.PAINTING && type != EntityType.UNKNOWN &&
                    type != EntityType.DROPPED_ITEM)
                options.add(WordUtils.capitalize(type.toString().replace("_", " ")));
        }
        m.addItem(new MenuItemList("Entity Type", Material.SKELETON_SKULL, new Callback<>() {

            @Override
            public String getValue() {
                return WordUtils.capitalize(type.getFlag().replace("_", " "));
            }

            @Override
            public void setValue(String value) {
                type.setFlag(value.toUpperCase().replace(" ", "_"));
                settings.clear();
                addBaseSettings();
            }


        }, options));

        m.addItem(new MenuItemDecimal(Material.ARROW, "X Velocity", new Callback<>() {

            @Override
            public Double getValue() {
                return Double.valueOf(settings.get("velocityx"));
            }

            @Override
            public void setValue(Double value) {
                settings.put("velocityx", value.toString());
            }


        }, 0.5, 1, null, null));
        m.addItem(new MenuItemDecimal(Material.ARROW, "Y Velocity", new Callback<>() {

            @Override
            public Double getValue() {
                return Double.valueOf(settings.get("velocityy"));
            }

            @Override
            public void setValue(Double value) {
                settings.put("velocityy", value.toString());
            }


        }, 0.5, 1, null, null));
        m.addItem(new MenuItemDecimal(Material.ARROW, "Z Velocity", new Callback<>() {

            @Override
            public Double getValue() {
                return Double.valueOf(settings.get("velocityz"));
            }

            @Override
            public void setValue(Double value) {
                settings.put("velocityz", value.toString());
            }


        }, 0.5, 1, null, null));

        m.addItem(new MenuItemNewLine());

        final Menu eSet = new Menu(3, "Settings", mgPlayer);
        final MenuItemBack backButton = new MenuItemBack(m);
        final MenuItemCustom cus = new MenuItemCustom(Material.CHEST, "Entity Settings");
        final MinigamePlayer fply = mgPlayer;
        cus.setClick(() -> {
            if (type.getFlag().equals("ZOMBIE")) {
                eSet.clearMenu();
                eSet.addItem(backButton, eSet.getSize() - 9);
                livingEntitySettings(eSet);
                eSet.displayMenu(fply);
                return null;
            }
            return cus.getItem();
        });
        m.addItem(cus);

        m.displayMenu(mgPlayer);
        return true;
    }

    private void livingEntitySettings(Menu eSet) {
        settings.put("displayname", "");
        settings.put("displaynamevisible", "false");

        eSet.addItem(new MenuItemString(Material.NAME_TAG, "Display Name", new Callback<>() {

            @Override
            public String getValue() {
                return settings.get("displayname");
            }

            @Override
            public void setValue(String value) {
                settings.put("displayname", value);
            }


        }));
        eSet.addItem(new MenuItemBoolean(Material.ENDER_PEARL, "Display Name Visible", new Callback<>() {

            @Override
            public Boolean getValue() {
                return Boolean.valueOf(settings.get("displaynamevisible"));
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("displaynamevisible", value.toString());
            }


        }));
    }
}
