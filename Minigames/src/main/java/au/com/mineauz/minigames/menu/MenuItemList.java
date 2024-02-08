package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
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

public class MenuItemList<T> extends MenuItem {
    private final Callback<T> value;
    private final List<T> options;

    public MenuItemList(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull Callback<@NotNull T> value,
                        @NotNull List<@NotNull T> options) {
        super(displayMat, langKey);
        this.value = value;
        this.options = options;
        updateDescription();
    }

    public MenuItemList(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<@NotNull T> value,
                        @NotNull List<@NotNull T> options) {
        this(name, null, displayMat, value, options);
    }

    public MenuItemList(@Nullable Material displayMat, @Nullable Component name,
                        @Nullable List<@NotNull Component> description, @NotNull Callback<@NotNull T> value,
                        @NotNull List<@NotNull T> options) {
        super(displayMat, name, description);
        this.value = value;
        this.options = options;
        updateDescription();
    }

    public void updateDescription() {
        List<Component> description;
        int pos = options.indexOf(value.getValue());
        int before = pos - 1;
        int after = pos + 1;
        if (before < 0)
            before = options.size() - 1;
        if (after == options.size())
            after = 0;

        if (getDescription() != null) {
            description = getDescription();
            if (getDescription().size() >= 3) {
                String desc = ChatColor.stripColor(getDescription().get(1));

                if (options.contains(desc)) {
                    description.set(0, ChatColor.GRAY + options.get(before));
                    description.set(1, ChatColor.GREEN + value.getValue());
                    description.set(2, ChatColor.GRAY + options.get(after));
                } else {
                    description.add(0, ChatColor.GRAY + options.get(before));
                    description.add(1, ChatColor.GREEN + value.getValue());
                    description.add(2, ChatColor.GRAY + options.get(after));
                }
            } else {
                description.add(0, ChatColor.GRAY + options.get(before));
                description.add(1, ChatColor.GREEN + value.getValue());
                description.add(2, ChatColor.GRAY + options.get(after));
            }
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GRAY + options.get(before));
            description.add(ChatColor.GREEN + value.getValue());
            description.add(ChatColor.GRAY + options.get(after));
        }

        setDescription(description);
    }

    @Override
    public ItemStack onClick() {
        int ind = options.lastIndexOf(value.getValue());
        ind++;
        if (ind == options.size())
            ind = 0;

        value.setValue(options.get(ind));
        updateDescription();

        return getDisplayItem();
    }

    @Override
    public ItemStack onRightClick() {
        int ind = options.lastIndexOf(value.getValue());
        ind--;
        if (ind == -1)
            ind = options.size() - 1;

        value.setValue(options.get(ind));
        updateDescription();

        return getDisplayItem();
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();

        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();
        mgPlayer.sendInfoMessage("Enter the name of the option into chat for " + getName() + ", the menu will automatically reopen in 10s if nothing is entered.");
        mgPlayer.setManualEntry(this);
        if (MinigameUtils.listToString(options).getBytes().length > 16000) {
            mgPlayer.sendInfoMessage("Unfortunately there are too many options to provide a list in game. Perhaps use the WIKI");
        } else {
            mgPlayer.sendInfoMessage("Possible Options: " + MinigameUtils.listToString(options));
        }
        getContainer().startReopenTimer(10);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        for (String opt : options) {
            if (opt.equalsIgnoreCase(entry)) {
                value.setValue(opt);
                updateDescription();

                getContainer().cancelReopenTimer();
                getContainer().displayMenu(getContainer().getViewer());
                return;
            }
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        getContainer().getViewer().sendMessage("Could not find matching value!", MinigameMessageType.ERROR);
    }
}
