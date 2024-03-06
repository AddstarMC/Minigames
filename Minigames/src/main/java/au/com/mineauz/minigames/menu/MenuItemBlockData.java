package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemBlockData extends MenuItem {
    private Callback<BlockData> data;

    public MenuItemBlockData(String name, Material displayItem) {
        super(name, displayItem);
        data.setValue(displayItem.createBlockData());
        setDescription(createDescription(data.getValue()));
    }

    public MenuItemBlockData(String name, Material displayItem, Callback<BlockData> callback) {
        super(name, displayItem);
        this.data = callback;
        setDescription(createDescription(data.getValue()));
    }

    @Override
    public void update() {
        setDescription(createDescription(this.data.getValue()));
    }

    /**
     * minecraft:chest[facing=north,type=single,waterlogged=false]{Items:[{Slot:0b,id:"minecraft:grass_block",Count:1b}],Lock:""}
     */
    private List<String> createDescription(BlockData data) {
        List<String> result = new ArrayList<>();
        result.add("Material: " + data.getMaterial().name());
        String dataString = data.getAsString();
        int firstbracket = StringUtils.indexOf(dataString, "[", 0);
        String minecraftname = StringUtils.left(dataString, firstbracket - 1);
        result.add("Minecraft Name: " + minecraftname);
        int secondbracket = StringUtils.indexOf(dataString, "]", 0);
        String meta = StringUtils.mid(dataString, firstbracket + 1, secondbracket - 1);
        String[] vals = StringUtils.split(meta, ",");
        for (String val : vals) {
            result.add(ChatColor.GOLD + val.replace("=", ": " + ChatColor.GREEN) +
                    ChatColor.RESET);
        }
        if (secondbracket < dataString.length()) {
            result.add(ChatColor.GOLD + "Extra:" + ChatColor.GREEN + " " + StringUtils.mid(dataString,
                    secondbracket + 1, dataString.length()) + ChatColor.RESET);
        }
        return result;
    }

    @Override
    public ItemStack onClickWithItem(@Nullable ItemStack item) {
        try {
            BlockData data = item.getType().createBlockData();
            this.data.setValue(data);
        } catch (IllegalArgumentException | NullPointerException e) {
            String name = "unknown";
            if (item != null) {
                name = item.getType().name();
            }
            getContainer().getViewer().sendMessage(name + " cannot be made into a block!", MinigameMessageType.ERROR);
        }
        return getItem();
    }

    @Override
    public void checkValidEntry(String entry) {
        String err = "No MgBlockData detected";
        try {
            BlockData d = Bukkit.createBlockData(entry);
            data.setValue(d);
            setDescription(createDescription(data.getValue()));
            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
            return;
        } catch (IllegalArgumentException e) {
            err = "Invalid MgBlockData !";
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());
        getContainer().getViewer().sendMessage(err, MinigameMessageType.ERROR);

    }

    @Override
    public ItemStack onDoubleClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendMessage("Click a block to set Data for " + getName() + ", the menu" +
                " " +
                "will " +
                "automatically reopen in 10s if nothing is clicked.", MinigameMessageType.INFO);
        ply.setManualEntry(this);
        getContainer().startReopenTimer(10);
        return null;
    }

}
