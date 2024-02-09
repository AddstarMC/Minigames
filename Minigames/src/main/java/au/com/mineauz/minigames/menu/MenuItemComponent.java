package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MenuItemComponent extends MenuItem {
    protected final static String DESCRIPTION_VALUE_TOKEN = "COMPONENT_VALUE_DESCRIPTION";
    protected @NotNull Callback<Component> stringCallback;
    protected boolean allowNull = false;

    public MenuItemComponent(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<Component> stringCallback) {
        super(displayMat, name);
        this.stringCallback = stringCallback;
        updateDescription();
    }

    public MenuItemComponent(@Nullable Material displayMat, @Nullable Component name,
                             @Nullable List<@NotNull Component> description, @NotNull Callback<Component> stringCallback) {
        super(displayMat, name, description);
        this.stringCallback = stringCallback;
        updateDescription();
    }

    public void setAllowNull(boolean allow) {
        allowNull = allow;
    }

    public void updateDescription() {
        Component settingComp = stringCallback.getValue();
        if (settingComp == null) {
            settingComp = "<red>Not Set</red>";
        }

        // limit to a still readable size
        settingComp = MinigameUtils.limitIgnoreFormat(settingComp, 20);

        setDescriptionPartAtEnd(DESCRIPTION_VALUE_TOKEN, List.of(settingComp));
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendMessage("Enter mini message value into chat for " + getName() + ", the menu will automatically reopen in 20s if nothing is entered.", MinigameMessageType.INFO);
        if (allowNull) {
            ply.sendInfoMessage("Enter \"null\" to remove the string value");
        }
        ply.setManualEntry(this);
        getContainer().startReopenTimer(20);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.equals("null") && allowNull) {
            stringCallback.setValue(null);
        } else {
            stringCallback.setValue(entry);
        }

        updateDescription();
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }
}
