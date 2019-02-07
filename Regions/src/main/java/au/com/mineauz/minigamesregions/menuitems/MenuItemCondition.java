package au.com.mineauz.minigamesregions.menuitems;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;

public class MenuItemCondition extends MenuItem{
    
    private RegionExecutor rexec;
    private NodeExecutor nexec;
    private ConditionInterface con;

    public MenuItemCondition(String name, Material displayItem, RegionExecutor exec, ConditionInterface con) {
        super(name, displayItem);
        this.rexec = exec;
        this.con = con;
        
        updateDescription();
    }

    public MenuItemCondition(String name, Material displayItem, NodeExecutor exec, ConditionInterface con) {
        super(name, displayItem);
        this.nexec = exec;
        this.con = con;
        
        updateDescription();
    }
    
    @Override
    public void update() {
        updateDescription();
    }
    
    private void updateDescription() {
        Map<String, Object> out = Maps.newHashMap();
        con.describe(out);
        
        if (out.isEmpty()) {
            return;
        }
        
        int lineLimit = 35;
        
        // Convert the description
        List<String> description = Lists.newArrayList();
        for (Entry<String, Object> entry : out.entrySet()) {
            Object value = entry.getValue();
            String line = ChatColor.GRAY + entry.getKey() + ": ";
            
            // Translate the value
            if (value instanceof Boolean) {
                if (((Boolean)value)) {
                    value = ChatColor.GREEN + "True";
                } else {
                    value = ChatColor.RED + "False";
                }
            } else if (value == null) {
                value = ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Not Set";
            } else {
                value = ChatColor.GREEN + String.valueOf(value);
            }
            
            line += value;
            
            // Trim
            String lastColor = "";
            while (line.length() > lineLimit) {
                String part = lastColor + line.substring(0, lineLimit + 1);
                line = line.substring(lineLimit + 1);
                description.add(part);
                lastColor = ChatColor.getLastColors(part);
            }
            
            if (!line.isEmpty()) {
                description.add(lastColor + line);
            }
        }
        
        setDescription(description);
    }
    
    @Override
    public ItemStack onClick(){
        if(rexec != null){
            if(con.displayMenu(getContainer().getViewer(), getContainer()))
                return null;
        }
        else{
            if(con.displayMenu(getContainer().getViewer(), getContainer()))
                return null;
        }
        return getItem();
    }
    
    @Override
    public ItemStack onRightClick(){
        if(rexec != null)
            rexec.removeCondition(con);
        else
            nexec.removeCondition(con);
        getContainer().removeItem(getSlot());
        return null;
    }

}
