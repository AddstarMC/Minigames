package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.ComponentFlag;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.ResourcePack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ResourcePackModule extends MinigameModule { //todo rework to work with multiple ressource packs
    private final BooleanFlag enabled = new BooleanFlag(false, "resourcePackEnabled");
    private final ComponentFlag resourcePackDisplayName = new ComponentFlag(Component.empty(), "resourcePackName");
    private final BooleanFlag forced = new BooleanFlag(false, "forceResourcePack");
    private String resourcePackName = PlainTextComponentSerializer.plainText().serialize(resourcePackDisplayName.getFlag());

    public ResourcePackModule(@NotNull Minigame mgm, @NotNull String name) {
        super(mgm, name);
    }

    public static @Nullable ResourcePackModule getMinigameModule(@NotNull Minigame mgm) {
        return ((ResourcePackModule) mgm.getModule(MgModules.RESOURCEPACK.getName()));
    }

    public boolean isEnabled() {
        return enabled.getFlag();
    }

    public void setEnabled(Boolean bool) {
        enabled.setFlag(bool);
    }

    public boolean isForced() {
        return forced.getFlag();
    }

    public void setResourcePackname(Component name) {
        resourcePackDisplayName.setFlag(name);
    }

    public @NotNull Component getResourcePackDisplayName() {
        return resourcePackDisplayName.getFlag();
    }

    public @NotNull String getResourcePackName() {
        return resourcePackName;
    }

    @Override
    public Map<String, Flag<?>> getConfigFlags() {
        Map<String, Flag<?>> map = new HashMap<>();
        addConfigFlag(enabled, map);
        addConfigFlag(resourcePackDisplayName, map);
        return map;
    }

    private void addConfigFlag(Flag<?> flag, Map<String, Flag<?>> flags) {
        flags.put(flag.getName(), flag);
    }

    @Override
    public boolean useSeparateConfig() {
        return false;
    }

    @Override
    public void save(FileConfiguration config) {
    }

    @Override
    public void load(FileConfiguration config) {

    }

    @Override
    public void addEditMenuOptions(@NotNull Menu previousMenu) {
        Menu menu = new Menu(3, MgMenuLangKey.MENU_RESOURCEPACK_OPTIONS_NAME, previousMenu.getViewer());
        menu.setPreviousPage(previousMenu);
        menu.addItem(enabled.getMenuItem(Material.MAP, MgMenuLangKey.MENU_RESOURCEPACK_OPTIONS_ENABLE_NAME));
        MenuItemComponent item = new MenuItemComponent(Material.PAPER, MgMenuLangKey.MENU_RESOURCEPACK_OPTIONS_DISPLAYNAME_NAME,
                new Callback<>() {
            @Override
            public Component getValue() {
                return resourcePackDisplayName.getFlag();
            }

            @Override
            public void setValue(Component value) {
                resourcePackDisplayName.setFlag(value);
                resourcePackName = PlainTextComponentSerializer.plainText().serialize(value);
            }
        }) {
            @Override
            public void checkValidEntry(String entry) {
                if (entry.isEmpty()) {
                    super.checkValidEntry(entry);
                    return;
                }
                ResourcePack pack = Minigames.getPlugin().getResourceManager().getResourcePack(entry);
                if (pack == null) {
                    getContainer().cancelReopenTimer();
                    getContainer().displayMenu(getContainer().getViewer());
                    MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR,
                            MinigameLangKey.MINIGAME_RESSOURCEPACK_NORESSOURCEPACK,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), entry));
                } else {
                    super.checkValidEntry(entry);
                }
            }
        };
        menu.addItem(item);
        menu.addItem(forced.getMenuItem(Material.SKELETON_SKULL, MgMenuLangKey.MENU_RESOURCEPACK_OPTIONS_FORCE_NAME));
        MenuItemPage previousMenuItem = new MenuItemPage(Material.MAP, MgMenuLangKey.MENU_RESOURCEPACK_OPTIONS_NAME, menu);
        menu.addItem(new MenuItemBack(previousMenu), menu.getSize() - 9);
        previousMenu.addItem(previousMenuItem);
    }

    @Override
    public boolean displayMechanicSettings(Menu previous) {
        return false;
    }
}
