package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

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
			LoadoutModule module = ply.getMinigame().getModule(LoadoutModule.class);
			if(args == null){
				module.displaySelectionMenu(ply, false);
			}
			else{
				String ln = args[0];
				if(module.hasLoadout(ln)){
					ply.setLoadout(module.getLoadout(ln));
					ply.sendMessage(MinigameUtils.formStr("player.loadout.nextSpawnName", ln), null);
				}
				else{
					ply.sendMessage(MinigameUtils.formStr("player.loadout.noLoadout", ln), "error");
				}
			}
		}
		else{
			ply.sendMessage(MinigameUtils.getLang("command.loadout.noMinigame"), "error");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args != null){
			MinigamePlayer ply = Minigames.plugin.pdata.getMinigamePlayer((Player)sender);
			if(ply.isInMinigame()){
				if(args.length == 1){
					return MinigameUtils.tabCompleteMatch(new ArrayList<String>(
							ply.getMinigame().getModule(LoadoutModule.class).getLoadoutMap().keySet()), 
							args[0]);
				}
			}
		}
		return null;
	}

}
