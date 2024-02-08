package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TakeItemAction extends AAction {
    private final StringFlag type = new StringFlag("STONE", "type");
    private final IntegerFlag count = new IntegerFlag(1, "count");

    protected TakeItemAction(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayname() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_ACTION_TAKEITEM_NAME);
    }

    @Override
    public @NotNull IActionCategory getCategory() {
        return RegionActionCategories.PLAYER;
    }

    @Override
    public void describe(Map<String, Object> out) {
        out.put("Item", type.getFlag() + ":all");
        out.put("Count", count);
    }

    @Override
    public boolean useInRegions() {
        return true;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeRegionAction(@Nullable MinigamePlayer mgPlayer, @NotNull Region region) {
        debug(mgPlayer, region);
        execute(mgPlayer);
    }

    @Override
    public void executeNodeAction(@Nullable MinigamePlayer mgPlayer, @NotNull Node node) {
        debug(mgPlayer, node);
        execute(mgPlayer);
    }

    private void execute(MinigamePlayer player) {
        Material mat = Material.getMaterial(type.getFlag());

        if (mat == null) {
            return;
        }
        ItemStack match = new ItemStack(mat, count.getFlag());
        ItemStack matched = null;
        boolean remove = false;
        int slot = 0;

        for (ItemStack i : player.getPlayer().getInventory().getContents()) {
            if (i != null && i.getType() == match.getType()) {
                if (match.getAmount() >= i.getAmount()) {
                    matched = i.clone();
                    remove = true;
                } else {
                    matched = i.clone();
                    matched.setAmount(matched.getAmount() - match.getAmount());
                }
                break;

            }
            slot++;
        }

        if (remove) {
            player.getPlayer().getInventory().removeItem(matched);
        } else {
            player.getPlayer().getInventory().getItem(slot).setAmount(matched.getAmount());
        }
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        type.saveValue(path, config);
        count.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        type.loadValue(path, config);
        count.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(final @NotNull MinigamePlayer mgPlayer, Menu previous) {
        Menu m = new Menu(3, getDisplayname(), mgPlayer);

        m.addItem(new MenuItemBack(previous), m.getSize() - 9);
        m.addItem(new MenuItemString(Material.STONE, "Type", new Callback<>() {

            @Override
            public String getValue() {
                return type.getFlag();
            }

            @Override
            public void setValue(String value) {
                if (Material.getMaterial(value.toUpperCase()) != null) {
                    type.setFlag(value.toUpperCase());
                } else {
                    MinigameMessageManager.sendMessage(mgPlayer, MinigameMessageType.ERROR, RegionMessageManager.getBundleKey(),
                            RegionLangKey.ERROR_INVALID_ITEMTYPE);
                }
            }
        }));
        m.addItem(count.getMenuItem(Material.STONE_SLAB, "Count", 1, 64));
        m.displayMenu(mgPlayer);
        return true;
    }

}
