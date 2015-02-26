package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;

public class JoinCommand implements ICommand{

	@Override
	public String getName() {
		return "join";
	}
	
	@Override
	public String[] getAliases(){
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return MinigameUtils.getLang("command.join.description");
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame join <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return MinigameUtils.getLang("command.join.noPermission");
	}

	@Override
	public String getPermission() {
		return "minigame.join";
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
					sender.sendMessage(ChatColor.GREEN + MinigameUtils.formStr("command.join.joining", mgm.getName(false)));
					player.joinMinigame(mgm);
				} catch (IllegalStateException e) {
					player.sendMessage(ChatColor.RED + e.getMessage(), "error");
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
			return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
		}
		return null;
	}

}
