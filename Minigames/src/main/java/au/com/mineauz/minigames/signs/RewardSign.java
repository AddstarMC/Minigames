package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameManager;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.menu.*;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardSign implements MinigameSign {

    private static final Minigames plugin = Minigames.getPlugin();
    private final MinigameManager mdata = plugin.getMinigameManager();

    @Override
    public @NotNull String getName() {
        return "Reward";
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
            event.setLine(1, ChatColor.GREEN + getName());
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
                        Rewards rew = mdata.getRewardSign(loc);
                        for (RewardType r : rew.getReward()) {
                            r.giveReward(mgPlayer);
                        }
                    }
                    mgPlayer.addTempClaimedReward(label);
                }
            } else {
                if (!mgPlayer.hasClaimedReward(label)) {
                    if (mdata.hasRewardSign(loc)) {
                        Rewards rew = mdata.getRewardSign(loc);
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
            rew = mdata.getRewardSign(loc);

            Menu rewardMenu = new Menu(5, getName(), mgPlayer);

            rewardMenu.addItem(new MenuItemRewardGroupAdd("Add Group", MenuUtility.getCreateMaterial(), rew), 42);
            rewardMenu.addItem(new MenuItemRewardAdd("Add Item", MenuUtility.getCreateMaterial(), rew), 43);
            final MenuItemCustom mic = new MenuItemCustom("Save Rewards", MenuUtility.getSaveMaterial());
            final Location floc = loc;
            mic.setClick(object -> {
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

            List<MenuItem> mi = new ArrayList<>();
            for (RewardType item : rew.getRewards()) {
                mi.add(item.getMenuItem());
            }
            List<String> des = new ArrayList<>();
            des.add("Double Click to edit");
            for (RewardGroup group : rew.getGroups()) {
                MenuItemRewardGroup rwg = new MenuItemRewardGroup(group.getName() + " Group", des, Material.CHEST, group, rew);
                mi.add(rwg);
            }
            rewardMenu.addItems(mi);
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
