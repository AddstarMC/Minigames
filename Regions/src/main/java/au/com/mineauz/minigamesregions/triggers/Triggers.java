package au.com.mineauz.minigamesregions.triggers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Triggers { //todo deleted
    private static final Map<String, Trigger> triggers = new HashMap<>();

    //this maps from legacy to updated trigger names
    private static final Map<String, String> renamedTriggers = new HashMap<>();

    static {
        addTrigger(new BlockBreakTrigger());
        addTrigger(new BlockPlaceTrigger());
        addTrigger(new DeathTrigger());
        addTrigger(new PlayerKillTrigger());
        addTrigger(new PlayerKilledTrigger());
        addTrigger(new EnterTrigger());
        addTrigger(new MoveInRegionTrigger());
        addTrigger(new GameEndPhaseTrigger());
        addTrigger(new GameEndedTrigger());
        addTrigger(new GameJoinTrigger());
        addTrigger(new GameQuitTrigger());
        addTrigger(new GameStartTrigger());
        addTrigger(new InteractTrigger());
        addTrigger(new LeaveTrigger());
        addTrigger(new RemoteTrigger());
        addTrigger(new RespawnTrigger());
        addTrigger(new TickTrigger());
        addTrigger(new MinigameTimerTrigger());
        addTrigger(new ItemPickupTrigger());
        addTrigger(new ItemDropTrigger());
        addTrigger(new RandomTrigger());
        addTrigger(new PlayerDamageTrigger());
        addTrigger(new PlayerFoodChangeTrigger());
        addTrigger(new PlayerXPChangeTrigger());
        addTrigger(new PlayerTakeFlagTrigger());
        addTrigger(new PlayerDropFlagTrigger());
        addTrigger(new LeftClickBlockTrigger());
        addTrigger(new RightClickBlockTrigger());
        addTrigger(new StartGlideTrigger());
        addTrigger(new StopGlideTrigger());
        addTrigger(new GameTickTrigger());
        addTrigger(new TimedRemoteTrigger());
    }

    static {
        renamedTriggers.put("GAME_END", "GAME_ENDPHASE");
    }

    public static void addTrigger(Trigger trigger) {
        if (triggers.containsKey(trigger.getName()))
            throw new InvalidTriggerException("A trigger already exists by that name!");
        else
            triggers.put(trigger.getName(), trigger);
    }

    /**
     * gets a trigger by its name and translates legacy names if needed
     */
    public static Trigger getTrigger(String trigger) {
        trigger = trigger.toUpperCase();
        String renamedTrigger = renamedTriggers.get(trigger);

        if (renamedTrigger != null) {
            return triggers.get(renamedTrigger.toUpperCase());
        } else {
            return triggers.get(trigger);
        }
    }

    public static List<String> getAllTriggers() {
        return new ArrayList<>(triggers.keySet());
    }

    public static List<String> getAllNodeTriggers() {
        List<String> nt = new ArrayList<>();
        for (Trigger t : triggers.values()) {
            if (t.useInNodes())
                nt.add(t.getName());
        }
        return nt;
    }

    public static List<String> getAllRegionTriggers() {
        List<String> rt = new ArrayList<>();
        for (Trigger t : triggers.values()) {
            if (t.useInRegions())
                rt.add(t.getName());
        }
        return rt;
    }
}
