package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 16/06/2018.
 */
public class MenuUtility {

    public static @NotNull Material getBackMaterial() {
        return Material.REDSTONE_TORCH;
    }

    public static @NotNull Material getSaveMaterial() {
        return Material.REDSTONE_TORCH;
    }

    public static @NotNull Material getCreateMaterial() {
        return Material.ITEM_FRAME;
    }

    public static @NotNull Material getSlotFillerItem() {
        return Material.RED_STAINED_GLASS_PANE;
    }

    public static @NotNull Material getUnknownDisplayItem() {
        return Material.WHITE_STAINED_GLASS_PANE;
    }
}
