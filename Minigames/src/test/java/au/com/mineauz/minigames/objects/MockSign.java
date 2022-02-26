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

    private LinkedList<Component> lines = new LinkedList<Component>();
    private boolean edittable = false;

    public MockSign(MaterialData data, boolean edittable) {
        super(data);
        this.edittable = edittable;
        lines.add(Component.text(""));
        lines.addLast(Component.text(""));
        lines.addLast(Component.text(""));
        lines.addLast(Component.text(""));
    }

    @Override
    public String[] getLines() {
        String[] out = new String[lines.size()];
        return lines.toArray(out);
    }

    @Override
    public String getLine(int i) throws IndexOutOfBoundsException {
        return lines.get(i).toString();
    }

    @Override
    public void setLine(int i, String s) throws IndexOutOfBoundsException {
        lines.set(i, Component.text(s));
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

    @Override
    public boolean isCollidable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGlowingText() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public @NotNull Component line(int arg0) throws IndexOutOfBoundsException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void line(int arg0, @NotNull Component arg1) throws IndexOutOfBoundsException {
        // TODO Auto-generated method stub

    }

    @Override
    public @NotNull List<Component> lines() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGlowingText(boolean arg0) {
        // TODO Auto-generated method stub

    }
}
