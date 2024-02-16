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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class MenuItemInteger extends MenuItem {
    private final static String DESCRIPTION_TOKEN = "Integer_description";
    private final Callback<Integer> value;
    private final Integer min; // inclusive
    private final Integer max; // inclusive

    public MenuItemInteger(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull Callback<Integer> value,
                           @Nullable Integer min, @Nullable Integer max) {
        super(displayMat, langKey);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemInteger(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<Integer> value,
                           @Nullable Integer min, @Nullable Integer max) {
        super(displayMat, name);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemInteger(@Nullable Material displayMat, @NotNull LangKey langKey, @Nullable List<Component> description,
                           @NotNull Callback<Integer> value, @Nullable Integer min, @Nullable Integer max) {
        super(displayMat, langKey, description);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemInteger(@Nullable Material displayMat, @Nullable Component name, @Nullable List<Component> description,
                           @NotNull Callback<Integer> value, @Nullable Integer min, @Nullable Integer max) {
        super(displayMat, name, description);
        this.value = value;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public void updateDescription() {
        setDescriptionPart(DESCRIPTION_TOKEN, List.of(Component.text(value.getValue(), NamedTextColor.GREEN)));
    }

    @Override
    public ItemStack onClick() {
        value.setValue(value.getValue() + 1);
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        value.setValue(value.getValue() - 1);
        if (min != null && value.getValue() < min)
            value.setValue(min);
        updateDescription();
        return getDisplayItem();
    }

    @Override
    public ItemStack onShiftClick() {
        value.setValue(value.getValue() + 10);
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getDisplayItem();
    }

    @Override
    public ItemStack onShiftRightClick() {
        value.setValue(value.getValue() - 10);
        if (min != null && value.getValue() < min)
            value.setValue(min);
        updateDescription();
        return getDisplayItem();
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
                Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), this.min == null ? "N/A" : this.min.toString()), //todo don't hardcode N/A
                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), this.max == null ? "N/A" : this.max.toString()));

        mgPlayer.setManualEntry(this);
        getContainer().startReopenTimer(reopenSeconds);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.matches("-?[0-9]+")) {
            int entryValue = Integer.parseInt(entry);
            if ((min == null || entryValue >= min) && (max == null || entryValue <= max)) {
                value.setValue(entryValue);
                updateDescription();

            } else {
                MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR,
                        MgCommandLangKey.COMMAND_ERROR_OUTOFBOUNDS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), this.min == null ? "N/A" : this.min.toString()),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), this.max == null ? "N/A" : this.max.toString()));
            }
        } else {
            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR,
                    MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry));
        }

        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }
}
