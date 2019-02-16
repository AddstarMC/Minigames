package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class ApplyPotionAction extends AbstractAction {
    
    private final StringFlag type = new StringFlag("SPEED", "type");
    private final IntegerFlag dur = new IntegerFlag(60, "duration");
    private final IntegerFlag amp = new IntegerFlag(1, "amplifier");

    @Override
    public String getName() {
        return "APPLY_POTION";
    }

    @Override
    public String getCategory() {
        return "Player Actions";
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
    public void executeRegionAction(MinigamePlayer player,
            Region region) {
        debug(player,region);
        execute(player);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
            Node node) {
        debug(player,node);
        execute(player);
    }
    
    private void execute(MinigamePlayer player){
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
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Apply Potion", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        List<String> pots = new ArrayList<>(PotionEffectType.values().length);
        for (PotionEffectType type : PotionEffectType.values()) {
            if (type != null) {
                pots.add(MinigameUtils.capitalize(type.getName().replace("_", " ")));
            }
        }
        m.addItem(new MenuItemList("Potion Type", Material.POTION, new Callback<String>() {
            
            @Override
            public void setValue(String value) {
                type.setFlag(value.toUpperCase().replace(" ", "_"));
            }
            
            @Override
            public String getValue() {
                return MinigameUtils.capitalize(type.getFlag().replace("_", " "));
            }
        }, pots));
        m.addItem(new MenuItemTime("Duration", Material.CLOCK, new Callback<Integer>() {


            @Override
            public void setValue(Integer value) {
                dur.setFlag(value);
            }

            @Override
            public Integer getValue() {
                return dur.getFlag();
            }
        }, 0, 86400));
        m.addItem(new MenuItemInteger("Level", Material.STONE, new Callback<Integer>() {

            @Override
            public void setValue(Integer value) {
                amp.setFlag(value);
            }

            @Override
            public Integer getValue() {
                return amp.getFlag();
            }
        }, 0, 100));
        m.displayMenu(player);
        return true;
    }

}
