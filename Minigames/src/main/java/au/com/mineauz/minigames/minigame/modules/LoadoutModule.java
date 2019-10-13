package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.LoadoutSetFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.minigame.Minigame;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoadoutModule extends MinigameModule {

    private static Map<Class<? extends LoadoutAddon>, LoadoutAddon<?>> addons = Maps.newHashMap();
    private Map<String, PlayerLoadout> extraLoadouts = new HashMap<>();
    private LoadoutSetFlag loadoutsFlag = new LoadoutSetFlag(extraLoadouts, "loadouts");

    public LoadoutModule(Minigame mgm) {
        super(mgm);
        PlayerLoadout def = new PlayerLoadout("default");
        def.setDeleteable(false);
        extraLoadouts.put("default", def);
    }

    public static LoadoutModule getMinigameModule(Minigame minigame) {
        return (LoadoutModule) minigame.getModule("Loadouts");
    }

    /**
     * Registers a loadout addon. This addon will be available for all loadouts on all games.
     *
     * @param plugin The plugin registering the addon
     * @param addon  The addon to register
     */
    public static void registerAddon(Plugin plugin, LoadoutAddon<?> addon) {
        addons.put(addon.getClass(), addon);
    }

    /**
     * Unregisters a previously registered addon
     *
     * @param addon The addon to unregister
     */
    public static void unregisterAddon(Class<? extends LoadoutAddon<?>> addon) {
        addons.remove(addon);
    }

    /**
     * Retrieves a registered addon
     *
     * @param addonClass The addon class to get the addon for
     * @return The addon or null
     */
    @SuppressWarnings("unchecked")
    public static <T extends LoadoutAddon<?>> T getAddon(Class<T> addonClass) {
        return (T) addons.get(addonClass);
    }

    public static void addAddonMenuItems(Menu menu, PlayerLoadout loadout) {
        for (LoadoutAddon<?> addon : addons.values()) {
            addon.addMenuOptions(menu, loadout);
        }
    }

    @Override
    public String getName() {
        return "Loadouts";
    }

    @Override
    public Map<String, Flag<?>> getFlags() {
        Map<String, Flag<?>> flags = new HashMap<>();
        flags.put(loadoutsFlag.getName(), loadoutsFlag);
        return flags;
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(FileConfiguration config) {
        //Do Nothing
    }

    @Override
    public void load(FileConfiguration config) {

        //TODO: Remove entire load after 1.7
        if (config.contains(getMinigame() + ".loadout")) {
            Set<String> keys = config.getConfigurationSection(getMinigame() + ".loadout").getKeys(false);
            for (String key : keys) {
                if (key.matches("[-]?[0-9]+"))
                    getLoadout("default").addItem(config.getItemStack(getMinigame() + ".loadout." + key), Integer.parseInt(key));
            }
            if (config.contains(getMinigame() + ".loadout.potions")) {
                keys = config.getConfigurationSection(getMinigame() + ".loadout.potions").getKeys(false);
                for (String eff : keys) {
                    if (PotionEffectType.getByName(eff) != null) {
                        PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
                                config.getInt(getMinigame() + ".loadout.potions." + eff + ".dur"),
                                config.getInt(getMinigame() + ".loadout.potions." + eff + ".amp"), true);
                        getLoadout("default").addPotionEffect(effect);
                    }
                }
            }
            if (config.contains(getMinigame() + ".loadout.usepermissions")) {
                getLoadout("default").setUsePermissions(config.getBoolean(getMinigame() + ".loadout.usepermissions"));
            }
            if (config.contains(getMinigame() + ".loadout.falldamage")) {
                getLoadout("default").setHasFallDamage(config.getBoolean(getMinigame() + ".loadout.falldamage"));
            }
            if (config.contains(getMinigame() + ".loadout.hunger")) {
                getLoadout("default").setHasHunger(config.getBoolean(getMinigame() + ".loadout.hunger"));
            }
        }
        if (config.contains(getMinigame() + ".extraloadouts")) {
            Set<String> keys = config.getConfigurationSection(getMinigame() + ".extraloadouts").getKeys(false);
            for (String loadout : keys) {
                addLoadout(loadout);
                Set<String> items = config.getConfigurationSection(getMinigame() + ".extraloadouts." + loadout).getKeys(false);
                for (String key : items) {
                    if (key.matches("[-]?[0-9]+"))
                        getLoadout(loadout).addItem(config.getItemStack(getMinigame() + ".extraloadouts." + loadout + "." + key), Integer.parseInt(key));
                }
                if (config.contains(getMinigame() + ".extraloadouts." + loadout + ".potions")) {
                    Set<String> pots = config.getConfigurationSection(getMinigame() + ".extraloadouts." + loadout + ".potions").getKeys(false);
                    for (String eff : pots) {
                        if (PotionEffectType.getByName(eff) != null) {
                            PotionEffect effect = new PotionEffect(PotionEffectType.getByName(eff),
                                    config.getInt(getMinigame() + ".extraloadouts." + loadout + ".potions." + eff + ".dur"),
                                    config.getInt(getMinigame() + ".extraloadouts." + loadout + ".potions." + eff + ".amp"));
                            getLoadout(loadout).addPotionEffect(effect);
                        }
                    }
                }

                if (config.contains(getMinigame() + ".extraloadouts." + loadout + ".usepermissions")) {
                    getLoadout(loadout).setUsePermissions(config.getBoolean(getMinigame() + ".extraloadouts." + loadout + ".usepermissions"));
                }

                if (config.contains(getMinigame() + ".extraloadouts." + loadout + ".falldamage"))
                    getLoadout(loadout).setHasFallDamage(config.getBoolean(getMinigame() + ".extraloadouts." + loadout + ".falldamage"));

                if (config.contains(getMinigame() + ".extraloadouts." + loadout + ".hunger"))
                    getLoadout(loadout).setHasHunger(config.getBoolean(getMinigame() + ".extraloadouts." + loadout + ".hunger"));
            }
        }
    }

    public void addLoadout(String name) {
        extraLoadouts.put(name, new PlayerLoadout(name));
    }

    public void deleteLoadout(String name) {
        extraLoadouts.remove(name);
    }

    public Set<String> getLoadouts() {
        return extraLoadouts.keySet();
    }

    public Map<String, PlayerLoadout> getLoadoutMap() {
        return extraLoadouts;
    }

    public PlayerLoadout getLoadout(String name) {
        PlayerLoadout pl = null;
        if (extraLoadouts.containsKey(name)) {
            pl = extraLoadouts.get(name);
        } else {
            for (String loadout : extraLoadouts.keySet()) {
                if (loadout.equalsIgnoreCase(name)) {
                    pl = extraLoadouts.get(loadout);
                    break;
                }
            }
        }
        return pl;
    }

    public boolean hasLoadouts() {
        return !extraLoadouts.isEmpty();
    }

    public boolean hasLoadout(String name) {
        if (!name.equalsIgnoreCase("default")) {
            if (extraLoadouts.containsKey(name))
                return extraLoadouts.containsKey(name);
            else {
                for (String loadout : extraLoadouts.keySet()) {
                    if (loadout.equalsIgnoreCase(name))
                        return true;
                }
                return false;
            }
        } else {
            return true;
        }
    }

    public void displaySelectionMenu(MinigamePlayer player, final boolean equip) {
        Menu m = new Menu(6, "Select Loadout", player);
        final MinigamePlayer fply = player;

        for (PlayerLoadout loadout : extraLoadouts.values()) {
            if (loadout.isDisplayedInMenu()) {
                if (!loadout.getUsePermissions() || player.getPlayer().hasPermission("minigame.loadout." + loadout.getName(false).toLowerCase())) {
                    if (!player.getMinigame().isTeamGame() || loadout.getTeamColor() == null ||
                            player.getTeam().getColor() == loadout.getTeamColor()) {
                        MenuItemCustom c = new MenuItemCustom(loadout.getName(true), Material.GLASS);
                        if (!loadout.getItems().isEmpty()) {
                            ItemStack item = loadout.getItem(new ArrayList<>(loadout.getItems()).get(0));
                            c.setItem(item);
                        }
                        final PlayerLoadout floadout2 = loadout;
                        c.setClick(object -> {
                            fply.setLoadout(floadout2);
                            fply.getPlayer().closeInventory();
                            if (!equip)
                                fply.sendMessage(MinigameUtils.getLang("player.loadout.nextSpawn"), MinigameMessageType.INFO);
                            else {
                                fply.sendMessage(MinigameUtils.formStr("player.loadout.equipped", floadout2.getName(true)), MinigameMessageType.INFO);
                                floadout2.equiptLoadout(fply);
                            }
                            return null;
                        });
                        m.addItem(c);
                    }
                }
            }
        }
        m.displayMenu(player);
    }

    @Override
    public void addEditMenuOptions(Menu menu) {
        // TODO Move loadout menu stuff here

    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }

    /**
     * Represents a custom loadout element.
     * This can be used to add things like disguises
     * or commands.
     *
     * @param <T> The value type for this loadout addon.arg1
     */
    public interface LoadoutAddon<T> {
        String getName();

        void addMenuOptions(Menu menu, PlayerLoadout loadout);

        void save(ConfigurationSection section, T value);

        T load(ConfigurationSection section);

        void applyLoadout(MinigamePlayer player, T value);

        void clearLoadout(MinigamePlayer player, T value);
    }
}
