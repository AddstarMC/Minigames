package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;

public class MenuItemAddWhitelistBlock extends MenuItem{
	
	private List<Material> whitelist;

	public MenuItemAddWhitelistBlock(String name, List<Material> whitelist) {
		super(name, Material.ITEM_FRAME);
		setDescription(MinigameUtils.stringToList("Left Click with item to;add to whitelist/blacklist;Click without item to;manually add item."));
		this.whitelist = whitelist;
	}
	
	@Override
	public ItemStack onClickWithItem(ItemStack item) {
		if(!whitelist.contains(item.getType())){
			whitelist.add(item.getType());
			getContainer().addItem(new MenuItemWhitelistBlock(item.getType(), whitelist));
		}
		else{
			getContainer().getViewer().sendMessage("Whitelist/Blacklist already contains this material", null);
		}
		return getItem();
	}
	
	@Override
	public ItemStack onClick() {
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter material name into chat to add to the whitelist/blacklist, the menu will automatically reopen in 30s if nothing is entered.", null);
		ply.setManualEntry(this);

		getContainer().startReopenTimer(30);
		return null;
	}
	
	@Override
	public void checkValidEntry(String entry) {
		entry = entry.toUpperCase();
		if(Material.getMaterial(entry) != null){
			Material mat = Material.getMaterial(entry);
			if(!whitelist.contains(mat)){
				whitelist.add(mat);
				getContainer().addItem(new MenuItemWhitelistBlock(mat, whitelist));
			}
			else{
				getContainer().getViewer().sendMessage("Whitelist/Blacklist already contains this material", null);
			}
			
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("No material by the name \"" + entry + "\" was found!", "error");
	}
}
