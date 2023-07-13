package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

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
