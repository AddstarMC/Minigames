package au.com.mineauz.minigames.menu;

import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemItemNbt extends MenuItem {
    /**
     * NEVER EVER confuse this item with the display item of MenuItem!
     * this one contains the pure data as it should get written to config if needed.
     * The display item has a  different displayname and a description attached to it,
     * to explain a user what the MenuItem does.
     * Accidentally setting the same instance to both of them may tint your data or break your menu item.
     * Always use {@link ItemStack#clone} and best practice use the {@link MenuItem#setDisplayItem(ItemStack)}
     * to update the display item!
     * take care.
     **/
    private final @NotNull Callback<ItemStack> itemCallback;

    public MenuItemItemNbt(String name, Material displayMat, @NotNull Callback<ItemStack> c) {
        super(name, displayMat);
        itemCallback = c;
    }

    public MenuItemItemNbt(String name, List<String> description, Material displayMat, @NotNull Callback<ItemStack> c) {
        super(name, description, displayMat);
        itemCallback = c;
    }

    public MenuItemItemNbt(String name, @NotNull ItemStack itemStack, @NotNull Callback<ItemStack> c) {
        super(name, itemStack.clone()); // clone to not overwrite lore / name

        setDescriptionPartAtEnd("MenuItemItemNbt", createDescription(itemStack));
        itemCallback = c;
    }

    @Override
    public ItemStack onClickWithItem(ItemStack item) {
        // better make a copy, we don't know what happens with the item later
        itemCallback.setValue(item.clone());

        // Taken from itemCallback, since it could be able to change it,
        // and it may be different from our initial cloned item
        ItemStack workItemStack = itemCallback.getValue();

        // cloned one as display item since it gets changed to display name / description / other meta
        setDisplayItem(workItemStack.clone());
        // please note, the item used to create the components is the original - not cloned one!
        setDescriptionPartAtEnd("MenuItemItemNbt", createDescription(workItemStack));

        return super.onClickWithItem(itemCallback.getValue());
    }

    @Override
    public ItemStack onShiftRightClick() {
        // note: this was done so display item and value do NOT share the same reference.
        // the display item gets changed by MenuItem!
        setDisplayItem(new ItemStack(Material.STONE));
        itemCallback.setValue(new ItemStack(Material.STONE));
        return super.onShiftRightClick();
    }

    public ItemStack getItem() {
        return itemCallback.getValue();
    }

    public void processNewName(@NotNull Component newName) {
        ItemStack oldData = itemCallback.getValue();
        setDescriptionPartAtEnd("MenuItemItemNbt", createDescription(oldData.getType(), newName, oldData.lore()));
    }

    public void processNewLore(@Nullable List<@NotNull Component> newLore) {
        ItemStack oldData = itemCallback.getValue();
        setDescriptionPartAtEnd("MenuItemItemNbt", createDescription(oldData.getType(), oldData.displayName(), newLore));
    }

    private List<Component> createDescription(@NotNull ItemStack data) {
        return createDescription(data.getType(), data.displayName(), data.lore());
    }

    private List<Component> createDescription(@NotNull Material type, @NotNull Component displayName, @Nullable List<@NotNull Component> lore) {
        List<Component> result = new ArrayList<>();
        result.add(Component.text("Name: ").append(displayName));
        result.add(Component.text("Material: " + WordUtils.capitalizeFully(type.name())));

        if (lore != null) {
            result.add(Component.text("lore: "));
            result.addAll(lore);
        }
        return result;
    }
}
