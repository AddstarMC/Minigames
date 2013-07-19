package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;

public interface MinigameSign {
	
	public String getName();
	
	public String getCreatePermission();
	
	public String getCreatePermissionMessage();
	
	public String getUsePermission();
	
	public String getUsePermissionMessage();
	
	public boolean signCreate(SignChangeEvent event);
	
	public boolean signUse(Sign sign, MinigamePlayer player);
}
