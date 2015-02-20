package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.PlayerLoadout;

public class MenuItemPotion extends MenuItem{
	
	private PotionEffect eff;
	private PlayerLoadout loadout;

	public MenuItemPotion(String name, Material displayItem, PotionEffect eff, PlayerLoadout loadout) {
		super(name, displayItem);
		this.eff = eff;
		this.loadout = loadout;
		updateDescription();
	}

	public MenuItemPotion(String name, String description, Material displayItem, PotionEffect eff, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.eff = eff;
		this.loadout = loadout;
		updateDescription();
	}
	
	public void updateDescription(){
		List<String> description = new ArrayList<String>(getDescription());
		if(!description.isEmpty()){
			if(description.size() >= 2){
				String desc = ChatColor.stripColor(description.get(0));
				
				if(desc.equals("Level: " + (eff.getAmplifier() + 1))){
					description.set(0, ChatColor.GREEN.toString() + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
					description.set(1, ChatColor.GREEN.toString() + "Duration: " + ChatColor.GRAY + eff.getDuration());
				}
				else{
					description.add(0, ChatColor.GREEN.toString() + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
					description.add(1, ChatColor.GREEN.toString() + "Duration: " + ChatColor.GRAY + eff.getDuration());
				}
			}
			else{
				description.add(0, ChatColor.GREEN.toString() + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
				description.add(1, ChatColor.GREEN.toString() + "Duration: " + ChatColor.GRAY + eff.getDuration());
			}
		}
		else{
			description = new ArrayList<String>();
			description.add(0, ChatColor.GREEN.toString() + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
			description.add(1, ChatColor.GREEN.toString() + "Duration: " + ChatColor.GRAY + eff.getDuration());
		}
		
		setDescription(description);
	}
	
	@Override
	public void onShiftRightClick(MinigamePlayer player){
		loadout.removePotionEffect(eff);
		remove();
	}
	
	public PotionEffect getEffect(){
		return eff;
	}
}
