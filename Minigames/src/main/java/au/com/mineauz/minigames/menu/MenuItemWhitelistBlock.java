package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemWhitelistBlock extends MenuItem {
    private final @NotNull List<Material> whitelist;

    public MenuItemWhitelistBlock(@NotNull Material displayMat, @NotNull List<@NotNull Material> whitelist) {
        super(displayMat, Component.translatable(displayMat.translationKey()),
                MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_RIGHTCLICK));
        this.whitelist = whitelist;
    }

    @Override
    public ItemStack onRightClick() {
        whitelist.remove(getItem().getType());
        getContainer().removeItem(getSlot());
        return null;
    }
}
