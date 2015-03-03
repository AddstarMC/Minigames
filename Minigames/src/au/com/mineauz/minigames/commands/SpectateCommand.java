package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;

public class SpectateCommand implements ICommand {

	@Override
	public String getName() {
		return "spectate";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"spec"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Allows a player to force spectate a Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame spectate <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the spectate command!";
	}

	@Override
	public String getPermission() {
		return "minigame.spectate";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
		if(args != null){
			MinigamePlayer player = plugin.pdata.getMinigamePlayer((Player)sender);
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
			if (mgm == null) {
				player.sendMessage(ChatColor.RED + MinigameUtils.getLang("minigame.error.noMinigame"));
			} else {
				try {
					player.spectateMinigame(mgm);
				} catch (IllegalStateException e) {
					player.sendMessage(ChatColor.RED + e.getMessage(), MessageType.Error);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
		return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
	}

}
