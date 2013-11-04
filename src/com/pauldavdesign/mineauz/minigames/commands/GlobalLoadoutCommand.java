package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemDisplayLoadout;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemLoadoutAdd;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class GlobalLoadoutCommand implements ICommand {
	private MinigameData mdata = Minigames.plugin.mdata;

	@Override
	public String getName() {
		return "globalloadout";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"gloadout", "loadout"};
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Opens the Loadout edit window for Global Loadouts. These loadouts may be used in any Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame globalloadout"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to edit global loadouts!";
	}

	@Override
	public String getPermission() {
		return "minigame.globalloadout";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		MinigamePlayer player = Minigames.plugin.pdata.getMinigamePlayer((Player)sender);
		Menu loadouts = new Menu(6, getName(), player);
		
		List<String> des = new ArrayList<String>();
		des.add("Shift + Right Click to Delete");
		int inc = 0;
		for(String ld : mdata.getLoadouts()){
			Material item = Material.THIN_GLASS;
			if(mdata.getLoadout(ld).getItems().size() != 0){
				item = mdata.getLoadout(ld).getItem((Integer)mdata.getLoadout(ld).getItems().toArray()[0]).getType();
			}
			loadouts.addItem(new MenuItemDisplayLoadout(ld, des, item, mdata.getLoadout(ld)), inc);
			inc++;
		}
		loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, mdata.getLoadoutMap()), 53);
		
		loadouts.displayMenu(player);
		return true;
	}

}
