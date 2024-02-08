package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemDisplayEffects extends MenuItem { // todo unused
    private final @NotNull PlayerLoadout loadout;

    public MenuItemDisplayEffects(@Nullable Material displayMaterial, @Nullable Component name,
                                  @NotNull PlayerLoadout loadout) {
        super(displayMaterial, name);
        this.loadout = loadout;
    }

    public MenuItemDisplayEffects(@Nullable Material displayMaterial, @Nullable Component name,
                                  @Nullable List<@NotNull Component> description, @NotNull PlayerLoadout loadout) {
        super(displayMaterial, name, description);
        this.loadout = loadout;
    }


    @Override
    public ItemStack onClick() {
        Menu potionMenu = new Menu(5, getContainer().getName(), getContainer().getViewer());

        potionMenu.setAllowModify(true);
        potionMenu.setPreviousPage(getContainer());
        potionMenu.addItem(new MenuItemPotionAdd(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_POTIONADD_NAME, loadout), 43);
        potionMenu.addItem(new MenuItemPage(MenuUtility.getSaveMaterial(),
                MgMenuLangKey.MENU_EFFECTS_SAVE_NAME,
                getContainer().getPreviousPage()), 44);

        int inc = 0;
        for (PotionEffect eff : loadout.getAllPotionEffects()) {
            potionMenu.addItem(new MenuItemPotion(Material.POTION,
                    Component.translatable(eff.getType().translationKey()),
                    List.of(MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK)),
                    eff, loadout), inc);
            inc++;
        }

        potionMenu.displayMenu(getContainer().getViewer());

        return null;
    }
}
