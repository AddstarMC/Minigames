package au.com.mineauz.minigames.objects;

import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * Created for the AddstarMC Project. Created by Narimm on 10/01/2019.
 */
public class SignBlockDataMock implements BlockData {
    @Override
    public Material getMaterial() {
        return null;
    }

    @Override
    public String getAsString() {
        return null;
    }

    @Override
    public String getAsString(boolean b) {
        return null;
    }

    @Override
    public BlockData merge(BlockData blockData) {
        return null;
    }

    @Override
    public boolean matches(BlockData blockData) {
        return false;
    }

    @Override
    public BlockData clone() {
        return null;
    }

    @Override
    public @NotNull SoundGroup getSoundGroup() {
        return null;
    }
}
