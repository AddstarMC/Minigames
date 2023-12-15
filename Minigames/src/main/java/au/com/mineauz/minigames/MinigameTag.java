package au.com.mineauz.minigames;

import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Set;

public interface MinigameTag<T> {

    /**
     * MinigameTag representing vanilla potions with negative effect(s).
     * Also represents all potions with exclusive negative effects, which aren't in the value list.
     */
    MinigameTag<PotionEffectType> NEGATIVE_POTION = new MinigameTag<>() {
        @Override
        public boolean isTagged(PotionEffectType item) {
            return getValues().contains(item);
        }

        @Override
        public boolean allTagged(Collection<PotionEffectType> items) {
            return getValues().containsAll(items);
        }

        @Override
        public Set<PotionEffectType> getValues() {
            return Set.of(
                    PotionEffectType.SLOW,
                    PotionEffectType.HARM,
                    PotionEffectType.WEAKNESS,
                    PotionEffectType.POISON,
                    PotionEffectType.BLINDNESS,
                    PotionEffectType.BAD_OMEN,
                    PotionEffectType.CONFUSION,
                    PotionEffectType.DARKNESS,
                    PotionEffectType.GLOWING,
                    PotionEffectType.HUNGER,
                    PotionEffectType.LEVITATION,
                    PotionEffectType.SLOW_DIGGING,
                    PotionEffectType.UNLUCK,
                    PotionEffectType.WITHER
            );
        }
    };

    /**
     * MinigameTag representing vanilla potions with positive effect(s)
     * Also represents all potions with exclusive positive effects, which aren't in the value list.
     */
    MinigameTag<PotionEffectType> POSITIVE_POTION = new MinigameTag<>() {
        @Override
        public boolean isTagged(PotionEffectType item) {
            return getValues().contains(item);
        }

        @Override
        public boolean allTagged(Collection<PotionEffectType> items) {
            return getValues().containsAll(items);
        }

        @Override
        public Set<PotionEffectType> getValues() {
            return Set.of(
                    PotionEffectType.FIRE_RESISTANCE,
                    PotionEffectType.LUCK,
                    PotionEffectType.HEAL,
                    PotionEffectType.NIGHT_VISION,
                    PotionEffectType.REGENERATION,
                    PotionEffectType.SLOW_FALLING,
                    PotionEffectType.SPEED,
                    PotionEffectType.JUMP,
                    PotionEffectType.INCREASE_DAMAGE,
                    PotionEffectType.INVISIBILITY,
                    PotionEffectType.WATER_BREATHING,
                    PotionEffectType.ABSORPTION,
                    PotionEffectType.DAMAGE_RESISTANCE,
                    PotionEffectType.CONDUIT_POWER,
                    PotionEffectType.DOLPHINS_GRACE,
                    PotionEffectType.FAST_DIGGING,
                    PotionEffectType.HEALTH_BOOST,
                    PotionEffectType.HERO_OF_THE_VILLAGE,
                    PotionEffectType.SATURATION
            );
        }
    };

    /**
     * MinigameTag representing vanilla potions with both, positive and negative, effects
     */
    MinigameTag<Set<PotionEffectType>> MIXED_POTION = new MinigameTag<>() {

        @Override
        public boolean isTagged(Set<PotionEffectType> item) {
            return getValues().contains(item);
        }

        @Override
        public boolean allTagged(Collection<Set<PotionEffectType>> items) {
            return getValues().containsAll(items);
        }

        @Override
        public Set<Set<PotionEffectType>> getValues() {
            return Set.of(
                    Set.of(PotionEffectType.SLOW, PotionEffectType.DAMAGE_RESISTANCE)
            );
        }
    };

    /**
     * Returns whether this tag has an entry for the specified object
     *
     * @param item to check
     * @return if it is tagged
     */
    boolean isTagged(T item);

    boolean allTagged(Collection<T> items);

    /**
     * Returns a list of all tagged objects
     */
    Set<T> getValues();
}
