package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MenuItemFlag extends MenuItem{
	
	private String flag;
	private List<String> flags;

	public MenuItemFlag(Material displayItem, String flag, List<String> flags) {
		super(flag, displayItem);
		this.flag = flag;
		this.flags = flags;
	}

	public MenuItemFlag(List<String> description, Material displayItem, String flag, List<String> flags) {
		super(flag, description, displayItem);
		this.flag = flag;
		this.flags = flags;
	}
	
	@Override
	public ItemStack onShiftRightClick(){
		getContainer().getViewer().sendMessage("Removed " + flag + " flag.", "error");
		flags.remove(flag);

		getContainer().removeItem(getSlot());
		return null;
	}
}
