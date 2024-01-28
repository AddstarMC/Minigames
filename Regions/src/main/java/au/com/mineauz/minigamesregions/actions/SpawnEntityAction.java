package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.ConfigSerializableBridge;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

/*
 * todo
 * Entity settings:
 *   rotation
 *
 * Living Entity Settings:
 *   Potion effect
 *   EntityEquipment
 * Ageable:
 *   setAge
 * --> Breeadble
 *   setAgeLock
 */

public class SpawnEntityAction extends AbstractAction {
    /**
     * Contains all entities that are problematic to spawn as is.
     * Some problems may get resolved, if their (default)settings get added in the future;
     * others probably stay problematic and shouldn't be spawn able like players
     */
    private final Set<EntityType> NOT_SPAWNABLE = Set.of( //todo enabled feature by world
            EntityType.AREA_EFFECT_CLOUD, // todo needs effect
            EntityType.BLOCK_DISPLAY, // todo needs block state/data and display settings
            EntityType.DROPPED_ITEM, // todo needs ItemMeta
            EntityType.FALLING_BLOCK, // todo needs block state/data
            EntityType.FISHING_HOOK, // needs a fishing rod; we can't guarantee this
            EntityType.GLOW_ITEM_FRAME, // hanging items need support and direction; we can't guarantee this
            EntityType.ITEM_DISPLAY, // todo needs ItemMeta and display settings
            EntityType.ITEM_FRAME, // hanging items need support and direction; we can't guarantee this
            EntityType.LEASH_HITCH, // hanging items need support also does not easy leash an entity; we can't guarantee this
            EntityType.LIGHTNING, // todo needs lightning settings
            EntityType.PAINTING, // hanging items need support and direction; we can't guarantee this
            EntityType.PLAYER, // we don't support npcs; todo maybe in the future integrate citizens support?
            EntityType.TEXT_DISPLAY, // todo needs text and display settings
            EntityType.UNKNOWN // not a spawn able entity type
    );

    private final EnumFlag<EntityType> type = new EnumFlag<>(EntityType.ZOMBIE, "type");
    private final Map<String, ConfigSerializableBridge<?>> settings = new HashMap<>();

    @Override
    public @NotNull String getName() {
        return "SPAWN_ENTITY";
    }

    @Override
    public @NotNull String getCategory() {
        return "World Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Type", type.getFlag());

        if (type.getFlag().isAlive() && settings.containsKey("displayname")) {
            out.put("Display Name", settings.get("displayname").getObject());
        }
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
    public void executeRegionAction(MinigamePlayer player,
                                    @NotNull Region region) {
        debug(player, region);
    }

    @Override
    public void executeNodeAction(@NotNull MinigamePlayer player, @NotNull Node node) {
        if (player == null || !player.isInMinigame()) return;
        debug(player, node);
        node.getLocation().getWorld().spawnEntity(node.getLocation(), type.getFlag(), CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {

            if (settings.containsKey("velocity")) {
                final Vector velocity = (Vector) settings.get("velocity").getObject();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> entity.setVelocity(velocity));
            }

            if (settings.containsKey("customName")) {
                entity.customName(MiniMessage.miniMessage().deserialize((String) settings.get("customName").getObject()));
            }

            if (settings.containsKey("customNameVisible")) {
                entity.setCustomNameVisible((Boolean) settings.get("customNameVisible").getObject());
            }

            if (settings.containsKey("visualFire")) {
                entity.setVisualFire((Boolean) settings.get("visualFire").getObject());
            }

            if (settings.containsKey("persistent")) {
                entity.setPersistent((Boolean) settings.get("persistent").getObject());
            }

            if (settings.containsKey("glowing")) {
                entity.setGlowing((Boolean) settings.get("glowing").getObject());
            }

            if (settings.containsKey("invulnerable")) {
                entity.setInvulnerable((Boolean) settings.get("invulnerable").getObject());
            }

            if (settings.containsKey("silent")) {
                entity.setSilent((Boolean) settings.get("silent").getObject());
            }

            if (settings.containsKey("hasGravity")) {
                entity.setGravity((Boolean) settings.get("hasGravity").getObject());
            }

            if (entity instanceof LivingEntity livingEntity) {
                if (settings.containsKey("canPickupItems")) {
                    livingEntity.setCanPickupItems((Boolean) settings.get("canPickupItems").getObject());
                }

                if (settings.containsKey("hasAI")) {
                    livingEntity.setAI((Boolean) settings.get("hasAI").getObject());
                }

                if (settings.containsKey("isCollidable")) {
                    livingEntity.setCollidable((Boolean) settings.get("isCollidable").getObject());
                }
            }

            entity.setMetadata("MinigameEntity", new FixedMetadataValue(Minigames.getPlugin(), true)); //todo use in recorder to despawn + add parameter for specific Minigame
            player.getMinigame().getRecorderData().addEntity(entity, player, true);
        });
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        type.saveValue(path, config);

