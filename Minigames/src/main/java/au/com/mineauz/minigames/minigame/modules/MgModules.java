package au.com.mineauz.minigames.minigame.modules;

import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.reward.RewardsModule;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public enum MgModules implements ModuleFactory {
    CAPTURE_THE_FLAG("CTF", CTFModule::new),
    GAME_OVER("GameOver", GameOverModule::new),
    INFECTION("Infection", InfectionModule::new),
    JUGGERNAUT("Juggernaut", JuggernautModule::new),
    LOADOUT("Loadouts", LoadoutModule::new),
    LOBBY_SETTINGS("LobbySettings", LobbySettingsModule::new),
    RESOURCEPACK("ResourcePack", ResourcePackModule::new),
    REWARDS("Rewards", RewardsModule::new),
    TEAMS("Teams", TeamsModule::new),
    TREASURE_HUNT("TreasureHunt", TreasureHuntModule::new),
    WEATHER_TIME("WeatherTime", WeatherTimeModule::new);

    private final @NotNull BiFunction<@NotNull Minigame, @NotNull String, @NotNull MinigameModule> minigameModuleInit;
    private final @NotNull String name;

    MgModules(@NotNull String name, @NotNull BiFunction<@NotNull Minigame, @NotNull String, @NotNull MinigameModule> minigameModuleInit) {
        this.minigameModuleInit = minigameModuleInit;
        this.name = name;
    }

    public @NotNull MinigameModule makeNewModule(Minigame minigame) {
        return minigameModuleInit.apply(minigame, name);
    }

    @Override
    public @NotNull String toString() {
        return name;
    }

    public @NotNull String getName() {
        return name;
    }
}
