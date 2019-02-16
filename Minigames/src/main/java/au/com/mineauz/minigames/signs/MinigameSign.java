package au.com.mineauz.minigames.signs;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.mineauz.minigames.objects.MinigamePlayer;

public interface MinigameSign {

    String getName();

    String getCreatePermission();

    String getCreatePermissionMessage();

    String getUsePermission();

    String getUsePermissionMessage();

    boolean signCreate(SignChangeEvent event);

    boolean signUse(Sign sign, MinigamePlayer player);

    void signBreak(Sign sign, MinigamePlayer player);
}
