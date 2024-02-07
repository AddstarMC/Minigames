package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemSaveLoadoutPage extends MenuItemPage {
    private final PlayerLoadout loadout;

    public MenuItemSaveLoadoutPage(@Nullable Material displayMat, @NotNull LangKey langKey,
                                   @NotNull PlayerLoadout loadout, @NotNull Menu menu) {
        super(displayMat, langKey, menu);
        this.loadout = loadout;
    }

    public MenuItemSaveLoadoutPage(@Nullable Material displayMat, @Nullable Component name,
                                   @NotNull PlayerLoadout loadout, @NotNull Menu menu) {
        super(displayMat, name, menu);
        this.loadout = loadout;
    }

    public MenuItemSaveLoadoutPage(@Nullable Material displayMat, @Nullable Component name,
                                   @Nullable List<@NotNull Component> description,
                                   @NotNull PlayerLoadout loadout, @NotNull Menu menu) {
        super(displayMat, name, description, menu);
        this.loadout = loadout;
    }

    @Override
    public ItemStack onClick() {
        ItemStack[] items = getContainer().getInventory();
        loadout.clearLoadout();

        for (int i = 0; i < 36; i++) {
            if (items[i] != null)
                loadout.addItem(items[i], i);
        }
        int numOfSpecialSlots = loadout.allowOffHand() ? 41 : 40;
        for (int i = 36; i < numOfSpecialSlots; i++) {
            if (items[i] != null) {
                switch (i) {
                    case 36 -> loadout.addItem(items[i], 103);
                    case 37 -> loadout.addItem(items[i], 102);
                    case 38 -> loadout.addItem(items[i], 101);
                    case 39 -> loadout.addItem(items[i], 100);
                    case 40 -> loadout.addItem(items[i], -106);
                }
            }
        }
        MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.INFO, MgMenuLangKey.MENU_LOADOUT_SAVE,
                Placeholder.unparsed(MinigamePlaceHolderKey.LOADOUT.getKey(), loadout.getName()));

        return super.onClick();
    }
}
