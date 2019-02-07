package au.com.mineauz.minigamesregions.menuitems;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;
import au.com.mineauz.minigamesregions.conditions.Conditions;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MenuItemConditionAdd extends MenuItem{
    
    private RegionExecutor rexec;
    private NodeExecutor nexec;

    public MenuItemConditionAdd(String name, Material displayItem, RegionExecutor exec) {
        super(name, displayItem);
        this.rexec = exec;
    }

    public MenuItemConditionAdd(String name, Material displayItem, NodeExecutor exec) {
        super(name, displayItem);
        this.nexec = exec;
    }
    
    @Override
    public ItemStack onClick(){
        Menu m = new Menu(6, "Conditions", getContainer().getViewer());
        m.setPreviousPage(getContainer());
        Map<String, Menu> cats = new HashMap<>();
        List<String> cons = new ArrayList<>(Conditions.getAllConditionNames());
        Collections.sort(cons);
        for(String con : cons){
            if((Conditions.getConditionByName(con).useInNodes() && nexec != null) ||
                    (Conditions.getConditionByName(con).useInRegions() && rexec != null)){
                String catname = Conditions.getConditionByName(con).getCategory();
                if(catname == null)
                    catname = "misc conditions";
                catname.toLowerCase();
                Menu cat = null;
                if(!cats.containsKey(catname)){
                    cat = new Menu(6, MinigameUtils.capitalize(catname), getContainer().getViewer());
                    cats.put(catname, cat);
                    m.addItem(new MenuItemPage(MinigameUtils.capitalize(catname), Material.CHEST, cat));
                    cat.addItem(new MenuItemPage("Back",MenuUtility.getBackMaterial(), m), cat.getSize() - 9);
                }
                else
                    cat = cats.get(catname);
                MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(con), Material.PAPER);
                final String fcon = con;
                c.setClick(object -> {
                    ConditionInterface condition = Conditions.getConditionByName(fcon);
                    if(rexec != null){
                        rexec.addCondition(condition);
                        getContainer().addItem(new MenuItemCondition(MinigameUtils.capitalize(fcon), Material.PAPER, rexec, condition));
                    }
                    else{
                        nexec.addCondition(condition);
                        getContainer().addItem(new MenuItemCondition(MinigameUtils.capitalize(fcon), Material.PAPER, nexec, condition));
                    }
                    getContainer().displayMenu(getContainer().getViewer());
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
