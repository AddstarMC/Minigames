package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDisplayLoadout;
import au.com.mineauz.minigames.menu.MenuItemLoadoutAdd;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetLoadoutCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "loadout";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_LOADOUT_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_LOADOUT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.loadout";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {

        MinigamePlayer player = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
        Menu loadoutMenu = new Menu(6, Component.text(getName()), player);
        List<MenuItem> mi = new ArrayList<>();
        LoadoutModule mod = LoadoutModule.getMinigameModule(minigame);

        Material material;
        for (PlayerLoadout ld : mod.getLoadouts()) {
            material = Material.WHITE_STAINED_GLASS_PANE;
            if (!ld.getItemSlots().isEmpty()) {
                material = ld.getItem((Integer) ld.getItemSlots().toArray()[0]).getType();
            }

            MenuItemDisplayLoadout mil = new MenuItemDisplayLoadout(material, ld.getDisplayName(),
                    MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK), ld, minigame);

            mil.setAllowDelete(ld.isDeleteable());
            mi.add(mil);
        }
        loadoutMenu.addItem(new MenuItemLoadoutAdd(Material.ITEM_FRAME, MgMenuLangKey.MENU_LOADOUT_ADD_NAME,
                mod.getLoadoutMap(), minigame), 53);
        loadoutMenu.addItems(mi);

        loadoutMenu.displayMenu(player);
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
