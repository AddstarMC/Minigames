package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MenuItemLong extends MenuItem {
    protected final Callback<Long> value;
    protected final @Nullable Long min;
    protected final @Nullable Long max;

    public MenuItemLong(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull Callback<Long> value,
                        @Nullable Long min, @Nullable Long max) {
        super(displayMat, langKey);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemLong(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<Long> value,
                        @Nullable Long min, @Nullable Long max) {
        super(displayMat, name);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemLong(@Nullable Material displayMat, @NotNull LangKey langKey, @Nullable List<Component> description,
                        @NotNull Callback<Long> value, @Nullable Long min, @Nullable Long max) {
        super(displayMat, langKey, description);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemLong(@Nullable Material displayMat, @Nullable Component name, @Nullable List<Component> description,
                        @NotNull Callback<Long> value, @Nullable Long min, @Nullable Long max) {
        super(displayMat, name, description);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public void updateDescription() {
        List<Component> description;
        if (getDescription() != null) {
            description = getDescription();
            String desc = ChatColor.stripColor(getDescription().get(0));

            if (desc.matches("-?[0-9]+"))
                description.set(0, ChatColor.GREEN.toString() + value.getValue());
            else
                description.add(0, ChatColor.GREEN.toString() + value.getValue());
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GREEN.toString() + value.getValue());
        }

        setDescription(description);
    }

    @Override
    public ItemStack onClick() {
        value.setValue(value.getValue() + 1);
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onRightClick() {
        value.setValue(value.getValue() - 1);
        if (min != null && value.getValue() < min)
            value.setValue(min);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onShiftClick() {
        value.setValue(value.getValue() + 10);
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onShiftRightClick() {
        value.setValue(value.getValue() - 10);
        if (min != null && value.getValue() < min)
            value.setValue(min);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();
        final int reopenSeconds = 10;
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_NUMBER_ENTERCHAT,
                Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), getName()),
                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(reopenSeconds))),
                Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), this.min == null ? "N/A" : this.min.toString()),
                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), this.max == null ? "N/A" : this.max.toString()));
        getContainer().startReopenTimer(reopenSeconds);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.matches("-?[0-9]+")) {
            long entryValue = Long.parseLong(entry);
            if ((min == null || entryValue >= min) && (max == null || entryValue <= max)) {
                value.setValue(entryValue);
                updateDescription();

                getContainer().cancelReopenTimer();
                getContainer().displayMenu(getContainer().getViewer());
            }
        } else {
            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());

            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR,
                    MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry));
        }
    }
}
