package au.com.mineauz.minigamesregions.triggers;

import au.com.mineauz.minigamesregions.Main;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TriggerRegistry {
    private static final List<Trigger> triggers = new ArrayList<>(); // sorted by order of inserting

    // add all build in triggers
    static {
        try {
            for (Trigger trigger : MgRegTrigger.values()) {
                addTrigger(trigger);
            }
        } catch (TriggerAlreadyRegisteredException e) {
            Main.getPlugin().getComponentLogger().error("", e);
        }
    }

    // we use a registry instead of just an enum to support triggers from companion plugins
    public static void addTrigger(@NotNull Trigger trigger) throws TriggerAlreadyRegisteredException {
        if (triggers.contains(trigger)) {
            throw new TriggerAlreadyRegisteredException("A trigger by name" + trigger.getName() + " already exists!");
        } else {
            triggers.add(trigger);
        }
    }

    @Contract(value = "null -> null", pure = true)
    public static @Nullable Trigger matchTrigger(@Nullable String triggerName) {
        if (triggerName != null) {
            for (Trigger trigger : triggers) {
                if (triggerName.equalsIgnoreCase(trigger.getName()) ||
                        (trigger instanceof MgRegTrigger mgRegTrigger && triggerName.equalsIgnoreCase(mgRegTrigger.getLegacyName()))) {
                    return trigger;
                }
            }
        }

        return null;
    }

    public static List<Trigger> getAllNodeTriggers() {
        return triggers.stream().filter(Trigger::useInNodes).toList();
    }

    public static List<Trigger> getAllRegionTriggers() {
        return triggers.stream().filter(Trigger::useInRegions).toList();
    }
}
