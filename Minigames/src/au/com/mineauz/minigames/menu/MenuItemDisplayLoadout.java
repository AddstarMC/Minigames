package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;

public class MenuItemDisplayLoadout extends MenuItem{
	
	private PlayerLoadout loadout = null;
	private Minigame mgm = null;
	private boolean allowDelete = true;
	
	public MenuItemDisplayLoadout(String name, Material displayItem, PlayerLoadout loadout, Minigame minigame) {
		super(name, displayItem);
		this.loadout = loadout;
		mgm = minigame;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}
	
	public MenuItemDisplayLoadout(String name, Material displayItem, PlayerLoadout loadout) {
		super(name, displayItem);
		this.loadout = loadout;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}

	public MenuItemDisplayLoadout(String name, String description, Material displayItem, PlayerLoadout loadout, Minigame minigame) {
		super(name, description, displayItem);
		this.loadout = loadout;
		mgm = minigame;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}

	public MenuItemDisplayLoadout(String name, String description, Material displayItem, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.loadout = loadout;
		if(!loadout.isDeleteable())
			allowDelete = false;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu loadoutMenu = new Menu(5, loadout.getName(false), new MenuPageInventory(45, null));
		Menu loadoutSettings = new Menu(6, loadout.getName(false));
		
		List<MenuItem> mItems = new ArrayList<MenuItem>();
		if(!loadout.getName(false).equals("default"))
			mItems.add(new MenuItemBoolean("Use Permissions", "Permission:;minigame.loadout." + loadout.getName(false).toLowerCase(), 
					Material.GOLD_INGOT, loadout.getUsePermissionsCallback()));
		MenuItemString disName = new MenuItemString("Display Name", Material.PAPER, loadout.getDisplayNameCallback());
		disName.setAllowNull(true);
		mItems.add(disName);
		mItems.add(new MenuItemBoolean("Allow Fall Damage", Material.LEATHER_BOOTS, loadout.getFallDamageCallback()));
		mItems.add(new MenuItemBoolean("Allow Hunger", Material.APPLE, loadout.getHungerCallback()));
		mItems.add(new MenuItemInteger("XP Level", "Use -1 to not;use loadout levels", Material.EXP_BOTTLE, loadout.getLevelCallback(), -1, Integer.MAX_VALUE));
		mItems.add(new MenuItemBoolean("Lock Inventory", Material.DIAMOND_SWORD, loadout.getInventoryLockedCallback()));
		mItems.add(new MenuItemBoolean("Lock Armour", Material.DIAMOND_CHESTPLATE, loadout.getArmourLockedCallback()));
		mItems.add(new MenuItemBoolean("Display in Loadout Menu", Material.THIN_GLASS, loadout.getDisplayInMenuCallback()));
		List<String> teams = new ArrayList<String>();
		teams.add("None");
		for(TeamColor col : TeamColor.values())
			teams.add(MinigameUtils.capitalize(col.toString()));
		mItems.add(new MenuItemList("Lock to Team", Material.LEATHER_CHESTPLATE, loadout.getTeamColorCallback(), teams));
		loadoutSettings.addItems(mItems);
		
		Menu potionMenu = new Menu(5, getContainer().getName());
		
		potionMenu.setControlItem(new MenuItemPotionAdd("Add Potion", Material.ITEM_FRAME, loadout), 4);
		
		List<MenuItem> potions = new ArrayList<MenuItem>();
		
		for(PotionEffect eff : loadout.getAllPotionEffects()){
			potions.add(new MenuItemPotion(MinigameUtils.capitalize(eff.getType().getName().replace("_", " ")), "Shift + Right Click to Delete", Material.POTION, eff, loadout));
		}
		potionMenu.addItems(potions);
		
		loadoutMenu.setAllowModify(true);
		
		MenuPageInventory inventory = (MenuPageInventory)loadoutMenu.getFirstPage();
		loadoutMenu.setControlItem(new MenuItemSaveLoadout("Loadout Settings", Material.CHEST, loadout, loadoutSettings), 2);
		loadoutMenu.setControlItem(new MenuItemSaveLoadout("Edit Potion Effects", Material.POTION, loadout, potionMenu), 3);
		loadoutMenu.setControlItem(new MenuItemSaveLoadout("Save Loadout", Material.REDSTONE_TORCH_ON, loadout), 4);
		
		for(Integer item : loadout.getItems()){
			if(item < 100)
				inventory.setSlot(loadout.getItem(item), item);
			else if(item == 100)
				inventory.setSlot(loadout.getItem(item), 39);
			else if(item == 101)
				inventory.setSlot(loadout.getItem(item), 38);
			else if(item == 102)
				inventory.setSlot(loadout.getItem(item), 37);
			else if(item == 103)
				inventory.setSlot(loadout.getItem(item), 36);
		}
		
		loadoutMenu.displayMenu(player);
	}
	
	@Override
	public void onShiftRightClick(MinigamePlayer player){
		if(allowDelete){
			beginManualEntry(player, "Delete the " + loadout.getName(false) + " loadout from " + getName() + "? Type \"Yes\" to confirm.", 10);;
			player.sendMessage("The menu will automatically reopen in 10s if nothing is entered.");
		}
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		if(entry.equalsIgnoreCase("yes")){
			String loadoutName = loadout.getName(false);
			if(mgm != null)
				mgm.getModule(LoadoutModule.class).deleteLoadout(loadoutName);
			else
				Minigames.plugin.mdata.deleteLoadout(loadoutName);
			remove();
			
			player.sendMessage(loadoutName + " has been deleted.", MessageType.Normal);
			return;
		}
		player.sendMessage(loadout.getName(false) + " was not deleted.", MessageType.Error);
	}
	
	public void setAllowDelete(boolean bool){
		allowDelete = bool;
	}
}
