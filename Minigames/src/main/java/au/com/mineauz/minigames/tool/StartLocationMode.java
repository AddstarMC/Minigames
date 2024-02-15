package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StartLocationMode implements ToolMode { //todo waring if other world

    @Override
    public String getName() {
        return "START";
    }

    @Override
    public Component getDisplayName() {
        return MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_TOOL_LOCATION_START_NAME);
    }

    @Override
    public List<Component> getDescription() {
        return MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_TOOL_LOCATION_START_DESCRIPTION);
    }

    @Override
    public Material getIcon() {
        return Material.SKELETON_SKULL;
    }

    @Override
    public void onLeftClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null) {
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

                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_REMOVE_STARTLOCTION,
                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName() + " ", team.getTextColor())));
                } else {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.TOOL_ERROR_NOSTARTLOCATION,
                            Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName() + " ", team.getTextColor())));
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
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_REMOVE_STARTLOCTION,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), ""));
                } else {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.TOOL_ERROR_NOSTARTLOCATION,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), ""));
                }
            }
        }
    }

    @Override
    public void onRightClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event) {
        if (team == null) {
            minigame.addStartLocation(mgPlayer.getLocation());

            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_ADDED_STARTLOCATION,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), ""),
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
        } else {
            team.addStartLocation(mgPlayer.getLocation());

            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_ADDED_STARTLOCATION,
                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName() + " ", team.getTextColor())),
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
        }
    }

    @Override
    public void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        if (team != null) {
            for (Location loc : team.getStartLocations()) {
                mgPlayer.getPlayer().sendBlockChange(loc, Material.SKELETON_SKULL.createBlockData());
            }

            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_SELECTED_STARTLOCATION,
                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName() + " ", team.getTextColor())),
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
        } else {
            for (Location loc : minigame.getStartLocations()) {
                mgPlayer.getPlayer().sendBlockChange(loc, Material.SKELETON_SKULL.createBlockData());
            }
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_SELECTED_STARTLOCATION,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), ""),
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
        }
    }

    @Override
    public void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        if (team != null) {
            for (Location loc : team.getStartLocations()) {
                mgPlayer.getPlayer().sendBlockChange(loc, loc.getBlock().getBlockData());
            }

            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_DESELECTED_STARTLOCATION,
                    Placeholder.component(MinigamePlaceHolderKey.TEAM.getKey(), Component.text(team.getDisplayName() + " ", team.getTextColor())),
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
        } else {
            for (Location loc : minigame.getStartLocations()) {
                mgPlayer.getPlayer().sendBlockChange(loc, loc.getBlock().getBlockData());
            }

            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_DESELECTED_STARTLOCATION,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEAM.getKey(), ""),
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
        }
    }

    @Override
    public void onSetMode(@Nullable MinigamePlayer player, @Nullable MinigameTool tool) {
    }

    @Override
    public void onUnsetMode(@NotNull MinigamePlayer mgPlayer, @Nullable MinigameTool tool) {
    }

}