        for (Map.Entry<String, ConfigSerializableBridge<?>> entry : settings.entrySet()) {
            config.set(path + ".settings." + entry.getKey(), entry.getValue().serialize());
        }
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        type.loadValue(path, config);

        settings.clear();
        ConfigurationSection section = config.getConfigurationSection(path + ".settings");
        if (section != null) { // may was empty
            Set<String> keys = section.getKeys(false);

            for (String key : keys) {
                ConfigSerializableBridge<?> serializableBridge = ConfigSerializableBridge.deserialize(config.get(path + ".settings." + key));

                if (serializableBridge != null) {
                    settings.put(key, serializableBridge);
                } else {
                    Minigames.log().log(Level.WARNING, "Key \"" + key + "\" of ConfigSerializableBridge in SpawnEntityAction of path \"" + path + ".settings." + key + "\" failed to load!");
                }
            }
        }
    }

    @Override
    public boolean displayMenu(MinigamePlayer mgPlayer, Menu previous) {
        Menu menu = new Menu(3, "Spawn Entity", mgPlayer);
        menu.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), menu.getSize() - 9);
        List<String> options = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (!NOT_SPAWNABLE.contains(type)) {
                options.add(WordUtils.capitalizeFully(type.toString().toLowerCase().replace("_", " ")));
            }
        }
        menu.addItem(new MenuItemList("Entity Type", Material.SKELETON_SKULL, new Callback<>() { //todo spawn egg?

            @Override
            public String getValue() {
                return WordUtils.capitalizeFully(type.getFlag().toString().toLowerCase(Locale.ENGLISH).replace("_", " "));
            }

            @Override
            public void setValue(String value) {
                type.setFlag(EntityType.valueOf(value.toUpperCase().replace(" ", "_")));
            }


        }, options));

        final MenuItemCustom customMenuItem = new MenuItemCustom("Entity Settings", Material.CHEST);
        final Menu entitySettingsMenu = new Menu(6, "Settings", mgPlayer);
        final MinigamePlayer fply = mgPlayer;
        customMenuItem.setClick(object -> {
            if (type.getFlag().isAlive()) {
                entitySettingsMenu.clearMenu();

                final MenuItemPage backButton = new MenuItemPage("Back", MenuUtility.getBackMaterial(), menu);
                entitySettingsMenu.addItem(backButton, entitySettingsMenu.getSize() - 1);
                populateEntitySettings(entitySettingsMenu, mgPlayer);

                entitySettingsMenu.displayMenu(fply);
                return null;
            }
            return customMenuItem.getDisplayItem();
        });
        menu.addItem(customMenuItem);

        menu.displayMenu(mgPlayer);
        return true;
    }

    private void populateEntitySettings(@NotNull Menu entitySettingsMenu, @NotNull MinigamePlayer mgPlayer) {
        entitySettingsMenu.addItem(new MenuItemComponent("Display Name", Material.NAME_TAG, new Callback<>() {
            @Override
            public String getValue() {
                ConfigSerializableBridge<?> value = settings.get("customName");
                if (value == null) {
                    return "";
                } else {
                    return (String) value.getObject();
                }
            }

            @Override
            public void setValue(String value) {
                settings.put("customName", new ConfigSerializableBridge<>(value));
            }
        }));

        entitySettingsMenu.addItem(new MenuItemBoolean("Display Name Visible", Material.SPYGLASS, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("customNameVisible");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("customNameVisible", new ConfigSerializableBridge<>(value));
            }
        }));

        Menu velocityMenu = new Menu(3, "Entity velocity", mgPlayer);
        final MenuItemPage backButton = new MenuItemPage("Back", MenuUtility.getBackMaterial(), entitySettingsMenu);
        velocityMenu.addItem(backButton, velocityMenu.getSize() - 1);

        velocityMenu.addItem(new MenuItemDecimal("X Velocity", Material.ARROW, new Callback<>() {
            @Override
            public Double getValue() {
                ConfigSerializableBridge<?> value = settings.get("velocity");

                if (value == null) {
                    return 0D;
                } else {
                    return ((Vector) value.getObject()).getX();
                }
            }

            @Override
            public void setValue(Double value) {
                Vector vector;

                ConfigSerializableBridge<?> savedVelocity = settings.get("velocity");
                if (savedVelocity == null) {
                    vector = new Vector(value, 0, 0);
                } else {
                    vector = (Vector) savedVelocity.getObject();
                    vector.setX(value);
                }

                settings.put("velocity", new ConfigSerializableBridge<>(vector));
            }


        }, 0.5, 1, null, null));
        velocityMenu.addItem(new MenuItemDecimal("Y Velocity", Material.ARROW, new Callback<>() {
            @Override
            public Double getValue() {
                ConfigSerializableBridge<?> value = settings.get("velocity");

                if (value == null) {
                    return 0D;
                } else {
                    return ((Vector) value.getObject()).getY();
                }
            }

            @Override
            public void setValue(Double value) {
                Vector vector;

                ConfigSerializableBridge<?> savedVelocity = settings.get("velocity");
                if (savedVelocity == null) {
                    vector = new Vector(0, value, 0);
                } else {
                    vector = (Vector) savedVelocity.getObject();
                    vector.setY(value);
                }

                settings.put("velocity", new ConfigSerializableBridge<>(vector));
            }


        }, 0.5, 1, null, null));
        velocityMenu.addItem(new MenuItemDecimal("Z Velocity", Material.ARROW, new Callback<>() {

            @Override
            public Double getValue() {
                ConfigSerializableBridge<?> value = settings.get("velocity");

                if (value == null) {
                    return 0D;
                } else {
                    return ((Vector) value.getObject()).getZ();
                }
            }

            @Override
            public void setValue(Double value) {
                Vector vector;

                ConfigSerializableBridge<?> savedVelocity = settings.get("velocity");
                if (savedVelocity == null) {
                    vector = new Vector(0, 0, value);
                } else {
                    vector = (Vector) savedVelocity.getObject();
                    vector.setZ(value);
                }

                settings.put("velocity", new ConfigSerializableBridge<>(vector));
            }
        }, 0.5, 1, null, null));
        entitySettingsMenu.addItem(new MenuItemPage("Velocity", Material.FIREWORK_ROCKET, velocityMenu));

        entitySettingsMenu.addItem(new MenuItemBoolean("Visual fire", Material.CAMPFIRE, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("visualFire");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("visualFire", new ConfigSerializableBridge<>(value));
            }
        }));

        entitySettingsMenu.addItem(new MenuItemBoolean("Persistent", Material.SLIME_BALL, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("persistent");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("persistent", new ConfigSerializableBridge<>(value));
            }
        }));

        entitySettingsMenu.addItem(new MenuItemBoolean("Glowing", Material.GLOWSTONE_DUST, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("glowing");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("glowing", new ConfigSerializableBridge<>(value));
            }
        }));

        entitySettingsMenu.addItem(new MenuItemBoolean("Invulnerable", Material.SHIELD, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("invulnerable");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("invulnerable", new ConfigSerializableBridge<>(value));
            }
        }));

        entitySettingsMenu.addItem(new MenuItemBoolean("Silent", Material.SCULK_SENSOR, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("silent");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("silent", new ConfigSerializableBridge<>(value));
            }
        }));

        // don't overflow to next page
        entitySettingsMenu.addItem(new MenuItemNewLine());

        entitySettingsMenu.addItem(new MenuItemBoolean("Has gravity", Material.ELYTRA, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("hasGravity");
                if (value == null) {
                    return true;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("hasGravity", new ConfigSerializableBridge<>(value));
            }
        }));

        if (type.getFlag().isAlive()) {
            entitySettingsMenu.addItem(new MenuItemNewLine());
            populateLivingEntitySettings(entitySettingsMenu, mgPlayer);
        }
    }

    private void populateLivingEntitySettings(@NotNull Menu entitySettingsMenu, @NotNull MinigamePlayer mgPlayer) {
        entitySettingsMenu.addItem(new MenuItemNewLine());

        entitySettingsMenu.addItem(new MenuItemBoolean("Can pickup items", Material.HOPPER, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("canPickupItems");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("canPickupItems", new ConfigSerializableBridge<>(value));
            }
        }));

        entitySettingsMenu.addItem(new MenuItemBoolean("Has AI", Material.LIGHT, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("hasAI");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("hasAI", new ConfigSerializableBridge<>(value));
            }
        }));

        entitySettingsMenu.addItem(new MenuItemBoolean("Is collidable", Material.GLASS, new Callback<>() {
            @Override
            public Boolean getValue() {
                ConfigSerializableBridge<?> value = settings.get("isCollidable");
                if (value == null) {
                    return false;
                } else {
                    return (Boolean) value.getObject();
                }
            }

            @Override
            public void setValue(Boolean value) {
                settings.put("isCollidable", new ConfigSerializableBridge<>(value));
            }
        }));


    }
}
