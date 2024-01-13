package au.com.mineauz.minigamesregions.language;

import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import org.jetbrains.annotations.NotNull;

public enum RegionLangKey implements LangKey {
    ACTION_ERROR_INVALIDMATERIAL("action.error.invalidMaterial"),
    ACTION_ERROR_NOREGION("action.error.noRegion"),
    ACTION_MEMORYSWAPBLOCK_ERROR_ODD("action.memoryswapblock.error.odd"),
    ACTION_MEMORYSWAPBLOCK_ERROR_TOOBIG("action.memoryswapblock.error.tooBig"),
    ACTION_PLAYSOUND_ERROR_NOSOUND("action.playsound.error.noSound"),
    ACTION_REGIONSWAP_ERROR_SIZE("action.regionswap.error.size"),
    COMMAND_NODE_EXISTS("command.node.nodeExists"),
    ERROR_INVALID_ITEMTYPE("error.invalid.itemtype"),
    ITEM_ERROR_NOTBLOCK("item.error.notABlock"),
    NODE_ADDED("node.addedNode"),
    NODE_EDITED("node.edited"),
    NODE_ERROR_NONODE("node.error.noNode"),
    REGION_CREATED("region.created"),
    REGION_EDITED("region.edited"),
    REGION_ERROR_NOREGION("region.error.noRegion"),
    REGION_REMOVED("region.removed"),
    TOOL_NODEREGION_SELECTED("tool.noderegion.selected"),
    TOOL_NODE_DESELECTED("tool.node.deselected"),
    TOOL_NODE_EDIT("tool.node.edit"),
    TOOL_NODE_SELECTED("tool.node.selected"),
    TOOL_REGION_EDIT("tool.region.edit"),
    TOOL_REGION_SELECTED("tool.region.selected"),
    TRIGGER_TICK_ERROR_CONDITION("trigger.tick.error.condition"),
    TOOL_REGION_DESELECTED("tool.region.deselected");

    private final @NotNull String path;

    RegionLangKey(@NotNull String path) {
        this.path = path;
    }

    @Override
    public @NotNull String getPath() {
        return path;
    }
}