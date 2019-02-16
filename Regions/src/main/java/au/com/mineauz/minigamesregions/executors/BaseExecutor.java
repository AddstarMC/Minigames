package au.com.mineauz.minigamesregions.executors;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;
import au.com.mineauz.minigamesregions.triggers.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created for the AddstarMC.
 * Created by Narimm on 27/04/2017.
 */
public class BaseExecutor {

    private final Trigger trigger;
    private final List<ConditionInterface> conditions = new ArrayList<>();
    private final List<ActionInterface> actions = new ArrayList<>();
    private boolean triggerPerPlayer = false;
    private int triggerCount = 0;
    private final Map<String, Integer> triggers = new HashMap<>();

    public BaseExecutor(Trigger trigger){
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

    public int getTriggerCount(){
        return triggerCount;
    }

    public void setTriggerCount(int count){
        triggerCount = count;
    }

    public Callback<Integer> getTriggerCountCallback(){
        return new Callback<Integer>() {

            @Override
            public void setValue(Integer value) {
                setTriggerCount(value);
            }

            @Override
            public Integer getValue() {
                return getTriggerCount();
            }
        };
    }

    public boolean isTriggerPerPlayer(){
        return triggerPerPlayer;
    }

    public void setTriggerPerPlayer(boolean perPlayer){
        triggerPerPlayer = perPlayer;
    }

    public Callback<Boolean> getIsTriggerPerPlayerCallback(){
        return new Callback<Boolean>() {

            @Override
            public void setValue(Boolean value) {
                setTriggerPerPlayer(value);
            }

            @Override
            public Boolean getValue() {
                return isTriggerPerPlayer();
            }
        };
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
        if(triggerCount != 0){
            if(!triggerPerPlayer){
                return triggers.get("public") == null ||
                        triggers.get("public") < triggerCount;
            }
            else{
                return triggers.get(player.getUUID().toString()) == null ||
                        triggers.get(player.getUUID().toString()) < triggerCount;
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
