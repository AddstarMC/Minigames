package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemTime;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigames.properties.types.StringProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class ApplyPotionAction extends ActionInterface {
	
	private final StringProperty type = new StringProperty("SPEED", "type"); // TODO: Change this to enum property on PotionEffectType
	private final IntegerProperty dur = new IntegerProperty(60, "duration");
	private final IntegerProperty amp = new IntegerProperty(1, "amplifier");
	
	public ApplyPotionAction() {
		properties.addProperty(type);
		properties.addProperty(dur);
		properties.addProperty(amp);
	}

	@Override
	public String getName() {
		return "APPLY_POTION";
	}

	@Override
	public String getCategory() {
		return "Player Actions";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		PotionEffect effect = new PotionEffect(PotionEffectType.getByName(type.getValue()), dur.getValue() * 20, amp.getValue() - 1);
		player.getPlayer().addPotionEffect(effect);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Apply Potion");
		List<String> pots = new ArrayList<String>(PotionEffectType.values().length);
		for(PotionEffectType type : PotionEffectType.values())
			pots.add(MinigameUtils.capitalize(type.toString().replace("_", " ")));
		m.addItem(new MenuItemList("Potion Type", Material.POTION, type, pots));
		m.addItem(new MenuItemTime("Duration", Material.WATCH, dur, 0, 86400));
		m.addItem(new MenuItemInteger("Level", Material.STONE, amp, 0, 100));
		m.displayMenu(player);
		return true;
	}

}
