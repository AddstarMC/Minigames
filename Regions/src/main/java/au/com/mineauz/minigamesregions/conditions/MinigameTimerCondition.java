package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.TimeFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;

public class MinigameTimerCondition extends ACondition {
    private final TimeFlag minTime = new TimeFlag(5L, "minTime");
    private final TimeFlag maxTime = new TimeFlag(10L, "maxTime");

    protected MinigameTimerCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_MINIGAMETIMER_NAME);
    }

    @Override
    public @NotNull IConditionCategory getCategory() {
        return RegionConditionCategories.MINIGAME;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Time", MinigameUtils.convertTime(Duration.ofSeconds(minTime.getFlag()), true) + " - " + MinigameUtils.convertTime(Duration.ofSeconds(maxTime.getFlag()), true));
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
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        return check(region.getMinigame());
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        return check(node.getMinigame());
    }

    private boolean check(Minigame mg) {
        MinigameTimer timer = mg.getMinigameTimer();

        if (timer == null) {
            return false;
        } else {
            long timeLeft = timer.getTimeLeft();
            long min = minTime.getFlag();
            long max = maxTime.getFlag();
            debug(mg);
            return timeLeft >= min && timeLeft <= max;
        }
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        minTime.saveValue(path, config);
        maxTime.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        minTime.loadValue(path, config);
        maxTime.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, getDisplayName(), player);

        m.addItem(minTime.getMenuItem(Material.CLOCK, "Min Time", 0L, null));
        m.addItem(maxTime.getMenuItem(Material.CLOCK, "Max Time", 0L, null));

        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return false;
    }
}
