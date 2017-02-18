package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;

public class QuitCommand implements ICommand{

	@Override
	public String getName() {
		return "quit";
	}

	@Override
	public String[] getAliases(){
		return new String[] {
			"q",
			"leave",
			"l"
		};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return MinigameUtils.getLang("command.quit.description");
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame quit [Player]",
				"/minigame quit ALL [MinigameName]"
		};
	}

	@Override
	public String getPermissionMessage() {
		return MinigameUtils.getLang("command.quit.noPermission");
	}

	@Override
	public String getPermission() {
		return "minigame.quit";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args == null && sender instanceof Player){
			MinigamePlayer player = plugin.pdata.getMinigamePlayer((Player)sender);
			if(player.isInMinigame()){
				plugin.pdata.quitMinigame(player, false);
			}
			else {
				sender.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.quit.notPlaying"));
			}
			return true;
		}
		else if(args != null){
			Player player = null;
			if(sender instanceof Player){
				player = (Player)sender;
			}
			if(player == null || player.hasPermission("minigame.quit.other")){
				List<Player> players = plugin.getServer().matchPlayer(args[0]);
				MinigamePlayer ply = null;
				if(args[0].equals("ALL")){
					if(args.length > 1){
						if(plugin.mdata.hasMinigame(args[1])){
							Minigame mg = plugin.mdata.getMinigame(args[1]);
							List<MinigamePlayer> pls = new ArrayList<MinigamePlayer>(mg.getPlayers());
							for(MinigamePlayer pl : pls){
								plugin.pdata.quitMinigame(pl, true);
							}
							sender.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("command.quit.quitAllMinigame", mg.getName(false)));
						}
						else{
							sender.sendMessage(ChatColor.RED + MinigameUtils.formStr("minigame.error.noMinigameName", args[1]));
						}
					}
					else{
						for(MinigamePlayer pl : plugin.getPlayerData().getAllMinigamePlayers()){
							if(pl.isInMinigame()){
								plugin.pdata.quitMinigame(pl, true);
							}
						}
						sender.sendMessage(ChatColor.GRAY + MinigameUtils.getLang("command.quit.quitAll"));
					}
					return true;
				}
				else if(players.isEmpty()){
					sender.sendMessage(ChatColor.RED + MinigameUtils.formStr("command.quit.invalidPlayer", args[0]));
					return true;
				}
				else{
					ply = plugin.pdata.getMinigamePlayer(players.get(0));
				}

				if(ply != null && ply.isInMinigame()){
					plugin.pdata.quitMinigame(ply, false);
					sender.sendMessage(ChatColor.GRAY + MinigameUtils.formStr("command.quit.quitOther", ply.getName()));
				}
				else{
					sender.sendMessage(ChatColor.RED + MinigameUtils.formStr("command.quit.invalidPlayer", args[0]));
				}
			}
			else if(player != null){
				sender.sendMessage(ChatColor.RED + MinigameUtils.getLang("command.quit.noPermissionOther"));
				sender.sendMessage(ChatColor.RED + "minigame.quit.other");
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> plys = new ArrayList<String>(plugin.getServer().getOnlinePlayers().size() + 1);
			for(Player ply : plugin.getServer().getOnlinePlayers()){
				plys.add(ply.getName());
			}
			plys.add("ALL");
			return MinigameUtils.tabCompleteMatch(plys, args[0]);
		}
		else if(args.length == 2){
			List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
			return MinigameUtils.tabCompleteMatch(mgs, args[1]);
		}
		return null;
	}

}
