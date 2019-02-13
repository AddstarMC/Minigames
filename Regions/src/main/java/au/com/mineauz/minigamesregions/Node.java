package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import au.com.mineauz.minigames.script.ScriptWrapper;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Node implements ScriptObject {
    
    private String name;
    private Location loc;
    private List<NodeExecutor> executors = new ArrayList<>();
    private boolean enabled = true;
    
    public Node(String name, Location loc){
        this.name = name;
        this.loc = loc;
    }
    
    public String getName(){
        return name;
    }
    
    public Location getLocation(){
        return loc.clone();
    }
    
    public void setLocation(Location loc) {
        this.loc = loc.clone();
    }
    
    public int addExecutor(Trigger trigger){
        executors.add(new NodeExecutor(trigger));
        return executors.size();
    }
    
    public int addExecutor(NodeExecutor exec){
        executors.add(exec);
        return executors.size();
    }
    
    public List<NodeExecutor> getExecutors(){
        return executors;
    }
    
    public void removeExecutor(int id){
        if(executors.size() <= id){
            executors.remove(id - 1);
        }
    }
    
    public void removeExecutor(NodeExecutor executor){
        executors.remove(executor);
    }
    
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    public boolean getEnabled(){
        return enabled;
    }
    
    public void execute(Trigger trigger, MinigamePlayer player){
        if(player != null && player.getMinigame() != null && player.getMinigame().isSpectator(player)) return;
        if(player == null || player.getMinigame() == null)return;
        List<NodeExecutor> toExecute = new ArrayList<>();
        for(NodeExecutor exec : executors){
            if(exec.getTrigger() == trigger){
                if(checkConditions(exec, player) && exec.canBeTriggered(player))
                    toExecute.add(exec);
            }
        }
        for(NodeExecutor exec : toExecute){
            execute(exec, player);
        }
    }
    
    public boolean checkConditions(NodeExecutor exec, MinigamePlayer player){
        for(ConditionInterface con : exec.getConditions()){
            boolean c = con.checkNodeCondition(player, this);
            if(con.isInverted())
                c = !c;
            if(!c){
                return false;
            }
        }
        return true;
    }
    
    public void execute(NodeExecutor exec, MinigamePlayer player){
        for(ActionInterface act : exec.getActions()){
            if(!enabled && !act.getName().equalsIgnoreCase("SET_ENABLED")) continue;
            act.executeNodeAction(player, this);
            if(!exec.isTriggerPerPlayer())
                exec.addPublicTrigger();
            else
                exec.addPlayerTrigger(player);
        }
    }
    
    @Override
    public ScriptReference get(String name) {
        if (name.equalsIgnoreCase("name")) {
            return ScriptValue.of(name);
        } else if (name.equalsIgnoreCase("pos")) {
            return ScriptWrapper.wrap(loc);
        } else if (name.equalsIgnoreCase("block")) {
            return ScriptWrapper.wrap(loc.getBlock());
        }
        
        return null;
    }
    
    @Override
    public Set<String> getKeys() {
        return ImmutableSet.of("name", "pos", "block");
    }
    
    @Override
    public String getAsString() {
        return name;
    }
}
