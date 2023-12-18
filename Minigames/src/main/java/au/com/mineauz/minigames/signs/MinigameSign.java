package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MinigameSign {

    @NotNull String getName();

    @Nullable String getCreatePermission();

    /**
     * if the return value is null, there is no permission and everybody should be allowed to use it
     *
     * @return
     */
    @Nullable String getUsePermission();

    /**
     * if false the sign is invalid and the event will be canceled.
     *
     * @param event
     * @return
     */
    boolean signCreate(@NotNull SignChangeEvent event);

    boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer);

    void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer);
}
