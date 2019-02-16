package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.script.ExpressionParser;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.Set;

public class BroadcastAction extends AbstractAction{
    
    private final StringFlag message = new StringFlag("Hello World", "message");
    private final BooleanFlag excludeExecutor = new BooleanFlag(false, "exludeExecutor");
    private final BooleanFlag redText = new BooleanFlag(false, "redText");

    @Override
    public String getName() {
        return "BROADCAST";
    }

    @Override
    public String getCategory() {
        return "Minigame Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Message", message.getFlag());
        out.put("Is Excluding", excludeExecutor.getFlag());
        out.put("Red Text", redText.getFlag());
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
    public void executeRegionAction(final MinigamePlayer player, final Region region) {
        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("player", "area", "minigame", "team");
            }
            
            @Override
            public String getAsString() {
                return "";
            }
            
            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return player;
                } else if (name.equalsIgnoreCase("area")) {
                    return region;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return player.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return player.getTeam();
                }
                
                return null;
            }
        };
        debug(player,base);
        execute(player, base);
    }

    @Override
    public void executeNodeAction(final MinigamePlayer player, final Node node) {
        ScriptObject base = new ScriptObject() {
            @Override
            public Set<String> getKeys() {
                return ImmutableSet.of("player", "area", "minigame", "team");
            }
            
            @Override
            public String getAsString() {
                return "";
            }
            
            @Override
            public ScriptReference get(String name) {
                if (name.equalsIgnoreCase("player")) {
                    return player;
                } else if (name.equalsIgnoreCase("area")) {
                    return node;
                } else if (name.equalsIgnoreCase("minigame")) {
                    return player.getMinigame();
                } else if (name.equalsIgnoreCase("team")) {
                    return player.getTeam();
                }
                
                return null;
            }
        };
        debug(player,base);
        execute(player, base);
    }
    
    private void execute(MinigamePlayer player, ScriptObject base){
        MinigameMessageType type = MinigameMessageType.INFO;
        if(redText.getFlag())
            type = MinigameMessageType.ERROR;
        MinigamePlayer exclude = null;
        if(excludeExecutor.getFlag())
            exclude = player;
        
        // Old replacement
        String message = this.message.getFlag();
        if (player != null) {
            message = message.replace("%player%", player.getDisplayName(player.getMinigame().usePlayerDisplayNames()));
        }
        // New expression system
        message = ExpressionParser.stringResolve(message, base, true, true);
        if (player != null)
            Minigames.getPlugin().getMinigameManager().sendMinigameMessage(player.getMinigame(), message, type, exclude);

    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        message.saveValue(path, config);
        excludeExecutor.saveValue(path, config);
        redText.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        message.loadValue(path, config);
        excludeExecutor.loadValue(path, config);
        redText.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Broadcast", player);
        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        
        m.addItem(message.getMenuItem("Message", Material.NAME_TAG));
        m.addItem(excludeExecutor.getMenuItem("Don't Send to Executor", Material.ENDER_PEARL));
        m.addItem(redText.getMenuItem("Red Message", Material.ENDER_PEARL));
        
        m.displayMenu(player);
        return true;
    }

}
