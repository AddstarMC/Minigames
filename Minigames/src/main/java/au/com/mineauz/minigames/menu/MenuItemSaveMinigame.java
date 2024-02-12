package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemSaveMinigame extends MenuItem {
    private final @NotNull Minigame mgm;

    public MenuItemSaveMinigame(@Nullable Material displayMat, @Nullable Component name, @NotNull Minigame minigame) {
        super(displayMat, name);
        mgm = minigame;
    }

    public MenuItemSaveMinigame(@Nullable Material displayMat, @Nullable Component name,
                                @Nullable List<@NotNull Component> description, @NotNull Minigame minigame) {
        super(displayMat, name, description);
        mgm = minigame;
    }

    @Override
    public ItemStack onClick() {
        mgm.saveMinigame();
        MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.SUCCESS,
                MinigameLangKey.MINIGAME_SAVED,
                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName()));
        return getItem();
    }
}
