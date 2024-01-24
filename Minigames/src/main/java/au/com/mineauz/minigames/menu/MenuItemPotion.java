package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.PlayerLoadout;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class MenuItemPotion extends MenuItem {

    private final PotionEffect eff;
    private final PlayerLoadout loadout;

    public MenuItemPotion(String name, Material displayItem, PotionEffect eff, PlayerLoadout loadout) {
        super(name, displayItem);
        this.eff = eff;
        this.loadout = loadout;
        updateDescription();
    }

    public MenuItemPotion(String name, List<String> description, Material displayItem, PotionEffect eff, PlayerLoadout loadout) {
        super(name, description, displayItem);
        this.eff = eff;
        this.loadout = loadout;
        updateDescription();
    }

    public void updateDescription() {
        List<String> description;
        if (getDescriptionStr() != null) {
            description = getDescriptionStr();
            if (getDescriptionStr().size() >= 2) {
                String desc = ChatColor.stripColor(getDescriptionStr().get(0));

                if (desc.equals("Level: " + (eff.getAmplifier() + 1))) {
                    description.set(0, ChatColor.GREEN + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
                    description.set(1, ChatColor.GREEN + "Duration: " + ChatColor.GRAY + eff.getDuration());
                } else {
                    description.add(0, ChatColor.GREEN + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
                    description.add(1, ChatColor.GREEN + "Duration: " + ChatColor.GRAY + eff.getDuration());
                }
            } else {
                description.add(0, ChatColor.GREEN + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
                description.add(1, ChatColor.GREEN + "Duration: " + ChatColor.GRAY + eff.getDuration());
            }
        } else {
            description = new ArrayList<>();
            description.add(0, ChatColor.GREEN + "Level: " + ChatColor.GRAY + (eff.getAmplifier() + 1));
            description.add(1, ChatColor.GREEN + "Duration: " + ChatColor.GRAY + eff.getDuration());
        }

        setDescriptionStr(description);
    }

    @Override
    public ItemStack onShiftRightClick() {
        loadout.removePotionEffect(eff);
        getContainer().removeItem(getSlot());
        return null;
    }

    public PotionEffect getEffect() {
        return eff;
    }
}
