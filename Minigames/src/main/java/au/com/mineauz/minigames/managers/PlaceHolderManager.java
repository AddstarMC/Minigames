package au.com.mineauz.minigames.managers;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;



/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 3/06/2020.
 */
public class PlaceHolderManager extends  PlaceholderExpansion  {

    private Minigames plugin;

    public PlaceHolderManager(Minigames plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        return super.onRequest(p, params);
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public boolean persist(){
        return true;
    }
    @Override
    public String getIdentifier() {
        return Minigames.getPlugin().getName();
    }

    @Override
    public String getAuthor() {
        return Minigames.getPlugin().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return Minigames.getVERSION().toString();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }
        Set<String> games = plugin.getMinigameManager().getAllMinigames().keySet();
        if(identifier.contains("_")){
            String[] parts = identifier.split("_");
            String gameName = parts[0];
            if(games.contains(gameName)){
                Minigame minigame = plugin.getMinigameManager().getMinigame(gameName);
                try {
                    switch (parts[1]) {
                        case "enabled":
                            return Boolean.toString(minigame.isEnabled());
                        case "maxPlayers":
                            return Integer.toString(minigame.getMaxPlayers());
                        case "currentPlayers":
                            return Integer.toString(minigame.getPlayers().size());
                        case "type":
                            return minigame.getType().getName();
                        case "mechanic":
                            return minigame.getMechanicName();
                        case "state":
                            return minigame.getState().name();
                        case "objective":
                            return minigame.getObjective();
                        case "gameType":
                            return minigame.getGametypeName();
                        case "timeLeft":
                            return Integer.toString(minigame.getMinigameTimer().getTimeLeft());
                        case "name":
                            return minigame.getName(true);
                        default:
                            return null;
                    }
                }catch (Exception e){
                    plugin.getLogger().warning("Error processing PAPI:" + identifier);
                    plugin.getLogger().warning(e.getMessage());
                    if(plugin.isDebugging()){
                        e.printStackTrace();
                    }
                    return null;
                }
            } else {
                //this means the first part is not a gameName ?? what else could it be
                return null;
            }
        }

        switch(identifier){
            case "gameCount":
                return Integer.toString(plugin.getMinigameManager().getAllMinigames().size());
            case "enabledGameCount":
                return Long.toString(plugin.getMinigameManager().getAllMinigames().values()
                        .stream().filter(Minigame::isEnabled).count());
            case "totalPlaying":
                return Long.toString(plugin.getPlayerManager().getAllMinigamePlayers().stream()
                        .filter(MinigamePlayer::isInMinigame).count());
            default:
                return null;
        }
    }
}
