package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItemList extends MenuItem {
    private final Callback<String> value;
    private final List<String> options;

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
        List<String> description;
        int pos = options.indexOf(value.getValue());
        int before = pos - 1;
        int after = pos + 1;
        if (before < 0)
            before = options.size() - 1;
        if (after == options.size())
            after = 0;

        if (getDescriptionStr() != null) {
            description = getDescriptionStr();
            if (getDescriptionStr().size() >= 3) {
                String desc = ChatColor.stripColor(getDescriptionStr().get(1));

                if (options.contains(desc)) {
                    description.set(0, ChatColor.GRAY + options.get(before));
                    description.set(1, ChatColor.GREEN + value.getValue());
                    description.set(2, ChatColor.GRAY + options.get(after));
                } else {
                    description.add(0, ChatColor.GRAY + options.get(before));
                    description.add(1, ChatColor.GREEN + value.getValue());
                    description.add(2, ChatColor.GRAY + options.get(after));
                }
            } else {
                description.add(0, ChatColor.GRAY + options.get(before));
                description.add(1, ChatColor.GREEN + value.getValue());
                description.add(2, ChatColor.GRAY + options.get(after));
            }
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GRAY + options.get(before));
            description.add(ChatColor.GREEN + value.getValue());
            description.add(ChatColor.GRAY + options.get(after));
        }

        setDescriptionStr(description);
    }

    @Override
    public ItemStack onClick() {
        int ind = options.lastIndexOf(value.getValue());
        ind++;
        if (ind == options.size())
            ind = 0;

        value.setValue(options.get(ind));
        updateDescription();

        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        int ind = options.lastIndexOf(value.getValue());
        ind--;
        if (ind == -1)
            ind = options.size() - 1;

        value.setValue(options.get(ind));
        updateDescription();

        return getDisplayItem();
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
