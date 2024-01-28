package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemAddWhitelistBlock extends MenuItem {
    private final List<Material> whitelist;

    public MenuItemAddWhitelistBlock(Component name, List<Material> whitelist) {
        super(name, MenuUtility.getCreateMaterial());
        setDescription(List.of("Left Click with item to", "add to whitelist/blacklist", "Click without item to", "manually add item."));
        this.whitelist = whitelist;
    }

    @Override
    public ItemStack onClickWithItem(ItemStack item) {
        if (!whitelist.contains(item.getType())) {
            whitelist.add(item.getType());
            getContainer().addItem(new MenuItemWhitelistBlock(item.getType(), whitelist));
        } else {
            getContainer().getViewer().sendMessage("Whitelist/Blacklist already contains this material", MinigameMessageType.ERROR);
        }
        return getDisplayItem();
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendMessage("Enter material name into chat to add to the whitelist/blacklist, the menu will automatically reopen in 30s if nothing is entered.", MinigameMessageType.INFO);
        ply.setManualEntry(this);

        getContainer().startReopenTimer(30);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        // try a direct match in case of a chat input
        Material mat = Material.matchMaterial(entry);

        // didn't work, now try the input as a block data, as we get when a block was clicked
        if (mat == null) {
            try {
                mat = Bukkit.createBlockData(entry).getMaterial();

            } catch (IllegalArgumentException ignored) {
            }
        }

        if (mat == null) {
            // still didn't work.
            getContainer().getViewer().sendMessage("No material with BlockData \"" + entry + "\" was found!", MinigameMessageType.ERROR);
        } else {
            // nice we got a Material! try to add it!
            if (!whitelist.contains(mat)) {
                // intern
                whitelist.add(mat);

                // visual
                getContainer().addItem(new MenuItemWhitelistBlock(mat, whitelist));

            } else {
                getContainer().getViewer().sendMessage("Whitelist/Blacklist already contains this material", MinigameMessageType.ERROR);
            }
        }

        /* cancel automatic reopening and reopen {@link MenuItemDisplayWhitelist}*/
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }
}
