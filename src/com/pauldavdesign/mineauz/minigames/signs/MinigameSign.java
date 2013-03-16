package com.pauldavdesign.mineauz.minigames.signs;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public interface MinigameSign {
	
	public String getName();
	
	public String getCreatePermission();
	
	public String getCreatePermissionMessage();
	
	public String getUsePermission();
	
	public String getUsePermissionMessage();
	
	public boolean signCreate(SignChangeEvent event);
	
	public boolean signUse(Sign sign, Player player);
}
