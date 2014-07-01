package au.com.mineauz.minigames.signs;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.mineauz.minigames.MinigamePlayer;

public interface MinigameSign {
	
	public String getName();
	
	public String getCreatePermission();
	
	public String getCreatePermissionMessage();
	
	public String getUsePermission();
	
	public String getUsePermissionMessage();
	
	public boolean signCreate(SignChangeEvent event);
	
	public boolean signUse(Sign sign, MinigamePlayer player);
	
	public void signBreak(Sign sign, MinigamePlayer player);
}
