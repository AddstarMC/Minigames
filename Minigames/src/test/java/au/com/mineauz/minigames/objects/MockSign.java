package au.com.mineauz.minigames.objects;

import java.util.LinkedList;
import java.util.List;

import be.seeseemelk.mockbukkit.UnimplementedOperationException;
import be.seeseemelk.mockbukkit.block.BlockStateMock;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created for the AddstarMC Project. Created by Narimm on 9/01/2019.
 */
public class MockSign extends BlockStateMock implements Sign {

    private LinkedList<String> lines = new LinkedList<String>();
    private boolean edittable = false;

    public MockSign(MaterialData data, boolean edittable) {
        super(data);
        this.edittable = edittable;
        lines.add("");
        lines.addLast("");
        lines.addLast("");
        lines.addLast("");
    }

    @Override
    public String[] getLines() {
        String[] out = new String[lines.size()];
        return lines.toArray(out);
    }

    @Override
    public String getLine(int i) throws IndexOutOfBoundsException {
        return lines.get(i);
    }


    @Override
    public void setLine(int i, String s) throws IndexOutOfBoundsException {
        lines.set(i, s);
    }

    @Override
    public boolean isEditable() {
        return edittable;
    }

    @Override
    public void setEditable(boolean b) {
        edittable = b;
    }

    @Override
    public Location getLocation(Location loc) {
        return super.getLocation(loc);
    }

  @Override
  public @NotNull PersistentDataContainer getPersistentDataContainer() {
    throw new UnimplementedOperationException("This is not yet implemented");
  }

  @Override
  public @Nullable DyeColor getColor() {
    return null;
  }

  @Override
  public void setColor(DyeColor color) {

  }
}
