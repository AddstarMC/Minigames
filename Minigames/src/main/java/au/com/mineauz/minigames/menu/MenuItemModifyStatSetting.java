package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.stats.MinigameStat;
import au.com.mineauz.minigames.stats.MinigameStats;
import au.com.mineauz.minigames.stats.StatFormat;
import com.google.common.base.Functions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        Menu subMenu = new Menu(6, "Edit " + stat.getDisplayName(), getContainer().getViewer());

        subMenu.addItem(new MenuItemString("Display Name", Material.NAME_TAG, new Callback<>() {
            @Override
            public String getValue() {
                return minigame.getSettings(stat).getDisplayName();
            }

            @Override
            public void setValue(String value) {
                minigame.getSettings(stat).setDisplayName(value);
            }


        }));
        if (stat != MinigameStats.Losses) {
            subMenu.addItem(new MenuItemList("Storage Format", Material.ENDER_CHEST, new Callback<>() {
                @Override
                public String getValue() {
                    return minigame.getSettings(stat).getFormat().toString();
                }

                @Override
                public void setValue(String value) {
                    StatFormat format = StatFormat.valueOf(value);
                    minigame.getSettings(stat).setFormat(format);
                }


            }, Arrays.stream(StatFormat.values()).map(Functions.toStringFunction()).collect(Collectors.toList())));
        }

        subMenu.addItem(new MenuItemBack(getContainer()), subMenu.getSize() - 9);
        subMenu.displayMenu(getContainer().getViewer());

        return super.onClick();
    }
}
