package au.com.mineauz.minigames.signs;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.TakeCTFFlagEvent;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.langkeys.MgSignLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.MinigameState;
import au.com.mineauz.minigames.objects.CTFFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class SignBase implements Listener {
    private static final List<AMinigameSign> minigameSigns = new ArrayList<>();
    private static final Pattern alternativeMgmPattern = Pattern.compile("(?:\\[mgm])|(?:\\[mg])", Pattern.CASE_INSENSITIVE);
    private final HashSet<CTFFlag> takenFlags = new HashSet<>();

    static {
        registerMinigameSign(new FinishSign());
        registerMinigameSign(new JoinSign());
        registerMinigameSign(new BetSign());
        registerMinigameSign(new CheckpointSign());
        registerMinigameSign(new CTFFlagSign());
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

    public static void registerMinigameSign(AMinigameSign mgSign) {
        minigameSigns.add(mgSign);
    }

    private @Nullable AMinigameSign getMgSign(@NotNull Component secondLine) {
        // don't use a map here, with names as keys since it might be possible to reload messages via command
        for (AMinigameSign mgSign : minigameSigns) {
            if (mgSign.isType(secondLine)) {
                return mgSign;
            }
        }

        return null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void takeFlag(@NotNull TakeCTFFlagEvent event) {
        if (event.getFlag().getAttachedToLocation() != null) {
            this.takenFlags.add(event.getFlag());
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void signPlace(SignChangeEvent event) {
        String firstLine = PlainTextComponentSerializer.plainText().serialize(event.line(0));

        if (MinigameMessageManager.getUnformattedMgMessage(MgSignLangKey.MINIGAME).equalsIgnoreCase(firstLine) ||
                alternativeMgmPattern.matcher(firstLine).matches()) {
            if (event.getSide() == Side.FRONT) {
                AMinigameSign mgSign = getMgSign(event.line(2));

                if (mgSign != null) {
                    event.line(0, MinigameMessageManager.getMgMessage(MgSignLangKey.MINIGAME));
                    ((Sign) event.getBlock().getState()).setWaxed(true);

                    if (mgSign.getCreatePermission() != null && !event.getPlayer().hasPermission(mgSign.getCreatePermission())) {
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
                        return;
                    }

                    if (!mgSign.signCreate(event)) {
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_INVALID);
                    }
                } else {
                    MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_INVALID);
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                }
            } else { //just gives an error but doesn't break the sign in case the front was important
                MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.SIGN_ERROR_BACKSIDE);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void signUse(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block cblock = event.getClickedBlock();
            if (cblock != null && cblock.getState() instanceof Sign sign) {
                String firstLine = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(0));
                AMinigameSign mgSign = getMgSign(sign.getSide(Side.FRONT).line(2));
                if (MinigameMessageManager.getUnformattedMgMessage(MgSignLangKey.MINIGAME).equalsIgnoreCase(firstLine) && mgSign != null) {

                    if (mgSign.getUsePermission() != null && !event.getPlayer().hasPermission(mgSign.getUsePermission())) {
                        event.setCancelled(true);
                        MinigameMessageManager.sendMgMessage(event.getPlayer(), MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOPERMISSION);
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
            String firstLine = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(0));
            AMinigameSign mgSign = getMgSign(sign.getSide(Side.FRONT).line(2));
            if (MinigameMessageManager.getUnformattedMgMessage(MgSignLangKey.MINIGAME).equalsIgnoreCase(firstLine) && mgSign != null) {

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
                    // new creation for easy access to permissions.
                    // I wish once again that you could define abstract static methods,
                    // so we could access static values like permissions without an object
                    // but guarantee that this methode exists
                    AMinigameSign mgSign = new CTFFlagSign();

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
