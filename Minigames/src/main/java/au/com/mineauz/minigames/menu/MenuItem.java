package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MenuItem {
    private ItemStack displayItem = null;
    private Menu container = null;
    private int slot = 0;

    public MenuItem(String name, Material displayItem) {
        this(name, null, displayItem);

    }

    public MenuItem(String name, List<String> description, Material displayItem) {
        if (displayItem == null)
            if (description == null) {
                displayItem = MenuUtility.getSlotFillerItem();
            } else {
                displayItem = MenuUtility.getUnknownDisplayItem();
            }
        this.displayItem = new ItemStack(displayItem);
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        if (description != null) meta.setLore(description);
        this.displayItem.setItemMeta(meta);
    }

    @Deprecated(forRemoval = true)
    public List<String> getDescriptionStr() {
        return displayItem.getItemMeta().getLore();
    }


    public List<Component> getDescriptionComp() {
        return displayItem.getItemMeta().lore();
    }

    @Deprecated(forRemoval = true)
    public void setDescriptionStr(List<String> description) {
        ItemMeta meta = displayItem.getItemMeta();

        meta.setLore(description);
        displayItem.setItemMeta(meta);
    }

    public void setDescriptionComp(List<Component> description) {
        ItemMeta meta = displayItem.getItemMeta();

        meta.lore(description);
        displayItem.setItemMeta(meta);
    }

    public String getName() {
        return displayItem.getItemMeta().getDisplayName();
    }

    public ItemStack getItem() {
        return displayItem;
    }

    public void setItem(ItemStack item) {
        if (item == null) {
            Bukkit.getLogger().fine("Item Stack was null on: " + this.getDescriptionStr().toString());
            return;
        }
        ItemMeta ometa = displayItem.getItemMeta();
        displayItem = item.clone();
        ItemMeta nmeta = displayItem.getItemMeta();
        nmeta.setDisplayName(ometa.getDisplayName());
        nmeta.setLore(nmeta.getLore());
        displayItem.setItemMeta(nmeta);
    }

    public void update() {
    }

    public ItemStack onClick() {
        //Do stuff
        return getItem();
    }

    public ItemStack onClickWithItem(ItemStack item) {
        //Do stuff
        return getItem();
    }

    public ItemStack onRightClick() {
        //Do stuff
        return getItem();
    }

    public ItemStack onShiftClick() {
        //Do stuff
        return getItem();
    }

    public ItemStack onShiftRightClick() {
        //Do stuff
        return getItem();
    }

    public ItemStack onDoubleClick() {
        //Do Stuff
        return getItem();
    }

    public void checkValidEntry(String entry) {
        //Do Stuff
    }

    public Menu getContainer() {
        return container;
    }

    public void setContainer(Menu container) {
        this.container = container;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
