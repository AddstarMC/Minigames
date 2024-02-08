package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemSaveMinigame;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.tool.MinigameTool;
import au.com.mineauz.minigames.tool.ToolMode;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import au.com.mineauz.minigamesregions.language.RegionPlaceHolderKey;
import au.com.mineauz.minigamesregions.menuitems.MenuItemNode;
import au.com.mineauz.minigamesregions.menuitems.MenuItemRegion;
import com.google.common.collect.Iterables;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RegionNodeEditToolMode implements ToolMode {

    @Override
    public String getName() {
        return "REGION_AND_NODE_EDITOR";
    }

    @Override
    public Component getDisplayName() {
        return "Region and Node editor";
    }

    @Override
    public List<Component> getDescription() { //todo translation String
        return List.of(
                "Allows you to simply",
                "edit regions and nodes",
                "with right click");
    }

    @Override
    public Material getIcon() {
        return Material.WRITABLE_BOOK;
    }

    @Override
    public void onSetMode(MinigamePlayer player, MinigameTool tool) {
        if (tool.getMinigame() != null) {
            Main.getPlugin().getDisplayManager().hideAll(player.getPlayer());
            Main.getPlugin().getDisplayManager().showAll(tool.getMinigame(), player);
        }
    }

    @Override
    public void onUnsetMode(@NotNull MinigamePlayer mgPlayer, MinigameTool tool) {
        if (tool.getMinigame() != null) {
            Main.getPlugin().getDisplayManager().hideAll(mgPlayer.getPlayer());
        }
    }

    @Override
    public void onLeftClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event) {
    }

    @Override
    public void onRightClick(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team, @NotNull PlayerInteractEvent event) {
        Vector origin = event.getPlayer().getEyeLocation().toVector();
        Vector direction = event.getPlayer().getEyeLocation().getDirection().normalize();

        // Prepare region and node bounds for efficiency
        RegionModule module = RegionModule.getMinigameModule(minigame);
        Map<Region, Vector[]> regionBBs = new IdentityHashMap<>();
        for (Region region : module.getRegions()) {
            Vector point1 = region.getFirstPoint().toVector();
            Vector point2 = region.getSecondPoint().toVector();

            regionBBs.put(region, new Vector[]{Vector.getMinimum(point1, point2), Vector.getMaximum(point1, point2).add(new Vector(1, 1, 1))});
        }

        Map<Node, Vector> nodeLocs = new IdentityHashMap<>();
        for (Node node : module.getNodes()) {
            nodeLocs.put(node, node.getLocation().toVector());
        }

        Set<ExecutableScriptObject> hits = Collections.newSetFromMap(new IdentityHashMap<>());

        // Raytrace the view vector
        for (double dist = 0; dist < 10; dist += 0.25) {
            Vector pos = origin.clone().add(direction.clone().multiply(dist));

            for (Entry<Region, Vector[]> regionEntry : regionBBs.entrySet()) {
                if (pos.isInAABB(regionEntry.getValue()[0], regionEntry.getValue()[1])) {
                    hits.add(regionEntry.getKey());
                }
            }

            for (Entry<Node, Vector> nodeEntry : nodeLocs.entrySet()) {
                if (pos.isInSphere(nodeEntry.getValue(), 0.4)) {
                    hits.add(nodeEntry.getKey());
                }
            }
        }

        // Tracing done, now show the results
        if (hits.size() == 1) {
            openMenu(mgPlayer, minigame, Iterables.getFirst(hits, null));
        } else if (!hits.isEmpty()) {
            openChooseMenu(mgPlayer, module, hits);
        }
    }

    private void openMenu(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @NotNull ExecutableScriptObject hit) {
        Menu menu = null;
        if (hit instanceof Region region) {
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                    RegionLangKey.TOOL_REGION_EDIT,
                    Placeholder.unparsed(RegionPlaceHolderKey.REGION.getKey(), region.getName()));
            menu = MenuItemRegion.createMenu(mgPlayer, null, region);
        } else if (hit instanceof Node node) {
            MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                    RegionLangKey.TOOL_NODE_EDIT,
                    Placeholder.unparsed(RegionPlaceHolderKey.NODE.getKey(), node.getName()));
            menu = MenuItemNode.createMenu(mgPlayer, null, node);
        }

        menu.addItem(new MenuItemSaveMinigame(MenuUtility.getSaveMaterial(), "Save", minigame), menu.getSize() - 9);

        menu.displayMenu(mgPlayer);
    }

    private void openChooseMenu(@NotNull MinigamePlayer mgPlayer, @NotNull RegionModule module, @NotNull Set<@NotNull ExecutableScriptObject> objects) {
        Menu menu = new Menu(3, "Choose Region or Node", mgPlayer);

        StringBuilder options = new StringBuilder();
        for (ExecutableScriptObject object : objects) {
            if (!options.isEmpty()) {
                options.append(", ");
            }

            if (object instanceof Region) {
                options.append(((Region) object).getName());
                MenuItemRegion item = new MenuItemRegion(Material.CHEST, ((Region) object).getName(), (Region) object, module);
                menu.addItem(item);
            } else if (object instanceof Node) {
                options.append(((Node) object).getName());
                MenuItemNode item = new MenuItemNode(Material.STONE_BUTTON, ((Node) object).getName(), (Node) object, module);
                menu.addItem(item);
            }
        }

        menu.addItem(new MenuItemSaveMinigame(MenuUtility.getSaveMaterial(), "Save", module.getMinigame()), menu.getSize() - 9);

        menu.displayMenu(mgPlayer);

        MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.INFO, RegionMessageManager.getBundleKey(),
                RegionLangKey.TOOL_NODEREGION_SELECTED,
                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), options.toString()));
    }

    @Override
    public void select(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        Main.getPlugin().getDisplayManager().showAll(minigame, mgPlayer);
    }

    @Override
    public void deselect(@NotNull MinigamePlayer mgPlayer, @NotNull Minigame minigame, @Nullable Team team) {
        Main.getPlugin().getDisplayManager().hideAll(mgPlayer.getPlayer());
    }
}
