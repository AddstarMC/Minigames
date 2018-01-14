package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigamesregions.executors.BaseExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.triggers.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 22/12/2017.
 */
public abstract class BaseObject implements ScriptObject {

    protected List<BaseExecutor> executors = new ArrayList<>();

    public List<BaseExecutor> getExecutors(){
        return executors;
    }

    public void removeExecutor(int id){
        if(executors.size() <= id){
            executors.remove(id - 1);
        }
    }

    public int addExecutor(Trigger trigger){
        executors.add(new RegionExecutor(trigger));
        return executors.size();
    }

    public int addExecutor(BaseExecutor exec){
        executors.add(exec);
        return executors.size();
    }

    public void removeExecutor(BaseExecutor executor){
        if(executors.contains(executor)){
            executors.remove(executor);
        }
    }

    public abstract boolean checkConditions(BaseExecutor executor, MinigamePlayer player);

    public abstract void execute(BaseExecutor exec, MinigamePlayer player);
    public abstract void execute(Trigger trigger, MinigamePlayer player);
}
