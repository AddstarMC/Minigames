package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
@SuppressWarnings("deprecation")
public class MenuItemBack extends MenuItem{
	
	private Menu prev;
	
	public MenuItemBack(Menu prev){
		super("Back", Material.LEGACY_REDSTONE_TORCH_ON);
		this.prev = prev;
	}
	
	@Override
	public ItemStack onClick(){
		prev.displayMenu(prev.getViewer());
		return null;
	}

}
