package au.com.mineauz.minigames.objects;

import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.block.BlockStateMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

/**
 * Created for the AddstarMC Project. Created by Narimm on 10/01/2019.
 */
public class SignBlockMock extends BlockMock {

    private BlockStateMock state;
    private BlockData data;

    /**
     * Creates a basic block with a given material that is also linked to a specific location.
     *
     * @param material The material of the block.
     * @param location The location of the block. Can be {@code null} if not needed.
     */
    public SignBlockMock(Material material, Location location, BlockStateMock state, BlockData data) {
        super(material, location);
        this.state = state;
        this.data = data;
    }

    @Override
    public BlockData getBlockData() {
        return data;
    }

    @Override
    public BlockState getState() {
        return state;
    }

    public void setBlockState(BlockStateMock state) {
        this.state = state;
    }
}
