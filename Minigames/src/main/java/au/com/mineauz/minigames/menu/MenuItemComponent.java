package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemComponent extends MenuItem {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    protected final Callback<Component> component;
    private boolean allowNull = false;

    public MenuItemComponent(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull Callback<Component> component) {
        super(displayMat, langKey);
        this.component = component;
        updateDescription();
    }

    public MenuItemComponent(@Nullable Material displayMat, @Nullable Component name, @NotNull Callback<Component> component) {
        super(displayMat, name);
        this.component = component;
        updateDescription();
    }

    public MenuItemComponent(@Nullable Material displayMat, @Nullable Component name,
                             @Nullable List<@NotNull Component> description, @NotNull Callback<Component> component) {
        super(displayMat, name, description);
        this.component = component;
        updateDescription();
    }

    public void setAllowNull(boolean allow) {
        allowNull = allow;
    }

    public void updateDescription() {
        List<Component> description;
        Component setting = component.getValue();
        if (setting == null)
            setting = "Not Set";
        if (setting.length() > 20) {
            setting = setting.substring(0, 17) + "...";
        }

        if (getDescription() != null) {
            description = getDescription();
            Component desc = getDescription().get(0);

            if (desc.startsWith(ChatColor.GREEN.toString())) {
                description.set(0, ChatColor.GREEN + setting);
            } else {
                description.add(0, ChatColor.GREEN + setting);
            }
        } else {
            description = new ArrayList<>();
            description.add(ChatColor.GREEN + setting);
        }

        setDescription(description);
    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_STRING_ENTERCHAT,
                Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), getName()));
        if (allowNull) {
            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_STRING_ALLOWNULL,
                    Placeholder.component(MinigamePlaceHolderKey.TYPE.getKey(), getName()));
        }
        mgPlayer.setManualEntry(this);
        getContainer().startReopenTimer(20);

        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        if (entry.equals("null") && allowNull) {
            component.setValue(null);
        } else {
            component.setValue(miniMessage.deserialize(entry));
        }

        updateDescription();
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
    }
}
