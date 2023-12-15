package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegionToolMode implements ToolMode {

    @Override
    public String getName() {
        return "REGION";
    }

    @Override
    public Component getDisplayName() {
        return "Region Selection";
    }

    @Override
    public List<Component> getDescription() { //todo translation String
        return List.of("Selects an area", "for a region.", "Create via left click");
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_BLOCK;
    }

    @Override
    public void onSetMode(final MinigamePlayer player, MinigameTool tool) {
        tool.addSetting("Region", "None");
        final Menu m = new Menu(2, "Region Selection", player);
        if (player.isInMenu()) {
            m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), player.getMenu()), m.getSize() - 9);
        }
        final MinigameTool ftool = tool;
        m.addItem(new MenuItemString("Region Name", Material.PAPER, new Callback<>() {

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
            // Node selection menu
            RegionModule module = RegionModule.getMinigameModule(tool.getMinigame());

            Menu regionMenu = new Menu(6, "Regions", player);
            List<MenuItem> items = new ArrayList<>();

            for (final Region region : module.getRegions()) {
                MenuItemCustom item = new MenuItemCustom(region.getName(), Material.CHEST);

                // Set the node and go back to the main menu
                item.setClick(object -> {
                    ftool.changeSetting("Region", region.getName());

                    m.displayMenu(player);

                    return object;
                });

                items.add(item);
            }

            regionMenu.addItems(items);
            regionMenu.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), m), regionMenu.getSize() - 9);

            m.addItem(new MenuItemPage("Edit Region", Material.CHEST, regionMenu));
        }
        m.displayMenu(player);
    }

    @Override
    public void onUnsetMode(@NotNull MinigamePlayer mgPlayer, MinigameTool tool) {
        tool.removeSetting("Region");
    }

    @Override
    public void onLeftClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame,
                            @Nullable Team team, @NotNull PlayerInteractEvent event) {
        if (mgPlayer.hasSelection()) {
            String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting("Region");
            RegionModule module = RegionModule.getMinigameModule(minigame);
            Region region = module.getRegion(name);

            if (region == null) {
                module.addRegion(name, new Region(name, mgPlayer.getSelectionPoints()[0], mgPlayer.getSelectionPoints()[1]));
                mgPlayer.sendInfoMessage("Created a new region in " + minigame + " called " + name);
                mgPlayer.clearSelection();
            } else {
                region.updateRegion(mgPlayer.getSelectionPoints()[0], mgPlayer.getSelectionPoints()[1]);
                Main.getPlugin().getDisplayManager().update(region);
                mgPlayer.sendInfoMessage("Updated region " + name + " in " + minigame);
            }
        } else {
            mgPlayer.sendMessage("You need to select a region with right click first!", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void onRightClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame,
                             @Nullable Team team, @NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            mgPlayer.addSelectionPoint(event.getClickedBlock().getLocation());
            if (mgPlayer.hasSelection()) {
                mgPlayer.sendInfoMessage("Selection complete, finalise with left click.");
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
    public void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting("Region");
        if (mod.hasRegion(name)) {
            Main.getPlugin().getDisplayManager().show(mod.getRegion(name), mgPlayer);
            mgPlayer.sendInfoMessage("Selected the " + name + " region in " + minigame);
        } else {
            mgPlayer.sendMessage("No region created by the name '" + name + "'", MinigameMessageType.ERROR);
        }
    }

    @Override
    public void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting("Region");
        if (mod.hasRegion(name)) {
            Main.getPlugin().getDisplayManager().hide(mod.getRegion(name), mgPlayer);
            mgPlayer.clearSelection();
            mgPlayer.sendInfoMessage("Deselected the region");
        } else {
            mgPlayer.sendMessage("No region created by the name '" + name + "'", MinigameMessageType.ERROR);
        }
    }

}
