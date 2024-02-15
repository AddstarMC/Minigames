package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgMenuLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgSignLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardSign extends AMinigameSign {
    private static final Minigames plugin = Minigames.getPlugin();
    private final MinigameManager mdata = plugin.getMinigameManager();

    @Override
    public @NotNull Component getName() {
        return MinigameMessageManager.getMgMessage(MgSignLangKey.TYPE_REWARD);
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.reward";
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.reward";
    }

    @Override
    public boolean signCreate(@NotNull SignChangeEvent event) {
        if (!event.getLine(2).isEmpty()) {
            event.line(1, getName());
            return true;
        }
        MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_REWARD_ERROR_NONAME);
        return false;
    }

    @Override
    public boolean signUse(@NotNull Sign sign, @NotNull MinigamePlayer mgPlayer) {
        Location loc = sign.getLocation();
        if (!MinigameUtils.isMinigameTool(mgPlayer.getPlayer().getInventory().getItemInMainHand())) {
            String label = sign.getLine(2).toLowerCase();
            if (mgPlayer.isInMinigame()) {
                if (!mgPlayer.hasTempClaimedReward(label)) {
                    if (mdata.hasRewardSign(loc)) {
                        Rewards rew = mdata.getRewardsRewardSign(loc);
                        for (RewardType r : rew.getReward()) {
                            r.giveReward(mgPlayer);
                        }
                    }
                    mgPlayer.addTempClaimedReward(label);
                }
            } else {
                if (!mgPlayer.hasClaimedReward(label)) {
                    if (mdata.hasRewardSign(loc)) {
                        Rewards rew = mdata.getRewardsRewardSign(loc);
                        for (RewardType r : rew.getReward()) {
                            r.giveReward(mgPlayer);
                        }

                        mgPlayer.updateInventory();
                    }
                    mgPlayer.addClaimedReward(label);
                }
            }
        } else if (mgPlayer.getPlayer().hasPermission("minigame.tool")) {
            Rewards rew;
            if (!mdata.hasRewardSign(loc)) {
                mdata.addRewardSign(loc);
            }
            rew = mdata.getRewardsRewardSign(loc);

            Menu rewardMenu = new Menu(5, getName(), mgPlayer);

            rewardMenu.addItem(new MenuItemRewardGroupAdd(MenuUtility.getCreateMaterial(),
                    MgMenuLangKey.MENU_REWARD_GROUP_ADD_NAME, rew), 42);
            rewardMenu.addItem(new MenuItemRewardAdd(MenuUtility.getCreateMaterial(), MgMenuLangKey.MENU_REWARD_ITEM_ADD_NAME, rew), 43);
            final MenuItemCustom mic = new MenuItemCustom(MenuUtility.getSaveMaterial(), MgMenuLangKey.MENU_REWARD_SAVE_ALL_NAME);
            final Location floc = loc;
            mic.setClick(() -> {
                mdata.saveRewardSign(MinigameUtils.createLocationID(floc), true);
                MinigameMessageManager.sendMgMessage(mic.getContainer().getViewer(), MinigameMessageType.INFO, MinigameLangKey.SIGN_REWARD_SAVED);
                mic.getContainer().getViewer().getPlayer().closeInventory();
                return null;
            });
            rewardMenu.addItem(mic, 44);
            //List<String> list = new ArrayList<String>();
            //for(RewardRarity r : RewardRarity.values()){
            //    list.add(r.toString());
            //}

            List<MenuItem> menuItems = new ArrayList<>();
            for (RewardType item : rew.getRewards()) {
                menuItems.add(item.getMenuItem());
            }

            List<Component> des = MinigameMessageManager.getMgMessageList(MgMenuLangKey.MENU_EDIT_DOUBLECLICK);
            for (RewardGroup group : rew.getGroups()) {
                MenuItemRewardGroup rwg = new MenuItemRewardGroup(Material.CHEST,
                        MinigameMessageManager.getMgMessage(MgMenuLangKey.MENU_REWARD_GROUP_NAME,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), group.getName())),
                        des, group, rew);
                menuItems.add(rwg);
            }
            rewardMenu.addItems(menuItems);
            rewardMenu.displayMenu(mgPlayer);
        }
        return true;
    }

    @Override
    public void signBreak(@NotNull Sign sign, MinigamePlayer mgPlayer) {
        if (plugin.getMinigameManager().hasRewardSign(sign.getLocation())) {
            plugin.getMinigameManager().removeRewardSign(sign.getLocation());
        }
    }
}
