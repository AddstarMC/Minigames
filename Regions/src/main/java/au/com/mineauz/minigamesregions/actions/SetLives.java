package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 6/11/2017.
 */
public class SetLives extends AbstractAction { //todo unused!
    private final IntegerFlag amount = new IntegerFlag(1, "amount");

    @Override
    public String getName() {
        return "SET_LIVES";
    }

    @Override
    public String getCategory() {
        return "Minigame Actions";
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Set Lives to:", amount.getFlag());
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
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        mgPlayer.getMinigame().setLives(amount.getFlag());
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {

    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {

    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {

    }

    @Override
    public boolean displayMenu(@NotNull MinigamePlayer mgPlayer, Menu previous) {
        return false;
    }
}
