package au.com.mineauz.minigames.commands.set;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;

public class SetGameMechanicCommand implements ICommand {

	@Override
	public String getName() {
		return "gamemechanic";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"scoretype", "mech", "gamemech", "mechanic"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets the game mechanic for a multiplayer Minigame.";
	}

	@Override
	public String[] getParameters() {
		String[] types = new String[plugin.getScoreTypes().getGameMechanics().keySet().size()];
		int inc = 0;
		for(String type : plugin.getScoreTypes().getGameMechanics().keySet()){
			types[inc] = type;
			inc++;
		}
		return types;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> gamemechanic <Parameter>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set the game mechanic!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.gamemechanic";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			boolean bool = false;
			for(String par : getParameters()){
				if(par.equalsIgnoreCase(args[0])){
					bool = true;
					break;
				}
			}
			
			if(bool){
				minigame.setMechanic(args[0].toLowerCase());
				sender.sendMessage(ChatColor.GRAY + minigame.getName(false) + " game mechanic has been set to " + args[0]);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> types = new ArrayList<String>(plugin.getScoreTypes().getGameMechanics().keySet().size());
			for(String type : plugin.getScoreTypes().getGameMechanics().keySet()){
				types.add(type);
			}
			return MinigameUtils.tabCompleteMatch(types, args[0]);
		}
		return null;
	}

}
