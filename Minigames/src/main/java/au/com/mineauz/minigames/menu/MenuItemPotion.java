package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.PlayerLoadout;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuItemPotion extends MenuItem {
    private final @NotNull PotionEffect eff;
    private final @NotNull PlayerLoadout loadout;

    public MenuItemPotion(@Nullable Material displayMat, @Nullable Component name, @NotNull PotionEffect eff,
                          @NotNull PlayerLoadout loadout) {
        super(displayMat, name);
        this.eff = eff;
        this.loadout = loadout;
        updateDescription();
    }

    public MenuItemPotion(@Nullable Material displayMat, @Nullable Component name, @Nullable List<@NotNull Component> description,
                          @NotNull PotionEffect eff, @NotNull PlayerLoadout loadout) {
        super(displayMat, name, description);
        this.eff = eff;
        this.loadout = loadout;
        updateDescription();
    }

    public void updateDescription() {
        List<Component> description;
        if (getDescription() != null) {
            description = getDescription();
            if (getDescription().size() >= 2) {
                String desc = ChatColor.stripColor(getDescription().get(0));

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

        setDescription(description);
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
