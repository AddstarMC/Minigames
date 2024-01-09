package au.com.mineauz.minigames.minigame.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RewardTypes {
    private static final Map<String, RewardTypeFactory> types = new HashMap<>();

    public interface RewardTypeFactory {
        @NotNull RewardType makeNewType(@NotNull Rewards rewards);

        @NotNull String getName();
    }

    public enum MgRewardType implements RewardTypeFactory {
        COMMAND(CommandReward::new),
        ITEM(ItemReward::new),
        MONEY(MoneyReward::new);

        final @NotNull Function<@NotNull Rewards, ? extends @NotNull RewardType> init;

        MgRewardType(@NotNull Function<@NotNull Rewards, ? extends @NotNull RewardType> init) {
            this.init = init;
        }

        @Override
        public @NotNull RewardType makeNewType(@NotNull Rewards rewards) {
            return init.apply(rewards);
        }

        @Override
        public @NotNull String getName() {
            return toString();
        }
    }

    static {
        for (MgRewardType factory : MgRewardType.values()) {
            addRewardType(factory);
        }
    }

    public static void addRewardType(RewardTypeFactory factory) {
        if (types.containsKey(factory.getName())) {
            throw new InvalidRewardTypeException("A reward type already exists by that name");
        } else {
            types.put(factory.getName(), factory);
        }
    }

    public static @Nullable RewardType getRewardType(final @NotNull String name, @NotNull Rewards rewards) {
        if (types.containsKey(name.toUpperCase())) {
            return types.get(name.toUpperCase()).makeNewType(rewards);
        }
        return null;
    }

    public static List<String> getAllRewardTypeNames() {
        return new ArrayList<>(types.keySet());
    }
}
