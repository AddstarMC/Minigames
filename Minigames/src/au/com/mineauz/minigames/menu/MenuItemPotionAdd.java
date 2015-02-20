package au.com.mineauz.minigames.menu;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.PlayerLoadout;

public class MenuItemPotionAdd extends MenuItem{
	
	PlayerLoadout loadout;

	public MenuItemPotionAdd(String name, Material displayItem, PlayerLoadout loadout) {
		super(name, displayItem);
		this.loadout = loadout;
	}

	public MenuItemPotionAdd(String name, String description, Material displayItem, PlayerLoadout loadout) {
		super(name, description, displayItem);
		this.loadout = loadout;
	}
	
	@Override
	public void onClick(MinigamePlayer player){
		beginManualEntry(player, "Enter a potion using the syntax below into chat, the menu will automatically reopen in 30s if nothing is entered.", 30);
		player.sendMessage("PotionName, level, duration (duration can be \"inf\")");
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		String[] split = entry.split(", ");
		if(split.length == 3){
			String effect = split[0].toUpperCase();
			if(PotionEffectType.getByName(effect) != null){
				PotionEffectType eff = PotionEffectType.getByName(effect);
				if(split[1].matches("[0-9]+") && Integer.parseInt(split[1]) != 0){
					int level = Integer.parseInt(split[1]) - 1;
					if((split[2].matches("[0-9]+") && Integer.parseInt(split[2]) != 0) || split[2].equalsIgnoreCase("inf")){
						int dur = 0;
						if(split[2].equalsIgnoreCase("inf"))
							dur = 100000;
						else
							dur = Integer.parseInt(split[2]);
						
						if(dur > 100000){
							dur = 100000;
						}
						dur*=20;

						PotionEffect peff = new PotionEffect(eff, dur, level);
						MenuItem toRemove = null;
						
						for (MenuItem rawItem : getContainer()) {
							if (rawItem instanceof MenuItemPotion) {
								MenuItemPotion pot = (MenuItemPotion)rawItem;
								if(pot.getEffect().getType() == peff.getType()){
									toRemove = rawItem;
									break;
								}
							}
						}
						
						if (toRemove != null) {
							toRemove.onShiftRightClick(player);
						}
						
						getContainer().addItem(new MenuItemPotion(eff.getName().toLowerCase().replace("_", " "), "Shift + Right Click to Delete", Material.POTION, peff, loadout));
						getContainer().refresh();
					}
					else
						player.sendMessage(split[2] + " is not a valid duration! The time must be in seconds", "error");
				}
				else
					player.sendMessage(split[1] + " is not a valid level number!", "error");
			}
			else
				player.sendMessage(split[0] + " is not a valid potion name!", "error");
			return;
		}
		
		player.sendMessage("Invalid syntax entry! Make sure there is an comma and a space (\", \") between each item.", "error");
	}
}
