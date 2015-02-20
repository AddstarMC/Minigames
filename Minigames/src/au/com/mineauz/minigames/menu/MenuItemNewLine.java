package au.com.mineauz.minigames.menu;

import org.bukkit.Material;

/**
 * This is a special MenuItem which is treated as a line break when adding MenuItems through {@link Menu#addItem(MenuItem)}
 */
public class MenuItemNewLine extends MenuItem{
	public MenuItemNewLine() {
		super("NL", Material.STONE);
	}
}
