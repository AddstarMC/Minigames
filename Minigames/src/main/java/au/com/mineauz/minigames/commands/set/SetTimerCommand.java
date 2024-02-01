package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameTimer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetTimerCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "timer";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_TIMER_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_TIMER_USAGE);
    }

    public @Nullable String getPermission() {
        return "minigame.set.timer";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null) {
            Long millis = MinigameUtils.parsePeriod(args[0]);

            if (millis != null) {
                minigame.setTimer(TimeUnit.MILLISECONDS.toSeconds(millis));
                if (millis <= 0) {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_TIMER_SUCCESS,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)),
                            Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofMillis(millis))));
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_TIMER_REMOVE,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("display") && args.length >= 2){ //todo
                if (args[1].equalsIgnoreCase("xpBar")) {
                    minigame.setTimerDisplayType(MinigameTimer.DisplayType.XP_BAR);
                    sender.sendMessage(ChatColor.GRAY + minigame.toString() + " will now show the timer in the XP bar.");
                    return true;
                } else if (args[1].equalsIgnoreCase("bossBar")) {
                    minigame.setTimerDisplayType(MinigameTimer.DisplayType.BOSS_BAR);
                    sender.sendMessage(ChatColor.GRAY + minigame.toString() + " will now show the timer in the boss bar.");
                    return true;
                } else if (args[1].equalsIgnoreCase("none")) {
                    minigame.setTimerDisplayType(MinigameTimer.DisplayType.NONE);
                    sender.sendMessage(ChatColor.GRAY + minigame.toString() + " will no longer show the timer.");
                    return true;
                } // else here will default to false
            }
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull @Nullable [] args) {
        return null;
    }

}
