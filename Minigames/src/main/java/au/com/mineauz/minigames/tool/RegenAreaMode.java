package au.com.mineauz.minigames.tool;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.display.IDisplayObject;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.RegenRegionChangeResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RegenAreaMode implements ToolMode {
    private final static String SETTING_KEY = "Region";
    private final HashMap<UUID, IDisplayObject> displayedRegions = new HashMap<>();

    @Override
    public String getName() {
        return "REGEN_AREA";
    }

    @Override
    public Component getDisplayName() {
        return MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_TOOL_REGENAREA_NAME);
    }

    @Override
    public List<Component> getDescription() {
        return MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_TOOL_REGENAREA_DESCRIPTION);
    }

    @Override
    public Material getIcon() {
        return Material.OAK_SAPLING;
    }

    @Override
    public void onSetMode(final @NotNull MinigamePlayer mgPlayer, final @NotNull MinigameTool tool) {
        tool.setSetting(SETTING_KEY, "None");
        final Menu menu = new Menu(2, MgMenuLangKey.MENU_TOOL_REGENAREA_SELECT_NAME, mgPlayer);

        if (mgPlayer.isInMenu()) {
            menu.addItem(new MenuItemBack(mgPlayer.getMenu()), menu.getSize() - 9);
        }

        menu.addItem(new MenuItemString(Material.PAPER, MgMenuLangKey.MENU_TOOL_REGENAREA_REGIONNAME_NAME, new Callback<>() {

            @Override
            public String getValue() {
                return tool.getSetting(SETTING_KEY);
            }

            @Override
            public void setValue(String value) {
                tool.setSetting(SETTING_KEY, value);
            }
        }));

        if (tool.getMinigame() != null) {
            Menu regionMenu = new Menu(6, MgMenuLangKey.MENU_TOOL_REGENAREA_REGIONS_NAME, mgPlayer);
            List<MenuItem> menuItems = new ArrayList<>();

            for (final MgRegion region : tool.getMinigame().getRegenRegions()) {
                MenuItemCustom customMenuItem = new MenuItemCustom(Material.CHEST, Component.text(region.getName()));

                // Set the region area and go back to the main menu
                customMenuItem.setClick(() -> {
                    tool.setSetting(SETTING_KEY, region.getName());

                    menu.displayMenu(mgPlayer);

                    return null;
                });

                menuItems.add(customMenuItem);
            }

            regionMenu.addItems(menuItems);
            regionMenu.addItem(new MenuItemBack(menu), regionMenu.getSize() - 9);

            menu.addItem(new MenuItemPage(Material.CHEST, MgMenuLangKey.MENU_TOOL_REGENAREA_REGIONEDIT_NAME, regionMenu));
        }
        menu.displayMenu(mgPlayer);
    }

    @Override
    public void onUnsetMode(@NotNull MinigamePlayer mgPlayer, @NotNull MinigameTool tool) {
        tool.removeSetting(SETTING_KEY);
    }

    @Override
    public void onLeftClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame,
                            @Nullable Team team, @NotNull PlayerInteractEvent event) {
        if (mgPlayer.hasSelection()) {
            String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting(SETTING_KEY); //todo expose Settings
            MgRegion region = minigame.getRegenRegion(name);

            RegenRegionChangeResult result = minigame.setRegenRegion(new MgRegion(name, mgPlayer.getSelectionLocations()[0], mgPlayer.getSelectionLocations()[1]));

            if (result.success()) {
                if (region == null) {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.REGION_REGENREGION_CREATED,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                            Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), name),
                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(result.numOfBlocksTotal())),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getRegenBlocklimit())));
                } else {
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.REGION_REGENREGION_UPDATED,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()),
                            Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), name),
                            Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(result.numOfBlocksTotal())),
                            Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getRegenBlocklimit())));
                }

                mgPlayer.clearSelection();
            } else {
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_REGENREGION_ERROR_LIMIT,
                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(result.numOfBlocksTotal())),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(minigame.getRegenBlocklimit())));
            }
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.TOOL_ERROR_NOREGIONSELECTED);
        }
    }

    @Override
    public void onRightClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame,
                             @Nullable Team team, @NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            mgPlayer.addSelectionPoint(event.getClickedBlock().getLocation());
            if (mgPlayer.hasSelection()) {
                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_SELECTED_REGION);
            }
        }
    }

    @Override
    public void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting(SETTING_KEY);
        if (minigame.getRegenRegion(name) != null) {
            displayedRegions.put(mgPlayer.getUUID(),
                    Minigames.getPlugin().display.displayCuboid(mgPlayer.getPlayer(), minigame.getRegenRegion(name)));
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_SELECTED_REGENREGION,
                    Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), name),
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_ERROR_NOREGENREION,
                    Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), name));
        }
    }

    @Override
    public void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting(SETTING_KEY);
        if (minigame.getRegenRegion(name) != null) {

            IDisplayObject displayed = displayedRegions.get(mgPlayer.getUUID());
            if (displayed != null) {
                displayed.remove();
            }

            mgPlayer.clearSelection();

            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.TOOL_DESELECTED_REGION);
        } else {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_ERROR_NOREGENREION,
                    Placeholder.unparsed(MinigamePlaceHolderKey.REGION.getKey(), name));
        }
    }
}
