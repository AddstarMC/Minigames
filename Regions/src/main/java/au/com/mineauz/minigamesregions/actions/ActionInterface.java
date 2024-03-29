package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public interface ActionInterface {
    String getName();

    String getCategory();

    void describe(Map<String, Object> out);

    boolean useInRegions();

    boolean useInNodes();

    void executeRegionAction(MinigamePlayer player, Region region);

    void executeNodeAction(MinigamePlayer player, Node node);

    void saveArguments(FileConfiguration config, String path);

    void loadArguments(FileConfiguration config, String path);

    boolean displayMenu(MinigamePlayer player, Menu previous);

    void debug(MinigamePlayer p, ScriptObject obj);


}
