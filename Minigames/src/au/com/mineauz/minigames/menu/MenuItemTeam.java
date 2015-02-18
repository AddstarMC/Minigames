package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.NameTagVisibility;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class MenuItemTeam extends MenuItem{
	
	private Team team;

	public MenuItemTeam(String name, Team team) {
		super(name, Material.LEATHER_CHESTPLATE);
		
		setDescription(MinigameUtils.stringToList(ChatColor.DARK_PURPLE + "(Right Click to delete)"));
		this.team = team;
		setTeamIcon();
	}

	public MenuItemTeam(String name, List<String> description, Team team) {
		super(name, description, Material.LEATHER_CHESTPLATE);
		
		getDescription().add(0, ChatColor.DARK_PURPLE + "(Right Click to delete)");
		this.team = team;
		setTeamIcon();
	}
	
	private void setTeamIcon(){
		LeatherArmorMeta m = (LeatherArmorMeta) getItem().getItemMeta();
		if(team.getColor() == TeamColor.RED )
			m.setColor(Color.RED);
		else if(team.getColor() == TeamColor.BLUE)
			m.setColor(Color.BLUE);
		else if(team.getColor() == TeamColor.GREEN)
			m.setColor(Color.GREEN);
		else if(team.getColor() == TeamColor.YELLOW)
			m.setColor(Color.YELLOW);
		else if(team.getColor() == TeamColor.BLACK)
			m.setColor(Color.BLACK);
		else if(team.getColor() == TeamColor.WHITE)
			m.setColor(Color.WHITE);
		else if(team.getColor() == TeamColor.GRAY)
			m.setColor(Color.GRAY);
		else if(team.getColor() == TeamColor.PURPLE)
			m.setColor(Color.PURPLE);
		else if(team.getColor() == TeamColor.DARK_BLUE)
			m.setColor(Color.BLUE);
		else if(team.getColor() == TeamColor.DARK_GREEN)
			m.setColor(Color.GREEN);
		else if(team.getColor() == TeamColor.DARK_PURPLE)
			m.setColor(Color.PURPLE);
		else if(team.getColor() == TeamColor.DARK_RED)
			m.setColor(Color.RED);
		getItem().setItemMeta(m);
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		Menu m = new Menu(3, getName());
		m.addItem(new MenuItemString("Display Name", Material.NAME_TAG, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				team.setDisplayName(value);
			}
			
			@Override
			public String getValue() {
				return team.getDisplayName();
			}
		}));
		m.addItem(new MenuItemInteger("Max Players", Material.STONE, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				team.setMaxPlayers(value);
			}
			
			@Override
			public Integer getValue() {
				return team.getMaxPlayers();
			}
		}, 0, null));
		for(Flag<?> flag : team.getFlags()){
			if(flag.getName().equals("assignMsg")){
				m.addItem(flag.getMenuItem("Join Team Message", Material.PAPER, 
						MinigameUtils.stringToList("Message sent to player;when they join;the team.;Use %s for team name")));
			}
			else if(flag.getName().equals("gameAssignMsg")){
				m.addItem(flag.getMenuItem("Join Team Broadcast Message", Material.PAPER, 
						MinigameUtils.stringToList("Message sent to all players;when someone joins;a team.;Use %s for team/player name")));
			}
			else if(flag.getName().equals("autobalanceMsg")){
				m.addItem(flag.getMenuItem("Autobalance Message", Material.PAPER, 
						MinigameUtils.stringToList("Message sent to player;when they are;autobalanced.;Use %s for team name")));
			}
			else if(flag.getName().equals("gameAutobalanceMsg")){
				m.addItem(flag.getMenuItem("Autobalance Broadcast Message", Material.PAPER, 
						MinigameUtils.stringToList("Message sent to all players;when someone is;autobalanced.;Use %s for team/player name")));
			}
		}
		List<String> ntvo = new ArrayList<String>();
		for(NameTagVisibility v : NameTagVisibility.values()){
			ntvo.add(v.toString());
		}
		m.addItem(new MenuItemList("NameTag Visibility", Material.NAME_TAG, team.getNameTagVisibilityCallback(), ntvo));
		
		m.displayMenu(player);
		return null;
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player){
		TeamsModule.getMinigameModule(team.getMinigame()).removeTeam(team.getColor());
		remove();
		return null;
	}
}
