package au.com.mineauz.minigamesregions.actions;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum RegionActions implements ActionFactory {
    ADD_SCORE("ADD_SCORE", AddScoreAction::new),
    ADD_TEAM_SCORE("ADD_TEAM_SCORE", AddTeamScoreAction::new),
    APPLY_POTION("APPLY_POTION", ApplyEffectAction::new),
    BARRIER("BARRIER", BarrierAction::new),
    BROADCAST("BROADCAST", BroadcastAction::new),
    CHECKPOINT("CHECKPOINT", CheckpointAction::new),
    END("END", EndAction::new),
    EQUIP_LOADOUT("EQUIP_LOADOUT", EquipLoadoutAction::new),
    EXECUTE_COMMAND("EXECUTE_COMMAND", ExecuteCommandAction::new),
    EXPLODE("EXPLODE", ExplodeAction::new),
    FALLING_BLOCK("FALLING_BLOCK", FallingBlockAction::new),
    FLIGHT("FLIGHT", FlightAction::new),
    GIVE_ITEM("GIVE_ITEM", GiveItemAction::new),
    HEAL("HEAL", HealAction::new),
    KILL("KILL", KillAction::new),
    LIGHTNING("LIGHTNING", LightningAction::new),
    MEMORY_SWAP_BLOCK("MEMORY_SWAP_BLOCK", MemorySwapBlockAction::new),
    MESSAGE("MESSAGE", MessageAction::new),
    PLAY_SOUND("PLAY_SOUND", PlaySoundAction::new),
    PULSE_REDSTONE("PULSE_REDSTONE", PulseRedstoneAction::new),
    QUIT("QUIT", QuitAction::new),
    REEQUIP_LOADOUT("REEQUIP_LOADOUT", ReequipLoadoutAction::new),
    REGION_SWAP_ACTION("REGION_SWAP_ACTION", RegionSwapAction::new),
    RESET_TRIGGER_COUNT("RESET_TRIGGER_COUNT", ResetTriggerCountAction::new),
    REVERT("REVERT", RevertAction::new),
    SET_BLOCK("SET_BLOCK", SetBlockAction::new),
    SET_ENABLED("SET_ENABLED", SetEnabledAction::new),
    SET_SCORE("SET_SCORE", SetScoreAction::new),
    SET_TEAM_SCORE("SET_TEAM_SCORE", SetTeamScoreAction::new),
    SPAWN_ENTITY("SPAWN_ENTITY", SpawnEntityAction::new),
    SWAP_BLOCK("SWAP_BLOCK", SwapBlockAction::new),
    SWITCH_TEAM("SWITCH_TEAM", SwitchTeamAction::new),
    TAKE_ITEM("TAKE_ITEM", TakeItemAction::new),
    TELEPORT("TELEPORT", TeleportAction::new),
    TIMED_TRIGGER("TIMED_TRIGGER", TimedTriggerAction::new),
    TRIGGER_NODE("TRIGGER_NODE", TriggerNodeAction::new),
    TRIGGER_RANDOM("TRIGGER_RANDOM", TriggerRandomAction::new),
    TRIGGER_REGION("TRIGGER_REGION", TriggerRegionAction::new),
    VELOCITY("VELOCITY", VelocityAction::new),
    addAction("RANDOM_FILLING", RandomFillingAction::new);

    private final @NotNull String name;
    private final @NotNull Function<@NotNull String, @NotNull ActionInterface> constructor;

    RegionActions(@NotNull String name, @NotNull Function<@NotNull String, @NotNull ActionInterface> constructor) {
        this.name = name;
        this.constructor = constructor;
    }

    @Override
    public @NotNull ActionInterface makeNewAction() {
        return constructor.apply(name);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
}
