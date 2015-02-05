package au.com.mineauz.minigames.display;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface IDisplayObject {
	
	public boolean isPlayerDisplay();
	
	public Player getPlayer();
	
	public World getWorld();
	
	public void show();
	public void hide();
	
	public void remove();
}
