package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemFlag extends MenuItem {

    private String flag;
    private List<String> flags;

    public MenuItemFlag(Material displayItem, String flag, List<String> flags) {
        super(flag, displayItem);
        this.flag = flag;
        this.flags = flags;
    }

    public MenuItemFlag(List<String> description, Material displayItem, String flag, List<String> flags) {
        super(flag, description, displayItem);
        this.flag = flag;
        this.flags = flags;
    }

    @Override
    public ItemStack onShiftRightClick() {
        getContainer().getViewer().sendMessage("Removed " + flag + " flag.", MinigameMessageType.ERROR);
        flags.remove(flag);

        getContainer().removeItem(getSlot());
        return null;
    }
}
