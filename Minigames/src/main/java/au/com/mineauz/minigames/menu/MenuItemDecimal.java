package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDecimal extends MenuItem {

    protected Callback<Double> value;
    protected DecimalFormat form = new DecimalFormat("#.##");
    private double lowerInc;
    private double upperInc;
    private Double min;
    private Double max;

    public MenuItemDecimal(String name, Material displayItem, Callback<Double> value,
                           double lowerInc, double upperInc, Double min, Double max) {
        super(name, displayItem);
        this.value = value;
        this.lowerInc = lowerInc;
        this.upperInc = upperInc;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public MenuItemDecimal(String name, List<String> description, Material displayItem, Callback<Double> value,
                           double lowerInc, double upperInc, Double min, Double max) {
        super(name, description, displayItem);
        this.value = value;
        this.lowerInc = lowerInc;
        this.upperInc = upperInc;
        this.min = min;
        this.max = max;
        updateDescription();
    }

    public void setFormat(DecimalFormat format) {
        form = format;
    }

    public void updateDescription() {
        List<String> description = null;
        if (getDescription() != null) {
            description = getDescription();
            String desc = ChatColor.stripColor(getDescription().get(0));

            if (desc.matches("-?[0-9]+(.[0-9]+)?"))
                description.set(0, ChatColor.GREEN.toString() + form.format(value.getValue()));
            else if (value.getValue().isInfinite()) {
                description.add(0, ChatColor.GREEN.toString() + "INFINITE");
            } else {
                description.add(0, ChatColor.GREEN.toString() + form.format(value.getValue()));
            }
        } else {
            description = new ArrayList<>();
            if (value.getValue().isInfinite()) {
                description.add(0, ChatColor.GREEN.toString() + "INFINITE");
            } else {
                description.add(0, ChatColor.GREEN.toString() + form.format(value.getValue()));
            }
        }

        setDescription(description);
    }

    @Override
    public ItemStack onClick() {
        if (max == null || value.getValue() < max)
            value.setValue(Double.valueOf(form.format(value.getValue() + lowerInc)));
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onRightClick() {
        if (min == null || value.getValue() > min)
            value.setValue(Double.valueOf(form.format(value.getValue() - lowerInc)));
        if (min != null && value.getValue() < min)
            value.setValue(min);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onShiftClick() {
        if (max == null || value.getValue() < max)
            value.setValue(Double.valueOf(form.format(value.getValue() + upperInc)));
        if (max != null && value.getValue() > max)
            value.setValue(max);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onShiftRightClick() {
        if (min == null || value.getValue() > min)
            value.setValue(Double.valueOf(form.format(value.getValue() - upperInc)));
        if (min != null && value.getValue() < min)
            value.setValue(min);
        updateDescription();
        return getItem();
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendMessage("Enter decimal value into chat for " + getName() + ", the menu will automatically reopen in 15s if nothing is entered.", MinigameMessageType.INFO);
        String min = "N/A";
        String max = "N/A";
        if (this.min != null) {
            min = this.min.toString();
        }
        if (this.max != null) {
            max = this.max.toString();
        }
        ply.setManualEntry(this);
        ply.sendInfoMessage("Min: " + min + ", Max: " + max);
        getContainer().startReopenTimer(15);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.matches("-?[0-9]+(.[0-9]+)?")) {
            double entryValue = Double.parseDouble(entry);
            if ((min == null || entryValue >= min) && (max == null || entryValue <= max)) {
                value.setValue(entryValue);
                updateDescription();

                getContainer().cancelReopenTimer();
                getContainer().displayMenu(getContainer().getViewer());
                return;
            }
        }
        if (entry.equals("INFINITE")) {
            double entryValue = Double.POSITIVE_INFINITY;
            value.setValue(entryValue);
            updateDescription();
            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
            return;
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        getContainer().getViewer().sendMessage("Invalid value entry!", MinigameMessageType.ERROR);
    }

}
