package au.com.mineauz.minigamesregions.language;

import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import org.jetbrains.annotations.NotNull;

public enum RegionLangKey implements LangKey {
    ACTION_ERROR_INVALIDMATERIAL("action.error.invalidMaterial"),
    ACTION_ERROR_NOREGION("action.error.noRegion"),
    ACTION_MEMORYSWAPBLOCK_ERROR_ODD("action.memorySwapBlock.error.odd"),
    ACTION_MEMORYSWAPBLOCK_ERROR_TOOBIG("action.memorySwapBlock.error.tooBig"),
    ACTION_PLAYSOUND_ERROR_NOSOUND("action.playSound.error.noSound"),
    ACTION_REGIONSWAP_ERROR_SIZE("action.regionSwap.error.size"),
    COMMAND_NODE_EXISTS("command.node.nodeExists"),
    ERROR_INVALID_ITEMTYPE("error.invalid.itemType"),
    ITEM_ERROR_NOTBLOCK("item.error.notABlock"),
    NODE_ADDED("node.addedNode"),
    NODE_EDITED("node.edited"),
    NODE_ERROR_NONODE("node.error.noNode"),
    REGION_CREATED("region.created"),
    REGION_EDITED("region.edited"),
    REGION_ERROR_NOREGION("region.error.noRegion"),
    REGION_REMOVED("region.removed"),
    TOOL_NODEREGION_SELECTED("tool.nodeRegion.selected"),
    TOOL_NODE_DESELECTED("tool.node.deselected"),
    TOOL_NODE_EDIT("tool.node.edit"),
    TOOL_NODE_SELECTED("tool.node.selected"),
    TOOL_REGION_DESELECTED("tool.region.deselected"),
    TOOL_REGION_EDIT("tool.region.edit"),
    TOOL_REGION_SELECTED("tool.region.selected"),
    TRIGGER_GAME_END_NAME("trigger.gameEnd.name"),
    TRIGGER_GAME_JOIN_NAME("trigger.player.gameJoin.name"),
    TRIGGER_GAME_QUIT_NAME("trigger.player.gameQuit.name"),
    TRIGGER_GAME_START_NAME("trigger.gameStart.name"),
    TRIGGER_ITEM_DROP_NAME("trigger.item.drop.name"),
    TRIGGER_ITEM_PICKUP_NAME("trigger.player.item.pickup.name"),
    TRIGGER_PLAYER_BLOCK_BREAK_NAME("trigger.player.block.break.name"),
    TRIGGER_PLAYER_BLOCK_CLICK_LEFT_NAME("trigger.player.block.leftClick.name"),
    TRIGGER_PLAYER_BLOCK_CLICK_RIGHT_NAME("trigger.player.block.click.right.name"),
    TRIGGER_PLAYER_BLOCK_PLACE_NAME("trigger.player.block.place.name"),
    TRIGGER_PLAYER_CTFFLAG_DROP_NAME("trigger.player.CTFFlag.drop.name"),
    TRIGGER_PLAYER_CTFFLAG_TAKE_NAME("trigger.player.CTFFlag.take.name"),
    TRIGGER_PLAYER_DAMAGED_NAME("trigger.player.damaged.name"),
    TRIGGER_PLAYER_DEATH_GENERAL_NAME("trigger.player.death.general.name"),
    TRIGGER_PLAYER_DEATH_PVP_NAME("trigger.player.death.pvp.name"),
    TRIGGER_PLAYER_FOOD_CHANGE_NAME("player.foodChange.name"),
    TRIGGER_PLAYER_GLIDE_START_NAME("trigger.player.glide.start.name"),
    TRIGGER_PLAYER_GLIDE_STOP_NAME("trigger.player.glide.stop.name"),
    TRIGGER_PLAYER_BLOCK_INTERACT_NAME("trigger.player.interact.name"),
    TRIGGER_PLAYER_KILLS_PLAYER_NAME("trigger.player.killsPlayer.name"),
    TRIGGER_PLAYER_REGION_ENTER_NAME("trigger.player.region.enter.name"),
    TRIGGER_PLAYER_REGION_LEAVE_NAME("trigger.player.region.leave.name"),
    TRIGGER_PLAYER_REGION_MOVE_INSIDE_NAME("trigger.player.region.moveInside"),
    TRIGGER_PLAYER_RESPAWN_NAME("trigger.player.respawn.name"),
    TRIGGER_PLAYER_XP_CHANGE_NAME("trigger.player.xpChange.name"),
    TRIGGER_RANDOM_NAME("trigger.random.name"),
    TRIGGER_REMOTE_NAME("trigger.remote.name"),
    TRIGGER_REMOTE_TIMED_NAME("trigger.remote.timed.name"),
    TRIGGER_TICK_ERROR_CONDITION("trigger.tick.error.condition"),
    TRIGGER_TIME_GAMETICK_NAME("trigger.time.gameTick.name"),
    TRIGGER_TIME_TICK_NAME("trigger.time.tick.name"),
    TRIGGER_TIME_TIMER_NAME("trigger.time.timer.name"),
    MENU_REGIONEXECUTOR_ADD_TRIGGER_NAME("menu.executor.add.trigger.name"),
    MENU_REGION_NAME("menu.region.name"),
    MENU_NODE_NAME("menu.node.name"),
    MENU_EXECUTOR_ADD_NAME("menu.executor.add.name"),
    MENU_EXECUTOR_NAME("menu.executor.name"),
    MENU_CONDITION_BLOCKONANDHELD_NAME("menu.condition.blockOnAndHeld.name"),
    MENU_CONDITION_CONTAINSENTIRETEAM_NAME("menu.condition.containsEntireTeam.name"),
    MENU_CONDITION_CONTAINSENTITY_NAME("menu.condition.containsEntity.name"),
    MENU_CONDITION_CONTAINSONETEAM_NAME("menu.condition.containsOneTeam.name"),
    MENU_CONDITION_HASFLAG_NAME("menu.condition.hasFlag.name"),
    MENU_CONDITION_HASLOADOUT_NAME("menu.condition.hasLoadout.name"),
    MENU_CONDITION_HASREQUIREDFLAGS_NAME("menu.condition.hasRequiredFlags.name"),
    MENU_CONDITION_MATCHBLOCK_NAME("menu.condition.matchBlock.name"),
    MENU_CONDITION_MATCHTEAM_NAME("menu.condition.matchTeam.name"),
    MENU_CONDITION_MINIGAMETIMER_NAME("menu.condition.minigameTimer.name"),
    MENU_CONDITION_PLAYERFOODRANGE_NAME("menu.condition.playerFoodRange.name"),
    MENU_CONDITION_PLAYERCOUNT_NAME("menu.condition.playerCount.name"),
    MENU_CONDITION_PLAYERHASITEM_NAME("menu.condition.playerHasItem.name"),
    MENU_CONDITION_PLAYERHEALTHRANGE_NAME("menu.condition.playerHealthRange.name"),
    MENU_CONDITION_PLAYERSCORERANGE_NAME("menu.condition.playerScoreRange.name"),
    MENU_CONDITION_PLAYERXPRANGE_NAME("menu.condition.playerXPRange.name"),
    MENU_CONDITION_RANCOMCHANCE_NAME("menu.condition.randomChance.name"),
    MENU_CONDITION_TEAMPLAYERCOUNT_NAME("menu.condition.teamPlayerCount.name"),
    MENU_CONDITION_TEAMSCORERANGE_NAME("menu.condition.teamScoreRange.name");

    private final @NotNull String path;

    RegionLangKey(@NotNull String path) {
        this.path = path;
    }

    @Override
    public @NotNull String getPath() {
        return path;
    }
}