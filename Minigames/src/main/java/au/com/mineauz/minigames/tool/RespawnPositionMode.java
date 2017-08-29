package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.MinigameModule;
import au.com.mineauz.minigames.minigame.modules.RespawnModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created for the AddstarMC.
 * Created by Narimm on 29/08/2017.
 */
public class RespawnPositionMode implements ToolMode {
    @Override
    public String getName() {
        return "RESPAWN";
    }

    @Override
    public String getDisplayName() {
        return "Respawn Position";
    }

    @Override
    public String getDescription() {
        return "The location the Player will respawn on death";
    }

    @Override
    public Material getIcon() {
        return Material.NETHER_STAR;
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {

    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {

    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {

    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        Location loc = player.getLocation();
        RespawnModule respawn = RespawnModule.getMinigameModule(minigame);
        respawn.setRespawnLocation(loc);
        player.sendMessage("Set lobby position.", null);

    }

    @Override
    public void select(MinigamePlayer player, Minigame minigame, Team team) {
        Location loc = RespawnModule.getMinigameModule(minigame).getRespawnLocation();
        if(loc != null) {
            player.getPlayer().sendBlockChange(loc, Material.END_CRYSTAL, (byte) 1);
            player.sendMessage("Selected respawn position (marked with End Crystal)", null);
        }else{
            player.sendMessage("No respawn position set!", "error");
        }

    }

    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        Location loc = RespawnModule.getMinigameModule(minigame).getRespawnLocation();
        if(loc != null){
            player.getPlayer().sendBlockChange(loc,
                    minigame.getLobbyPosition().getBlock().getType(),
                    minigame.getLobbyPosition().getBlock().getData());
            player.sendMessage("Deselected respawn position", null);
        }
        else{
            player.sendMessage("No respawn position set!", "error");
        }
    }
}
