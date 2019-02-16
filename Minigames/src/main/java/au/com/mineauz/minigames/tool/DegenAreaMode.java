package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DegenAreaMode implements ToolMode {

    @Override
    public String getName() {
        return "DEGEN_AREA";
    }

    @Override
    public String getDisplayName() {
        return "Degeneration Area";
    }

    @Override
    public String getDescription() {
        return "Selects the degeneration;area with right click;finalise with left";
    }

    @Override
    public Material getIcon() {
        return Material.LAVA_BUCKET;
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame,
                            Team team, PlayerInteractEvent event) {
        if (player.hasSelection()) {
            minigame.setFloorDegen1(player.getSelectionPoints()[0]);
            minigame.setFloorDegen2(player.getSelectionPoints()[1]);
            player.sendInfoMessage("Created a degeneration area for " + minigame);
        } else if (player.getSelectionPoints()[1] == null) {
            player.sendMessage("You must make a selection with right click first!", MinigameMessageType.ERROR);
        } else {
            minigame.setFloorDegen1(null);
            minigame.setFloorDegen2(null);
            player.sendInfoMessage("Cleared degeneration area from " + minigame);
        }
    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame,
                             Team team, PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            player.addSelectionPoint(event.getClickedBlock().getLocation());
            if (player.getSelectionPoints()[1] != null) {
                player.sendInfoMessage("Left click to finalise selection");
            }
        }
    }

    @Override
    public void onEntityLeftClick(MinigamePlayer player, Minigame minigame, Team team, EntityDamageByEntityEvent event) {

    }

    @Override
    public void onEntityRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEntityEvent event) {

    }

    @Override
    public void select(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getFloorDegen1() != null && minigame.getFloorDegen2() != null) {
            player.setSelection(minigame.getFloorDegen1(), minigame.getFloorDegen2());
            player.showSelection(false);
            player.sendInfoMessage("Selected degeneration area in " + minigame);
        } else {
            player.sendMessage("No degeneration area selected for " + minigame, MinigameMessageType.ERROR);
        }
    }

    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getFloorDegen1() != null && minigame.getFloorDegen2() != null) {
            player.setSelection(minigame.getFloorDegen1(), minigame.getFloorDegen2());
            player.showSelection(true);
            player.sendInfoMessage("Selected degeneration area in " + minigame);
        } else {
            player.sendMessage("No degeneration area selected for " + minigame, MinigameMessageType.ERROR);
        }
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
    }

}
