package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.FloatFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlaySoundAction extends AAction {
    private final StringFlag sound = new StringFlag("ENTITY_PLAYER_LEVELUP", "sound");
    private final BooleanFlag priv = new BooleanFlag(true, "private");
    private final FloatFlag vol = new FloatFlag(1f, "volume");
    private final FloatFlag pit = new FloatFlag(1f, "pitch");

    protected PlaySoundAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_PLAYSOUND_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.WORLD;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Sound", sound.getFlag());
        out.put("Volume", vol.getFlag());
        out.put("Pitch", pit.getFlag());
        out.put("Is Private", priv.getFlag());
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer,
                                    @NotNull Region region) {
        debug(mgPlayer, region);
        execute(mgPlayer, mgPlayer.getLocation());
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        execute(mgPlayer, node.getLocation());
    }

    private void execute(MinigamePlayer player, Location loc) {
        if (player == null || !player.isInMinigame()) return;
        if (priv.getFlag()) {
            player.getPlayer().playSound(loc,
                    getSound(sound.getFlag()),
                    vol.getFlag(),
                    pit.getFlag());
        } else
            player.getPlayer().getWorld().playSound(loc,
                    getSound(sound.getFlag()),
                    vol.getFlag(),
                    pit.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        sound.saveValue(path, config);
        priv.saveValue(path, config);
        vol.saveValue(path, config);
        pit.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        sound.loadValue(path, config);
        priv.loadValue(path, config);
        vol.loadValue(path, config);
        pit.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, MgMenuLangKey.MENU_PLAYSOUND_MENU_NAME, mgPlayer);

        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        List<Sound> sounds = Arrays.asList(Sound.values());
        m.addItem(new MenuItemList<>(Material.NOTE_BLOCK, MgMenuLangKey.MENU_PLAYSOUND_SOUND_NAME, new Callback<>() {

            @Override
            public Sound getValue() {
                Sound s = getSound(sound.getFlag());              //ENSURE CONFIG doesn't contain old enums replace if they do.
                if (!s.toString().equals(sound.getFlag())) {
                    sound.setFlag(s.toString());
                }
                return s;
            }

            @Override
            public void setValue(Sound value) {
                sound.setFlag(value.toString().toUpperCase().replace(" ", "_"));
            }


        }, sounds));
        m.addItem(priv.getMenuItem(Material.ENDER_PEARL, MgMenuLangKey.MENU_PLAYSOUND_PRIVATEPLAYBACK_NAME));
        m.addItem(new MenuItemDecimal(Material.JUKEBOX, MgMenuLangKey.MENU_PLAYSOUND_VOLUME_NAME, new Callback<>() {

            @Override
            public Double getValue() {
                return vol.getFlag().doubleValue();
            }

            @Override
            public void setValue(Double value) {
                vol.setFlag(value.floatValue());
            }


        }, 0.1, 1d, 0.5, null));
        m.addItem(new MenuItemDecimal(Material.ENDER_EYE, MgMenuLangKey.MENU_PLAYSOUND_PITCH_NAME, new Callback<>() {

            @Override
            public Double getValue() {
                return pit.getFlag().doubleValue();
            }

            @Override
            public void setValue(Double value) {
                pit.setFlag(value.floatValue());
            }


        }, 0.05, 0.1, 0d, 2d));
        m.displayMenu(mgPlayer);
        return true;
    }

    private Sound getSound(String sound) {
        Sound result;
        try {
            result = Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            Minigames.getPlugin().getComponentLogger().warn("Bad Sound Config in Minigame Config : " + sound);
            result = Sound.ENTITY_PLAYER_BURP;
        }
        return result;
    }

}
