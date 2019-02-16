package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EndPositionMode implements ToolMode {

    @Override
    public String getName() {
        return "END";
    }

    @Override
    public String getDisplayName() {
        return "End Position";
    }

    @Override
    public String getDescription() {
        return "Sets the end;position";
    }

    @Override
    public Material getIcon() {
        return Material.GOLD_BLOCK;
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame,
                            Team team, PlayerInteractEvent event) {

    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame,
                             Team team, PlayerInteractEvent event) {
        minigame.setEndPosition(player.getLocation());
        player.sendInfoMessage("Set end position.");
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
        if (minigame.getEndPosition() != null) {
            player.getPlayer().sendBlockChange(minigame.getEndPosition(), Material.SKELETON_SKULL, (byte) 1);
            player.sendInfoMessage("Selected end position (marked with skull)");
        } else {
            player.sendMessage("No end position set!", MinigameMessageType.ERROR);
        }
    }

    @SuppressWarnings("deprecation") //TODO: Use alternate method once available
    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getEndPosition() != null) {
            player.getPlayer().sendBlockChange(minigame.getEndPosition(),
                    minigame.getEndPosition().getBlock().getType(),
                    minigame.getEndPosition().getBlock().getData());
            player.sendInfoMessage("Deselected end position");
        } else {
            player.sendMessage("No end position set!", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
