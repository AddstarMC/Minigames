package au.com.mineauz.minigamesregions.actions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.config.BlockDataFlag;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
//todo
public class SetBlockAction extends AbstractAction {
    
    private BlockDataFlag type = new BlockDataFlag(Material.STONE.createBlockData(), "type");
    private BooleanFlag useBlockData = new BooleanFlag(false, "usedur");//todo rename flag
    private IntegerFlag dur = new IntegerFlag(0, "dur");

    @Override
    public String getName() {
        return "SET_BLOCK";
    }

    @Override
    public String getCategory() {
        return "Block Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
        if (useBlockData.getFlag()) {
            out.put("Block", type.getFlag() + ":" + dur.getFlag());
        } else {
            out.put("Block", type.getFlag());
        }
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
    public void executeRegionAction(MinigamePlayer player,
            Region region) {
        debug(player,region);
        Location temp = region.getFirstPoint();
        for(int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++){
            temp.setY(y);
            for(int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++){
                temp.setX(x);
                for(int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++){
                    temp.setZ(z);
                    
                    BlockState bs = temp.getBlock().getState();
                    if(useBlockData.getFlag()){
                        bs.setBlockData(type.getFlag());
                    }else {
                        bs.setBlockData(type.getFlag().getMaterial().createBlockData());
                    }
                    bs.update(true);
                }
            }
        }
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
            Node node) {
        debug(player,node);
        BlockState bs = node.getLocation().getBlock().getState();
        if(useBlockData.getFlag()){
            bs.setBlockData(type.getFlag());
        }else {
            bs.setBlockData(type.getFlag().getMaterial().createBlockData());
        }
        bs.update(true);
    }

    @Override
    public void saveArguments(FileConfiguration config,
            String path) {
        type.saveValue(path, config);
    }

    @Override
    public void loadArguments(FileConfiguration config,
            String path) {
        type.loadValue(path, config);
        useBlockData.loadValue(path, config);
        dur.loadValue(path, config);
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        Menu m = new Menu(3, "Set Block", player);
        final MinigamePlayer fply = player;
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previous), m.getSize() - 9);
        m.addItem(new MenuItemBlockData("Type", Material.STONE, new Callback<BlockData>() {
            @Override
            public void setValue(BlockData value) {
                type.setFlag(value);
            }
            
            @Override
            public BlockData getValue() {
                return type.getFlag();
            }
        }));
        m.addItem(useBlockData.getMenuItem("Use Specific BlockData", Material.ENDER_PEARL));
        m.displayMenu(player);
        return true;
    }

}
