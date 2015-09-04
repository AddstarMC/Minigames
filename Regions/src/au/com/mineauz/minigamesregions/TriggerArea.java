package au.com.mineauz.minigamesregions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public abstract class TriggerArea {
	private final String name;
	private final Property<Boolean> enabled;
	
	private final ListMultimap<Trigger, TriggerExecutor> executors;
	
	public TriggerArea(String name) {
		this.name = name;
		
		enabled = Properties.create(true);
		//executors = Lists.newArrayList();
		executors = ArrayListMultimap.create();
	}
	
	/**
	 * Gets the name of this area
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Checks if this area is enabled
	 * @return True if enabled
	 */
	public boolean isEnabled() {
		return enabled.getValue();
	}
	
	/**
	 * Changes whether this area is enabled
	 * @param enabled True to enable it
	 */
	public void setEnabled(boolean enabled) {
		this.enabled.setValue(enabled);
	}
	
	/**
	 * Gets the enabled property
	 * @return The property
	 */
	public Property<Boolean> enabled() {
		return enabled;
	}
	
	/**
	 * Adds an executor to this area
	 * @param trigger The type trigger for the executor
	 */
	public TriggerExecutor addExecutor(Trigger trigger) {
		Preconditions.checkNotNull(trigger);
		TriggerExecutor executor = new TriggerExecutor(trigger, this); 
		executors.put(trigger, executor);
		
		return executor;
	}
	
	/**
	 * Removes an executor from this area
	 * @param executor The executor to remove
	 */
	public void removeExecutor(TriggerExecutor executor) {
		Preconditions.checkNotNull(executor);
		executors.remove(executor.getTrigger(), executor);
	}
	
	/**
	 * Removes all executors for the specified trigger
	 * @param trigger The trigger of the executors
	 */
	public void removeAllExecutors(Trigger trigger) {
		Preconditions.checkNotNull(trigger);
		executors.removeAll(trigger);
	}
	
	/**
	 * Gets all the executors for this area
	 * @return An unmodifiable collection containing all executors in this area
	 */
	public Collection<TriggerExecutor> getExecutors() {
		return Collections.unmodifiableCollection(executors.values());
	}
	
	/**
	 * Gets all the executors for this area that use the speicifed trigger
	 * @param trigger The trigger to match
	 * @return An unmodifiable list containing all executors that match
	 */
	public List<TriggerExecutor> getExecutors(Trigger trigger) {
		return Collections.unmodifiableList(executors.get(trigger));
	}
	
	/**
	 * Executes a trigger on this area
	 * @param trigger The trigger to execute
	 * @param player The player triggering it
	 */
	public void execute(Trigger trigger, MinigamePlayer player) {
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(trigger);
		
		if (!player.isInMinigame()) {
			return;
		}
		
		if (player.getMinigame().isSpectator(player)) {
			return;
		}
		
		List<TriggerExecutor> toExecute = Lists.newArrayList();
		
		for (TriggerExecutor executor : executors.get(trigger)) {
			if (executor.canTrigger(player, this)) {
				toExecute.add(executor);
			}
		}
		
		for (TriggerExecutor executor : toExecute) {
			executor.execute(player, this);
		}
	}
	
	public void save(ConfigurationSection section) {
		ConfigurationSection executorsSection = section.createSection("executors");
		
		// Save executors
		int index = 0;
		for (TriggerExecutor executor : executors.values()) {
			ConfigurationSection executorSection = executorsSection.createSection(String.valueOf(index));
			executorSection.set("trigger", executor.getTrigger().getName());
			executor.save(executorSection);
			
			++index;
		}
	}
	
	public void load(ConfigurationSection section) {
		ConfigurationSection executorsSection = section.getConfigurationSection("executors");
		
		// Load executors
		executors.clear();
		for (String key : executorsSection.getKeys(false)) {
			ConfigurationSection executorSection = executorsSection.getConfigurationSection(key);
			
			TriggerExecutor executor = addExecutor(Triggers.getTrigger(executorSection.getString("trigger")));
			executor.load(executorSection);
		}
	}
}
