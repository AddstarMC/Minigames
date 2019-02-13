package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionModule;

public class MenuItemNode extends MenuItem{
    
    private Node node;
    private RegionModule rmod;

    public MenuItemNode(String name, Material displayItem, Node node, RegionModule rmod) {
        super(name, displayItem);
        this.node = node;
        this.rmod = rmod;
    }

    public MenuItemNode(String name, List<String> description, Material displayItem, Node node, RegionModule rmod) {
        super(name, description, displayItem);
        this.node = node;
        this.rmod = rmod;
    }
    
    @Override
    public ItemStack onClick(){
        Menu m = createMenu(getContainer().getViewer(), getContainer(), node);
        m.displayMenu(getContainer().getViewer());
        return null;
    }
    
    @Override
    public ItemStack onRightClick(){
        rmod.removeNode(node.getName());
        getContainer().removeItem(getSlot());
        return null;
    }
    
    public static Menu createMenu(MinigamePlayer viewer, Menu previousPage, Node node){
        Menu m = new Menu(3, "Node: " + node.getName(), viewer);
        m.setPreviousPage(previousPage);
        List<MenuItem> items = new ArrayList<>();
        for(NodeExecutor ex : node.getExecutors()){
            items.add(new MenuItemNodeExecutor(node, ex));
        }
        if(previousPage != null){
            m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), previousPage), m.getSize() - 9);
        }
        m.addItem(new MenuItemNodeExecutorAdd("Add Executor", MenuUtility.getCreateMaterial(), node), m.getSize() - 1);
        m.addItems(items);
        
        return m;
    }

}
