package au.com.mineauz.minigamesregions.actions;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.properties.types.BooleanProperty;
import au.com.mineauz.minigames.properties.types.IntegerProperty;
import au.com.mineauz.minigamesregions.TriggerArea;
import au.com.mineauz.minigamesregions.TriggerExecutor;

public class TriggerRandomAction extends ActionInterface{
	
	private final IntegerProperty timesTriggered = new IntegerProperty(1, "timesTriggered");
	private final BooleanProperty randomPerTrigger = new BooleanProperty(false, "randomPerTrigger");
	
	public TriggerRandomAction() {
		properties.addProperty(timesTriggered);
		properties.addProperty(randomPerTrigger);
	}

	@Override
	public String getName() {
		return "TRIGGER_RANDOM";
	}

	@Override
	public String getCategory() {
		return "Region/Node Actions";
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
	public boolean requiresPlayer() {
		return false;
	}
	
	@Override
	public void executeAction(MinigamePlayer player, TriggerArea area) {
		List<TriggerExecutor> executors = Lists.newArrayList();
		for (TriggerExecutor executor : area.getExecutors()) {
			// TODO: Want to be able to get executors by trigger type
			if (executor.getTrigger().getName().equalsIgnoreCase("RANDOM")) {
				executors.add(executor);
			}
		}
		
		Collections.shuffle(executors);
		
		int triggerTimes = timesTriggered.getValue();
		for (int i = 0; i < triggerTimes; ++i) {
			if (randomPerTrigger.getValue()) {
				Collections.shuffle(executors);
			}
			
			TriggerExecutor executor = executors.get(i % executors.size());
			if (executor.canTrigger(player, area)) {
				executor.execute(player, area);
			}
		}
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Random");
		m.addItem(new MenuItemInteger("Times to Trigger Random", Material.COMMAND, timesTriggered, 1, Integer.MAX_VALUE));
		m.addItem(new MenuItemBoolean("Allow Same Executor", "Should there be a chance;that the same execeutor;can be triggered more?", Material.ENDER_PEARL, randomPerTrigger));
		m.displayMenu(player);
		return true;
	}

}
