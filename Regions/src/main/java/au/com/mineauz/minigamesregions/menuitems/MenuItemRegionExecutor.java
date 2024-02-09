package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.actions.ActionRegistry;
import au.com.mineauz.minigamesregions.conditions.ConditionRegistry;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuItemRegionExecutor extends MenuItem {
    private final static String DESCRIPTION_TOKEN = "Executor_description";
    private final @NotNull Region region;
    private final @NotNull RegionExecutor ex;

    public MenuItemRegionExecutor(@NotNull Region region, @NotNull RegionExecutor ex) {
        super(Material.ENDER_PEARL, RegionMessageManager.getMessage(RegionLangKey.MENU_REGIONEXECUTOR_NAME));
        this.region = region;
        this.ex = ex;
        setDescriptionPartAtEnd(DESCRIPTION_TOKEN, List.of(
                RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_TRIGGER,
                        Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(), ex.getTrigger().getDisplayName())),
                RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_ACTION,
                        Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), String.valueOf(ex.getActions().size()))),
                MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK).
                        color(NamedTextColor.DARK_PURPLE),
                RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_EDIT)));
    }

    @Override
    public ItemStack onClick() {
        final MinigamePlayer fviewer = getContainer().getViewer();
        final Menu menu = new Menu(3, RegionMessageManager.getMessage(RegionLangKey.MENU_REGIONEXECUTOR_NAME), fviewer);

        MenuItemCustom ca = new MenuItemCustom(Material.CHEST,
                RegionMessageManager.getMessage(RegionLangKey.MENU_ACTIONS_NAME));
        ca.setClick(object -> {
            ActionRegistry.displayMenu(fviewer, ex, menu);
            return null;
        });
        menu.addItem(ca);

        MenuItemCustom c2 = new MenuItemCustom(Material.CHEST,
                RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITIONS_NAME));
        c2.setClick(object -> {
            ConditionRegistry.displayMenu(fviewer, ex, menu);
            return null;
        });
        menu.addItem(c2);

        menu.addItem(new MenuItemNewLine());
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            menu.addItem(new MenuItemInteger(Material.STONE,
                    RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_TRIGGERCOUNT_NAME),
                    RegionMessageManager.getMessageList(RegionLangKey.MENU_EXECUTOR_TRIGGERCOUNT_DESCRIPTION),
                    ex.getTriggerCountCallback(), 0, null));
        }
        if (ex.getTrigger().triggerOnPlayerAvailable()) {
            menu.addItem(new MenuItemBoolean(Material.PLAYER_HEAD,
                    RegionMessageManager.getMessage(RegionLangKey.MENU_EXECUTOR_PERPLAYER_NAME),
                    RegionMessageManager.getMessageList(RegionLangKey.MENU_EXECUTOR_PERPLAYER_DESCRIPTION),
                    ex.getIsTriggerPerPlayerCallback()));
        }
        menu.addItem(new MenuItemBack(getContainer()), menu.getSize() - 9);
        menu.displayMenu(fviewer);
        return null;
    }

    @Override
    public ItemStack onRightClick() {
        region.removeExecutor(ex);
        getContainer().removeItem(getSlot());
        return null;
    }
}
