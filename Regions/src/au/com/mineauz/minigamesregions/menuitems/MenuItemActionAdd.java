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
import au.com.mineauz.minigamesregions.TriggerExecutor;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.actions.Actions;

public class MenuItemActionAdd extends MenuItem{
	
	private TriggerExecutor rexec;

	public MenuItemActionAdd(String name, Material displayItem, TriggerExecutor exec) {
		super(name, displayItem);
		this.rexec = exec;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		Menu m = new Menu(6, "Actions");
		Map<String, Menu> cats = new HashMap<String, Menu>();
		// TODO: Dont iterate through the string keys
		List<String> acts = new ArrayList<String>(Actions.getAllActionNames());
		Collections.sort(acts);
		for(String act : acts){
			final ActionInterface action = Actions.getActionByName(act);
			
			if (action.canUseIn(rexec.getOwner())) {
				String catname = action.getCategory();
				if(catname == null)
					catname = "misc actions";
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
				MenuItem c = new MenuItem(MinigameUtils.capitalize(act), Material.PAPER);
				final String fact = act;
				c.setClickHandler(new IMenuItemClick() {
					@Override
					public void onClick(MenuItem menuItem, MinigamePlayer player) {
						rexec.getActions().add(action);
						getContainer().addItem(new MenuItemAction(MinigameUtils.capitalize(fact), Material.PAPER, rexec, action));
						
						player.showPreviousMenu(2);
					}
				});
				cat.addItem(c);
			}
		}
		m.displayMenu(player);
	}
}
