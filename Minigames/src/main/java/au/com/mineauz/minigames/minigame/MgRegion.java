package au.com.mineauz.minigames.minigame;

import au.com.mineauz.minigames.blockRecorder.Position;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record MgRegion(@NotNull World world, @Nullable Position pos1, @Nullable Position pos2) {
}
