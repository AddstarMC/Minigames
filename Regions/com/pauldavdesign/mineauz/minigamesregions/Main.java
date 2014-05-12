package com.pauldavdesign.mineauz.minigamesregions;

import org.bukkit.plugin.java.JavaPlugin;

import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.commands.set.SetCommand;
import com.pauldavdesign.mineauz.minigamesregions.commands.SetNodeCommand;
import com.pauldavdesign.mineauz.minigamesregions.commands.SetRegionCommand;

public class Main extends JavaPlugin{
	
	private Minigames minigames;
	
	@Override
	public void onEnable(){
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
		getLogger().info("Minigames Regions disabled");
	}
	
	public Minigames getMinigames(){
		return minigames;
	}
}
