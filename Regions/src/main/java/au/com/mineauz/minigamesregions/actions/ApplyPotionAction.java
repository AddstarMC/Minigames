package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
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

public class ApplyPotionAction extends AbstractAction {
    private final StringFlag type = new StringFlag("SPEED", "type");
    private final IntegerFlag dur = new IntegerFlag(60, "duration");
    private final IntegerFlag amp = new IntegerFlag(1, "amplifier");

    @Override
    public @NotNull String getName() {
        return "APPLY_POTION";
    }

    @Override
    public @NotNull String getCategory() {
        return "Player Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
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
    public void executeNodeAction(@NotNull MinigamePlayer mgPlayer,
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
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        type.saveValue(path, config);
        dur.saveValue(path, config);
        amp.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        type.loadValue(path, config);
        dur.loadValue(path, config);
        amp.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, "Apply Potion", mgPlayer);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        List<String> pots = new ArrayList<>(PotionEffectType.values().length);
        for (PotionEffectType type : PotionEffectType.values()) {
            pots.add(WordUtils.capitalizeFully(type.getName().replace("_", " ")));
        }
        m.addItem(new MenuItemList("Potion Type", Material.POTION, new Callback<>() {

            @Override
            public String getValue() {
                return WordUtils.capitalizeFully(type.getFlag().replace("_", " "));
            }

            @Override
            public void setValue(String value) {
                type.setFlag(value.toUpperCase().replace(" ", "_"));
            }


        }, pots));
        m.addItem(new MenuItemTime("Duration", Material.CLOCK, new Callback<>() {


            @Override
            public Integer getValue() {
                return dur.getFlag();
            }

            @Override
            public void setValue(Integer value) {
                dur.setFlag(value);
            }


        }, 0, 86400));
        m.addItem(new MenuItemInteger("Level", Material.STONE, new Callback<>() {

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
