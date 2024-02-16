package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuItem {
    private final static String BASE_DESCRIPTION_TOKEN = "Base_description";
    private final @NotNull List<@NotNull IdComponent> descriptionRegistry = new ArrayList<>();
    private @NotNull ItemStack displayItem;
    private @Nullable Menu container = null;
    private int slot = 0;

    public MenuItem(@Nullable Material displayMat, @Nullable Component name) {
        this(displayMat, name, null);
    }

    public MenuItem(@Nullable Material displayMat, @NotNull LangKey langKey) {
        this(displayMat, MinigameMessageManager.getMgMessage(langKey), null);
    }

    public MenuItem(@Nullable Material displayMat, @NotNull LangKey langKey, @Nullable List<Component> description) {
        this(displayMat, MinigameMessageManager.getMgMessage(langKey), description);
    }

    public MenuItem(@Nullable Material displayMat, @Nullable Component name, @Nullable List<Component> description) {
        if (displayMat == null) {
            if (description == null) {
                displayMat = MenuUtility.getSlotFillerItem();
            } else {
                displayMat = MenuUtility.getUnknownDisplayItem();
            }
        }
        this.displayItem = new ItemStack(displayMat);
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.displayName(name);

        if (description == null) {
            this.displayItem.setItemMeta(meta);
        } else {
            // clear automatically generated lore in case there was one
            meta.lore(List.of());
            this.displayItem.setItemMeta(meta);

            setDescriptionPart(BASE_DESCRIPTION_TOKEN, description);
        }
    }

    public MenuItem(@NotNull ItemStack displayItem, @Nullable Component name) {
        ItemMeta meta = displayItem.getItemMeta();
        if (name != null) {
            meta.displayName(name);
        }

        List<Component> lore = meta.lore();
        if (lore != null && !lore.isEmpty()) {
            lore.stream().map(c -> new IdComponent(BASE_DESCRIPTION_TOKEN, c)).forEachOrdered(descriptionRegistry::add);
        }

        displayItem.setItemMeta(meta);
        this.displayItem = displayItem;
    }

    /**
     * Adds the description part to the end of the lore of an itemStack.
     * If a description part with the same type key will get replaced at the same place if it was already there.
     *
     * @param descriptionToken         unique identifier the description part is registered as
     * @param descriptionPart the part of description to get written. If null the part will get removed.
     */
    public void setDescriptionPart(final @NotNull String descriptionToken,
                                   @Nullable List<@NotNull Component> descriptionPart) {
        ItemMeta itemMeta = displayItem.getItemMeta();

        if (descriptionRegistry.isEmpty()) {
            if (descriptionPart != null && !descriptionPart.isEmpty()) { // add
                // set part as lore, as it is the first entry
                descriptionPart.stream().map(c -> new IdComponent(descriptionToken, c)).forEachOrdered(descriptionRegistry::add);

                itemMeta.lore(descriptionPart);
                displayItem.setItemMeta(itemMeta);
            } // nothing to remove, when there was nothing
        } else {
            if (descriptionPart == null || descriptionPart.isEmpty()) { // remove
                descriptionRegistry.stream().filter(idC -> idC.id().equals(descriptionToken)).forEach(descriptionRegistry::remove);
            } else {
                int index = -1;

                // check for last position and remove last description part registered under this token
                Iterator<IdComponent> idCIterator = descriptionRegistry.iterator();
                for (int i = 0; idCIterator.hasNext(); i++) {
                    IdComponent idCToCheck = idCIterator.next();

                    if (idCToCheck.id().equals(descriptionToken)) {
                        idCIterator.remove();

                        if (index == -1) {
                            index = i;
                        }
                    } else if (index > -1) {
                        break; // already over it
                    }
                }

                if (index >= 0) { // replace
                    descriptionRegistry.addAll(index, descriptionPart.stream().map(c -> new IdComponent(descriptionToken, c)).toList());
                } else { // add at end
                    descriptionPart.stream().map(c -> new IdComponent(descriptionToken, c)).forEachOrdered(descriptionRegistry::add);
                }
            }

            itemMeta.lore(descriptionRegistry.stream().map(IdComponent::component).toList());
            displayItem.setItemMeta(itemMeta);
        }
    }

    /**
     * Inserts the description part at the inserted position.
     * A description part with the same type key will get removed if one was already present in the description.
     * This will not skip over values if a higher position was provided
     *
     * @param descriptionToken         unique identifier the description part is registered as
     * @param postion         the position to insert the part at. (Positions are of description parts - NOT elements of Components!)
     * @param descriptionPart the part of description to get inserted
     */
    public void setDescriptionPartAtIndex(final @NotNull String descriptionToken, int postion,
                                          final @Nullable List<@NotNull Component> descriptionPart) {
        ItemMeta itemMeta = displayItem.getItemMeta();
        if (descriptionRegistry.isEmpty()) {
            if (descriptionPart != null && !descriptionPart.isEmpty()) { // add - ignoring index
                // set part as lore, as it is the first entry
                descriptionPart.stream().map(c -> new IdComponent(descriptionToken, c)).forEachOrdered(descriptionRegistry::add);

                itemMeta.lore(descriptionPart);
                displayItem.setItemMeta(itemMeta);
            }
        } else {
            // todo Math.clamp in Java 21
            postion = Math.max(0, Math.min(postion, descriptionRegistry.size()));

            removeDescriptionPart(descriptionToken);

            if (descriptionPart != null && !descriptionPart.isEmpty()) {
                if (descriptionRegistry.isEmpty()) { // after removed the last entry the registry might be empty now!
                    // set part as lore, as it is the first entry
                    descriptionPart.stream().map(c -> new IdComponent(descriptionToken, c)).forEachOrdered(descriptionRegistry::add);
                } else {
                    int workingPos = 0;
                    String lastId = null;

                    for (int i = 0; i < descriptionRegistry.size(); i++) {
                        IdComponent idC = descriptionRegistry.get(i);

                        if (lastId == null || !lastId.equals(idC.id())) {
                            if (workingPos == postion) {
                                descriptionRegistry.addAll(i, descriptionPart.stream().map(c -> new IdComponent(descriptionToken, c)).toList());

                                break;
                            }

                            lastId = idC.id();
                            workingPos++;
                        }
                    } // for loop
                } // else
            } // description part empty

            itemMeta.lore(descriptionRegistry.stream().map(IdComponent::component).toList());
            displayItem.setItemMeta(itemMeta);
        }
    }

    public void removeDescriptionPart(@NotNull String descriptionToken) {
        boolean found = false;
        for (Iterator<IdComponent> it = descriptionRegistry.iterator(); it.hasNext(); ) {
            IdComponent idCToCheck = it.next();

            if (idCToCheck.id().equals(descriptionToken)) {
                found = true;

                it.remove();
            } else if (found) {
                break;
            }
        }
    }

    public List<Component> getDescription() {
        return displayItem.getItemMeta().lore();
    }

    /**
     * Sets the description unter the base description token.
     *
     * @see #setDescriptionPart(String, List)
     * @see #setDescriptionPartAtIndex(String, int, List)
     */
    public void setBaseDescriptionPart(List<Component> description) {
        setDescriptionPartAtIndex(BASE_DESCRIPTION_TOKEN, 0, description);
    }

    public @NotNull Component getName() {
        Component displayName = displayItem.getItemMeta().displayName();
        return displayName == null ? Component.translatable(displayItem.translationKey()) : displayName;
    }

    public @NotNull ItemStack getDisplayItem() {
        return displayItem;
    }

    /**
     * overwrites name with last name and lore with lore registered in {@link #setDescriptionPart(String, List)} and
     * {@link #setDescriptionPartAtIndex(String, int, List)},
     * however this will set the BaseDescription to the new lore - potentially clearing it
     *
     * @see #setBaseDescriptionPart(List)
     */
    public void setDisplayItem(@NotNull ItemStack item) {
        ItemMeta originalMeta = displayItem.getItemMeta();
        displayItem = item.clone();
        ItemMeta newMeta = displayItem.getItemMeta();
        newMeta.displayName(originalMeta.displayName());

        List<Component> newBaseLore = newMeta.lore(); // save temporary
        newMeta.lore(descriptionRegistry.stream().map(IdComponent::component).toList());
        displayItem.setItemMeta(newMeta);

        setBaseDescriptionPart(newBaseLore);
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

    private record IdComponent(@NotNull String id, @NotNull Component component) {
    }
}
