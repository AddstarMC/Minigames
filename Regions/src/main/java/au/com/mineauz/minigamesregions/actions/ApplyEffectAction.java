package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.config.TimeFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplyEffectAction extends AAction {
    private final StringFlag type = new StringFlag("SPEED", "type");
    private final TimeFlag dur = new TimeFlag(60L, "duration");
    private final IntegerFlag amp = new IntegerFlag(1, "amplifier");

    protected ApplyEffectAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_EFFECTAPPLY_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.PLAYER;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Effect", type.getFlag() + " " + amp.getFlag());
        out.put("Duration", MinigameUtils.convertTime(amp.getFlag(), true));
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
        execute(mgPlayer);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer,
                                  @NotNull Node node) {
        debug(mgPlayer, node);
        execute(mgPlayer);
    }

    private void execute(MinigamePlayer player) {
        PotionEffect effect = new PotionEffect(PotionEffectType.getByName(type.getFlag()),
                dur.getFlag() * 20, amp.getFlag() - 1);
        player.getPlayer().addPotionEffect(effect);
    }

    @Override
    public void saveArguments(FileConfiguration config,
                              String path) {
        type.saveValue(path, config);
        dur.saveValue(path, config);
        amp.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
                              String path) {
        type.loadValue(path, config);
        dur.loadValue(path, config);
        amp.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        List<String> pots = new ArrayList<>(PotionEffectType.values().length);
        for (PotionEffectType type : PotionEffectType.values()) {
            pots.add(WordUtils.capitalize(type.getName().replace("_", " ")));
        }
        m.addItem(new MenuItemList<PotionEffectType>(Material.POTION, "Potion Type", new Callback<>() {

            @Override
            public PotionEffectType getValue() {
                return WordUtils.capitalize(type.getFlag().replace("_", " "));
            }

            @Override
            public void setValue(PotionEffectType value) {
                type.setFlag(value.getName().toUpperCase().replace(" ", "_"));
            }


        }, pots));
        m.addItem(dur.getMenuItem(Material.CLOCK, "Duration", 0L, 86400));
        m.addItem(new MenuItemInteger(Material.EXPERIENCE_BOTTLE, "Level", new Callback<>() {

            @Override
            public Integer getValue() {
                return amp.getFlag();
            }

            @Override
            public void setValue(Integer value) {
                amp.setFlag(value);
            }

        }, 0, 100));
        m.displayMenu(mgPlayer);
        return true;
    }

}
