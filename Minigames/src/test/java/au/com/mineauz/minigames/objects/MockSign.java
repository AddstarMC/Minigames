package au.com.mineauz.minigames.objects;

import java.util.LinkedList;
import java.util.List;

import be.seeseemelk.mockbukkit.UnimplementedOperationException;
import be.seeseemelk.mockbukkit.block.BlockStateMock;
import net.kyori.adventure.text.Component;
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
    public @NotNull List<Component> lines() {
        return null;
    }

    @Override
    public @NotNull Component line(int i) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public void line(int i, @NotNull Component component) throws IndexOutOfBoundsException {

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
    public boolean isGlowingText() {
        return false;
    }

    @Override
    public void setGlowingText(boolean b) {

    }

    @Override
    public Location getLocation(Location loc) {
        return super.getLocation(loc);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
  public @NotNull PersistentDataContainer getPersistentDataContainer() {
    throw new UnimplementedOperationException("This is not yet implemented");
  }

    @Override
    public boolean isSnapshot() {
        return false;
    }

    @Override
  public @Nullable DyeColor getColor() {
    return null;
  }

  @Override
  public void setColor(DyeColor color) {

  }
}
