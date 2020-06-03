package au.com.mineauz.minigames.minigame.modules;

import java.util.Map;

import au.com.mineauz.minigames.objects.ModulePlaceHolderProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.ComparableVersion;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;
import org.jetbrains.annotations.Nullable;

public abstract class MinigameModule {
    private static ComparableVersion minRequired = null;
    private final Minigame mgm;

    public MinigameModule(Minigame mgm) {
        this.mgm = mgm;
    }

    public static void setVersion(ComparableVersion version) {
        minRequired = version;
    }

    /**
     * This returns true if you the Minigames version is higher than your required version
     * ie if you require version 1.13 then and Minigames is at 1.14 it will be true
     *
     * @return true if the version exceeds your version
     */
    public static boolean checkVersion() {
        if (minRequired == null) return true;
        return !(minRequired.compareTo(Minigames.getVERSION()) > 0);
    }

    public static void addMetricChart(Metrics.CustomChart chart) {
        Minigames.getPlugin().addMetric(chart);
    }

    public static ComparableVersion getMinRequired() {
        return minRequired;
    }

    public Minigame getMinigame() {
        return mgm;
    }

    public abstract String getName();

    public abstract Map<String, Flag<?>> getFlags();

    public abstract boolean useSeparateConfig();

    public abstract void save(FileConfiguration config);

    public abstract void load(FileConfiguration config);

    public abstract void addEditMenuOptions(Menu menu);

    public abstract boolean displayMechanicSettings(Menu previous);

    /**
     * You should override this methid if the module should provide more placeholders for a game it services.
     * @return ModulePlaceHolderProvider
     */
    @Nullable
    public ModulePlaceHolderProvider getModulePlaceHolders(){
        return null;
    }
}
