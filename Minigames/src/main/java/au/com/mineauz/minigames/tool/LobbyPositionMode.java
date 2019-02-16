package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyPositionMode implements ToolMode {

    @Override
    public String getName() {
        return "LOBBY";
    }

    @Override
    public String getDisplayName() {
        return "Lobby Position";
    }

    @Override
    public String getDescription() {
        return "Sets the lobby;position";
    }

    @Override
    public Material getIcon() {
        return Material.OAK_TRAPDOOR;
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame,
                            Team team, PlayerInteractEvent event) {

    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame,
                             Team team, PlayerInteractEvent event) {
        minigame.setLobbyPosition(player.getLocation());
        player.sendInfoMessage("Set lobby position.");
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
        if (minigame.getLobbyPosition() != null) {
            player.getPlayer().sendBlockChange(minigame.getLobbyPosition(), Material.SKELETON_SKULL, (byte) 1);
            player.sendInfoMessage("Selected lobby position (marked with skull)");
        } else {
            player.sendMessage("No lobby position set!", MinigameMessageType.ERROR);
        }
    }

    @SuppressWarnings("deprecation") //TODO: Use alternate method once available
    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getLobbyPosition() != null) {
            player.getPlayer().sendBlockChange(minigame.getLobbyPosition(),
                    minigame.getLobbyPosition().getBlock().getType(),
                    minigame.getLobbyPosition().getBlock().getData());
            player.sendInfoMessage("Deselected lobby position");
        } else {
            player.sendMessage("No lobby position set!", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
    }
}
