package com.pauldavdesign.mineauz.minigames.menu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.minigame.Team;
import com.pauldavdesign.mineauz.minigames.minigame.TeamColor;
import com.pauldavdesign.mineauz.minigames.minigame.modules.TeamsModule;

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
	public ItemStack onClick(){
		Menu m = new Menu(3, getName(), getContainer().getViewer());
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
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, getContainer()), m.getSize() - 9);
		m.displayMenu(getContainer().getViewer());
		return null;
	}
	
	@Override
	public ItemStack onRightClick(){
		TeamsModule.getMinigameModule(team.getMinigame()).removeTeam(team.getMinigame(), team.getColor());
		getContainer().removeItem(getSlot());
		return null;
	}
}
