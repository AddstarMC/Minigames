package au.com.mineauz.minigames.display;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface IDisplayObject {

    boolean isPlayerDisplay();

    Player getPlayer();

    World getWorld();

    void show();

    void hide();

    void remove();
}
