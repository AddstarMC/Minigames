package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionPlaceHolderKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NodeToolMode implements ToolMode {

    @Override
    public String getName() {
        return "NODE";
    }

    @Override
    public Component getDisplayName() {
        return "Node Creation";
    }

    @Override
    public List<Component> getDescription() { //todo translation String
        return List.of(
                "Creates a node where",
                "you are standing",
                "on right click");
    }

    @Override
    public Material getIcon() {
        return Material.STONE_BUTTON;
    }

    @Override
    public void onSetMode(final MinigamePlayer player, MinigameTool tool) {
        tool.addSetting("Node", "None");
        final Menu m = new Menu(2, "Node Selection", player);
        if (player.isInMenu()) {
            m.addItem(new MenuItemBack(player.getMenu()), m.getSize() - 9);
        }
        final MinigameTool ftool = tool;
        m.addItem(new MenuItemString("Node Name", Material.PAPER, new Callback<String>() {

            @Override
            public String getValue() {
                return ftool.getSetting("Node");
            }

            @Override
            public void setValue(String value) {
                ftool.changeSetting("Node", value);
            }
        }));

        if (tool.getMinigame() != null) {
            // Node selection menu
            RegionModule module = RegionModule.getMinigameModule(tool.getMinigame());

            Menu nodeMenu = new Menu(6, "Nodes", player);
            List<MenuItem> items = new ArrayList<>();

            for (final Node node : module.getNodes()) {
                MenuItemCustom item = new MenuItemCustom(Material.STONE_BUTTON, node.getName());

                // Set the node and go back to the main menu
                item.setClick(object -> {
                    ftool.changeSetting("Node", node.getName());
                    m.displayMenu(player);

                    return object;
                });

                items.add(item);
            }

            nodeMenu.addItems(items);
            nodeMenu.addItem(new MenuItemBack(m), nodeMenu.getSize() - 9);

            m.addItem(new MenuItemPage("Edit Node", Material.STONE_BUTTON, nodeMenu));
        }
        m.displayMenu(player);
    }

    @Override
    public void onUnsetMode(@NotNull MinigamePlayer mgPlayer, MinigameTool tool) {
        tool.removeSetting("Node");
    }

    @Override
    public void onLeftClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            RegionModule mod = RegionModule.getMinigameModule(minigame);
            String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting("Node");

            Location loc = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
            Node node = mod.getNode(name);
            if (node == null) {
                node = new Node(name, loc);
                mod.addNode(name, node);
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                        RegionLangKey.NODE_ADDED,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                        Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
            } else {
                node.setLocation(loc);
                MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                        RegionLangKey.NODE_EDITED,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                        Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
                Main.getPlugin().getDisplayManager().update(node);
            }
        }
    }

    @Override
    public void onRightClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting("Node");

        Node node = mod.getNode(name);
        if (node == null) {
            node = new Node(name, mgPlayer.getLocation());
            mod.addNode(name, node);
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                    RegionLangKey.NODE_ADDED,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
        } else {
            node.setLocation(mgPlayer.getLocation());
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                    RegionLangKey.NODE_EDITED,
                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
            Main.getPlugin().getDisplayManager().update(node);
        }
    }

    @Override
    public void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting("Node");
        if (mod.hasNode(name)) {
            Main.getPlugin().getDisplayManager().show(mod.getNode(name), mgPlayer);
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                    RegionLangKey.TOOL_NODE_SELECTED,
                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
        } else {
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                    RegionLangKey.NODE_ERROR_NONODE,
                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
        }
    }

    @Override
    public void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        RegionModule mod = RegionModule.getMinigameModule(minigame);
        String name = MinigameUtils.getMinigameTool(mgPlayer).getSetting("Node");
        if (mod.hasNode(name)) {
            Main.getPlugin().getDisplayManager().hide(mod.getNode(name), mgPlayer);
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                    RegionLangKey.TOOL_NODE_DESELECTED,
                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
        } else {
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                    RegionLangKey.NODE_ERROR_NONODE,
                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), name));
        }
    }

}
