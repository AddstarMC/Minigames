package au.com.mineauz.minigames.menu;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.NameTagVisibility;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class MenuItemTeam extends MenuItem{
	
	private Team team;

	public MenuItemTeam(String name, Team team) {
		super(name, "Right Click to delete", Material.LEATHER_CHESTPLATE);
		
		this.team = team;
		setTeamIcon();
	}

	public MenuItemTeam(String name, String description, Team team) {
		super(name, "Right Click to delete;" + description, Material.LEATHER_CHESTPLATE);
		
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
	public void onClick(MinigamePlayer player){
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
		}, 0, Integer.MAX_VALUE));
		for(Flag<?> flag : team.getFlags()){
			if(flag.getName().equals("assignMsg")){
				m.addItem(flag.getMenuItem("Join Team Message", "Message sent to player;when they join;the team.;Use %s for team name", Material.PAPER));
			}
			else if(flag.getName().equals("gameAssignMsg")){
				m.addItem(flag.getMenuItem("Join Team Broadcast Message", "Message sent to all players;when someone joins;a team.;Use %s for team/player name", Material.PAPER));
			}
			else if(flag.getName().equals("autobalanceMsg")){
				m.addItem(flag.getMenuItem("Autobalance Message", "Message sent to player;when they are;autobalanced.;Use %s for team name", Material.PAPER));
			}
			else if(flag.getName().equals("gameAutobalanceMsg")){
				m.addItem(flag.getMenuItem("Autobalance Broadcast Message", "Message sent to all players;when someone is;autobalanced.;Use %s for team/player name", Material.PAPER));
			}
		}
		m.addItem(new MenuItemEnum<NameTagVisibility>("NameTag Visibility", Material.NAME_TAG, team.getNameTagVisibilityCallback(), NameTagVisibility.class));
		
		m.displayMenu(player);
	}
	
	@Override
	public void onRightClick(MinigamePlayer player){
		team.getMinigame().getModule(TeamsModule.class).removeTeam(team.getColor());
		remove();
	}
}
