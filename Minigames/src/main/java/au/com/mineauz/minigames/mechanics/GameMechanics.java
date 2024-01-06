package au.com.mineauz.minigames.mechanics;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameMechanics {
    private static final Map<String, GameMechanicBase> gameMechanics = new HashMap<>();

    static {
        addGameMechanic(new CustomMechanic());
        Arrays.stream(MG_MECHANICS.values()).forEach(s -> addGameMechanic(s.getMechanic()));
    }

    /**
     * Adds a new game mechanic to Minigames
     *
     * @param mechanic A game mechanic extending GameMechanicBase
     */
    public static void addGameMechanic(GameMechanicBase mechanic) {
        gameMechanics.put(mechanic.getMechanic(), mechanic);
    }

    /**
     * Removes an existing game mechanic from Minigames
     *
     * @param mechanic The name of the mechanic to be removed
     * @throws NullPointerException if the mechanic cannot be found.
     */
    public static void removeGameMechanic(String mechanic) throws NullPointerException {
        if (gameMechanics.containsKey(mechanic)) {
            HandlerList.unregisterAll(gameMechanics.get(mechanic));
            gameMechanics.remove(mechanic);
        } else
            throw new NullPointerException("No GameMechanic of that name has been added!");
    }

    /**
     * Gets a specific game mechanic by name
     *
     * @param mechanic The name of the mechanic
     * @return A game mechanic extending GameMechanicBase or Null if none found.
     */
    public static GameMechanicBase getGameMechanic(String mechanic) {
        if (gameMechanics.containsKey(mechanic)) {
            return gameMechanics.get(mechanic);
        }
        return null;
    }

    /**
     * Gets all the registered game mechanics in Minigames
     *
     * @return a Set containing the game mechanics
     */
    public static Set<GameMechanicBase> getGameMechanics() {
        return new HashSet<>(gameMechanics.values());
    }

    public static @Nullable GameMechanicBase matchGameMechanic(@NotNull String name) {
        for (Map.Entry<String, GameMechanicBase> entry : gameMechanics.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public enum MG_MECHANICS {
        KILLS(new PlayerKillsMechanic()),
        CTF(new CTFMechanic()),
        INFECTION(new InfectionMechanic()),
        TREASUREHUNT(new TreasureHuntMechanic()),
        LIVES(new LivesMechanic()),
        JUGGERNAUT(new JuggernautMechanic());

        private final GameMechanicBase mechanic;

        MG_MECHANICS(GameMechanicBase name) {
            this.mechanic = name;
        }

        public GameMechanicBase getMechanic() {
            return this.mechanic;
        }

        @Override
        public String toString() {
            return mechanic.getMechanic();
        }
    }
}
