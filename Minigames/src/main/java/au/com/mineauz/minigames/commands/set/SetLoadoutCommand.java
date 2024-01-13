package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
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

public class SetLoadoutCommand implements ICommand {

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
        Menu loadouts = new Menu(6, getName(), player);
        List<MenuItem> mi = new ArrayList<>();
        LoadoutModule mod = LoadoutModule.getMinigameModule(minigame);

        List<String> des = new ArrayList<>();
        des.add("Shift + Right Click to Delete");

        Material item;

        for (String ld : mod.getLoadouts()) {
            item = Material.WHITE_STAINED_GLASS_PANE;
            if (!mod.getLoadout(ld).getItems().isEmpty()) {
                item = mod.getLoadout(ld).getItem((Integer) mod.getLoadout(ld).getItems().toArray()[0]).getType();
            }
            MenuItemDisplayLoadout mil = new MenuItemDisplayLoadout(ld, des, item, mod.getLoadout(ld), minigame);
            mil.setAllowDelete(mod.getLoadout(ld).isDeleteable());
            mi.add(mil);
        }
        loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, mod.getLoadoutMap(), minigame), 53);
        loadouts.addItems(mi);

        loadouts.displayMenu(player);
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
