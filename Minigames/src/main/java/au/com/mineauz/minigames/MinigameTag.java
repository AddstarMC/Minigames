package au.com.mineauz.minigames;

import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface MinigameTag<T> {

    /**
     * MinigameTag representing vanilla potions with negative effect(s).
     * Also represents all potions with exclusive negative effects, which aren't in the value list.
     */
    MinigameTag<Collection<PotionEffectType>> NEGATIVE_POTION = new MinigameTag() {

        @Override
        public boolean isTagged(Object item) {
            List<PotionEffectType> list = getValues().stream().flatMap(Collection::stream).toList();
            return list.containsAll((Collection) item);
        }

        @Override
        public List<Collection<PotionEffectType>> getValues() {
            return List.of(
                    List.of(PotionEffectType.SLOW),
                    List.of(PotionEffectType.HARM),
                    List.of(PotionEffectType.WEAKNESS),
                    List.of(PotionEffectType.POISON),
                    List.of(PotionEffectType.BLINDNESS),
                    List.of(PotionEffectType.BAD_OMEN),
                    List.of(PotionEffectType.CONFUSION),
                    List.of(PotionEffectType.DARKNESS),
                    List.of(PotionEffectType.GLOWING),
                    List.of(PotionEffectType.HUNGER),
                    List.of(PotionEffectType.LEVITATION),
                    List.of(PotionEffectType.SLOW_DIGGING),
                    List.of(PotionEffectType.UNLUCK),
                    List.of(PotionEffectType.WITHER)
            );
        }
    };

    /**
     * MinigameTag representing vanilla potions with positive effect(s)
     * Also represents all potions with exclusive positive effects, which aren't in the value list.
     */
    MinigameTag<Collection<PotionEffectType>> POSITIVE_POTION = new MinigameTag<>() {
        @Override
        public boolean isTagged(Collection<PotionEffectType> item) {
            List<PotionEffectType> list = getValues().stream().flatMap(Collection::stream).toList();
            return new HashSet<>(list).containsAll(item);
        }

        @Override
        public List<Collection<PotionEffectType>> getValues() {
            return List.of(
                    List.of(PotionEffectType.FIRE_RESISTANCE),
                    List.of(PotionEffectType.LUCK),
                    List.of(PotionEffectType.HEAL),
                    List.of(PotionEffectType.NIGHT_VISION),
                    List.of(PotionEffectType.REGENERATION),
                    List.of(PotionEffectType.SLOW_FALLING),
                    List.of(PotionEffectType.SPEED),
                    List.of(PotionEffectType.JUMP),
                    List.of(PotionEffectType.INCREASE_DAMAGE),
                    List.of(PotionEffectType.INVISIBILITY),
                    List.of(PotionEffectType.WATER_BREATHING),
                    List.of(PotionEffectType.ABSORPTION),
                    List.of(PotionEffectType.DAMAGE_RESISTANCE),
                    List.of(PotionEffectType.CONDUIT_POWER),
                    List.of(PotionEffectType.DOLPHINS_GRACE),
                    List.of(PotionEffectType.FAST_DIGGING),
                    List.of(PotionEffectType.HEALTH_BOOST),
                    List.of(PotionEffectType.HERO_OF_THE_VILLAGE),
                    List.of(PotionEffectType.SATURATION)
            );
        }
    };

    /**
     * MinigameTag representing vanilla potions with both, positive and negative, effects
     */
    MinigameTag<Collection<PotionEffectType>> MIXED_POTION = new MinigameTag() {

        @Override
        public boolean isTagged(Object item) {
            return getValues().contains(item);
        }

        @Override
        public List<Collection<PotionEffectType>> getValues() {
            return List.of(
                    List.of(PotionEffectType.SLOW, PotionEffectType.DAMAGE_RESISTANCE)
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

    /**
     * Returns a list of all tagged objects
     */
    List<T> getValues();
}
