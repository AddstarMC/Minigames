package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GlobalLoadoutCommand extends ACommand {
    private final MinigameManager mdata = Minigames.getPlugin().getMinigameManager();

    @Override
    public @NotNull String getName() {
        return "globalloadout";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"gloadout"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_GLOBALLOADOUT_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_GLOBALLOADOUT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.globalloadout";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        MinigamePlayer player = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
        Menu globalLoadoutMenu = new Menu(6, MgMenuLangKey.MENU_GLOBALLOADOUT_NAME, player);
        List<MenuItem> menuItems = new ArrayList<>();

        for (PlayerLoadout globalLoadout : mdata.getGlobalLoadouts()) {
            Material displayMaterial = Material.WHITE_STAINED_GLASS_PANE;
            if (!globalLoadout.getItemSlots().isEmpty()) {
                displayMaterial = globalLoadout.getItem((Integer) globalLoadout.getItemSlots().toArray()[0]).getType();
            }
            menuItems.add(new MenuItemDisplayLoadout(displayMaterial, globalLoadout.getDisplayName(),
                    MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK), globalLoadout));
        }
        globalLoadoutMenu.addItem(new MenuItemLoadoutAdd(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_LOADOUT_ADD_NAME,
                mdata.getGlobalLoadoutMap()), 53);
        globalLoadoutMenu.addItems(menuItems);

        globalLoadoutMenu.displayMenu(player);
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @Nullable [] args) {
        return null;
    }
}
