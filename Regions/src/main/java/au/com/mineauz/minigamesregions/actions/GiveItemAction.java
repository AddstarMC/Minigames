package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class GiveItemAction extends AbstractAction {

    private final StringFlag type = new StringFlag("STONE", "type");
    private final IntegerFlag count = new IntegerFlag(1, "count");
    private final StringFlag name = new StringFlag(null, "name");
    private final StringFlag lore = new StringFlag(null, "lore");

    @Override
    public @NotNull String getName() {
        return "GIVE_ITEM";
    }

    @Override
    public @NotNull String getCategory() {
        return "Player Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Item", count.getFlag() + "x " + type.getFlag());
        out.put("Display Name", name.getFlag());
        out.put("Lore", lore.getFlag());
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
    public void executeRegionAction(MinigamePlayer player, @NotNull Region region) {

        debug(player, region);
        execute(player);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player, @NotNull Node node) {
        debug(player, node);
        execute(player);
    }

    private void execute(MinigamePlayer player) {
        ItemStack item = new ItemStack(Material.matchMaterial(type.getFlag()), count.getFlag());
        ItemMeta meta = item.getItemMeta();
        if (name.getFlag() != null) {
            meta.setDisplayName(name.getFlag());
        }
        if (lore.getFlag() != null) {
            meta.setLore(List.of(lore.getFlag().split(";"))); //as the description states semicolons will be used for new lines
        }
        item.setItemMeta(meta);

        Map<Integer, ItemStack> unadded = player.getPlayer().getInventory().addItem(
                item);

        if (!unadded.isEmpty()) {
            for (ItemStack i : unadded.values()) {
                player.getLocation().getWorld().dropItem(player.getLocation(), i);
            }
        }
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config, @NotNull String path) {
        type.saveValue(path, config);
        count.saveValue(path, config);
        if (name.getFlag() != null)
            name.saveValue(path, config);
        if (lore.getFlag() != null)
            lore.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config, @NotNull String path) {
        type.loadValue(path, config);
        count.loadValue(path, config);
        if (config.contains(path + ".name"))
            name.loadValue(path, config);
        if (config.contains(path + ".lore"))
            lore.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(final MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Give Item", player);

        m.addItem(new MenuItemBack(previous), m.getSize() - 9);

        MenuItemString n = (MenuItemString) name.getMenuItem("Name", Material.NAME_TAG);
        n.setAllowNull(true);
        m.addItem(n);
        MenuItemString l = (MenuItemString) lore.getMenuItem("Lore", Material.PAPER,
                List.of("Separate with semicolons", "for new lines"));
        l.setAllowNull(true);
        m.addItem(l);

        m.addItem(new MenuItemString("Type", Material.STONE, new Callback<>() {

            @Override
            public String getValue() {
                return type.getFlag();
            }

            @Override
            public void setValue(String value) {
                if (Material.getMaterial(value.toUpperCase()) != null) {
                    type.setFlag(value.toUpperCase());
                } else
                    player.sendMessage("Invalid item type!", MinigameMessageType.ERROR);
            }
        }));
        m.addItem(count.getMenuItem("Count", Material.STONE_SLAB, 1, 64));
        m.displayMenu(player);
        return true;
    }

}
