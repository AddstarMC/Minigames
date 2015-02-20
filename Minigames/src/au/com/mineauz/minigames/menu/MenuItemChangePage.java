package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItemChangePage extends MenuItem {
	private MenuSession session;
	private int page;
	
	public MenuItemChangePage(String name, MenuSession session, int page) {
		super(name, Material.REDSTONE_TORCH_ON);
		this.session = session;
		this.page = page;
	}
	
	@Override
	public void onClick(MinigamePlayer player) {
		session.page = page;
		session.current.displaySession(player, session);
	}

}
