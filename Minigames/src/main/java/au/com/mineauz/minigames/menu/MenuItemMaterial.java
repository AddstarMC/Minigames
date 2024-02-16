package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Used when the menu item holds a material.
 * <p>
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 15/11/2018.
 */
public class MenuItemMaterial extends MenuItem {
    private final static String DESCRIPTION_TOKEN = "Material_description";
    private final @NotNull Callback<Material> materialCallback;

    public MenuItemMaterial(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<Material> c) {
        super(displayMat, name);
        materialCallback = c;
    }

    public MenuItemMaterial(@Nullable Material displayMat, @Nullable Component name,
                            @Nullable List<@NotNull Component> description, @NotNull Callback<Material> c) {
        super(displayMat, name, description);
        materialCallback = c;
    }

    @Override
    public ItemStack onClickWithItem(ItemStack item) {
        materialCallback.setValue(item.getType());
        updateDescription();
        return super.onClickWithItem(item);
    }

    @Override
    public ItemStack onShiftRightClick() {
        materialCallback.setValue(Material.STONE);
        return super.onShiftRightClick();
    }

    public void updateDescription() {
        setDescriptionPart(DESCRIPTION_TOKEN, List.of(
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_MATERIAL_DESCRIOPTION,
                        Placeholder.component(MinigamePlaceHolderKey.MATERIAL.getKey(),
                                Component.translatable(materialCallback.getValue().translationKey())))));

        setDisplayItem(new ItemStack(materialCallback.getValue(), 1));
    }
}
