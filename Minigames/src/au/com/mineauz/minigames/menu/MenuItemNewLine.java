package au.com.mineauz.minigames.menu;

import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItemNewLine extends MenuItem{

	public MenuItemNewLine() {
		super("NL", null);
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		return null;
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player){
		return null;
	}
	
	@Override
	public ItemStack onShiftClick(MinigamePlayer player){
		return null;
	}
	
	@Override
	public ItemStack onShiftRightClick(MinigamePlayer player){
		return null;
	}
	
	@Override
	public ItemStack onDoubleClick(MinigamePlayer player){
		return null;
	}
}
