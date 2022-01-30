package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class QuitPositionMode implements ToolMode {

    @Override
    public String getName() {
        return "QUIT";
    }

    @Override
    public String getDisplayName() {
        return "Quit Position";
    }

    @Override
    public String getDescription() {
        return "Sets the quit;position";
    }

    @Override
    public Material getIcon() {
        return Material.OAK_DOOR;
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame,
                            Team team, PlayerInteractEvent event) {

    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame,
                             Team team, PlayerInteractEvent event) {
        minigame.setQuitPosition(player.getLocation());
        player.sendInfoMessage("Set quit position.");
    }

    @Override
    public void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event) {

    }

    @SuppressWarnings("deprecation") //TODO: Use alternate method once available
    @Override
    public void select(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getQuitPosition() != null) {
            player.getPlayer().sendBlockChange(minigame.getQuitPosition(), Material.SKELETON_SKULL.createBlockData());
            player.sendInfoMessage("Selected quit position (marked with skull)");
        } else {
            player.sendMessage("No quit position set!", MinigameMessageType.ERROR);
        }
    }

    @SuppressWarnings("deprecation") //TODO: Use alternate method once available
    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getQuitPosition() != null) {
            player.getPlayer().sendBlockChange(minigame.getQuitPosition(),
                    minigame.getQuitPosition().getBlock().getType(),
                    minigame.getQuitPosition().getBlock().getData());
            player.sendInfoMessage("Deselected quit position");
        } else {
            player.sendMessage("No quit position set!", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
    }

}
