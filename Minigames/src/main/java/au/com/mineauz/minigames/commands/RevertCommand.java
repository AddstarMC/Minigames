package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

//import au.com.mineauz.minigames.StoredPlayerCheckpoints;

public class RevertCommand implements ICommand{

	@Override
	public String getName() {
		return "revert";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"r"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return MinigameUtils.getLang("command.revert.description");
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame revert"};
	}

	@Override
	public String getPermissionMessage() {
		return MinigameUtils.getLang("command.revert.noPermission");
	}

	@Override
	public String getPermission() {
		return "minigame.revert";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
        MinigamePlayer player = plugin.playerManager.getMinigamePlayer((Player) sender);
		
		if(player.isInMinigame()){
            plugin.playerManager.revertToCheckpoint(player);
        }
//		else if(plugin.playerManager.hasStoredPlayerCheckpoint(player)){
//			StoredPlayerCheckpoints spc = plugin.playerManager.getPlayersStoredCheckpoints(player);
//			if(spc.hasGlobalCheckpoint()){
//				player.getPlayer().teleport(spc.getGlobalCheckpoint());
//			}
//		}
		else {
			player.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.revert.noGlobal"));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		return null;
	}

}
