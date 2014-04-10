package com.pauldavdesign.mineauz.minigames.minigame.regions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;

public class MenuItemExecutor extends MenuItem{
	
	private Region region;

	public MenuItemExecutor(String name, Material displayItem, Region region) {
		super(name, displayItem);
		this.region = region;
	}

	public MenuItemExecutor(String name, List<String> description, Material displayItem, Region region) {
		super(name, description, displayItem);
		this.region = region;
	}
	
	@Override
	public ItemStack onClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter the name of a trigger and the action to take when triggered by the region (syntax and names below). "
				+ "Window will reopen in 60s if nothing is entered.", null);
		List<String> triggers = new ArrayList<String>(RegionTrigger.values().length);
		for(RegionTrigger t : RegionTrigger.values()){
			triggers.add(MinigameUtils.capitalize(t.toString()));
		}
		List<String> actions = new ArrayList<String>(RegionAction.values().length);
		for(RegionAction a : RegionAction.values()){
			actions.add(MinigameUtils.capitalize(a.toString()));
		}
		ply.sendMessage("Triggers: " + MinigameUtils.listToString(triggers));
		ply.sendMessage("Actions: " + MinigameUtils.listToString(actions));
		ply.sendMessage("Syntax: Trigger, Action");
		ply.setManualEntry(this);

		getContainer().startReopenTimer(60);
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry){
		String[] split = entry.split(", ");
		if(split.length == 2){
			RegionTrigger trig = RegionTrigger.getByName(split[0]);
			RegionAction act = RegionAction.getByName(split[1]);
			
			if(trig != null && act != null){
				final RegionExecutor ex = new RegionExecutor(trig, act);
				region.addExecutor(ex);
				MenuItemCustom cmi = new MenuItemCustom("Executor ID: " + region.getExecutors().size(), 
						MinigameUtils.stringToList(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + 
								MinigameUtils.capitalize(trig.toString()) + ";" +
								ChatColor.GREEN + "Action: " + ChatColor.GRAY + 
								MinigameUtils.capitalize(act.toString()) + ";" + 
								ChatColor.DARK_PURPLE + "(Right click to delete)"), Material.ENDER_PEARL);

				cmi.setRightClick(new InteractionInterface() {
					
					@Override
					public Object interact() {
						region.removeExecutor(ex);
						getContainer().removeItem(getSlot());
						return null;
					}
				});
				getContainer().addItem(cmi);
				getContainer().cancelReopenTimer();
				getContainer().displayMenu(getContainer().getViewer());
				return;
			}
		}
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("Invalid syntax entry! Make sure there is an comma and a space (\", \") between each item.", "error");
	}
}
