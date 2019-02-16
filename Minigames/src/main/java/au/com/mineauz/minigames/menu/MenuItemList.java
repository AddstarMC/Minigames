package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItemList extends MenuItem {

    private Callback<String> value = null;
    private List<String> options = null;

    public MenuItemList(String name, Material displayItem, Callback<String> value, List<String> options) {
        super(name, displayItem);
        this.value = value;
        this.options = options;
        updateDescription();
    }

    public MenuItemList(String name, List<String> description, Material displayItem, Callback<String> value, List<String> options) {
        super(name, description, displayItem);
        this.value = value;
        this.options = options;
        updateDescription();
    }

    public void updateDescription() {
        List<String> description = null;
        int pos = options.indexOf(value.getValue());
        int before = pos - 1;
        int after = pos + 1;
        if (before < 0)
            before = options.size() - 1;
        if (after == options.size())
            after = 0;

        if (getDescription() != null) {
            description = getDescription();
            if (getDescription().size() >= 3) {
                String desc = ChatColor.stripColor(getDescription().get(1));

                if (options.contains(desc)) {
                    description.set(0, ChatColor.GRAY.toString() + options.get(before));
                    description.set(1, ChatColor.GREEN.toString() + value.getValue());
                    description.set(2, ChatColor.GRAY.toString() + options.get(after));
                } else {
                    description.add(0, ChatColor.GRAY.toString() + options.get(before));
                    description.add(1, ChatColor.GREEN.toString() + value.getValue());
                    description.add(2, ChatColor.GRAY.toString() + options.get(after));
                }
            } else {
                description.add(0, ChatColor.GRAY.toString() + options.get(before));
                description.add(1, ChatColor.GREEN.toString() + value.getValue());
                description.add(2, ChatColor.GRAY.toString() + options.get(after));
            }
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GRAY.toString() + options.get(before));
            description.add(ChatColor.GREEN.toString() + value.getValue());
            description.add(ChatColor.GRAY.toString() + options.get(after));
        }

        setDescription(description);
    }

    @Override
    public ItemStack onClick() {
        int ind = options.lastIndexOf(value.getValue());
        ind++;
        if (ind == options.size())
            ind = 0;

        value.setValue(options.get(ind));
        updateDescription();

        return getItem();
    }

    @Override
    public ItemStack onRightClick() {
        int ind = options.lastIndexOf(value.getValue());
        ind--;
        if (ind == -1)
            ind = options.size() - 1;

        value.setValue(options.get(ind));
        updateDescription();

        return getItem();
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer ply = getContainer().getViewer();

        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendInfoMessage("Enter the name of the option into chat for " + getName() + ", the menu will automatically reopen in 10s if nothing is entered.");
        ply.setManualEntry(this);
        if (MinigameUtils.listToString(options).getBytes().length > 16000) {
            ply.sendInfoMessage("Unfortunately there are too many options to provide a list in game. Perhaps use the WIKI");
        } else {
            ply.sendInfoMessage("Possible Options: " + MinigameUtils.listToString(options));
        }
        getContainer().startReopenTimer(10);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        for (String opt : options) {
            if (opt.equalsIgnoreCase(entry)) {
                value.setValue(opt);
                updateDescription();

                getContainer().cancelReopenTimer();
                getContainer().displayMenu(getContainer().getViewer());
                return;
            }
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        getContainer().getViewer().sendMessage("Could not find matching value!", MinigameMessageType.ERROR);
    }
}
