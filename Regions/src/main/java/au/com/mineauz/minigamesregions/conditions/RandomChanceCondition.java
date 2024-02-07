package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
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
import java.util.Random;

public class RandomChanceCondition extends ACondition {
    private final IntegerFlag chance = new IntegerFlag(50, "chance");

    protected RandomChanceCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_RANCOMCHANCE_NAME);
    }

    @Override
    public String getCategory() {
        return "Misc ConditionRegistry";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Chance", chance.getFlag() + "%");
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
        return check();
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return check();
    }

    private boolean check() {
        double chance = this.chance.getFlag().doubleValue() / 100d;
        Random rand = new Random();
        return rand.nextDouble() <= chance;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        chance.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        chance.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Random Chance", player);
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        m.addItem(chance.getMenuItem("Percentage Chance", Material.ENDER_EYE, 1, 99));
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean onPlayerApplicable() {
        return false;
    }
}
