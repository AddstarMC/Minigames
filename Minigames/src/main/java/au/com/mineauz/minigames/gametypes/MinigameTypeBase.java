package au.com.mineauz.minigames.gametypes;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

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

    public abstract boolean teleportOnJoin(MinigamePlayer mgPlayer, Minigame mgm);

    /**
     * This should actually join the Player to the game Type
     *
     * @param player the player
     * @param mgm    the Game
     * @return True if they join1
     */
    public abstract boolean joinMinigame(@NotNull MinigamePlayer player, @NotNull Minigame mgm);

    public abstract void quitMinigame(@NotNull MinigamePlayer player, Minigame mgm, boolean forced);

    public abstract void endMinigame(List<MinigamePlayer> winners, List<MinigamePlayer> losers, Minigame mgm);

    public void callGeneralQuit(MinigamePlayer player, Minigame minigame) {
        if (!player.getPlayer().isDead()) {
            if (player.getPlayer().getWorld() != minigame.getQuitLocation().getWorld() && player.getPlayer().hasPermission("minigame.set.quit") &&
                    plugin.getConfig().getBoolean("warnings")) {
                MinigameMessageManager.sendMgMessage(player, MinigameMessageType.WARNING, MinigameLangKey.MINIGAME_WARNING_TELEPORT_ACROSS_WORLDS);
            }
            player.teleport(minigame.getQuitLocation());
        } else {
            player.setQuitPos(minigame.getQuitLocation());
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
