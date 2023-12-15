package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.events.TakeFlagEvent;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.objects.CTFFlag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SignBase implements Listener {
    private static final Map<String, MinigameSign> minigameSigns = new HashMap<>();
    private final HashSet<CTFFlag> takenFlags = new HashSet<>();

    static {
        registerMinigameSign(new FinishSign());
        registerMinigameSign(new JoinSign());
        registerMinigameSign(new BetSign());
        registerMinigameSign(new CheckpointSign());
        registerMinigameSign(new FlagSign());
        registerMinigameSign(new QuitSign());
        registerMinigameSign(new LoadoutSign());
        registerMinigameSign(new TeleportSign());
        registerMinigameSign(new SpectateSign());
        registerMinigameSign(new RewardSign());
        registerMinigameSign(new TeamSign());
        registerMinigameSign(new ScoreboardSign());
        registerMinigameSign(new ScoreSign());
    }

    public SignBase() {
        Minigames.getPlugin().getServer().getPluginManager().registerEvents(this, Minigames.getPlugin());
    }

    public static void registerMinigameSign(MinigameSign mgSign) {
        minigameSigns.put(mgSign.getName().toLowerCase(), mgSign);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void takeFlag(@NotNull TakeFlagEvent event) {
        if (event.getFlag().getAttachedToLocation() != null) {
            this.takenFlags.add(event.getFlag());
        }
    }

    @EventHandler
    private void signPlace(SignChangeEvent event) {
        String[] signinfo = new String[4];
        for (int i = 0; i < 4; i++) {
            signinfo[i] = ChatColor.stripColor(event.getLine(i));
        }
        if ("[minigame]".equalsIgnoreCase(signinfo[0]) || "[mgm]".equalsIgnoreCase(signinfo[0]) || "[mg]".equals(signinfo[0])) {
            if (event.getSide() == Side.FRONT) {
                if (minigameSigns.containsKey(signinfo[1].toLowerCase())) {
                    event.setLine(0, ChatColor.DARK_BLUE + "[Minigame]");
                    MinigameSign mgSign = minigameSigns.get(signinfo[1].toLowerCase());

                    ((Sign) event.getBlock().getState()).setWaxed(true);

                    if (mgSign.getCreatePermission() != null && !event.getPlayer().hasPermission(mgSign.getCreatePermission())) {
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + mgSign.getCreatePermissionMessage());
                        return;
                    }

                    if (!mgSign.signCreate(event)) {
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + "Invalid Minigames sign!");
                    }
                } else {
                    Minigames.getPlugin().getPlayerManager().getMinigamePlayer(event.getPlayer()).sendMessage("Invalid Minigame sign!", MinigameMessageType.ERROR);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                }
            } else { //just gives an error but doesn't break the sign in case the front was important
                Minigames.getPlugin().getPlayerManager().getMinigamePlayer(event.getPlayer()).sendMessage("Invalid Minigame sign - used Backside!", MinigameMessageType.ERROR);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void signUse(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block cblock = event.getClickedBlock();
            if (cblock.getState() instanceof Sign sign) {
                if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Minigame]") &&
                        minigameSigns.containsKey(ChatColor.stripColor(sign.getLine(1).toLowerCase()))) {
                    MinigameSign mgSign = minigameSigns.get(ChatColor.stripColor(sign.getLine(1).toLowerCase()));

                    if (mgSign.getUsePermission() != null && !event.getPlayer().hasPermission(mgSign.getUsePermission())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + mgSign.getUsePermissionMessage());
                        return;
                    }

                    event.setCancelled(true);

                    mgSign.signUse(sign, Minigames.getPlugin().getPlayerManager().getMinigamePlayer(event.getPlayer()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void signBreak(BlockBreakEvent event) {
        if (Tag.ALL_SIGNS.isTagged(event.getBlock().getType())) {
            Sign sign = (Sign) event.getBlock().getState();
            if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Minigame]") &&
                    minigameSigns.containsKey(ChatColor.stripColor(sign.getLine(1).toLowerCase()))) {
                MinigameSign mgSign = minigameSigns.get(ChatColor.stripColor(sign.getLine(1).toLowerCase()));

                if (mgSign.getCreatePermission() != null && !event.getPlayer().hasPermission(mgSign.getCreatePermission())) {
                    event.setCancelled(true);
                    return;
                }
                mgSign.signBreak(sign, Minigames.getPlugin().getPlayerManager().getMinigamePlayer(event.getPlayer()));
            }
        } else {
            Location blockLocation = event.getBlock().getLocation().toBlockLocation();

            for (CTFFlag ctfFlag : takenFlags) {
                if (ctfFlag.getAttachedToLocation().equals(blockLocation)) {
                    MinigameSign mgSign = minigameSigns.get("Flag");

                    if (mgSign.getCreatePermission() != null && !event.getPlayer().hasPermission(mgSign.getCreatePermission())) {
                        event.setCancelled(true);
                        return;
                    } else {
                        // todo what to do here? The Flag will not be able to respawn!
                    }

                    break;
                } else if (ctfFlag.getMinigame().getState() != MinigameState.STARTED) {
                    takenFlags.remove(ctfFlag);
                }
            }

        }
    }
}
