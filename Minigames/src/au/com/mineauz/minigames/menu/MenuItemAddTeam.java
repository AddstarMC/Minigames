package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
		tm = minigame.getModule(TeamsModule.class);
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		beginManualEntry(player, "Enter the color of the team you wish to add. All colors available below:", 30);
		List<String> teams = new ArrayList<String>();
		for(TeamColor col : TeamColor.values())
			teams.add(col.getColor() + MinigameUtils.capitalize(col.toString().replace("_", " ")));
		player.sendMessage(MinigameUtils.listToString(teams));
	}

	@Override
	public void checkValidEntry(MinigamePlayer player, String entry) {
		entry = entry.toUpperCase().replace(" ", "_");
		if(TeamColor.matchColor(entry) != null){
			TeamColor col = TeamColor.matchColor(entry);
			if(!tm.hasTeam(col)){
				tm.addTeam(col);
				Team t = tm.getTeam(col);
				
				getContainer().addItem(new MenuItemTeam(t.getChatColor() + t.getDisplayName(), t));
			}
			else{
				player.sendMessage(ChatColor.RED + "A team already exists using that color!");
			}
			
//			List<String> teams = new ArrayList<String>(tm.getTeams().size() + 1);
//			for(Team t : tm.getTeams()){
//				teams.add(MinigameUtils.capitalize(t.getColor().toString().replace("_", " ")));
//			}
//			teams.add("None");
			//getContainer().setItem(new MenuItemList("Default Winning Team", Material.PAPER, tm.getDefaultWinnerCallback(), teams), 0, 0);
			getContainer().refresh();
			return;
		}
		
		player.sendMessage("There is no team color by the name of " + entry.toLowerCase().replace("_", " "), "error");
	}

}
