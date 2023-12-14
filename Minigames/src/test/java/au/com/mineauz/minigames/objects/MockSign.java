package au.com.mineauz.minigames.objects;

import be.seeseemelk.mockbukkit.UnimplementedOperationException;
import be.seeseemelk.mockbukkit.block.state.BlockStateMock;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MockSign extends BlockStateMock implements Sign {
    private final LinkedList<String> lines = new LinkedList<>();
    private boolean edittable;

    public MockSign(Material material, boolean edittable) {
        super(material);

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
    public @NotNull String getLine(int i) throws IndexOutOfBoundsException {
        return lines.get(i);
    }


    @Override
    public void setLine(int i, @NotNull String s) throws IndexOutOfBoundsException {
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
    public boolean isWaxed() {
        return false;
    }

    @Override
    public void setWaxed(boolean b) {

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
    public @Unmodifiable Collection<ItemStack> getDrops() {
        return null;
    }

    @Override
    public @Unmodifiable Collection<ItemStack> getDrops(@Nullable ItemStack tool) {
        return null;
    }

    @Override
    public @Unmodifiable Collection<ItemStack> getDrops(@NotNull ItemStack tool, @Nullable Entity entity) {
        return null;
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

    @Override
    public @NotNull SignSide getSide(@NotNull Side side) {
        return null;
    }
}
