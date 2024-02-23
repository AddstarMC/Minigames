package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class StartPositionMode implements ToolMode {

    @Override
    public String getName() {
        return "START";
    }

    @Override
    public String getDisplayName() {
        return "Start Positions";
    }

    @Override
    public List<String> getDescription() {
        return List.of("Sets the starting", "positions for a team", "or player");
    }

    @Override
    public Material getIcon() {
        return Material.SKELETON_SKULL;
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            int x = event.getClickedBlock().getLocation().getBlockX();
            int y = event.getClickedBlock().getLocation().getBlockY();
            int z = event.getClickedBlock().getLocation().getBlockZ();
            String world = event.getClickedBlock().getLocation().getWorld().getName();

            int nx;
            int ny;
            int nz;
            String nworld;
            Location delLoc = null;
            if (team != null) {
                if (team.hasStartLocations()) {
                    for (Location loc : team.getStartLocations()) {
                        nx = loc.getBlockX();
                        ny = loc.getBlockY();
                        nz = loc.getBlockZ();
                        nworld = loc.getWorld().getName();

                        if (x == nx && y == ny && z == nz && world.equals(nworld)) {
                            delLoc = loc;
                            break;
                        }
                    }
                }
                if (delLoc != null) {
                    team.getStartLocations().remove(delLoc);
                    player.sendInfoMessage("Removed selected " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE +
                            " start location.");
                } else {
                    player.sendMessage("Could not find a " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE +
                            " start location at that point.", MinigameMessageType.ERROR);
                }
            } else {
                for (Location loc : minigame.getStartLocations()) {
                    nx = loc.getBlockX();
                    ny = loc.getBlockY();
                    nz = loc.getBlockZ();
                    nworld = loc.getWorld().getName();

                    if (x == nx && y == ny && z == nz && world.equals(nworld)) {
                        delLoc = loc;
                        break;
                    }
                }
                if (delLoc != null) {
                    minigame.getStartLocations().remove(delLoc);
                    player.sendInfoMessage("Removed selected start location.");
                } else
                    player.sendMessage("Could not find a start location at that point.", MinigameMessageType.ERROR);
            }
        }
    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame, Team team, PlayerInteractEvent event) {
        if (team == null) {
            minigame.addStartLocation(player.getLocation());
            player.sendInfoMessage("Added start location for " + minigame);
        } else {
            team.addStartLocation(player.getLocation());
            player.sendInfoMessage("Added " + team.getChatColor() +
                    team.getDisplayName() + ChatColor.WHITE + " start location to " + minigame);
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
        if (team != null) {
            for (Location loc : team.getStartLocations()) {
                player.getPlayer().sendBlockChange(loc, Material.SKELETON_SKULL.createBlockData());
            }
            player.sendInfoMessage("Selected " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE +
                    " start points in " + minigame);
        } else {
            for (Location loc : minigame.getStartLocations()) {
                player.getPlayer().sendBlockChange(loc, Material.SKELETON_SKULL.createBlockData());
            }
            player.sendInfoMessage("Selected start points in " + minigame);
        }
    }


    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        if (team != null) {
            for (Location loc : team.getStartLocations()) {
                player.getPlayer().sendBlockChange(loc, loc.getBlock().getBlockData());
            }
            player.sendInfoMessage("Deselected " + team.getChatColor() + team.getDisplayName() + ChatColor.WHITE +
                    " start points in " + minigame);
        } else {
            for (Location loc : minigame.getStartLocations()) {
                player.getPlayer().sendBlockChange(loc, loc.getBlock().getBlockData());
            }
            player.sendInfoMessage("Deselected start points in " + minigame);
        }
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
    }

}
