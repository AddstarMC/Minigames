package com.pauldavdesign.mineauz.minigames.commands.set;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemDisplayLoadout;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemLoadoutAdd;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class SetLoadoutCommand implements ICommand {

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
		return "Opens the Loadout edit window for the desired Minigames Loadouts.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame set <Minigame> loadout"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to edit a Minigames loadouts!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.loadout";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {

		MinigamePlayer player = Minigames.plugin.pdata.getMinigamePlayer((Player)sender);
		Menu loadouts = new Menu(6, getName(), player);
		List<MenuItem> mi = new ArrayList<MenuItem>();
		
		List<String> des = new ArrayList<String>();
		des.add("Shift + Right Click to Delete");
		
		Material item = Material.THIN_GLASS;
		if(minigame.getDefaultPlayerLoadout().getItems().size() != 0){
			item = minigame.getDefaultPlayerLoadout().getItem((Integer)minigame.getDefaultPlayerLoadout().getItems().toArray()[0]).getType();
		}
		MenuItemDisplayLoadout defLoad = new MenuItemDisplayLoadout("Default Loadout", item, minigame.getDefaultPlayerLoadout(), minigame);
		defLoad.setAllowDelete(false);
		mi.add(defLoad);
		
		for(String ld : minigame.getLoadouts()){
			item = Material.THIN_GLASS;
			if(minigame.getLoadout(ld).getItems().size() != 0){
				item = minigame.getLoadout(ld).getItem((Integer)minigame.getLoadout(ld).getItems().toArray()[0]).getType();
			}
			mi.add(new MenuItemDisplayLoadout(ld, des, item, minigame.getLoadout(ld), minigame));
		}
		loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, minigame.getLoadoutMap(), minigame), 53);
		loadouts.addItems(mi);
		
		loadouts.displayMenu(player);
		return true;
	}

}
