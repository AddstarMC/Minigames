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
    private final BooleanFlag invert = new BooleanFlag(false, "invert");
    protected @NotNull String name;

    protected ACondition(@NotNull String name) {
        this.name = name;
    }

    protected void addInvertMenuItem(Menu m) {
        m.addItem(invert.getMenuItem(Material.ENDER_PEARL, MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_INVERT_NAME)), m.getSize() - 1);
    }

    protected void saveInvert(FileConfiguration config, String path) {
        invert.saveValue(path, config);
    }

    protected void loadInvert(FileConfiguration config, String path) {
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

    public abstract boolean checkRegionCondition(MinigamePlayer player, Region region);

    public abstract boolean checkNodeCondition(MinigamePlayer player, Node node);

    public abstract void saveArguments(FileConfiguration config, String path);

    public abstract void loadArguments(FileConfiguration config, String path);

    public abstract boolean displayMenu(MinigamePlayer player, Menu prev);

    public abstract boolean onPlayerApplicable();

    public abstract void describe(Map<String, Object> out);

    public void debug(Minigame mg) {
        if (Minigames.getPlugin().isDebugging()) {
            Main.getPlugin().getComponentLogger().info("Cat " + this.getCategory() + " : " + this.getName() +
                    " Check:" + mg.getName() + " mech: " + mg.getMechanicName() + "Condition:                     " + this);
        }
    }
}
