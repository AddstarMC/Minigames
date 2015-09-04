package au.com.mineauz.minigamesregions.actions;

import org.bukkit.Material;
import org.bukkit.Sound;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import au.com.mineauz.minigames.menu.MenuItemEnum;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.EnumProperty;
import au.com.mineauz.minigames.properties.types.FloatProperty;
import au.com.mineauz.minigamesregions.TriggerArea;

public class PlaySoundAction extends ActionInterface {
	
	private final EnumProperty<Sound> sound = new EnumProperty<Sound>(Sound.LEVEL_UP, "sound");
	private final BooleanProperty priv = new BooleanProperty(true, "private");
	private final FloatProperty vol = new FloatProperty(1f, "volume");
	private final FloatProperty pit = new FloatProperty(1f, "pitch");
	
	public PlaySoundAction() {
		properties.addProperty(sound);
		properties.addProperty(priv);
		properties.addProperty(vol);
		properties.addProperty(pit);
	}

	@Override
	public String getName() {
		return "PLAY_SOUND";
	}

	@Override
	public String getCategory() {
		return "World Actions";
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
		if(priv.getValue())
			player.getPlayer().playSound(player.getLocation(), 
					sound.getValue(), 
					vol.getValue(), 
					pit.getValue());
		else
			player.getPlayer().getWorld().playSound(player.getLocation(), 
					sound.getValue(), 
					vol.getValue(), 
					pit.getValue());
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Play Sound");
		m.addItem(new MenuItemEnum<Sound>("Sound", Material.NOTE_BLOCK, sound, Sound.class));
		m.addItem(new MenuItemBoolean("Private Playback", Material.ENDER_PEARL, priv));
		m.addItem(new MenuItemDecimal("Volume", Material.JUKEBOX, Properties.toDouble(vol), 0.1, 1d, 0.5, Double.MAX_VALUE));
		m.addItem(new MenuItemDecimal("Pitch", Material.EYE_OF_ENDER, Properties.toDouble(pit), 0.05, 0.1, 0d, 2d));
		m.displayMenu(player);
		return true;
	}

}
