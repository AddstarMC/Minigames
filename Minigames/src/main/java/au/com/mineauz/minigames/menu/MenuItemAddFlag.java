package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemAddFlag extends MenuItem {

    private final Minigame mgm;

    public MenuItemAddFlag(Component name, Material displayItem, Minigame mgm) {
        super(name, displayItem);
        this.mgm = mgm;
    }

    public MenuItemAddFlag(Component name, List<Component> description, Material displayItem, Minigame mgm) {
        super(name, description, displayItem);
        this.mgm = mgm;
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendMessage("Enter a flag name into chat for " + getName() + ", the menu will automatically reopen in 20s if nothing is entered.", MinigameMessageType.INFO);
        ply.setManualEntry(this);
        getContainer().startReopenTimer(20);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        mgm.addFlag(entry);
        getContainer().addItem(new MenuItemFlag(Material.OAK_SIGN, entry, mgm.getFlags()));

        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }
}
