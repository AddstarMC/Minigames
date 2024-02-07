package au.com.mineauz.minigamesregions.conditions;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum RegionConditions implements ConditionFactory {
    CONTAINS_ENTIRE_TEAM("CONTAINS_ENTIRE_TEAM", ContainsEntireTeamCondition::new),
    CONTAINS_ONE_TEAM("CONTAINS_ONE_TEAM", ContainsOneTeamCondition::new),
    HAS_REQUIRED_FLAGS("HAS_REQUIRED_FLAGS", HasRequiredFlagsCondition::new),
    MATCH_BLOCK("MATCH_BLOCK", MatchBlockCondition::new),
    MATCH_TEAM("MATCH_TEAM", MatchTeamCondition::new),
    PLAYER_COUNT("PLAYER_COUNT", PlayerCountCondition::new),
    PLAYER_HAS_ITEM("PLAYER_HAS_ITEM", PlayerHasItemCondition::new),
    PLAYER_HEALTH_RANGE("PLAYER_HEALTH_RANGE", PlayerHealthRangeCondition::new),
    PLAYER_SCORE_RANGE("PLAYER_SCORE_RANGE", PlayerScoreRangeCondition::new),
    RANDOM_CHANCE("RANDOM_CHANCE", RandomChanceCondition::new),
    TEAM_PLAYER_COUNT("TEAM_PLAYER_COUNT", TeamPlayerCountCondition::new),
    TEAM_SCORE_RANGE("TEAM_SCORE_RANGE", TeamScoreRangeCondition::new),
    MINIGAME_TIMER("MINIGAME_TIMER", MinigameTimerCondition::new),
    PLAYER_XP_RANGE("PLAYER_XP_RANGE", PlayerXPRangeCondition::new),
    PLAYER_FOOD_RANGE("PLAYER_FOOD_RANGE", PlayerFoodRangeCondition::new),
    HAS_FLAG("HAS_FLAG", HasFlagCondition::new),
    CONTAINS_ENTITY("CONTAINS_ENTITY", ContainsEntityCondition::new),
    HAS_LOADOUT("HAS_LOADOUT", HasLoadoutCondition::new),
    BLOCK_ON_AND_HELD("BLOCK_ON_AND_HELD", BlockOnAndHeldCondition::new);

    private final @NotNull String name;
    private final @NotNull Function<String, ACondition> constructor;

    RegionConditions(@NotNull String name, @NotNull Function<String, ACondition> constructor) {
        this.name = name;
        this.constructor = constructor;
    }

    @Override
    public @NotNull ACondition makeNewCondition() {
        return constructor.apply(name);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
}
