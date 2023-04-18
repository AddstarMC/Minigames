package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.display.IDisplayObject;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.RegenRegionSetResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RegenAreaMode implements ToolMode {
    private final HashMap<UUID, IDisplayObject> displayedRegions = new HashMap<>();

    @Override
    public String getName() {
        return "REGEN_AREA";
    }

    @Override
    public String getDisplayName() {
        return "Regeneration Region Selection";
    }

    @Override
    public String getDescription() {
        return "Selects an area;for a regen region.;Create via left click";
    }

    @Override
    public Material getIcon() {
        return Material.OAK_SAPLING;
    }

    @Override
    public void onSetMode(final MinigamePlayer player, MinigameTool tool) {
        tool.addSetting("Region", "None");
        final Menu menu = new Menu(2, "Regen Region Selection", player);

        if (player.isInMenu()) {
            menu.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), player.getMenu()), menu.getSize() - 9);
        }

        final MinigameTool ftool = tool;

        menu.addItem(new MenuItemString("Region Name", Material.PAPER, new Callback<>() {

            @Override
            public String getValue() {
                return ftool.getSetting("Region");
            }

            @Override
            public void setValue(String value) {
                ftool.changeSetting("Region", value);
            }
        }));

        if (tool.getMinigame() != null) {
            Menu regionMenu = new Menu(6, "Regen Regions", player);
            List<MenuItem> items = new ArrayList<>();

            for (final MgRegion region : tool.getMinigame().getRegenRegions()) {
                MenuItemCustom item = new MenuItemCustom(region.getName(), Material.CHEST);

                // Set the node and go back to the main menu
                item.setClick(object -> {
                    ftool.changeSetting("Region", region.getName());

                    menu.displayMenu(player);

                    return object;
                });

                items.add(item);
            }

            regionMenu.addItems(items);
            regionMenu.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), menu), regionMenu.getSize() - 9);

            menu.addItem(new MenuItemPage("Edit Region", Material.CHEST, regionMenu));
        }
        menu.displayMenu(player);
    }

    @Override
    public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
        tool.removeSetting("Region");
    }

    @Override
    public void onLeftClick(MinigamePlayer player, Minigame minigame,
                            Team team, PlayerInteractEvent event) {
        if (player.hasSelection()) {
            String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
            MgRegion region = minigame.getRegenRegion(name);

            RegenRegionSetResult result = minigame.setRegenRegion(new MgRegion(name, player.getSelectionPoints()[0], player.getSelectionPoints()[1]));

            if (result.success()) {
                if (region == null) {
                    player.sendInfoMessage(Component.text("Created a new regen region in " + minigame + " called " + name + ", " + result.numOfBlocksTotal() + "/" + minigame.getRegenBlocklimit()));
                } else {
                    player.sendInfoMessage(Component.text("Updated region " + name + " in " + minigame));
                }

                player.clearSelection();
            } else {
                player.sendMessage(Component.text("Error: the limit of Blocks of all regen areas together has been reached +(" + result.numOfBlocksTotal() + "/" + minigame.getRegenBlocklimit() + ")." +
                        " Please contact an admin if necessary.", NamedTextColor.RED), MinigameMessageType.ERROR);
            }
        } else {
            player.sendMessage(Component.text("You need to select a region with right click first!"), MinigameMessageType.ERROR);
        }
    }

    @Override
    public void onRightClick(MinigamePlayer player, Minigame minigame,
                             Team team, PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            player.addSelectionPoint(event.getClickedBlock().getLocation());
            if (player.hasSelection()) {
                player.sendInfoMessage(Component.text("Selection complete, finalise with left click."));
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
        String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
        if (minigame.getRegenRegion(name) != null) {
            displayedRegions.put(player.getUUID(),
                    Minigames.getPlugin().display.displayCuboid(player.getPlayer(), minigame.getRegenRegion(name)));
            player.sendInfoMessage(Component.text("Selected the " + name + " region in " + minigame));
        } else {
            player.sendMessage(Component.text("No region created by the name '" + name + "'"), MinigameMessageType.ERROR);
        }
    }

    @Override
    public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
        String name = MinigameUtils.getMinigameTool(player).getSetting("Region");
        if (minigame.getRegenRegion(name) != null) {

            IDisplayObject displayed = displayedRegions.get(player.getUUID());
            if (displayed != null) {
                displayed.remove();
            }

            player.clearSelection();
            player.sendInfoMessage(Component.text("Deselected the region"));
        } else {
            player.sendMessage(Component.text("No region created by the name '" + name + "'"), MinigameMessageType.ERROR);
        }
    }

}
