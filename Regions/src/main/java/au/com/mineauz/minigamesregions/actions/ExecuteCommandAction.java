package au.com.mineauz.minigamesregions.actions;

import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.ImmutableSet;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.script.ExpressionParser;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.util.NullCommandSender;

public class ExecuteCommandAction extends AbstractAction {
    
    private StringFlag comd = new StringFlag("say Hello World!", "command");
    private BooleanFlag silentExecute = new BooleanFlag(false, "silent");

    @Override
    public String getName() {
        return "EXECUTE_COMMAND";
    }

    @Override
    public String getCategory() {
        return "Server Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        out.put("Command", comd.getFlag());
        out.put("Silent", silentExecute.getFlag());
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }
    
    private String replacePlayerTags(MinigamePlayer player, String string) {
        if (player == null) {
            return string;
        }
        
        return string
            .replace("{player}", player.getName())
            .replace("{dispplayer}", player.getDisplayName())
            .replace("{px}", String.valueOf(player.getLocation().getX()))
            .replace("{py}", String.valueOf(player.getLocation().getY()))
            .replace("{pz}", String.valueOf(player.getLocation().getZ()))
            .replace("{yaw}", String.valueOf(player.getLocation().getYaw()))
            .replace("{pitch}", String.valueOf(player.getLocation().getPitch()))
            .replace("{minigame}", player.getMinigame().getName(false))
            .replace("{dispminigame}", player.getMinigame().getName(true))
            .replace("{deaths}", String.valueOf(player.getDeaths()))
            .replace("{kills}", String.valueOf(player.getKills()))
            .replace("{reverts}", String.valueOf(player.getReverts()))
            .replace("{score}", String.valueOf(player.getScore()))
            .replace("{team}", (player.getTeam() != null ? player.getTeam().getDisplayName() : ""));
    }

    @Override
    public void executeRegionAction(final MinigamePlayer player, final Region region) {
        debug(player,region);
        String command = replacePlayerTags(player, comd.getFlag());
        command = command.replace("{region}", region.getName());
        
        // New expression system
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
        
        command = ExpressionParser.stringResolve(command, base, true, true);
        dispatch(command);
    }

    @Override
    public void executeNodeAction(final MinigamePlayer player, final Node node) {
        debug(player,node);
        String command = replacePlayerTags(player, comd.getFlag());
        command = command
            .replace("{x}", String.valueOf(node.getLocation().getBlockX()))
            .replace("{y}", String.valueOf(node.getLocation().getBlockY()))
            .replace("{z}", String.valueOf(node.getLocation().getBlockZ()))
            .replace("{node}", node.getName());
        
        // New expression system
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
        
        command = ExpressionParser.stringResolve(command, base, true, true);
        dispatch(command);
    }
    
    private void dispatch(String command) {
        if (silentExecute.getFlag()) {
            Bukkit.dispatchCommand(new NullCommandSender(), command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
    
    @Override
    public void saveArguments(FileConfiguration config,
            String path) {
        comd.saveValue(path, config);
        silentExecute.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
            String path) {
        comd.loadValue(path, config);
        silentExecute.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Execute Command", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(new MenuItemString("Command", MinigameUtils.stringToList("Do not include '/';If '//' command, start with './'"),
                Material.COMMAND_BLOCK, new Callback<String>() {
            
            @Override
            public void setValue(String value) {
                if(value.startsWith("./"))
                    value = value.replaceFirst("./", "/");
                comd.setFlag(value);
            }
            
            @Override
            public String getValue() {
                return comd.getFlag();
            }
        }));
        m.addItem(silentExecute.getMenuItem("Is Silent", Material.NOTE_BLOCK, MinigameUtils.stringToList("When on, console output;for a command will be;silenced.;NOTE: Does not work with;minecraft commands")));
        m.displayMenu(player);
        return true;
    }

}
