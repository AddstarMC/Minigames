package au.com.mineauz.minigamesregions;

import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.set.SetCommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigamesregions.commands.SetNodeCommand;
import au.com.mineauz.minigamesregions.commands.SetRegionCommand;

public class Main extends JavaPlugin{
	
	private static Minigames minigames;
	private static Main plugin;
	
	@Override
	public void onEnable(){
		plugin = this;
		if(getServer().getPluginManager().getPlugin("Minigames") != null){
			minigames = Minigames.plugin;
		}
		else{
			getLogger().severe("Minigames plugin not found! You must have the plugin to use Regions!");
			this.getPluginLoader().disablePlugin(this);
		}
		
		minigames.mdata.addModule(RegionModule.class);
		
		SetCommand.registerSetCommand(new SetNodeCommand());
		SetCommand.registerSetCommand(new SetRegionCommand());
		
		getServer().getPluginManager().registerEvents(new RegionEvents(), this);
		
		getLogger().info("Minigames Regions successfully enabled!");
	}
	
	@Override
	public void onDisable(){
		for(Minigame mg : minigames.mdata.getAllMinigames().values()){
			mg.saveMinigame();
		}
		minigames.mdata.removeModule("Regions", RegionModule.class);
		getLogger().info("Minigames Regions disabled");
	}
	
	public static Minigames getMinigames(){
		return minigames;
	}
	
	public static Main getPlugin(){
		return plugin;
	}
}
