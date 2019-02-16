package au.com.mineauz.minigamesregions.conditions;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BlockDataFlag;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MatchBlockCondition extends ConditionInterface {
    
    private BlockDataFlag type = new BlockDataFlag(Material.STONE.createBlockData(), "type");
    private BooleanFlag useBlockData = new BooleanFlag(false, "usedur");
    @Override
    public String getName() {
        return "MATCH_BLOCK";
    }
    
    @Override
    public String getCategory(){
        return "World Conditions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        if (useBlockData.getFlag()) {
            out.put("Type", type.getFlag().getMaterial() + " with full data)");
        } else {
            out.put("Type", type.getFlag());
        }
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
        return check(node.getLocation());
    }
    
    private boolean check(Location location){
        Block block = location.getBlock();
        return block.getType() == type.getFlag().getMaterial() &&
                (!useBlockData.getFlag() ||
                        block.getBlockData() == type.getFlag());
    }

    @Override
    public void saveArguments(FileConfiguration config, String path) {
        type.saveValue(path, config);
        useBlockData.saveValue(path, config);
        saveInvert(config, path);
    }

    @Override
    public void loadArguments(FileConfiguration config, String path) {
        type.loadValue(path, config);
        useBlockData.loadValue(path, config);
        loadInvert(config, path);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu prev) {
        Menu m = new Menu(3, "Match Block", player);
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), prev), m.getSize() - 9);
        final MenuItemCustom c = new MenuItemCustom("Auto Set Block",
                MinigameUtils.stringToList("Click here with a;block you wish to;match to."), Material.ITEM_FRAME);
        m.addItem(c, m.getSize() - 1);
        final MinigamePlayer ply = m.getViewer();
        
        final MenuItemBlockData btype = new MenuItemBlockData("Block Type", Material.STONE, new Callback<BlockData>() {

            @Override
            public void setValue(BlockData value) {
                    type.setFlag(value);
            }

            @Override
            public BlockData getValue() {
                return type.getFlag();
            }
        });
        m.addItem(btype);
        final MenuItemBoolean busedur = (MenuItemBoolean) useBlockData.getMenuItem("Use Data Values", Material.ENDER_PEARL);
        m.addItem(busedur);
        c.setClickItem(object -> {
            ItemStack i = (ItemStack) object;
            try {
                type.setFlag(i.getType().createBlockData());
                useBlockData.setFlag(true);
            } catch (IllegalArgumentException e) {
                c.getContainer().getViewer().sendMessage("That item is not a block",MinigameMessageType.ERROR);
            }
            useBlockData.setFlag(true);
            busedur.updateDescription();
            btype.update();
            return c.getItem();
        });
        addInvertMenuItem(m);
        m.displayMenu(player);
        return true;
    }

}
