package au.com.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.Material;

import au.com.mineauz.minigames.MessageType;
import au.com.mineauz.minigames.MinigamePlayer;

public class MenuItemFlag extends MenuItem{
	
	private String flag;
	private List<String> flags;

	public MenuItemFlag(Material displayItem, String flag, List<String> flags) {
		super(flag, displayItem);
		this.flag = flag;
		this.flags = flags;
	}

	public MenuItemFlag(String description, Material displayItem, String flag, List<String> flags) {
		super(flag, description, displayItem);
		this.flag = flag;
		this.flags = flags;
	}
	
	@Override
	public void onShiftRightClick(MinigamePlayer player){
		player.sendMessage("Removed " + flag + " flag.", MessageType.Error);
		flags.remove(flag);

		remove();
	}
}
