package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuItemLoadoutAdd extends MenuItem {

    private final Map<String, PlayerLoadout> loadouts;
    private Minigame minigame = null;

    public MenuItemLoadoutAdd(Component name, Material displayItem, Map<String, PlayerLoadout> loadouts, Minigame mgm) {
        super(name, displayItem);
        this.loadouts = loadouts;
        this.minigame = mgm;
    }

    public MenuItemLoadoutAdd(Component name, List<Component> description, Material displayItem, Map<String, PlayerLoadout> loadouts, Minigame mgm) {
        super(name, description, displayItem);
        this.loadouts = loadouts;
        this.minigame = mgm;
    }

    public MenuItemLoadoutAdd(Component name, Material displayItem, Map<String, PlayerLoadout> loadouts) {
        super(name, displayItem);
        this.loadouts = loadouts;
    }

    public MenuItemLoadoutAdd(Component name, List<Component> description, Material displayItem, Map<String, PlayerLoadout> loadouts) {
        super(name, description, displayItem);
        this.loadouts = loadouts;
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendInfoMessage("Enter a name for the new Loadout, the menu will automatically reopen in 10s if nothing is entered.");
        ply.setManualEntry(this);

        getContainer().startReopenTimer(30);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        entry = entry.replace(" ", "_");
        if (!loadouts.containsKey(entry)) {
            for (int i = 0; i < 45; i++) {
                if (!getContainer().hasMenuItem(i)) {
                    PlayerLoadout loadout = new PlayerLoadout(entry);
                    loadouts.put(entry, loadout);
                    List<Component> des = new ArrayList<>();
                    des.add("Shift + Right Click to Delete");
                    if (minigame != null) {
                        getContainer().addItem(new MenuItemDisplayLoadout(entry, des, Material.DIAMOND_SWORD, loadout, minigame), i);
                    } else {
                        getContainer().addItem(new MenuItemDisplayLoadout(entry, des, Material.DIAMOND_SWORD, loadout), i);
                    }
                    break;
                }
            }

            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
            return;
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        getContainer().getViewer().sendMessage("A Loadout already exists by the name \"" + entry + "\".", MinigameMessageType.ERROR);
    }
}
