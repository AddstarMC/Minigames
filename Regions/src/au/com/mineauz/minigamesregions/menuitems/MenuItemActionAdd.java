package au.com.mineauz.minigamesregions.menuitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemSubMenu;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.RegionExecutor;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.actions.Actions;

public class MenuItemActionAdd extends MenuItem{
	
	private RegionExecutor rexec;
	private NodeExecutor nexec;

	public MenuItemActionAdd(String name, Material displayItem, RegionExecutor exec) {
		super(name, displayItem);
		this.rexec = exec;
	}
	
	public MenuItemActionAdd(String name, Material displayItem, NodeExecutor exec) {
		super(name, displayItem);
		this.nexec = exec;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		Menu m = new Menu(6, "Actions");
		Map<String, Menu> cats = new HashMap<String, Menu>();
		List<String> acts = new ArrayList<String>(Actions.getAllActionNames());
		Collections.sort(acts);
		for(String act : acts){
			if((Actions.getActionByName(act).useInNodes() && nexec != null) || (Actions.getActionByName(act).useInRegions() && rexec != null)){
				String catname = Actions.getActionByName(act).getCategory();
				if(catname == null)
					catname = "misc actions";
				catname.toLowerCase();
				Menu cat = null;
				if(!cats.containsKey(catname)){
					cat = new Menu(6, MinigameUtils.capitalize(catname));
					cats.put(catname, cat);
					m.addItem(new MenuItemSubMenu(MinigameUtils.capitalize(catname), Material.CHEST, cat));
				}
				else
					cat = cats.get(catname);
				MenuItemCustom c = new MenuItemCustom(MinigameUtils.capitalize(act), Material.PAPER);
				final String fact = act;
				c.setClick(new InteractionInterface() {
					
					@Override
					public Object interact(MinigamePlayer player, Object object) {
						ActionInterface action = Actions.getActionByName(fact);
						if(nexec == null){
							rexec.addAction(action);
							getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, rexec, action));
							getContainer().displayMenu(player);
						}
						else{
							nexec.addAction(action);
							getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, nexec, action));
							getContainer().displayMenu(player);
						}
						return null;
					}
				});
				cat.addItem(c);
			}
		}
		m.displayMenu(player);
		return null;
	}
}
