package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemDisplayPotions extends MenuItem {
    private final @NotNull PlayerLoadout loadout;

    public MenuItemDisplayPotions(@Nullable Material displayMaterial, @Nullable Component name,
                                  @NotNull PlayerLoadout loadout) {
        super(displayMaterial, name);
        this.loadout = loadout;
    }

    public MenuItemDisplayPotions(@Nullable Material displayMaterial, @Nullable Component name,
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
        potionMenu.addItem(new MenuItemPage(MenuUtility.getSaveMaterial(), "Save Potions", getContainer().getPreviousPage()), 44);

        List<String> des = new ArrayList<>();
        des.add("Shift + Right Click to Delete");

        int inc = 0;
        for (PotionEffect eff : loadout.getAllPotionEffects()) {
            potionMenu.addItem(new MenuItemPotion(Material.POTION, eff.getType().getName().toLowerCase().replace("_", " "), des, eff, loadout), inc);
            inc++;
        }

        potionMenu.displayMenu(getContainer().getViewer());

        return null;
    }
}
