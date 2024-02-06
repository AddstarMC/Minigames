package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class MenuItemAddFlag extends MenuItem {
    private final Minigame mgm;

    public MenuItemAddFlag(@NotNull LangKey langKey, @Nullable Material displayItem, @NotNull Minigame mgm) {
        super(langKey, displayItem);
        this.mgm = mgm;
    }

    public MenuItemAddFlag(@NotNull Component name, @Nullable Material displayItem, @NotNull Minigame mgm) {
        super(name, displayItem);
        this.mgm = mgm;
    }

    public MenuItemAddFlag(@Nullable Component name, List<@NotNull Component> description, @Nullable Material displayItem,
                           @NotNull Minigame mgm) {
        super(name, description, displayItem);
        this.mgm = mgm;
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();

        final int reopenSeconds = 20;
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_FLAGADD_ENTERCHAT,
                Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), getName()),
                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(reopenSeconds))));
        mgPlayer.setManualEntry(this);
        getContainer().startReopenTimer(reopenSeconds);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        mgm.addFlag(entry);
        getContainer().addItem(new MenuItemFlag(Material.OAK_SIGN, entry, mgm.getFlags()));

        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }
}
