package au.com.mineauz.minigames.gametypes;

import au.com.mineauz.minigames.MinigameMessageType;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class MinigameTypeBase implements Listener {
    private static Minigames plugin;
    private MinigameType type;

    protected MinigameTypeBase() {
        plugin = Minigames.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public MinigameType getType() {
        return type;
    }

    public void setType(MinigameType type) {
        this.type = type;
    }

    public abstract boolean cannotStart(Minigame mgm, MinigamePlayer player);

    public abstract boolean teleportOnJoin(MinigamePlayer player, Minigame mgm);

    /**
     * This should actually join the Player to the game Type
     *
     * @param player the player
     * @param mgm    the Game
     * @returns True if they join1
     */
    public abstract boolean joinMinigame(MinigamePlayer player, Minigame mgm);

    public abstract void quitMinigame(MinigamePlayer player, Minigame mgm, boolean forced);

    public abstract void endMinigame(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame mgm);

    public void callGeneralQuit(MinigamePlayer player, Minigame minigame) {
        if (!player.getPlayer().isDead()) {
            if (player.getPlayer().getWorld() != minigame.getQuitPosition().getWorld() && player.getPlayer().hasPermission("minigame.set.quit") && plugin.getConfig().getBoolean("warnings")) {
                player.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "Quit location is across worlds! This may cause some server performance issues!", MinigameMessageType.ERROR);
            }
            player.teleport(minigame.getQuitPosition());
        } else {
            player.setQuitPos(minigame.getQuitPosition());
            player.setRequiredQuit(true);
        }
    }

//    private static void giveRewardItem(MinigamePlayer player, RewardType reward){
//        if(!player.isInMinigame()){
//            if(!player.getPlayer().isDead())
//                player.getPlayer().getInventory().addItem(reward.getItem());
//            else{
//                int c = 0;
//                for(ItemStack i : player.getOfflineMinigamePlayer().getStoredItems()){
//                    if(i == null){
//                        player.getOfflineMinigamePlayer().getStoredItems()[c] = reward.getItem();
//                        break;
//                    }
//                    c++; //TODO: Add temp reward item to player instead and give it to them on respawn
//                }
//                player.getOfflineMinigamePlayer().savePlayerData();
//            }
//        }
//        else{
//            player.addTempRewardItem(reward.getItem());
//        }
//        player.sendMessage(MinigameUtils.formStr("player.end.awardItem", reward.getItem().getAmount(), MinigameUtils.getItemStackName(reward.getItem())), MinigameMessageType.WIN);
//    }
}
