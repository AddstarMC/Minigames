package au.com.mineauz.minigames.recorder;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * this class encodes the data and state a changed block had
 */
public class MgBlockData {
    /**
     * location of the block
     */
    private final @NotNull Location location;
    /**
     * state the block was in
     */
    private final @NotNull BlockState state;
    /**
     * data the block had
     */
    private final String blockData;
    /**
     * the uuid of the player who changed this block.
     * If null, the block doesn't get reset if the player left the minigame
     */
    private @Nullable UUID playerUUID = null;
    /**
     * inventory of the block
     */
    private @Nullable ItemStack[] inventoryContents = null;
    /**
     * holds if the inventory was randomized ones
     */
    private boolean hasRandomized = false;

    public MgBlockData(@NotNull Block original, @Nullable MinigamePlayer modifier) {
        location = original.getLocation();
        state = original.getState(); // this already makes a copy of the data - not a reference!
        blockData = original.getBlockData().getAsString();
        if (modifier != null) {
            playerUUID = modifier.getUUID();
        } else {
            playerUUID = null;
        }
    }

    /**
     * Please make sure, the blocksate is a copy not a reference.
     * Obtaining through {@link Block#getState()} should be fine.
     */
    public MgBlockData(@NotNull BlockState original, @Nullable MinigamePlayer modifier) {
        location = original.getLocation();
        state = original;
        blockData = state.getBlockData().getAsString();
        if (modifier != null) {
            playerUUID = modifier.getUUID();
        } else {
            playerUUID = null;
        }
    }

    public @NotNull Location getLocation() {
        return location;
    }

    public @NotNull BlockState getBlockState() {
        return state;
    }

    public @Nullable MinigamePlayer getModifier() {
        if (playerUUID == null) {
            return null;
        } else {
            return Minigames.getPlugin().getPlayerManager().getMinigamePlayer(playerUUID);
        }
    }

    public void setModifier(@Nullable MinigamePlayer modifier) {
        playerUUID = (modifier != null) ? modifier.getUUID() : null;
    }

    public final @NotNull String getBlockDataString() {
        return blockData;
    }

    public @NotNull BlockData getBukkitBlockData() {
        return Bukkit.createBlockData(blockData);
    }

    public @Nullable ItemStack[] getInventoryContents() {
        return inventoryContents;
    }

    public void setInventory(@Nullable ItemStack[] items) {
        this.inventoryContents = items;
    }

    /**
     * shuffle the inventory (if any) of this block
     *
     * @param minContents minimum amount of items of all the ones already in the inventory of this block
     * @param maxContents maximum amount of items left in the inventory after randomizing
     */
    public void randomizeContents(int minContents, int maxContents) {
        if (hasRandomized || inventoryContents == null)
            return;

        if (state instanceof InventoryHolder holder) {
            //get a list of all non-null items
            List<ItemStack> itemRand = Arrays.stream(inventoryContents)
                    .filter(Objects::nonNull)
                    .map(ItemStack::clone)
                    .collect(Collectors.toCollection(ArrayList::new));

            //shuffle the list, it's random what item doesn't make it into the inventory if maxContents doesn't match the number of items
            Collections.shuffle(itemRand);

            //we can't put more Items into the inventory than we have to put into
            if (maxContents > itemRand.size()) {
                maxContents = itemRand.size();
            }
            if (minContents > itemRand.size()) {
                minContents = itemRand.size();
            }

            // get a random number to get how many items there should be left in the final inventory
            int rand = (new Random()).nextInt(minContents, maxContents + 1);
            List<ItemStack> itemResult = new ArrayList<>();

            for (int i = 0; i < inventoryContents.length; i++) {
                if (i < rand) {
                    itemResult.add(i, itemRand.get(i));
                } else {
                    itemResult.add(null);
                }
            }

            //shuffle where is no item
            Collections.shuffle(itemResult);

            //set the inventory
            holder.getInventory().setContents(itemResult.toArray(ItemStack[]::new));

            hasRandomized = true;
        }
    }

    /**
     * returns if the inventory (if any) was randomized
     */
    public boolean hasRandomized() {
        return hasRandomized;
    }

    /**
     * old string representation. Don't use it!
     */
    @Override
    @Deprecated
    public String toString() {
        StringBuilder ret = new StringBuilder("{");
        ret.append("mat:").append(state.getType()).append(";");
        ret.append("data:").append(blockData).append(";");
        ret.append("x:").append(state.getX()).append(";");
        ret.append("y:").append(state.getY()).append(";");
        ret.append("z:").append(state.getZ()).append(";");
        ret.append("world:").append(state.getWorld().getName());
        if (inventoryContents != null) {
            ret.append(";");

            int c = 0;
            ret.append("items:");
            for (ItemStack i : inventoryContents) {
                if (i != null) {
                    ret.append("(");
                    ret.append("item-").append(i.getType()).append("|");
                    ret.append("dur-").append(i.getDurability()).append("|");
                    ret.append("c-").append(i.getAmount()).append("|");
                    if (!i.getEnchantments().isEmpty()) {
                        ret.append("enc-");
                        for (Enchantment e : i.getEnchantments().keySet()) {
                            ret.append("[");
                            ret.append(e.getName()).append(",");
                            ret.append(i.getEnchantments().get(e));
                            ret.append("]");
                        }
                        ret.append("|");
                    }
                    ret.append("slot-").append(c);
                    ret.append(")");
                }
                c++;
            }
        }
        ret.append("}");
        return ret.toString();
    }
}
