package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemInteger extends MenuItem {
    private final Callback<Integer> value;
    private final Integer min;
    private final Integer max;

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

    public MenuItemInteger(@Nullable Material displayMat, @Nullable Component name, @Nullable List<Component> description,
                           @NotNull Callback<Integer> value, @Nullable Integer min, @Nullable Integer max) {
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

            if (desc.matches("-?[0-9]+")) {
                description.set(0, ChatColor.GREEN.toString() + value.getValue());
            } else {
                description.add(0, ChatColor.GREEN.toString() + value.getValue());
            }
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
        mgPlayer.sendMessage("Enter number value into chat for " + getName() + ", the menu will automatically reopen in 10s if nothing is entered.", MinigameMessageType.INFO);
        String min = "N/A";
        String max = "N/A";
        if (this.min != null) {
            min = this.min.toString();
        }
        if (this.max != null) {
            max = this.max.toString();
        }
        mgPlayer.setManualEntry(this);
        mgPlayer.sendInfoMessage("Min: " + min + ", Max: " + max);
        getContainer().startReopenTimer(10);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.matches("-?[0-9]+")) {
            int entryValue = Integer.parseInt(entry);
            if ((min == null || entryValue >= min) && (max == null || entryValue <= max)) {
                value.setValue(entryValue);
                updateDescription();

                getContainer().cancelReopenTimer();
                getContainer().displayMenu(getContainer().getViewer());
                return;
            }
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        getContainer().getViewer().sendMessage("Invalid value entry!", MinigameMessageType.ERROR);
    }

    Callback<Integer> getValue() {
        return value;
    }

    Integer getMin() {
        return min;
    }

    Integer getMax() {
        return max;
    }
}
