package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItemComponent extends MenuItem {
    protected Callback<String> stringCallback; // todo I'm not happy having String as callback here. I need it since the SerializeableBridge can't use Components, but this doesn't feel right.
    private boolean allowNull = false;

    public MenuItemComponent(String name, Material displayItem, Callback<String> stringCallback) {
        super(name, displayItem);
        this.stringCallback = stringCallback;
        updateDescription();
    }

    public MenuItemComponent(String name, List<String> description, Material displayItem, Callback<String> stringCallback) {
        super(name, description, displayItem);
        this.stringCallback = stringCallback;
        updateDescription();
    }

    public void setAllowNull(boolean allow) {
        allowNull = allow;
    }

    public void updateDescription() {
        List<Component> description;
        String setting = stringCallback.getValue();
        if (setting == null) {
            setting = "<red>Not Set</red>";

        }

        Component settingComp = MiniMessage.miniMessage().deserialize(setting);
        //todo find a way to effective limit the length without messing with styles
        //if (setting.length() > 20) {
        //    setting = setting.substring(0, 17) + "...";
        //}

        description = getDescriptionComp();
        if (description != null) {
            //todo find a way to not overwrite other descriptions
            //Component desc = description.get(0);

            // if (desc.color() == NamedTextColor.GREEN) {
            description.set(0, settingComp);
            //  } else {
            //     description.add(0, setting.color(NamedTextColor.GREEN));
            // }
        } else {
            description = new ArrayList<>();
            description.add(settingComp);
        }

        setDescriptionComp(description);
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
