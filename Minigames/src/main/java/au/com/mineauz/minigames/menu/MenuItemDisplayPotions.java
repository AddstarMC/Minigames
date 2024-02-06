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

import java.util.ArrayList;
import java.util.List;

public class MenuItemDisplayPotions extends MenuItem {
    private final @NotNull PlayerLoadout loadout;

    public MenuItemDisplayPotions(@Nullable Component name, @Nullable Material displayMaterial,
                                  @NotNull PlayerLoadout loadout) {
        super(name, displayMaterial);
        this.loadout = loadout;
    }

    public MenuItemDisplayPotions(@Nullable Component name, @Nullable List<@NotNull Component> description,
                                  @Nullable Material displayMaterial, @NotNull PlayerLoadout loadout) {
        super(name, description, displayMaterial);
        this.loadout = loadout;
    }


    @Override
    public ItemStack onClick() {
        Menu potionMenu = new Menu(5, getContainer().getName(), getContainer().getViewer());

        potionMenu.setAllowModify(true);
        potionMenu.setPreviousPage(getContainer());
        potionMenu.addItem(new MenuItemPotionAdd(MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_POTIONADD_NAME),
                MenuUtility.getCreateMaterial(), loadout), 43);
        potionMenu.addItem(new MenuItemPage("Save Potions", MenuUtility.getSaveMaterial(), getContainer().getPreviousPage()), 44);

        List<String> des = new ArrayList<>();
        des.add("Shift + Right Click to Delete");

        int inc = 0;
        for (PotionEffect eff : loadout.getAllPotionEffects()) {
            potionMenu.addItem(new MenuItemPotion(eff.getType().getName().toLowerCase().replace("_", " "), des, Material.POTION, eff, loadout), inc);
            inc++;
        }

        potionMenu.displayMenu(getContainer().getViewer());

        return null;
    }
}
