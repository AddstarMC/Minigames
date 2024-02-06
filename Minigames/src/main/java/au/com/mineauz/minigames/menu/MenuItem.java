package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItem {
    private ItemStack displayItem;
    private Menu container = null;
    private int slot = 0;

    public MenuItem(@Nullable Component name, @Nullable Material displayItem) {
        this(name, null, displayItem);
    }

    public MenuItem(@NotNull LangKey langKey, @Nullable Material displayItem) {
        this(MinigameMessageManager.getMgMessage(langKey), null, displayItem);
    }

    public MenuItem(@Nullable Component name, @Nullable List<Component> description, @Nullable Material displayItem) {
        if (displayItem == null)
            if (description == null) {
                displayItem = MenuUtility.getSlotFillerItem();
            } else {
                displayItem = MenuUtility.getUnknownDisplayItem();
            }
        this.displayItem = new ItemStack(displayItem);
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.displayName(name);
        if (description != null) meta.lore(description);
        this.displayItem.setItemMeta(meta);
    }

    public List<Component> getDescription() {
        return displayItem.getItemMeta().lore();
    }

    public void setDescription(List<Component> description) {
        ItemMeta meta = displayItem.getItemMeta();

        meta.lore(description);
        displayItem.setItemMeta(meta);
    }

    public Component getName() {
        return displayItem.getItemMeta().displayName();
    }

    public ItemStack getItem() {
        return displayItem;
    }

    public void setItem(ItemStack item) {
        if (item == null) {
            Bukkit.getLogger().fine("Item Stack was null on: " + this.getDescription().toString());
            return;
        }
        ItemMeta ometa = displayItem.getItemMeta();
        displayItem = item.clone();
        ItemMeta nmeta = displayItem.getItemMeta();
        nmeta.displayName(ometa.displayName());
        nmeta.lore(nmeta.lore());
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

    public @Nullable Menu getContainer() {
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
