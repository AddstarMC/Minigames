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
	public ItemStack onClickWithItem(MinigamePlayer player, ItemStack item) {
		if(!whitelist.contains(item.getType())){
			whitelist.add(item.getType());
			getContainer().addItem(new MenuItemWhitelistBlock(item.getType(), whitelist));
		}
		else{
			player.sendMessage("Whitelist/Blacklist already contains this material", null);
		}
		return getItem();
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player) {
		beginManualEntry(player, "Enter material name into chat to add to the whitelist/blacklist, the menu will automatically reopen in 30s if nothing is entered.", 30);
		return null;
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry) {
		entry = entry.toUpperCase();
		if(Material.getMaterial(entry) != null){
			Material mat = Material.getMaterial(entry);
			if(!whitelist.contains(mat)){
				whitelist.add(mat);
				getContainer().addItem(new MenuItemWhitelistBlock(mat, whitelist));
			}
			else{
				player.sendMessage("Whitelist/Blacklist already contains this material", null);
			}
			
			return;
		}
		
		player.sendMessage("No material by the name \"" + entry + "\" was found!", "error");
	}
}
