package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
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
            if (minigame.getFloorDegen() != null) {
                minigame.getFloorDegen().setFirstPos(player.getSelectionPoints()[0]);
                minigame.getFloorDegen().setSecondPos(player.getSelectionPoints()[1]);
            } else {
                //please note: the name is not important
                minigame.setFloorDegen(new MgRegion("degen", player.getSelectionPoints()[0], player.getSelectionPoints()[1]));
            }
            player.sendInfoMessage(Component.text("Created a degeneration area for " + minigame));
        } else if (player.getSelectionPoints()[1] == null) {
            player.sendMessage(Component.text("You must make a selection with right click first!"), MinigameMessageType.ERROR);
        } else {
            minigame.removeFloorDegen();
            player.sendInfoMessage(Component.text("Cleared degeneration area from " + minigame));
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
        if (minigame.getFloorDegen() != null) {
            player.setSelection(minigame.getFloorDegen());
            player.showSelection(false);
            player.sendInfoMessage("Selected degeneration area in " + minigame);
        } else {
            player.sendMessage("No degeneration area selected for " + minigame, MinigameMessageType.ERROR);
        }
    }

    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        if (minigame.getFloorDegen() != null) {
            player.setSelection(minigame.getFloorDegen());
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
