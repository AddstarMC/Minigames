package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class FallingBlockAction extends AbstractAction {

    @Override
    public String getName() {
        return "FALLING_BLOCK";
    }

    @Override
    public String getCategory() {
        return "World Actions";
    }
    
    @Override
    public void describe(Map<String, Object> out) {
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
        for(int y = region.getFirstPoint().getBlockY();
                y <= region.getSecondPoint().getBlockY();
                y++){
            temp.setY(y);
            for(int x = region.getFirstPoint().getBlockX();
                    x <= region.getSecondPoint().getBlockX();
                    x++){
                temp.setX(x);
                for(int z = region.getFirstPoint().getBlockZ();
                        z <= region.getSecondPoint().getBlockZ();
                        z++){
                    temp.setZ(z);
                    if(temp.getBlock().getType() != Material.AIR){
                        temp.getWorld().spawnFallingBlock(temp, temp.getBlock().getBlockData());
                        temp.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void executeNodeAction(MinigamePlayer player,
            Node node) {
        debug(player,node);
        if(node.getLocation().getBlock().getType() != Material.AIR){
            node.getLocation().getWorld().spawnFallingBlock(node.getLocation(),
                    node.getLocation().getBlock().getBlockData());
            node.getLocation().getBlock().setType(Material.AIR);
        }
    }

    @Override
    public void saveArguments(FileConfiguration config,
            String path) {
    }

    @Override
    public void loadArguments(FileConfiguration config,
            String path) {
    }

    @Override
    public boolean displayMenu(MinigamePlayer player, Menu previous) {
        return false;
    }

}
