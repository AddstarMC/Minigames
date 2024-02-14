package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDisplayLoadout extends MenuItem {
    private final @NotNull PlayerLoadout loadout;
    private @Nullable Minigame minigame = null;
    private boolean allowDelete = true;

    public MenuItemDisplayLoadout(@Nullable Material displayMat, @Nullable Component name, @NotNull PlayerLoadout loadout,
                                  @Nullable Minigame minigame) {
        super(displayMat, name);
        this.loadout = loadout;
        this.minigame = minigame;
        if (!loadout.isDeleteable()) {
            allowDelete = false;
        }
    }

    public MenuItemDisplayLoadout(@Nullable Material displayMat, @Nullable Component name, @NotNull PlayerLoadout loadout) {
        super(displayMat, name);
        this.loadout = loadout;
        if (!loadout.isDeleteable()) {
            allowDelete = false;
        }
    }

    public MenuItemDisplayLoadout(@Nullable Material displayMat, @Nullable Component name,
                                  @Nullable List<@NotNull Component> description, @NotNull PlayerLoadout loadout,
                                  @NotNull Minigame minigame) {
        super(displayMat, name, description);
        this.loadout = loadout;
        this.minigame = minigame;
        if (!loadout.isDeleteable()) {
            allowDelete = false;
        }
    }

    public MenuItemDisplayLoadout(@Nullable Material displayMat, @Nullable Component name,
                                  @Nullable List<@NotNull Component> description, @NotNull PlayerLoadout loadout) {
        super(displayMat, name, description);
        this.loadout = loadout;
        if (!loadout.isDeleteable()) {
            allowDelete = false;
        }
    }

    @Override
    public ItemStack onClick() {
        Menu loadoutMenu = new Menu(5, loadout.getDisplayName(), getContainer().getViewer());
        Menu loadoutSettingsMenu = new Menu(6, loadout.getDisplayName(), getContainer().getViewer());
        loadoutSettingsMenu.setPreviousPage(loadoutMenu);

        List<MenuItem> menuItems = new ArrayList<>();
        if (!loadout.getName().equals("default")) {
            menuItems.add(new MenuItemBoolean(Material.GOLD_INGOT,
                    MgMenuLangKey.MENU_DISPLAYLOADOUT_USEPERMISSIONS_NAME,
                    MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DISPLAYLOADOUT_USEPERMISSIONS_DESCRIPTION,
                            Placeholder.unparsed(MinigamePlaceHolderKey.LOADOUT.getKey(), loadout.getName().toLowerCase())),
                    loadout.getUsePermissionsCallback()));
        }
        MenuItemComponent disName = new MenuItemComponent(Material.PAPER, MgMenuLangKey.MENU_DISPLAYNAME_NAME, loadout.getDisplayNameCallback());
        disName.setAllowNull(true);
        menuItems.add(disName);
        menuItems.add(new MenuItemBoolean(Material.LEATHER_BOOTS,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_ALLOWFALLDAMAGE_NAME, loadout.getFallDamageCallback()));
        menuItems.add(new MenuItemBoolean(Material.APPLE,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_ALLOWHUNGER_NAME, loadout.getHungerCallback()));
        menuItems.add(new MenuItemInteger(Material.EXPERIENCE_BOTTLE,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_XPLEVEL_NAME,
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DISPLAYLOADOUT_XPLEVEL_DESCRIPTION),
                loadout.getLevelCallback(), -1, null));
        menuItems.add(new MenuItemBoolean(Material.DIAMOND_SWORD,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_LOCKINVENTORY_NAME,
                loadout.getInventoryLockedCallback()));
        menuItems.add(new MenuItemBoolean(Material.DIAMOND_CHESTPLATE,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_LOCKARMOR_NAME,
                loadout.getArmourLockedCallback()));
        menuItems.add(new MenuItemBoolean(Material.SHIELD,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_ALLOWOFFHAND_NAME,
                loadout.getAllowOffHandCallback()));
        menuItems.add(new MenuItemBoolean(Material.WHITE_STAINED_GLASS_PANE,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_DISPLAYINMENU_NAME,
                loadout.getDisplayInMenuCallback()));
        menuItems.add(new MenuItemList<>(Material.LEATHER_CHESTPLATE,
                MgMenuLangKey.MENU_DISPLAYLOADOUT_LOCKTOTEAM_NAME,
                loadout.getTeamColorCallback(), List.of(TeamColor.values())));
        loadoutSettingsMenu.addItems(menuItems);
        MenuItemBack menuItemBack = new MenuItemBack(loadoutMenu);
        loadoutSettingsMenu.addItem(menuItemBack, getContainer().getSize() - 9);

        LoadoutModule.addAddonMenuItems(loadoutSettingsMenu, loadout);

        Menu potionMenu = new Menu(5, getContainer().getName(), getContainer().getViewer());

        potionMenu.setPreviousPage(loadoutMenu);
        potionMenu.addItem(new MenuItemPotionAdd(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_POTIONADD_NAME, loadout), potionMenu.getSize() - 1);
        potionMenu.addItem(menuItemBack, potionMenu.getSize() - 2);

        List<Component> description = MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK);
        List<MenuItem> potionMenuItems = new ArrayList<>();

        for (PotionEffect eff : loadout.getAllPotionEffects()) {
            potionMenuItems.add(new MenuItemPotion(Material.POTION, Component.translatable(eff.getType().translationKey()), description, eff, loadout));
        }
        potionMenu.addItems(potionMenuItems);

        loadoutMenu.setAllowModify(true);
        loadoutMenu.setPreviousPage(getContainer());

        loadoutMenu.addItem(new MenuItemSaveLoadoutPage(Material.CHEST, MgMenuLangKey.MENU_DISPLAYLOADOUT_SETTINGS_NAME, loadout, loadoutSettingsMenu), 42);
        loadoutMenu.addItem(new MenuItemSaveLoadoutPage(Material.POTION, MgMenuLangKey.MENU_DISPLAYLOADOUT_EFFECTS_NAME, loadout, potionMenu), 43);
        loadoutMenu.addItem(new MenuItemSaveLoadoutPage(MenuUtility.getSaveMaterial(), MgMenuLangKey.MENU_DISPLAYLOADOUT_SAVE_NAME, loadout, getContainer()), 44);
        final int numOfSlots = loadout.allowOffHand() ? 41 : 40;
        for (int i = numOfSlots; i < 42; i++) {
            loadoutMenu.addItem(new MenuItem(null, Component.empty()), i);
        }
        loadoutMenu.displayMenu(getContainer().getViewer());

        for (Integer slot : loadout.getItemSlots()) {
            if (slot >= 0 && slot < 100) {
                loadoutMenu.addItemStack(loadout.getItem(slot), slot);
            } else {
                switch (slot) {
                    case 100 -> loadoutMenu.addItemStack(loadout.getItem(slot), 39);
                    case 101 -> loadoutMenu.addItemStack(loadout.getItem(slot), 38);
                    case 102 -> loadoutMenu.addItemStack(loadout.getItem(slot), 37);
                    case 103 -> loadoutMenu.addItemStack(loadout.getItem(slot), 36);
                    case -106 -> {
                        if (loadout.allowOffHand()) {
                            loadoutMenu.addItemStack(loadout.getItem(slot), 40);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public ItemStack onShiftRightClick() {
        if (allowDelete) {
            MinigamePlayer mgPlayer = getContainer().getViewer();
            mgPlayer.setNoClose(true);
            mgPlayer.getPlayer().closeInventory();
            final int reopenSeconds = 10;
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_DISPLAYLOADOUT_ENTERCHAT,
                    Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), getName()),
                    Placeholder.unparsed(MinigamePlaceHolderKey.LOADOUT.getKey(), loadout.getName()),
                    Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(reopenSeconds))));
            mgPlayer.setManualEntry(this);
            getContainer().startReopenTimer(reopenSeconds);
            return null;
        }

        return getItem();
    }

    @Override
    public void checkValidEntry(String entry) {
        String loadoutName = loadout.getName();

        if (entry.equalsIgnoreCase("yes")) {
            if (minigame != null) {
                LoadoutModule.getMinigameModule(minigame).deleteLoadout(loadoutName);
            } else {
                Minigames.getPlugin().getMinigameManager().deleteGlobalLoadout(loadoutName);
            }
            getContainer().removeItem(getSlot());
            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.SUCCESS, MgMenuLangKey.MENU_DISPLAYLOADOUT_DELETE,
                    Placeholder.unparsed(MinigamePlaceHolderKey.LOADOUT.getKey(), loadoutName));
        } else {
            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.WARNING, MgMenuLangKey.MENU_DISPLAYLOADOUT_NOTDELETE,
                    Placeholder.unparsed(MinigamePlaceHolderKey.LOADOUT.getKey(), loadoutName));
            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
        }
    }

    public void setAllowDelete(boolean bool) {
        allowDelete = bool;
    }
}
