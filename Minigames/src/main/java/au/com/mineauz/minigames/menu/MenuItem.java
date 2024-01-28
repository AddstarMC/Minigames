package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.objects.IndexedMap;
import au.com.mineauz.minigames.objects.StrIntMapPersistentDataType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MenuItem {
    public static final StrIntMapPersistentDataType STR_INT_MAP_TYPE = new StrIntMapPersistentDataType();
    private final static NamespacedKey DESCRIPTION_KEY = new NamespacedKey(Minigames.getPlugin(), "DescriptionOrder");
    private @NotNull ItemStack displayItem;
    private @Nullable Menu container = null;
    private int slot = 0;

    public MenuItem(Component name, Material displayItem) {
        this(name, null, displayItem);

    }

    public MenuItem(@NotNull Component name, @Nullable List<@NotNull Component> description, @Nullable Material displayMat) {
        if (displayMat == null)
            if (description == null) {
                displayMat = MenuUtility.getSlotFillerItem();
            } else {
                displayMat = MenuUtility.getUnknownDisplayItem();
            }
        this.displayItem = new ItemStack(displayMat);
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.displayName(name);
        if (description != null) meta.lore(description);
        this.displayItem.setItemMeta(meta);
    }

    public MenuItem(@NotNull Component name, @NotNull ItemStack displayItem) {
        ItemMeta meta = displayItem.getItemMeta();
        meta.displayName(name);
        displayItem.setItemMeta(meta);
        this.displayItem = displayItem;
    }

    /**
     * Adds the description part to the end of the lore of an itemStack.
     * If a description part with the same type key will get replaced if it was already there.
     *
     * @param typeStr         unique identifier the description part is registered as
     * @param descriptionPart the part of description to get written. If null the part will get removed.
     */
    public void setDescriptionPartAtEnd(@NotNull String typeStr,
                                        @Nullable List<@NotNull Component> descriptionPart) {
        ItemMeta itemMeta = displayItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        IndexedMap<String, Integer> descriptionRegistry = container.get(DESCRIPTION_KEY, STR_INT_MAP_TYPE);

        if (descriptionRegistry == null) {
            if (descriptionPart != null && !descriptionPart.isEmpty()) { // add
                // set part as lore, as it is the first entry
                descriptionRegistry = new IndexedMap<>();
                descriptionRegistry.put(typeStr, descriptionPart.size());

                itemMeta.lore(descriptionPart);
                itemMeta.getPersistentDataContainer().set(DESCRIPTION_KEY, STR_INT_MAP_TYPE, descriptionRegistry);
                displayItem.setItemMeta(itemMeta);
            } // nothing to remove, when there was nothing
        } else {
            removeDescriptionPart(itemMeta, descriptionRegistry, typeStr);

            if (descriptionPart != null) { // replace / add
                List<Component> loreList = new ArrayList<>();
                List<Component> oldLoreList = itemMeta.lore();

                if (oldLoreList != null) {
                    loreList.addAll(oldLoreList); // just in case the list is unmodifiable
                }
                loreList.addAll(descriptionPart);
                itemMeta.lore(loreList);

                descriptionRegistry.put(typeStr, descriptionPart.size());
            } else { // remove
                descriptionRegistry.remove(typeStr);
            }

            displayItem.setItemMeta(itemMeta);
        }
    }

    /**
     * Inserts the description part at the inserted position.
     * A description part with the same type key will get removed if one was already present in the description.
     * This will not skip over values if a higher position was provided
     *
     * @param typeStr         unique identifier the description part is registered as
     * @param postion         the position to insert the part at. ()
     * @param descriptionPart the part of description to get inserted
     */
    public void setDescriptionPartAtIndex(@NotNull String typeStr, int postion,
                                          @NotNull List<@NotNull Component> descriptionPart) {
        ItemMeta itemMeta = displayItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        IndexedMap<String, Integer> descriptionRegistry = container.get(DESCRIPTION_KEY, STR_INT_MAP_TYPE);

        if (descriptionRegistry == null) {
            if (!descriptionPart.isEmpty()) { // add - ignoring index
                // set part as lore, as it is the first entry
                descriptionRegistry = new IndexedMap<>();
                descriptionRegistry.put(typeStr, descriptionPart.size());

                itemMeta.lore(descriptionPart);
                itemMeta.getPersistentDataContainer().set(DESCRIPTION_KEY, STR_INT_MAP_TYPE, descriptionRegistry);
                displayItem.setItemMeta(itemMeta);
            }
        } else {
            // todo Math.clamp in Java 21
            postion = Math.max(0, Math.min(postion, descriptionRegistry.size()));
            if (postion >= descriptionRegistry.getKeyIndex(typeStr)) {
                postion--;
            }
            removeDescriptionPart(itemMeta, descriptionRegistry, typeStr);
            if (!descriptionPart.isEmpty()) {
                int startingPoint = 0;

                Iterator<Integer> lengthIter = descriptionRegistry.values().iterator(); // don't modify with this iterator unless you have overwritten the standard one in IndexedMap!

                for (int i = 0; lengthIter.hasNext() && i < postion; i++) {
                    Integer length = lengthIter.next();

                    if (length != null && length > 0) {
                        startingPoint += length;
                    } else {
                        // well crap. invalid data!
                        Minigames.getCmpnntLogger().warn("Found empty description data. Ignoring it for now.");
                        //todo we need a iterator of IndexedMap --> code it!
                    }
                }

                List<Component> loreList = new ArrayList<>();
                List<Component> oldLoreList = itemMeta.lore();
                if (oldLoreList != null) {
                    loreList.addAll(oldLoreList); // just in case the list is unmodifiable
                    loreList.addAll(startingPoint, descriptionPart);
                } else {
                    loreList = descriptionPart;
                }

                itemMeta.lore(loreList);
            }

            descriptionRegistry.put(typeStr, descriptionPart.size());
            itemMeta.getPersistentDataContainer().set(DESCRIPTION_KEY, STR_INT_MAP_TYPE, descriptionRegistry);
            displayItem.setItemMeta(itemMeta);
        }
    }

    private static void removeDescriptionPart(@NotNull ItemMeta itemMeta, @NotNull IndexedMap<String,
            Integer> descriptionRegistry, @NotNull String typeStr) {
        if (descriptionRegistry.containsKey(typeStr)) {
            Integer lastPartLength = descriptionRegistry.get(typeStr);

            if (lastPartLength != null && lastPartLength > 0) {
                int startingPoint = 0;
                for (Map.Entry<String, Integer> entryBefore : descriptionRegistry.entrySet()) {
                    if (typeStr.equals(entryBefore.getKey())) {
                        break;
                    } else {
                        Integer lengthBefore = entryBefore.getValue();
                        if (lengthBefore != null) {
                            startingPoint += entryBefore.getValue();
                        } else {
                            // well crap. invalid data!
                            Minigames.getCmpnntLogger().warn("Found empty description data. Ignoring it for now.");
                            //todo we need a iterator of IndexedMap --> code it!
                        }
                    }
                }

                List<Component> loreList = itemMeta.lore();
                if (loreList != null) {
                    for (int i = startingPoint; i < startingPoint + lastPartLength && i < loreList.size(); i++) {
                        loreList.remove(i);
                    }
                } else {
                    // well crap. invalid data!
                    Minigames.getCmpnntLogger().error("Menu item has empty lore but tried to remove a part of it. Clearing all parts mow. Please open an Issue!");
                    descriptionRegistry.clear();
                }
            } else {
                // well crap. invalid data!
                Minigames.getCmpnntLogger().warn("Menu item trying to remove empty description data.");
                descriptionRegistry.remove(typeStr);
            }
        }
    }

    public List<Component> getDescription() {
        return displayItem.getItemMeta().lore();
    }

    public void setDescription(List<Component> description) {
        ItemMeta meta = displayItem.getItemMeta();

        meta.lore(description);
        displayItem.setItemMeta(meta);
    }

    public @NotNull Component getName() {
        return displayItem.getItemMeta().displayName();
    }

    public @NotNull ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(@NotNull ItemStack item) {
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
        return getDisplayItem();
    }

    public ItemStack onClickWithItem(ItemStack item) {
        //Do stuff
        return getDisplayItem();
    }

    public ItemStack onRightClick() {
        //Do stuff
        return getDisplayItem();
    }

    public ItemStack onShiftClick() {
        //Do stuff
        return getDisplayItem();
    }

    public ItemStack onShiftRightClick() {
        //Do stuff
        return getDisplayItem();
    }

    public ItemStack onDoubleClick() {
        //Do Stuff
        return getDisplayItem();
    }

    public void checkValidEntry(String entry) {
        //Do Stuff
    }

    public @Nullable Menu getContainer() {
        return container;
    }

    public void setContainer(@NotNull Menu container) {
        this.container = container;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
