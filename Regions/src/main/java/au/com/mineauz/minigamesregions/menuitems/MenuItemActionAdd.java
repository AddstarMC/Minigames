package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.mineauz.minigames.menu.MenuUtility;
import au.com.mineauz.minigamesregions.executors.BaseExecutor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.actions.Actions;

public class MenuItemActionAdd extends MenuItem{
    
    private BaseExecutor exec;

    public MenuItemActionAdd(String name, Material displayItem, BaseExecutor exec) {
        super(name, displayItem);
        this.exec = exec;
    }

    
    @Override
    public ItemStack onClick(){
        Menu m = new Menu(6, "Actions", getContainer().getViewer());
        m.setPreviousPage(getContainer());
        Map<String, Menu> cats = new HashMap<>();
        List<String> acts = new ArrayList<>(Actions.getAllActionNames());
        Collections.sort(acts);
        for(String act : acts){
            if((Actions.getActionByName(act).useInNodes() && exec != null) || (Actions.getActionByName(act).useInRegions() && exec != null)){
                String catname = Actions.getActionByName(act).getCategory();
                if(catname == null) catname = "misc actions";
                catname = catname.toLowerCase();
                Menu cat;
                if(!cats.containsKey(catname)){
                    cat = new Menu(6, MinigameUtils.capitalize(catname), getContainer().getViewer());
                    cats.put(catname, cat);
                    m.addItem(new MenuItemPage(MinigameUtils.capitalize(catname), Material.CHEST, cat));
                    cat.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), m), cat.getSize() - 9);
                }
                else
                    cat = cats.get(catname);
                MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(act), Material.PAPER);
                final String fact = act;
                c.setClick(object -> {
                    ActionInterface action = Actions.getActionByName(fact);
                    if(exec == null){
                        exec.addAction(action);
                        getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, exec, action));
                        getContainer().displayMenu(getContainer().getViewer());
                    }
                    else{
                        exec.addAction(action);
                        getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, exec, action));
                        getContainer().displayMenu(getContainer().getViewer());
                    }
                    return null;
                });
                cat.addItem(c);
            }
        }
        m.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), getContainer()), m.getSize() - 9);
        m.displayMenu(getContainer().getViewer());
        return null;
    }
}
