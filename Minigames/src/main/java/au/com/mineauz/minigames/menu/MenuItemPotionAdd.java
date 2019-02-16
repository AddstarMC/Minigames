package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.PlayerLoadout;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class MenuItemPotionAdd extends MenuItem {

    PlayerLoadout loadout;

    public MenuItemPotionAdd(String name, Material displayItem, PlayerLoadout loadout) {
        super(name, displayItem);
        this.loadout = loadout;
    }

    public MenuItemPotionAdd(String name, List<String> description, Material displayItem, PlayerLoadout loadout) {
        super(name, description, displayItem);
        this.loadout = loadout;
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer ply = getContainer().getViewer();
        ply.setNoClose(true);
        ply.getPlayer().closeInventory();
        ply.sendInfoMessage("Enter a potion using the syntax below into chat, the menu will automatically reopen in 30s if nothing is entered.");
        ply.sendInfoMessage("PotionName, level, duration (duration can be \"inf\")");
        ply.setManualEntry(this);

        getContainer().startReopenTimer(30);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        String[] split = entry.split(", ");
        if (split.length == 3) {
            String effect = split[0].toUpperCase();
            if (PotionEffectType.getByName(effect) != null) {
                PotionEffectType eff = PotionEffectType.getByName(effect);
                if (split[1].matches("[0-9]+") && Integer.parseInt(split[1]) != 0) {
                    int level = Integer.parseInt(split[1]) - 1;
                    if ((split[2].matches("[0-9]+") && Integer.parseInt(split[2]) != 0) || split[2].equalsIgnoreCase("inf")) {
                        int dur = 0;
                        if (split[2].equalsIgnoreCase("inf"))
                            dur = 100000;
                        else
                            dur = Integer.parseInt(split[2]);

                        if (dur > 100000) {
                            dur = 100000;
                        }
                        dur *= 20;

                        List<String> des = new ArrayList<>();
                        des.add("Shift + Right Click to Delete");

                        PotionEffect peff = new PotionEffect(eff, dur, level);
                        for (int slot : getContainer().getSlotMap()) {
                            if (getContainer().getClicked(slot) instanceof MenuItemPotion) {
                                MenuItemPotion pot = (MenuItemPotion) getContainer().getClicked(slot);
                                if (pot.getEffect().getType() == peff.getType()) {
                                    pot.onShiftRightClick();
                                    break;
                                }
                            }
                        }
                        for (int i = 0; i < 36; i++) {
                            if (!getContainer().hasMenuItem(i)) {
                                getContainer().addItem(new MenuItemPotion(eff.getName().toLowerCase().replace("_", " "), des, Material.POTION, peff, loadout), i);
                                loadout.addPotionEffect(peff);
                                break;
                            }
                        }
                    } else
                        getContainer().getViewer().sendMessage(split[2] + " is not a valid duration! The time must be in seconds", MinigameMessageType.ERROR);
                } else
                    getContainer().getViewer().sendMessage(split[1] + " is not a valid level number!", MinigameMessageType.ERROR);
            } else
                getContainer().getViewer().sendMessage(split[0] + " is not a valid potion name!", MinigameMessageType.ERROR);

            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
            return;
        }
        getContainer().cancelReopenTimer();
        getContainer().displayMenu(getContainer().getViewer());

        getContainer().getViewer().sendMessage("Invalid syntax entry! Make sure there is an comma and a space (\", \") between each item.", MinigameMessageType.ERROR);
    }
}
