package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorPositionMode implements ToolMode {

    @Override
    public String getName() {
        return "SPECTATOR_START";
    }

    @Override
    public String getDisplayName() {
        return "Spectator Position";
    }

    @Override
    public String getDescription() {
        return "Sets the spectator;join position";
    }

    @Override
    public Material getIcon() {
        return Material.SOUL_SAND;
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame,
                            Team team, PlayerInteractEvent event) {

    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame,
                             Team team, PlayerInteractEvent event) {
        minigame.setSpectatorLocation(player.getLocation());
        player.sendInfoMessage("Set spectator start position.");
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
        if (minigame.getSpectatorLocation() != null) {
            player.getPlayer().sendBlockChange(minigame.getSpectatorLocation(), Material.SKELETON_SKULL, (byte) 1);
            player.sendInfoMessage("Selected spectator position (marked with skull).");
        } else {
            player.sendMessage("No spectator position set!", MinigameMessageType.ERROR);
        }
    }

    @SuppressWarnings("deprecation") //TODO: Use alternate method once available
    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getSpectatorLocation() != null) {
            player.getPlayer().sendBlockChange(minigame.getSpectatorLocation(),
                    minigame.getSpectatorLocation().getBlock().getType(),
                    minigame.getSpectatorLocation().getBlock().getData());
            player.sendInfoMessage("Spectator position deselected.");
        } else {
            player.sendMessage("No spectator position set!", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
    }

}
