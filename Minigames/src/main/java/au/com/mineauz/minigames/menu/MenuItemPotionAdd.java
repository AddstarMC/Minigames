package au.com.mineauz.minigames.menu;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.PlayerLoadout;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.LangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MenuItemPotionAdd extends MenuItem {
    private final @NotNull PlayerLoadout loadout;

    public MenuItemPotionAdd(@Nullable Material displayMat, @NotNull LangKey langKey, @NotNull PlayerLoadout loadout) {
        super(displayMat, langKey);
        this.loadout = loadout;
    }

    public MenuItemPotionAdd(@Nullable Material displayMat, @Nullable Component name, @NotNull PlayerLoadout loadout) {
        super(displayMat, name);
        this.loadout = loadout;
    }

    public MenuItemPotionAdd(@Nullable Material displayMat, @Nullable Component name,
                             @Nullable List<@NotNull Component> description, @NotNull PlayerLoadout loadout) {
        super(displayMat, name, description);
        this.loadout = loadout;
    }

    @Override
    public ItemStack onClick() {
        MinigamePlayer mgPlayer = getContainer().getViewer();
        mgPlayer.setNoClose(true);
        mgPlayer.getPlayer().closeInventory();

        // time for a player to write a valid potion into chat
        int reopenSeconds = 30;
        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgMenuLangKey.MENU_POTIONADD_ENTERCHAT,
                Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofSeconds(reopenSeconds))));
        mgPlayer.setManualEntry(this);

        getContainer().startReopenTimer(reopenSeconds);
        return null;
    }

    @Override
    public void checkValidEntry(String entry) {
        String[] split = entry.split(", ");
        if (split.length == 3) {
            String effect = split[0].toUpperCase();
            PotionEffectType eff = PotionEffectType.getByName(effect);
            if (eff != null) {
                if (split[1].matches("[+]?[0-9]+") && Integer.parseInt(split[1]) != 0) {
                    int level = Integer.parseInt(split[1]) - 1;

                    Long dur = split[2].equalsIgnoreCase("inf") ? Long.valueOf(-1L) : MinigameUtils.parsePeriod(split[2]);
                    if (dur != null) {
                        dur = Math.max(-1, Math.min(dur, 100000)); // stay in range
                        dur = TimeUnit.MILLISECONDS.toSeconds(dur) * 20; // millis to ticks

                        List<Component> des = new ArrayList<>();
                        des.add(MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_DELETE_SHIFTRIGHTCLICK));

                        PotionEffect peff = new PotionEffect(eff, dur.intValue(), level);
                        for (int slot : getContainer().getUsedSlots()) {
                            if (getContainer().getMenuItem(slot) instanceof MenuItemPotion pot) {
                                if (pot.getEffect().getType() == peff.getType()) {
                                    pot.onShiftRightClick();
                                    break;
                                }
                            }
                        }
                        for (int i = 0; i < 36; i++) {
                            if (!getContainer().hasMenuItem(i)) {
                                getContainer().addItem(new MenuItemPotion(Material.POTION, Component.text(eff.getName().toLowerCase().replace("_", " ")), des, peff, loadout), i);
                                loadout.addPotionEffect(peff);
                                break;
                            }
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTIME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), split[2]));
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTNUMBER,
                            Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), split[2]));
                }
            } else {
                MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTPOTION,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), split[2]));
            }

            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());
        } else {
            getContainer().cancelReopenTimer();
            getContainer().displayMenu(getContainer().getViewer());

            MinigameMessageManager.sendMgMessage(getContainer().getViewer(), MinigameMessageType.ERROR, MgMenuLangKey.MENU_POTIONADD_ERROR_SYNTAX);
        }
    }
}
