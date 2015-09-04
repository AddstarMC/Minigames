package au.com.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.properties.Properties;
import au.com.mineauz.minigames.properties.Property;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;
import au.com.mineauz.minigamesregions.triggers.Trigger;

// TODO: Node and Region Executors need to have one base class (or not be different at all?)
public class NodeExecutor {
	
	private Trigger trigger;
	private List<ConditionInterface> conditions = new ArrayList<ConditionInterface>();
	private List<ActionInterface> actions = new ArrayList<ActionInterface>();
	private Property<Boolean> triggerPerPlayer = Properties.create(false);
	private Property<Integer> triggerCount = Properties.create(0);
	private Map<String, Integer> triggers = new HashMap<String, Integer>();
	
	public NodeExecutor(Trigger trigger){
		this.trigger = trigger;
	}
	
	public Trigger getTrigger(){
		return trigger;
	}
	
	public List<ConditionInterface> getConditions(){
		return conditions;
	}
	
	public void addCondition(ConditionInterface condition){
		conditions.add(condition);
	}
	
	public void removeCondition(ConditionInterface condition){
		conditions.remove(condition);
	}
	
	public List<ActionInterface> getActions(){
		return actions;
	}
	
	public void addAction(ActionInterface action){
		actions.add(action);
	}
	
	public void removeAction(ActionInterface action){
		actions.remove(action);
	}
	
	public int getTriggerCount() {
		return triggerCount.getValue();
	}
	
	public void setTriggerCount(int count) {
		triggerCount.setValue(count);
	}
	
	public Property<Integer> triggerCount() {
		return triggerCount;
	}
	
	public boolean isTriggerPerPlayer() {
		return triggerPerPlayer.getValue();
	}
	
	public void setTriggerPerPlayer(boolean perPlayer) {
		triggerPerPlayer.setValue(perPlayer);
	}
	
	public Property<Boolean> triggerPerPlayer() {
		return triggerPerPlayer;
	}
	
	public void addPublicTrigger(){
		if(!triggers.containsKey("public"))
			triggers.put("public", 0);
		triggers.put("public", triggers.get("public") + 1);
	}
	
	public void addPlayerTrigger(MinigamePlayer player){
		String uuid = player.getUUID().toString();
		if(!triggers.containsKey(uuid))
			triggers.put(uuid, 0);
		triggers.put(uuid, triggers.get(uuid) + 1);
	}
	
	public boolean canBeTriggered(MinigamePlayer player){
		if(getTriggerCount() != 0){
			if(!triggerPerPlayer.getValue()){
				if(triggers.get("public") != null && 
						triggers.get("public") >= getTriggerCount()){
					return false;
				}
			}
			else{
				if(triggers.get(player.getUUID().toString()) != null && 
						triggers.get(player.getUUID().toString()) >= getTriggerCount()){
					return false;
				}
			}
		}
		return true;
	}
	
	public void clearTriggers(){
		triggers.clear();
	}
	
	public void removeTrigger(MinigamePlayer player){
		triggers.remove(player.getUUID().toString());
	}
}
