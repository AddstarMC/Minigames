package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MenuItemModifyStatSetting extends MenuItem {
    private final @NotNull Minigame minigame;
    private final @NotNull MinigameStat stat;

    public MenuItemModifyStatSetting(@Nullable Material displayMat, @NotNull Minigame minigame, @NotNull MinigameStat stat) {
        super(displayMat, stat.getDisplayName());

        this.minigame = minigame;
        this.stat = stat;
    }

    @Override
    public ItemStack onClick() {
        ;
        Menu subMenu = new Menu(6, MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_STAT_EDIT_NAME,
                Placeholder.component(MinigamePlaceHolderKey.STAT.getKey(), stat.getDisplayName())), getContainer().getViewer());

        subMenu.addItem(new MenuItemComponent(Material.NAME_TAG,
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_DISPLAYNAME_NAME), new Callback<>() {
            @Override
            public Component getValue() {
                return minigame.getSettings(stat).getDisplayName();
            }

            @Override
            public void setValue(Component value) {
                minigame.getSettings(stat).setDisplayName(value);
            }
        }));

        if (stat != MinigameStats.Losses) {
            subMenu.addItem(new MenuItemList<>(Material.ENDER_CHEST, MgMenuLangKey.MENU_STAT_STORAGEFORMAT, new Callback<>() {
                @Override
                public StatFormat getValue() {
                    return minigame.getSettings(stat).getFormat();
                }

                @Override
                public void setValue(StatFormat value) {
                    minigame.getSettings(stat).setFormat(value);
                }
            }, Arrays.asList(StatFormat.values())));
        }

        subMenu.addItem(new MenuItemBack(getContainer()), subMenu.getSize() - 9);
        subMenu.displayMenu(getContainer().getViewer());

        return super.onClick();
    }
}
