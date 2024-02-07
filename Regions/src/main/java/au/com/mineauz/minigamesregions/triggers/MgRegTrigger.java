package au.com.mineauz.minigamesregions.triggers;

import au.com.mineauz.minigamesregions.RegionMessageManager;
import au.com.mineauz.minigamesregions.language.RegionLangKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MgRegTrigger implements Trigger {
    GAME_END(false, true, true, RegionLangKey.TRIGGER_GAME_END_NAME),
    GAME_START(false, true, true, RegionLangKey.TRIGGER_GAME_START_NAME),
    PLAYER_BLOCK_BREAK(true, true, true, RegionLangKey.TRIGGER_PLAYER_BLOCK_BREAK_NAME, "BLOCK_BREAK"),
    PLAYER_BLOCK_CLICK_LEFT(false, true, true, RegionLangKey.TRIGGER_PLAYER_BLOCK_CLICK_LEFT_NAME, "LEFT_CLICK_BLOCK"),
    PLAYER_BLOCK_CLICK_RIGHT(false, true, true, RegionLangKey.TRIGGER_PLAYER_BLOCK_CLICK_RIGHT_NAME, "RIGHT_CLICK_BLOCK"),
    PLAYER_BLOCK_INTERACT(false, true, true, RegionLangKey.TRIGGER_PLAYER_BLOCK_INTERACT_NAME, "INTERACT"), // todo block or Entity interact?
    PLAYER_BLOCK_PLACE(true, true, true, RegionLangKey.TRIGGER_PLAYER_BLOCK_PLACE_NAME, "BLOCK_PLACE"),
    PLAYER_CTFFLAG_DROP(true, true, true, RegionLangKey.TRIGGER_PLAYER_CTFFLAG_DROP_NAME, "PLAYER_DROP_FLAG"),
    PLAYER_CTFFLAG_TAKE(true, true, true, RegionLangKey.TRIGGER_PLAYER_CTFFLAG_TAKE_NAME, "PLAYER_TAKE_FLAG"),
    PLAYER_DAMAGED(true, true, true, RegionLangKey.TRIGGER_PLAYER_DAMAGED_NAME, "PLAYER_DAMAGE"),
    PLAYER_DEATH_GENERAL(true, true, true, RegionLangKey.TRIGGER_PLAYER_DEATH_GENERAL_NAME, "DEATH"),
    PLAYER_DEATH_PVP(true, true, true, RegionLangKey.TRIGGER_PLAYER_DEATH_PVP_NAME, "PLAYER_KILLED"),
    PLAYER_FOOD_CHANGE(true, true, true, RegionLangKey.TRIGGER_PLAYER_FOOD_CHANGE_NAME, "FOOD_CHANGE"),
    PLAYER_GAME_JOIN(true, true, true, RegionLangKey.TRIGGER_GAME_JOIN_NAME, "GAME_JOIN"),
    PLAYER_GAME_QUIT(true, true, true, RegionLangKey.TRIGGER_GAME_QUIT_NAME, "GAME_QUIT"),
    PLAYER_GLIDE_START(true, false, true, RegionLangKey.TRIGGER_PLAYER_GLIDE_START_NAME, "START_GLIDE"),
    PLAYER_GLIDE_STOP(true, false, true, RegionLangKey.TRIGGER_PLAYER_GLIDE_STOP_NAME, "STOP_GLIDE"),
    PLAYER_ITEM_DROP(true, true, true, RegionLangKey.TRIGGER_ITEM_PICKUP_NAME, "ITEM_DROP"),
    PLAYER_ITEM_PICKUP(true, true, true, RegionLangKey.TRIGGER_ITEM_DROP_NAME, "ITEM_PICKUP"),
    PLAYER_KILLS_PLAYER(true, true, true, RegionLangKey.TRIGGER_PLAYER_KILLS_PLAYER_NAME, "PLAYER_KILL"),
    PLAYER_REGION_ENTER(true, false, true, RegionLangKey.TRIGGER_PLAYER_REGION_ENTER_NAME, "ENTER"),
    PLAYER_REGION_LEAVE(true, false, true, RegionLangKey.TRIGGER_PLAYER_REGION_LEAVE_NAME, "LEAVE"),
    PLAYER_REGION_MOVE_INSIDE(true, false, true, RegionLangKey.TRIGGER_PLAYER_REGION_MOVE_INSIDE_NAME, "MOVE_IN_REGION"),
    PLAYER_RESPAWN(true, true, true, RegionLangKey.TRIGGER_PLAYER_RESPAWN_NAME, "RESPAWN"),
    PLAYER_XP_CHANGE(true, true, true, RegionLangKey.TRIGGER_PLAYER_XP_CHANGE_NAME, "XP_CHANGE"),
    RANDOM(true, true, true, RegionLangKey.TRIGGER_RANDOM_NAME),
    REMOTE(true, true, true, RegionLangKey.TRIGGER_REMOTE_NAME),
    REMOTE_TIMED(true, true, false, RegionLangKey.TRIGGER_REMOTE_TIMED_NAME, "TIMED_REMOTE"),
    TIME_GAMETICK(true, false, false, RegionLangKey.TRIGGER_TIME_GAMETICK_NAME, "GAME_TICK"), // todo is it the same as tick?
    TIME_MINIGAMETIMER(false, true, true, RegionLangKey.TRIGGER_TIME_TIMER_NAME, "MINIGAME_TIMER"),
    TIME_TICK(true, false, true, RegionLangKey.TRIGGER_TIME_TICK_NAME, "TICK"); // todo is it the same as gametick?


    private final boolean useInRegions, useInNodes, triggerOnPlayerAvailable;
    private final @Nullable String legacyName; // dataFixerUpper
    private final @NotNull Component displayName;

    MgRegTrigger(boolean useInRegions, boolean useInNodes, boolean triggerOnPlayerAvailable, @NotNull RegionLangKey langKey) {
        this.useInRegions = useInRegions;
        this.useInNodes = useInNodes;
        this.triggerOnPlayerAvailable = triggerOnPlayerAvailable;
        this.legacyName = null;
        this.displayName = RegionMessageManager.getMessage(langKey);
    }

    MgRegTrigger(boolean useInRegions, boolean useInNodes, boolean triggerOnPlayerAvailable, @NotNull RegionLangKey langKey, @NotNull String legacyName) {
        this.useInRegions = useInRegions;
        this.useInNodes = useInNodes;
        this.triggerOnPlayerAvailable = triggerOnPlayerAvailable;
        this.displayName = RegionMessageManager.getMessage(langKey);
        this.legacyName = legacyName;
    }

    @Override
    public @NotNull String getName() {
        return toString();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return displayName;
    }

    public @Nullable String getLegacyName() {
        return legacyName;
    }

    @Override
    public boolean useInRegions() {
        return useInRegions;
    }

    @Override
    public boolean useInNodes() {
        return useInNodes;
    }

    @Override
    public boolean triggerOnPlayerAvailable() {
        return triggerOnPlayerAvailable;
    }
}
