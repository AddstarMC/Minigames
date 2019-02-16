package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.*;
import au.com.mineauz.minigames.managers.MinigameManager;
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

import java.util.ArrayList;
import java.util.List;

public class RewardSign implements MinigameSign {

    private static Minigames plugin = Minigames.getPlugin();
    private MinigameManager mdata = plugin.getMinigameManager();

    @Override
    public String getName() {
        return "Reward";
    }

    @Override
    public String getCreatePermission() {
        return "minigame.sign.create.reward";
    }

    @Override
    public String getCreatePermissionMessage() {
        return MinigameUtils.getLang("sign.reward.createPermission");
    }

    @Override
    public String getUsePermission() {
        return "minigame.sign.use.reward";
    }

    @Override
    public String getUsePermissionMessage() {
        return MinigameUtils.getLang("sign.reward.usePermission");
    }

    @Override
    public boolean signCreate(SignChangeEvent event) {
        if (!event.getLine(2).equals("")) {
            event.setLine(1, ChatColor.GREEN + getName());
            return true;
        }
        plugin.getPlayerManager().getMinigamePlayer(event.getPlayer()).sendMessage(MinigameUtils.getLang("sign.reward.noName"), MinigameMessageType.ERROR);
        return false;
    }

    @Override
    public boolean signUse(Sign sign, MinigamePlayer player) {
        Location loc = sign.getLocation();
        if (!MinigameUtils.isMinigameTool(player.getPlayer().getInventory().getItemInMainHand())) {
            String label = sign.getLine(2).toLowerCase();
            if (player.isInMinigame()) {
                if (!player.hasTempClaimedReward(label)) {
                    if (mdata.hasRewardSign(loc)) {
                        Rewards rew = mdata.getRewardSign(loc);
                        for (RewardType r : rew.getReward()) {
                            r.giveReward(player);
                        }
                    }
                    player.addTempClaimedReward(label);
                }
            } else {
                if (!player.hasClaimedReward(label)) {
                    if (mdata.hasRewardSign(loc)) {
                        Rewards rew = mdata.getRewardSign(loc);
                        for (RewardType r : rew.getReward()) {
                            r.giveReward(player);
                        }

                        player.updateInventory();
                    }
                    player.addClaimedReward(label);
                }
            }
        } else if (player.getPlayer().hasPermission("minigame.tool")) {
            Rewards rew = null;
            if (!mdata.hasRewardSign(loc)) {
                mdata.addRewardSign(loc);
            }
            rew = mdata.getRewardSign(loc);

            Menu rewardMenu = new Menu(5, getName(), player);

            rewardMenu.addItem(new MenuItemRewardGroupAdd("Add Group", MenuUtility.getCreateMaterial(), rew), 42);
            rewardMenu.addItem(new MenuItemRewardAdd("Add Item", MenuUtility.getCreateMaterial(), rew), 43);
            final MenuItemCustom mic = new MenuItemCustom("Save Rewards", MenuUtility.getSaveMaterial());
            final Location floc = loc;
            mic.setClick(object -> {
                mdata.saveRewardSign(MinigameUtils.createLocationID(floc), true);
                mic.getContainer().getViewer().sendInfoMessage("Saved rewards for this sign.");
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
            rewardMenu.displayMenu(player);
        }
        return true;
    }

    @Override
    public void signBreak(Sign sign, MinigamePlayer player) {
        if (plugin.getMinigameManager().hasRewardSign(sign.getLocation())) {
            plugin.getMinigameManager().removeRewardSign(sign.getLocation());
        }
    }

}
