package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AMinigameSign {
    private static final NamespacedKey MINIGAME_NAME_KEY = new NamespacedKey(Minigames.getPlugin(), "minigame_name");

    public abstract @NotNull Component getName();

    public boolean isType(@NotNull Component signLine) {
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();

        return plainSerializer.serialize(getName()).equalsIgnoreCase(plainSerializer.serialize(signLine));
    }

    public abstract @Nullable String getCreatePermission();

    /**
     * if the return value is null, there is no permission and everybody should be allowed to use it
     *
     * @return
     */
    public abstract @Nullable String getUsePermission();

    /**
     * if false the sign is invalid and the event will be canceled.
     *
     * @param event
     * @return
     */
    public abstract boolean signCreate(@NotNull SignChangeEvent event);

    public abstract boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer);

    public abstract void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer);

    public static @Nullable Minigame getMinigame(@NotNull Sign sign) {
        Minigame result = Minigames.getPlugin().getMinigameManager().getMinigame(
                PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(2)));

        if (result == null) {
            String name = sign.getPersistentDataContainer().get(MINIGAME_NAME_KEY, PersistentDataType.STRING);
            if (name != null) {
                result = Minigames.getPlugin().getMinigameManager().getMinigame(name);
            }
        }

        return result;
    }

    public static @Nullable Minigame getMinigame(@NotNull Sign sign, @Nullable Component changedSecondLine) {
        Minigame result = null;
        if (changedSecondLine != null) {
            result = Minigames.getPlugin().getMinigameManager().getMinigame(
                    PlainTextComponentSerializer.plainText().serialize(changedSecondLine));
        }

        if (result == null) {
            String name = sign.getPersistentDataContainer().get(MINIGAME_NAME_KEY, PersistentDataType.STRING);
            if (name != null) {
                result = Minigames.getPlugin().getMinigameManager().getMinigame(name);
            }
        }

        return result;
    }

    public static void setPersistentMinigame(@NotNull Sign sign, @NotNull Minigame minigame) {
        sign.getPersistentDataContainer().set(MINIGAME_NAME_KEY, PersistentDataType.STRING, minigame.getName());
    }
}
