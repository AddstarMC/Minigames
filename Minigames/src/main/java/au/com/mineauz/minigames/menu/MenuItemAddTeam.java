package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class MenuItemAddTeam extends MenuItem{
	
	private TeamsModule tm;

	public MenuItemAddTeam(String name, Minigame minigame) {
		super(name, Material.ITEM_FRAME);
		tm = TeamsModule.getMinigameModule(minigame);
	}
	
	@Override
	public ItemStack onClick(){
		MinigamePlayer ply = getContainer().getViewer();
		ply.setNoClose(true);
		ply.getPlayer().closeInventory();
		ply.sendMessage("Enter the color of the team you wish to add. All colors available below:", null);
		List<String> teams = new ArrayList<>();
		for(TeamColor col : TeamColor.values())
			teams.add(col.getColor() + MinigameUtils.capitalize(col.toString().replace("_", " ")));
		ply.sendMessage(MinigameUtils.listToString(teams));
		ply.setManualEntry(this);

		getContainer().startReopenTimer(30);
		return null;
	}

	
	@Override
	public void checkValidEntry(String entry) {
		entry = entry.toUpperCase().replace(" ", "_");
		if(TeamColor.matchColor(entry) != null){
			TeamColor col = TeamColor.matchColor(entry);
			if(!tm.hasTeam(col)){
				tm.addTeam(col);
				Team t = tm.getTeam(col);
				
				getContainer().addItem(new MenuItemTeam(t.getChatColor() + t.getDisplayName(), t));
			}
			else{
				getContainer().getViewer().sendMessage(ChatColor.RED + "A team already exists using that color!");
			}
			
			List<String> teams = new ArrayList<>(tm.getTeams().size() + 1);
			for(Team t : tm.getTeams()){
				teams.add(MinigameUtils.capitalize(t.getColor().toString().replace("_", " ")));
			}
			teams.add("None");
			getContainer().removeItem(0);
			getContainer().addItem(new MenuItemList("Default Winning Team", Material.PAPER, tm.getDefaultWinnerCallback(), teams), 0);
			
			getContainer().cancelReopenTimer();
			getContainer().displayMenu(getContainer().getViewer());
			return;
		}
		
		getContainer().cancelReopenTimer();
		getContainer().displayMenu(getContainer().getViewer());
		
		getContainer().getViewer().sendMessage("There is no team color by the name of " + entry.toLowerCase().replace("_", " "), "error");
	}

}
