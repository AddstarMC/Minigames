package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.EnumFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.ItemFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHasItemCondition extends ConditionInterface { //todo amount
    private final ItemFlag itemToSearchFor = new ItemFlag(new ItemStack(Material.STONE), "item");
    private final IntegerFlag count = new IntegerFlag(1, "amount");
    private final EnumFlag<PositionType> where = new EnumFlag<>(PositionType.ANYWHERE, "where");
    private final IntegerFlag slot = new IntegerFlag(0, "slot");

    private final BooleanFlag matchName = new BooleanFlag(false, "matchName");
    private final BooleanFlag matchLore = new BooleanFlag(false, "matchLore");
    private final BooleanFlag matchEnchantments = new BooleanFlag(false, "matchEnchantments");
    private final BooleanFlag matchExact = new BooleanFlag(false, "matchExact");

    static void createPattern(String value, StringBuffer buffer) {
        int start = 0;
        int index;
        while (true) {
            index = value.indexOf('%', start);
            // End of input, append the rest
            if (index == -1) {
                buffer.append(Pattern.quote(value.substring(start)));
                break;
            }

            // Append the start
            buffer.append(Pattern.quote(value.substring(start, index)));

            // Append the wildcard code
            buffer.append(".*?");

            // Move to next position
            start = index + 1;
        }
    }

    @Override
    public String getName() {
        return "PLAYER_HAS_ITEM";
    }

    @Override
    public String getCategory() {
        return "Player Conditions";
    }

    @Override
    public void describe(@NotNull Map<String, Object> out) {
        out.put("Item", itemToSearchFor.getFlag().getType());
        out.put("Amount", count.getFlag());
        out.put("Where", where.getFlag());
        out.put("Slot", slot.getFlag());

        if (matchName.getFlag()) {
            out.put("Name", itemToSearchFor.getFlag().getItemMeta().getDisplayName());
        }

        if (matchLore.getFlag()) {
            out.put("Lore", itemToSearchFor.getFlag().getItemMeta().getLore());
        }
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public boolean checkRegionCondition(MinigamePlayer player, @NotNull Region region) {
        return check(player);
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, @NotNull Node node) {
        return check(player);
    }

    private boolean check(MinigamePlayer player) {
        PositionType checkType = where.getFlag();

        PlayerInventory inventory = player.getPlayer().getInventory();
        ItemStack[] searchItems;
        int startSlot;
        int endSlot;
        int foundAmount = 0;


        if (checkType == PositionType.ARMOR) {
            searchItems = inventory.getArmorContents();
            startSlot = 0;
            endSlot = searchItems.length;
        } else {
            searchItems = inventory.getContents();

            switch (checkType) {
                case HOTBAR -> {
                    startSlot = 0;
                    endSlot = 8;
                }
                case MAIN -> {
                    startSlot = 9;
                    endSlot = 35;
                }
                case SLOT -> {
                    startSlot = slot.getFlag();
                    endSlot = startSlot;
                }
                default -> {
                    startSlot = 0;
                    endSlot = searchItems.length;
                }
            }
        }

        Material material = itemToSearchFor.getFlag().getType();

        Pattern namePattern = null;
        Pattern lorePattern = null;

        if (matchName.getFlag()) {
            namePattern = createNamePattern();
        }

        if (matchLore.getFlag()) {
            lorePattern = createLorePattern();
        }

        Map<Enchantment, Integer> enchantmentsToMatch = itemToSearchFor.getFlag().getEnchantments();

        int slotIndex = 0;
        for (ItemStack itemInSlot : searchItems) {
            if (slotIndex < startSlot) { // skip the start slot
                slotIndex++;
                continue;
            } else if (slotIndex > endSlot) {
                break;
            }
            slotIndex++;


            if (itemInSlot != null && itemInSlot.getType() == material) {
                if (matchExact.getFlag()) {
                    if (itemToSearchFor.getFlag().hasItemMeta() != itemInSlot.hasItemMeta() || (
                            itemToSearchFor.getFlag().hasItemMeta() &&
                                    !itemToSearchFor.getFlag().getItemMeta().equals(itemInSlot.getItemMeta()))) {
                        continue;
                    }
                } else {
                    ItemMeta meta = itemInSlot.getItemMeta();

                    if (namePattern != null) {
                        Matcher m = namePattern.matcher(meta.getDisplayName());
                        if (!m.matches()) {
                            continue;
                        }
                    }

                    if (matchLore.getFlag()) {
                        if (lorePattern != null) {
                            Matcher m = lorePattern.matcher(String.join("\n", meta.getLore()));
                            if (!m.matches()) {
                                continue;
                            }
                        } else {
                            // Only an unset lore pattern can match this
                            if (itemToSearchFor.getFlag().lore() != null) {
                                continue;
                            }
                        }
                    }

                    if (matchEnchantments.getFlag() && !enchantmentsToMatch.equals(itemInSlot.getEnchantments())) {
                        continue;
                    }
                }

                foundAmount += itemInSlot.getAmount();
                if (foundAmount >= count.getFlag()) {
                    // This item completely matches
                    return true;
                }
            } // material match
        } // for loop
        return false;
    }

    private Pattern createNamePattern() {
        ItemMeta meta = itemToSearchFor.getFlag().getItemMeta();

        if (meta.hasDisplayName()) {
            StringBuffer buffer = new StringBuffer();

            createPattern(meta.getDisplayName(), buffer);

            return Pattern.compile(buffer.toString());
        } else {
            return Pattern.compile(".*");
        }
    }

    private Pattern createLorePattern() {
        ItemMeta meta = itemToSearchFor.getFlag().getItemMeta();
        List<String> loreList = meta.getLore();

        if (loreList == null) {
            return Pattern.compile(".*");
        } else {
            StringBuffer buffer = new StringBuffer();
            createPattern(String.join("\n", loreList), buffer);
            return Pattern.compile(buffer.toString());
        }
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        itemToSearchFor.saveValue(path, config);
        count.saveValue(path, config);
        where.saveValue(path, config);
        slot.saveValue(path, config);

        matchName.saveValue(path, config);
        matchLore.saveValue(path, config);
        matchEnchantments.saveValue(path, config);
        matchExact.saveValue(path, config);
        saveInvert(config, path);

        // remove legacy
        // datafixerupper
        config.set(path + ".type", null);
        config.set(path + ".name", null);
        config.set(path + ".lore", null);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        if (config.contains(path + ".type")) { // load legacy data
            Material flag = Material.getMaterial(config.getString(path + ".type"));

            // datafixerupper
            if (flag != null) {
                ItemStack legacyItem = new ItemStack(flag);
                ItemMeta meta = legacyItem.getItemMeta();

                if (config.contains(path + ".name")) {
                    String displayname = config.getString(path + ".name");

                    if (displayname != null) {
                        meta.setDisplayName(displayname);
                    }
                }

                if (config.contains(path + ".lore")) {
                    String lore = config.getString(path + ".lore");

                    meta.setLore(Arrays.stream(lore.split(";")).toList());
                }

                legacyItem.setItemMeta(meta);
                itemToSearchFor.setFlag(legacyItem);

            } else {
                itemToSearchFor.loadValue(path, config);
            }
        } else { // new data load system
            itemToSearchFor.loadValue(path, config);
        }

        count.loadValue(path, config);

        where.loadValue(path, config);
        slot.loadValue(path, config);

        matchName.loadValue(path, config);
        matchLore.loadValue(path, config);
        matchEnchantments.loadValue(path, config);
        matchExact.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        final Menu menu = new Menu(3, "Player Has Item", player);
        menu.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), prev), menu.getSize() - 9);

        // we need a reference for two object we will create soon down the line
        final CompletableFuture<MenuItemString> futureNameItem = new CompletableFuture<>();
        final CompletableFuture<MenuItemString> futureLoreItem = new CompletableFuture<>();

        final MenuItemItemNbt itemMenuItem = new MenuItemItemNbt("Item", itemToSearchFor.getFlagOrDefault(), new Callback<>() {
            @Override
            public ItemStack getValue() {
                return itemToSearchFor.getFlagOrDefault();
            }

            @Override
            public void setValue(ItemStack value) {
                itemToSearchFor.setFlag(value);

                ItemMeta meta = value.getItemMeta();
                // sync with other menu Items
                try { // try - catch just to shut the IDE / compiler up. Everything gets already checked beforehand.
                    if (futureNameItem.isDone() && !futureNameItem.isCompletedExceptionally() && meta.displayName() != null) {
                        futureNameItem.get().checkValidEntry(value.getItemMeta().getDisplayName());
                    }

                    if (futureLoreItem.isDone() && !futureLoreItem.isCompletedExceptionally() && meta.lore() != null) {
                        futureLoreItem.get().checkValidEntry(String.join(";", meta.getLore()));
                    }
                } catch (Throwable ignored) {
                }
            }
        });

        menu.addItem(itemMenuItem);
        menu.addItem(count.getMenuItem("Amount", Material.STONE_SLAB, 1, 999));

        final MenuItemInteger slotMenuItem = slot.getMenuItem("Slot", Material.DIAMOND, null, 0, 40);
        final MenuItemEnum<PositionType> whereMenuItem = new MenuItemEnum<>("Search Where", Material.COMPASS, new Callback<>() {
            @Override
            public PositionType getValue() {
                return where.getFlag();
            }

            @Override
            public void setValue(PositionType value) {
                // only enable if relevant
                if (value == PositionType.SLOT) {
                    menu.addItem(slotMenuItem, 3);
                } else {
                    menu.removeItem(3);
                }

                where.setFlag(value);
            }
        }, PositionType.class);
        menu.addItem(whereMenuItem);

        menu.addItem(new MenuItemNewLine());

        menu.addItem(matchName.getMenuItem("Match Display Name", Material.NAME_TAG));
        final MenuItemString nameMenuItem = new MenuItemString("Display Name",
                List.of("The name to match.", "Use % to do a wildcard match"), Material.NAME_TAG, new Callback<>() {
            private String localCache = itemToSearchFor.getFlag().getItemMeta().getDisplayName();

            @Override
            public String getValue() {
                return localCache;
            }

            @Override
            public void setValue(String value) {
                localCache = value;
                itemMenuItem.processNewName(MiniMessage.miniMessage().deserialize(value));
            }
        });

        nameMenuItem.setAllowNull(true);
        futureNameItem.complete(nameMenuItem);
        menu.addItem(nameMenuItem);

        menu.addItem(matchLore.getMenuItem("Match Lore", Material.BOOK));
        final MenuItemString loreMenuItem = new MenuItemString("Lore",
                List.of("The lore to match. Separate", "with semi-colons", "for new lines.", "Use % to do a wildcard match"),
                Material.BOOK, new Callback<>() {
            private String localCache = itemToSearchFor.getFlag().getLore() == null ? null : String.join(";", itemToSearchFor.getFlag().getLore());

            @Override
            public String getValue() {
                return localCache;
            }

            @Override
            public void setValue(String value) {
                MiniMessage miniMessage = MiniMessage.miniMessage();

                String[] loreArray = value.split(";");
                List<Component> newLore = new ArrayList<>(loreArray.length);
                for (String line : loreArray) {
                    newLore.add(miniMessage.deserialize(line));
                }
                itemMenuItem.processNewLore(newLore);

                localCache = value;
            }
        });
        loreMenuItem.setAllowNull(true);
        futureLoreItem.complete(loreMenuItem);
        menu.addItem(loreMenuItem);

        menu.addItem(matchEnchantments.getMenuItem("Match enchantments", Material.ENCHANTED_BOOK));
        menu.addItem(matchExact.getMenuItem("Match exact", Material.BOOKSHELF)); //todo with callback to turn the others on

        addInvertMenuItem(menu);
        menu.displayMenu(player);
        return true;
    }

    @Override
    public boolean PlayerNeeded() {
        return true;
    }

    private enum PositionType {
        ANYWHERE,
        HOTBAR,
        MAIN,
        ARMOR,
        SLOT
    }
}
