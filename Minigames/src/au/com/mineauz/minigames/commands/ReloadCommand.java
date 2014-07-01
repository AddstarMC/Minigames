package au.com.mineauz.minigames.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

public class ReloadCommand implements ICommand{

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Reloads the Minigames config files.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame reload"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to reload the plugin!";
	}

	@Override
	public String getPermission() {
		return "minigame.reload";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(Player p : players){
			if(plugin.pdata.getMinigamePlayer(p).isInMinigame()){
				plugin.pdata.quitMinigame(plugin.pdata.getMinigamePlayer(p), true);
			}
		}
		
		Minigames.plugin.mdata.getAllMinigames().clear();
		
		try{
			plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
		}
		catch(FileNotFoundException ex){
			plugin.getLogger().info("Failed to load config, creating one.");
			try{
				plugin.getConfig().save(plugin.getDataFolder() + "/config.yml");
			} 
			catch(IOException e){
				plugin.getLogger().log(Level.SEVERE, "Could not save config.yml!");
				e.printStackTrace();
			}
		}
		catch(Exception e){
			plugin.getLogger().log(Level.SEVERE, "Failed to load config!");
			e.printStackTrace();
		}
		
		List<String> mgs = new ArrayList<String>();
		if(Minigames.plugin.getConfig().contains("minigames")){
			mgs = Minigames.plugin.getConfig().getStringList("minigames");
		}
		final List<String> allMGS = new ArrayList<String>();
		allMGS.addAll(mgs);
		
		if(!mgs.isEmpty()){
			for(String mgm : allMGS){
				Minigame game = new Minigame(mgm);
				game.loadMinigame();
				Minigames.plugin.mdata.addMinigame(game);
			}
		}
		
		sender.sendMessage(ChatColor.GREEN + "Reloaded Minigame configs");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		return null;
	}

}
