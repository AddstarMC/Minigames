package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemFlag extends MenuItem {
    private final @NotNull String flag;
    private final @NotNull List<@NotNull String> flags;

    public MenuItemFlag(@Nullable Material displayMat, @NotNull String flag, @NotNull List<@NotNull String> flags) {
        super(displayMat, Component.text(flag));
        this.flag = flag;
        this.flags = flags;
    }

    public MenuItemFlag(@Nullable Material displayMat, @Nullable List<@NotNull Component> description, @NotNull String flag,
                        @NotNull List<@NotNull String> flags) {
        super(displayMat, Component.text(flag), description);
        this.flag = flag;
        this.flags = flags;
    }

    @Override
    public ItemStack onShiftRightClick() {
        MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.INFO, MgMenuLangKey.MENU_FLAG_REMOVED,
                Placeholder.unparsed(MinigamePlaceHolderKey.FLAG.getKey(), flag));
        flags.remove(flag);

        getContainer().removeItem(getSlot());
        return null;
    }
}
