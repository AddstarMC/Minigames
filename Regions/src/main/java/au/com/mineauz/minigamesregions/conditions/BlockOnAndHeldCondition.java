package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This class allows a check if the first solid block under a player is equal
 * to the block that player holds in his hand.
 */
public class BlockOnAndHeldCondition extends ACondition {

    protected BlockOnAndHeldCondition(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return RegionMessageManager.getMessage(RegionLangKey.MENU_CONDITION_BLOCKONANDHELD_NAME);
    }

    @Override
    public @NotNull IConditionCategory getCategory() {
        return RegionConditionCategories.WORLD;
    }

    @Override
    public void describe(Map<String, Object> out) {

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
    public boolean checkRegionCondition(MinigamePlayer player, Region region) {
        return false;
    }

    @Override
    public boolean checkNodeCondition(MinigamePlayer player, Node node) {
        return check(player);
    }

    private boolean check(MinigamePlayer player) {
        if (player == null) return false;
        ItemStack heldItem = player.getPlayer().getInventory().getItemInMainHand();

        Location plyLoc = player.getPlayer().getLocation();
        int plyY = plyLoc.getBlockY();
        //In case that the player is in the air, this searches for the first solid block and checks if it equal
        while (plyY >= 0) {
            plyY -= 1;
            Block tempBlock = player.getPlayer().getWorld().getBlockAt(plyLoc.getBlockX(), plyY, plyLoc.getBlockZ());
            if (tempBlock.getType().equals(heldItem.getType())) {
                return true;
            } else if (!tempBlock.getType().equals(Material.AIR)) {
                return false;
            }

        }

        return false;
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, getDisplayName(), player);
        m.addItem(new MenuItemBack(prev), m.getSize() - 9);
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

    @Override
    public boolean onPlayerApplicable() {
        return false;
    }

}
