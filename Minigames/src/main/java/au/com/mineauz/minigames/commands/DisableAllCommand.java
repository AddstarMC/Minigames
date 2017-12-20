package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameData;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;

public class DisableAllCommand implements ICommand {

	@Override
	public String getName() {
		return "disableall";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"disall"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Disables all Minigames, unless it's added to the exclude list.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame disableall [ExcludedMinigame]..."};
	}

	@Override
	public String getPermissionMessage() {
		return "You don't have permission to disable all Minigames!";
	}

	@Override
	public String getPermission() {
		return "minigame.disableall";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		MinigameData mdata = Minigames.plugin.mdata;
		List<Minigame> minigames = new ArrayList<>(mdata.getAllMinigames().values());
		if(args != null){
			for(String arg : args){
				if(mdata.hasMinigame(arg))
					minigames.remove(mdata.getMinigame(arg));
				else
					sender.sendMessage(ChatColor.RED + "No Minigame found by the name \"" + arg + "\"; Ignoring...");
			}
		}
		for(Minigame mg : minigames){
			mg.setEnabled(false);
		}
		sender.sendMessage(ChatColor.GRAY + String.valueOf(minigames.size()) + " Minigames disabled!");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		List<String> mgs = new ArrayList<>(plugin.mdata.getAllMinigames().keySet());
		return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
	}

}
