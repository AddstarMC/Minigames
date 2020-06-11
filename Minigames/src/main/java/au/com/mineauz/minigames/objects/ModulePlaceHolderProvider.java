package au.com.mineauz.minigames.objects;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 3/06/2020.
 */
public interface ModulePlaceHolderProvider {

    boolean hasPlaceHolder(String placeHolder);

    default String onPlaceHolderRequest(Player player, String game, String placeHolder){
        return null;
    }

    List<String> getIdentifiers();
}
