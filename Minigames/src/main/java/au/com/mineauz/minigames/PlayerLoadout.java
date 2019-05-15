package au.com.mineauz.minigames;

import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule.LoadoutAddon;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.Map.Entry;

public class PlayerLoadout {
    private Map<Integer, ItemStack> itemSlot = new HashMap<>();
    private List<PotionEffect> potions = new ArrayList<>();
    private String loadoutName = "default";
    private boolean usePermission = false;
    private boolean fallDamage = true;
    private boolean hunger = false;
    private int level = -1;
    private boolean deleteable = true;
    private String displayname = null;
    private boolean lockInventory = false;
    private boolean lockArmour = false;
    private boolean allowOffHand = true;
    private TeamColor team = null;
    private boolean displayInMenu = true;

    private Map<Class<? extends LoadoutAddon>, Object> addonValues = Maps.newHashMap();

    public PlayerLoadout(String name) {
        loadoutName = name;
        for (TeamColor col : TeamColor.values()) {
            if (name.toUpperCase().equals(col.toString())) {
                team = col;
                break;
            }
        }
    }

    public Callback<String> getDisplayNameCallback() {
        return new Callback<String>() {

            @Override
            public String getValue() {
                return displayname;
            }            @Override
            public void setValue(String value) {
                displayname = value;
            }


        };
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayName(String name) {
        displayname = name;
    }

    public boolean getUsePermissions() {
        return usePermission;
    }

    public void setUsePermissions(boolean bool) {
        usePermission = bool;
    }

    public Callback<Boolean> getUsePermissionsCallback() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return usePermission;
            }            @Override
            public void setValue(Boolean value) {
                usePermission = value;
            }


        };
    }

    public String getName(boolean useDisplay) {
        if (!useDisplay || getDisplayName() == null)
            return loadoutName;
        return getDisplayName();
    }

    public void addItem(ItemStack item, int slot) {
        itemSlot.put(slot, item);
    }

    public void addPotionEffect(PotionEffect effect) {
        for (PotionEffect pot : potions) {
            if (effect.getType().getName().equals(pot.getType().getName())) {
                potions.remove(pot);
                break;
            }
        }
        potions.add(effect);
    }

    public void removePotionEffect(PotionEffect effect) {
        if (potions.contains(effect)) {
            potions.remove(effect);
        } else {
            for (PotionEffect pot : potions) {
                if (pot.getType().getName().equals(effect.getType().getName())) {
                    potions.remove(pot);
                    break;
                }
            }
        }
    }

    public List<PotionEffect> getAllPotionEffects() {
        return potions;
    }

    @SuppressWarnings("unchecked")
    public void equiptLoadout(MinigamePlayer player) {
        player.getPlayer().getInventory().clear();
        player.getPlayer().getInventory().setHelmet(null);
        player.getPlayer().getInventory().setChestplate(null);
        player.getPlayer().getInventory().setLeggings(null);
        player.getPlayer().getInventory().setBoots(null);
        for (PotionEffect potion : player.getPlayer().getActivePotionEffects()) {
            player.getPlayer().removePotionEffect(potion.getType());
        }
        if (!itemSlot.isEmpty()) {
            for (Integer slot : itemSlot.keySet()) {
                if (slot < 100)
                    player.getPlayer().getInventory().setItem(slot, getItem(slot));
                else if (slot == 100)
                    player.getPlayer().getInventory().setBoots(getItem(slot));
                else if (slot == 101)
                    player.getPlayer().getInventory().setLeggings(getItem(slot));
                else if (slot == 102)
                    player.getPlayer().getInventory().setChestplate(getItem(slot));
                else if (slot == 103)
                    player.getPlayer().getInventory().setHelmet(getItem(slot));
                else if (slot == -106)
                    player.getPlayer().getInventory().setItemInOffHand(getItem(slot));
            }
            player.updateInventory();
        }

        final MinigamePlayer fplayer = player;
        Bukkit.getScheduler().runTask(Minigames.getPlugin(), () -> fplayer.getPlayer().addPotionEffects(potions));

        for (Entry<Class<? extends LoadoutAddon>, Object> addonValue : addonValues.entrySet()) {
            LoadoutAddon<Object> addon = LoadoutModule.getAddon(addonValue.getKey());
            if (addon != null) {
                addon.applyLoadout(player, addonValue.getValue());
            }
        }

        if (level != -1)
            player.getPlayer().setLevel(level);
    }

    @SuppressWarnings("unchecked")
    public void removeLoadout(MinigamePlayer player) {
        for (Entry<Class<? extends LoadoutAddon>, Object> addonValue : addonValues.entrySet()) {
            LoadoutAddon<Object> addon = LoadoutModule.getAddon(addonValue.getKey());
            if (addon != null) {
                addon.clearLoadout(player, addonValue.getValue());
            }
        }
    }

    public Set<Integer> getItems() {
        return itemSlot.keySet();
    }

    public ItemStack getItem(int slot) {
        return itemSlot.get(slot);
    }

    public void clearLoadout() {
        itemSlot.clear();
    }

    public boolean hasFallDamage() {
        return fallDamage;
    }

    public void setHasFallDamage(boolean bool) {
        fallDamage = bool;
    }

    public Callback<Boolean> getFallDamageCallback() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return fallDamage;
            }            @Override
            public void setValue(Boolean value) {
                fallDamage = value;
            }


        };
    }

    public boolean hasHunger() {
        return hunger;
    }

    public void setHasHunger(boolean bool) {
        hunger = bool;
    }

    public Callback<Boolean> getHungerCallback() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return hunger;
            }            @Override
            public void setValue(Boolean value) {
                hunger = value;
            }


        };
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Callback<Integer> getLevelCallback() {
        return new Callback<Integer>() {

            @Override
            public Integer getValue() {
                return level;
            }            @Override
            public void setValue(Integer value) {
                if (level >= -1)
                    level = value;
            }


        };
    }

    public boolean isDeleteable() {
        return deleteable;
    }

    public void setDeleteable(boolean value) {
        deleteable = value;
    }

    public boolean isInventoryLocked() {
        return lockInventory;
    }

    public void setInventoryLocked(boolean locked) {
        lockInventory = locked;
    }

    public Callback<Boolean> getInventoryLockedCallback() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return isInventoryLocked();
            }            @Override
            public void setValue(Boolean value) {
                setInventoryLocked(value);
            }


        };
    }

    public boolean isArmourLocked() {
        return lockArmour;
    }

    public void setArmourLocked(boolean locked) {
        lockArmour = locked;
    }

    public Callback<Boolean> getArmourLockedCallback() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return isArmourLocked();
            }            @Override
            public void setValue(Boolean value) {
                setArmourLocked(value);
            }


        };
    }

    public boolean allowOffHand() {
        return allowOffHand;
    }

    public Callback<Boolean> getAllowOffHandCallback() {
        return new Callback<Boolean>() {
            @Override
            public Boolean getValue() {
                return allowOffHand;
            }            @Override
            public void setValue(Boolean value) {
                allowOffHand = value;
            }


        };
    }

    public void setAllowOffHand(boolean allow) {
        allowOffHand = allow;
    }

    public TeamColor getTeamColor() {
        return team;
    }

    public void setTeamColor(TeamColor color) {
        team = color;
    }

    public Callback<String> getTeamColorCallback() {
        return new Callback<String>() {

            @Override
            public String getValue() {
                if (getTeamColor() == null)
                    return "None";
                return MinigameUtils.capitalize(getTeamColor().toString());
            }            @Override
            public void setValue(String value) {
                setTeamColor(TeamColor.matchColor(value.toUpperCase()));
            }


        };
    }

    public boolean isDisplayedInMenu() {
        return displayInMenu;
    }

    public Callback<Boolean> getDisplayInMenuCallback() {
        return new Callback<Boolean>() {

            @Override
            public Boolean getValue() {
                return isDisplayedInMenu();
            }            @Override
            public void setValue(Boolean value) {
                setDisplayInMenu(value);
            }


        };
    }

    public void setDisplayInMenu(boolean bool) {
        displayInMenu = bool;
    }

    /**
     * Sets an addons value in this loadout
     *
     * @param addon The addon
     * @param value The value to use
     */
    public <T> void setAddonValue(Class<? extends LoadoutAddon<T>> addon, T value) {
        addonValues.put(addon, value);
    }

    /**
     * Gets an addons value in this loadout
     *
     * @param addon The addon
     * @return The value of the addon, or null
     */
    @SuppressWarnings("unchecked")
    public <T> T getAddonValue(Class<? extends LoadoutAddon<T>> addon) {
        return (T) addonValues.get(addon);
    }

    @SuppressWarnings("unchecked")
    public void save(ConfigurationSection section) {
        for (Integer slot : getItems())
            section.set("items." + slot, getItem(slot));

        for (PotionEffect eff : getAllPotionEffects()) {
            section.set("potions." + eff.getType().getName() + ".amp", eff.getAmplifier());
            section.set("potions." + eff.getType().getName() + ".dur", eff.getDuration());
        }

        if (getUsePermissions())
            section.set("usepermissions", true);

        if (!hasFallDamage())
            section.set("falldamage", hasFallDamage());

        if (hasHunger())
            section.set("hunger", hasHunger());

        if (getDisplayName() != null)
            section.set("displayName", getDisplayName());

        if (isArmourLocked())
            section.set("armourLocked", isArmourLocked());

        if (isInventoryLocked())
            section.set("inventoryLocked", isInventoryLocked());

        if (getTeamColor() != null)
            section.set("team", getTeamColor().toString());

        if (!isDisplayedInMenu())
            section.set("displayInMenu", isDisplayedInMenu());

        if (!allowOffHand())
            section.set("allowOffhand", allowOffHand());

        for (Entry<Class<? extends LoadoutAddon>, Object> addonValue : addonValues.entrySet()) {
            ConfigurationSection subSection = section.createSection("addons." + addonValue.getKey().getName().replace('.', '-'));
            LoadoutAddon<Object> addon = LoadoutModule.getAddon(addonValue.getKey());
            addon.save(subSection, addonValue.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public void load(ConfigurationSection section) {
        if (section.contains("items")) {
            ConfigurationSection itemSection = section.getConfigurationSection("items");
            for (String key : itemSection.getKeys(false)) {
                if (key.matches("[-]?[0-9]+")) {
                    addItem(itemSection.getItemStack(key), Integer.parseInt(key));
                }
            }
        }

        if (section.contains("potions")) {
            ConfigurationSection potionSection = section.getConfigurationSection("potions");
            for (String effectName : potionSection.getKeys(false)) {
                if (PotionEffectType.getByName(effectName) == null) {
                    continue;
                }

                PotionEffect effect = new PotionEffect(PotionEffectType.getByName(effectName),
                        potionSection.getInt(effectName + ".dur"),
                        potionSection.getInt(effectName + ".amp")
                );

                addPotionEffect(effect);
            }
        }

        if (section.contains("usepermissions"))
            setUsePermissions(section.getBoolean("usepermissions"));

        if (section.contains("falldamage"))
            setHasFallDamage(section.getBoolean("falldamage"));

        if (section.contains("hunger"))
            setHasHunger(section.getBoolean("hunger"));

        if (section.contains("displayName"))
            setDisplayName(section.getString("displayName"));

        if (section.contains("lockInventory"))
            setInventoryLocked(section.getBoolean("lockInventory"));

        if (section.contains("lockArmour"))
            setArmourLocked(section.getBoolean("lockArmour"));

        if (section.contains("team"))
            setTeamColor(TeamColor.matchColor(section.getString("team")));

        if (section.contains("displayInMenu"))
            setDisplayInMenu(section.getBoolean("displayInMenu"));

        if (section.contains("allowOffhand"))
            setAllowOffHand(section.getBoolean("allowOffhand"));

        if (section.contains("addons")) {
            ConfigurationSection addonSection = section.getConfigurationSection("addons");

            for (String addonKey : addonSection.getKeys(false)) {
                try {
                    // First determine the class
                    Class<?> rawClass = Class.forName(addonKey.replace('-', '.'));
                    if (LoadoutAddon.class.isAssignableFrom(rawClass)) {
                        Class<? extends LoadoutAddon> clazz = rawClass.asSubclass(LoadoutAddon.class);

                        // Now we can load the value
                        LoadoutAddon<Object> addon = LoadoutModule.getAddon(clazz);
                        Object value = addon.load(addonSection.getConfigurationSection(addonKey));
                        addonValues.put(clazz, value);
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore it
                }
            }
        }
    }
}
