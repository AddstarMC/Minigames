package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemBoolean extends MenuItem {
    private final @NotNull Callback<@NotNull Boolean> toggle;

    public MenuItemBoolean(@NotNull LangKey langKey, @Nullable Material displayItem,
                           @NotNull Callback<@NotNull Boolean> toggle) {
        super(langKey, displayItem);
        this.toggle = toggle;
        updateDescription();
    }

    public MenuItemBoolean(@Nullable Component name, @Nullable Material displayItem,
                           @NotNull Callback<@NotNull Boolean> toggle) {
        super(name, displayItem);
        this.toggle = toggle;
        updateDescription();
    }

    public MenuItemBoolean(@Nullable Component name, @Nullable List<@NotNull Component> description,
                           @Nullable Material displayItem, @NotNull Callback<@NotNull Boolean> toggle) {
        super(name, description, displayItem);
        this.toggle = toggle;
        updateDescription();
    }

    public void updateDescription() {
        List<Component> description;
        String col;
        if (toggle.getValue()) {
            col = ChatColor.GREEN + "true";
        } else {
            col = ChatColor.RED + "false";
        }
        if (getDescription() != null) {
            description = getDescription();
            String desc = ChatColor.stripColor(getDescription().get(0));

            if (desc.matches("true|false"))
                description.set(0, col);
            else
                description.add(0, col);
        } else {
            description = new ArrayList<>();
            description.add(col);
        }

        setDescription(description);
    }

    @Override
    public ItemStack onClick() {
        if (toggle.getValue())
            toggle.setValue(false);
        else
            toggle.setValue(true);

        updateDescription();
        return getItem();
    }
}
