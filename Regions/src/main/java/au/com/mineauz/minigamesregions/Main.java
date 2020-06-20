package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.set.SetCommand;
import au.com.mineauz.minigames.managers.MessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.tool.ToolModes;
import au.com.mineauz.minigamesregions.commands.SetNodeCommand;
import au.com.mineauz.minigamesregions.commands.SetRegionCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ResourceBundle;
import java.util.logging.Level;

public class Main extends JavaPlugin{
    
    private static Minigames minigames;
    private static Main plugin;
    public static String LANG_KEY = "minigames-regions";
    private RegionDisplayManager display;
    
    @Override
    public void onDisable() {
        if (plugin == null) {
            return;
        }
        for (Minigame mg : minigames.getMinigameManager().getAllMinigames().values()) {
            mg.saveMinigame();
        }
        minigames.getMinigameManager().removeModule("Regions", RegionModule.class);
        
        ToolModes.removeToolMode("REGION");
        ToolModes.removeToolMode("NODE");
        ToolModes.removeToolMode("REGION_AND_NODE_EDITOR");
        
        display.shutdown();
        
        getLogger().info("Minigames Regions disabled");
    }
    
    @Override
    public void onEnable(){
        try {
            plugin = this;
            Plugin mgPlugin = getServer().getPluginManager().getPlugin("Minigames");
            if(mgPlugin != null && mgPlugin.isEnabled()){
                minigames = (Minigames)mgPlugin;
            } else {
                getLogger().severe("Minigames plugin not found! You must have the plugin to use Regions!");
                plugin = null;
                minigames = null;
                this.getPluginLoader().disablePlugin(this);
                return;
            }
            
            display = new RegionDisplayManager();
            
            minigames.getMinigameManager().addModule(RegionModule.class);
            
            SetCommand.registerSetCommand(new SetNodeCommand());
            SetCommand.registerSetCommand(new SetRegionCommand());
            
            getServer().getPluginManager().registerEvents(new RegionEvents(), this);
            
            ToolModes.addToolMode(new RegionToolMode());
            ToolModes.addToolMode(new NodeToolMode());
            ToolModes.addToolMode(new RegionNodeEditToolMode());
            loadLanguages();
            getLogger().info("Minigames Regions successfully enabled!");
        } catch (Throwable e) {
            plugin = null;
            minigames = null;
            getLogger().log(Level.SEVERE, "Failed to enable Minigames Regions " + getDescription().getVersion() + ": ", e);
            getPluginLoader().disablePlugin(this);
        }
    }

    private void loadLanguages(){
        MessageManager.registerMessageFile(LANG_KEY, ResourceBundle.getBundle("minigames_regions"));
    }

    public String getMessage(String key, String... args){
        return MessageManager.getMessage(LANG_KEY,key,args);
    }
    
    public static Minigames getMinigames(){
        return minigames;
    }
    
    public static Main getPlugin(){
        return plugin;
    }
    
    public RegionDisplayManager getDisplayManager() {
        return display;
    }
}
