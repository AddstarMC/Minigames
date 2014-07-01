package au.com.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;

public class LoadoutCommand implements ICommand {

	@Override
	public String getName() {
		return "loadout";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return MinigameUtils.getLang("command.loadout.description");
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame loadout", "/minigame loadout <LoadoutName>"};
	}

	@Override
	public String getPermissionMessage() {
		return MinigameUtils.getLang("command.loadout.noPermission");
	}

	@Override
	public String getPermission() {
		return "minigame.loadout.menu";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		MinigamePlayer ply = Minigames.plugin.pdata.getMinigamePlayer((Player)sender);
		if(ply.isInMinigame()){
			LoadoutModule.getMinigameModule(ply.getMinigame()).displaySelectionMenu(ply, false);
		}
		else{
			sender.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.loadout.noMinigame"));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		return null;
	}

}
