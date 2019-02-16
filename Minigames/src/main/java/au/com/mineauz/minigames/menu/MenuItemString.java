package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItemString extends MenuItem {

    protected Callback<String> str;
    private boolean allowNull = false;

    public MenuItemString(String name, Material displayItem, Callback<String> str) {
        super(name, displayItem);
        this.str = str;
        updateDescription();
    }

    public MenuItemString(String name, List<String> description, Material displayItem, Callback<String> str) {
        super(name, description, displayItem);
        this.str = str;
        updateDescription();
    }

    public void setAllowNull(boolean allow) {
        allowNull = allow;
    }

    public void updateDescription() {
        List<String> description = null;
        String setting = str.getValue();
        if (setting == null)
            setting = "Not Set";
        if (setting.length() > 20) {
            setting = setting.substring(0, 17) + "...";
        }

        if (getDescription() != null) {
            description = getDescription();
            String desc = getDescription().get(0);

            if (desc.startsWith(ChatColor.GREEN.toString()))
                description.set(0, ChatColor.GREEN.toString() + setting);
            else
                description.add(0, ChatColor.GREEN.toString() + setting);
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GREEN.toString() + setting);
        }

        setDescription(description);
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendMessage("Enter string value into chat for " + getName() + ", the menu will automatically reopen in 20s if nothing is entered.", MinigameMessageType.INFO);
        if (allowNull) {
            ply.sendInfoMessage("Enter \"null\" to remove the string value");
        }
        ply.setManualEntry(this);
        getContainer().startReopenTimer(20);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.equals("null") && allowNull)
            str.setValue(null);
        else
            str.setValue(entry);

        updateDescription();
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }

    Callback<String> getString() {
        return str;
    }
}
