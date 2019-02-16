package au.com.mineauz.minigames.minigame.reward;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardTypes {
    private static Map<String, Class<? extends RewardType>> types = new HashMap<>();

    static {
        addRewardType("ITEM", ItemReward.class);
        addRewardType("MONEY", MoneyReward.class);
        addRewardType("COMMAND", CommandReward.class);
    }

    public static void addRewardType(String name, Class<? extends RewardType> type) {
        if (types.containsKey(name.toUpperCase())) {
            throw new InvalidRewardTypeException("A reward type already exists by that name");
        } else {
            types.put(name.toUpperCase(), type);
        }
    }

    public static RewardType getRewardType(String name, Rewards rewards) {
        if (types.containsKey(name.toUpperCase())) {
            try {
                return types.get(name.toUpperCase()).getDeclaredConstructor(Rewards.class).newInstance(rewards);
            } catch (InstantiationException | SecurityException | NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<String> getAllRewardTypeNames() {
        return new ArrayList<>(types.keySet());
    }
}
