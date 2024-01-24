package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PulseRedstoneAction extends AbstractAction {

    private final IntegerFlag time = new IntegerFlag(1, "time");
    private final BooleanFlag torch = new BooleanFlag(false, "torch");

    @Override
    public @NotNull String getName() {
        return "PULSE_REDSTONE";
    }

    @Override
    public @NotNull String getCategory() {
        return "Block Actions";
    }

    @Override
    public void describe(@NotNull Map<@NotNull String, @NotNull Object> out) {
        out.put("Time", MinigameUtils.convertTime(time.getFlag(), true));
        out.put("Use Torch", torch.getFlag());
    }

    @Override
    public boolean useInRegions() {
        return false;
    }

    @Override
    public boolean useInNodes() {
        return true;
    }

    @Override
    public void executeRegionAction(MinigamePlayer player,
                                    @NotNull Region region) {
        debug(player, region);
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
                                  @NotNull Node node) {
        debug(player, node);
        BlockData bdata = Material.REDSTONE_BLOCK.createBlockData();
        if (torch.getFlag()) {
            bdata = Material.REDSTONE_TORCH.createBlockData();
            if (bdata instanceof Lightable)
                ((Lightable) bdata).setLit(true);
        }
        final BlockState last = node.getLocation().getBlock().getState();
        node.getLocation().getBlock().setBlockData(bdata);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.getPlugin(), () -> last.update(true), 20L * time.getFlag());
    }

    @Override
    public void saveArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        time.saveValue(path, config);
        torch.saveValue(path, config);
    }

    @Override
    public void loadArguments(@NotNull FileConfiguration config,
                              @NotNull String path) {
        time.loadValue(path, config);
        torch.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Redstone Pulse", player);
        m.addItem(new MenuItemPage("Back", MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(time.getMenuItem("Pulse Time", Material.CLOCK));
        m.addItem(torch.getMenuItem("Use Redstone Torch", Material.REDSTONE_BLOCK));
        m.displayMenu(player);
        return true;
    }

}
