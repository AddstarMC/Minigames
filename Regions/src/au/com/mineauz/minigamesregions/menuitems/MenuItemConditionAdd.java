package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;
import au.com.mineauz.minigamesregions.conditions.Conditions;

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
	public void onClick(MinigamePlayer player){
		Menu m = new Menu(6, "Conditions");
		Map<String, Menu> cats = new HashMap<String, Menu>();
		List<String> cons = new ArrayList<String>(Conditions.getAllConditionNames());
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
					cat = new Menu(6, MinigameUtils.capitalize(catname));
					cat.setTrackHistory(false);
					cats.put(catname, cat);
					m.addItem(new MenuItemSubMenu(MinigameUtils.capitalize(catname), Material.CHEST, cat));
				}
				else
					cat = cats.get(catname);
				MenuItem c = new MenuItem(MinigameUtils.capitalize(con), Material.PAPER);
				final String fcon = con;
				c.setClickHandler(new IMenuItemClick() {
					@Override
					public void onClick(MenuItem menuItem, MinigamePlayer player) {
						ConditionInterface condition = Conditions.getConditionByName(fcon);
						if(rexec != null){
							rexec.addCondition(condition);
							getContainer().addItem(new MenuItemCondition(MinigameUtils.capitalize(fcon), Material.PAPER, rexec, condition));
						}
						else{
							nexec.addCondition(condition);
							getContainer().addItem(new MenuItemCondition(MinigameUtils.capitalize(fcon), Material.PAPER, nexec, condition));
						}
						
						player.showPreviousMenu(2);
					}
				});
				cat.addItem(c);
			}
		}
		m.displayMenu(player);
	}

}
