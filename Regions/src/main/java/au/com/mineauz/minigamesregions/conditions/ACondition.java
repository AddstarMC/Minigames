package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class ACondition {
    protected @NotNull String name;
    private final BooleanFlag invert = new BooleanFlag(false, "invert");

    protected ACondition(@NotNull String name) {
        this.name = name;
    }

    protected void addInvertMenuItem(Menu m) {
        m.addItem(invert.getMenuItem(Material.ENDER_PEARL, MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_INVERT_NAME)), m.getSize() - 1);
    }

    protected void saveInvert(FileConfiguration config, String path) {
        invert.saveValue(path, config);
    }

    protected void loadInvert(@NotNull FileConfiguration config, @NotNull String path) {
        invert.loadValue(path, config);
    }

    public boolean isInverted() {
        return invert.getFlag();
    }

    public @NotNull String getName() {
        return name;
    }

    public abstract @NotNull Component getDisplayName();

    public abstract @NotNull IConditionCategory getCategory();

    public abstract boolean useInRegions();

    public abstract boolean useInNodes();

    public abstract boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region);

    public abstract boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node);

    public abstract void saveArguments(@NotNull FileConfiguration config, @NotNull String path);

    public abstract void loadArguments(@NotNull FileConfiguration config, @NotNull String path);

    public abstract boolean displayMenu(MinigamePlayer player, Menu prev);

    /**
     * Returns if the condition needs a player who caused the check to happen.
     */
    public abstract boolean PlayerNeeded();

    public abstract void describe(@NotNull Map<@NotNull String, @NotNull Object> out);

    public void debug(@NotNull Minigame mg) {
        if (Minigames.getPlugin().isDebugging()) {
            Main.getPlugin().getComponentLogger().info("Cat " + this.getCategory() + " : " + this.getName() +
                    " Check:" + mg.getName() + " mech: " + mg.getMechanicName() + "Condition:                     " + this);
        }
    }
}
