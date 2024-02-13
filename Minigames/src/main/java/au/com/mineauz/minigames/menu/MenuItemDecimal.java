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

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

public class MenuItemDecimal extends MenuItem {
    private final static String DESCRIPTION_TOKEN = "Decimal_description";
    private final static Pattern DOUBLE_PATTERN = Pattern.compile("[+-]?[0-9]+(.[0-9]+)?");

    private final @NotNull Callback<Double> value;
    private final double lowerInc;
    private final double upperInc;
    private final @Nullable Double min;
    private final @Nullable Double max;
    private @NotNull DecimalFormat form = new DecimalFormat("#.##");

    public MenuItemDecimal(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull Callback<Double> value,
                           double lowerInc, double upperInc, @Nullable Double min, @Nullable Double max) {
        super(displayMat, langKey);
        this.value = value;
        this.lowerInc = lowerInc;
        this.upperInc = upperInc;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemDecimal(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<Double> value,
                           double lowerInc, double upperInc, @Nullable Double min, @Nullable Double max) {
        super(displayMat, name);
        this.value = value;
        this.lowerInc = lowerInc;
        this.upperInc = upperInc;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemDecimal(@Nullable Material displayMat, @Nullable Component name,
                           @Nullable List<@NotNull Component> description, @NotNull Callback<Double> value,
                           double lowerInc, double upperInc, @Nullable Double min, @Nullable Double max) {
        super(displayMat, name, description);
        this.value = value;
        this.lowerInc = lowerInc;
        this.upperInc = upperInc;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public void setFormat(@NotNull DecimalFormat format) {
        form = format;
    }

    public void updateDescription() {
        Component description;
        if (value.getValue().isInfinite()) {
            description = MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_NUMBER_INFINITE).color(NamedTextColor.GREEN);
        } else {
            description = Component.text(form.format(value.getValue()), NamedTextColor.GREEN);
        }

        setDescriptionPartAtEnd(DESCRIPTION_TOKEN, List.of(description));
    }

    @Override
    public ItemStack onClick() {
        if (max == null || value.getValue() < max)
            value.setValue(value.getValue() + lowerInc);
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        if (min == null || value.getValue() > min)
            value.setValue(value.getValue() - lowerInc);
        if (min != null && value.getValue() < min)
            value.setValue(min);
        updateDescription();
        return getDisplayItem();
    }

    @Override
    public ItemStack onShiftClick() {
        if (max == null || value.getValue() < max)
            value.setValue(value.getValue() + upperInc);
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getDisplayItem();
    }

    @Override
    public ItemStack onShiftRightClick() {
        if (min == null || value.getValue() > min)
            value.setValue(value.getValue() - upperInc);
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

        final int reopenSeconds = 15;
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_NUMBER_ENTERCHAT,
                Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), getName()),
                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(reopenSeconds))),
                Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), this.min == null ? "N/A" : this.min.toString()),
                Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), this.max == null ? "N/A" : this.max.toString()));
        mgPlayer.setManualEntry(this);
        getContainer().startReopenTimer(reopenSeconds);

        return null;
    }

    @Override
    public void checkValidEntry(@NotNull String entry) {
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        if (DOUBLE_PATTERN.matcher(entry).matches()) {
            double entryValue = Double.parseDouble(entry);
            if ((min == null || entryValue >= min) && (max == null || entryValue <= max)) {
                value.setValue(entryValue);
                updateDescription();
            } else {
                MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR,
                        MgCommandLangKey.COMMAND_ERROR_OUTOFBOUNDS,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MIN.getKey(), String.valueOf(min)),
                        Placeholder.unparsed(MinigamePlaceHolderKey.MAX.getKey(), String.valueOf(max)));
            }
        } else if (entry.equals("INFINITE")) {
            double entryValue = Double.POSITIVE_INFINITY;
            value.setValue(entryValue);
            updateDescription();
        } else {
            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR,
                    MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry));
        }
    }

}
