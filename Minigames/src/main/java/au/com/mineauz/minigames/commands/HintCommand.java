package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.TreasureHuntModule;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HintCommand extends ACommand { //todo make subcommands for all treasure hunt ones e.a. /minigames tr hint;  /minigames tr maxheight etc.

    @Override
    public @NotNull String getName() {
        return "hint";
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_HINT_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_HINT_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.treasure.hint";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        MinigamePlayer player = PLUGIN.getPlayerManager().getMinigamePlayer((Player) sender);
        if (args.length > 0) {
            Minigame mgm = PLUGIN.getMinigameManager().getMinigame(args[0]);

            if (mgm != null && mgm.getMinigameTimer() != null && mgm.getType() == MinigameType.GLOBAL &&
                    mgm.getMechanicName().equals("treasure_hunt")) {
                TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgm);
                if (thm != null && thm.hasTreasureLocation() && !thm.isTreasureFound()) {
                    thm.getHints(player);
                } else {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTSTARTED,
                            Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgm.getName()));
                }
            } else if (mgm == null || mgm.getType() != MinigameType.GLOBAL) {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_HINT_ERROR_NOTTREASUREHUNT,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), args[0]));
            }
        } else {
            List<Minigame> mgs = new ArrayList<>();
            for (Minigame mg : PLUGIN.getMinigameManager().getAllMinigames().values()) {
                if (mg.getType() == MinigameType.GLOBAL && mg.getMechanicName().equals("treasure_hunt")) {
                    mgs.add(mg);
                }
            }
            if (!mgs.isEmpty()) {
                if (mgs.size() > 1) {
                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_HINT_LISTHUNTS,
                            Placeholder.component(MinigamePlaceHolderKey.TEXT.getKey(),
                                    Component.join(JoinConfiguration.commas(true), mgs.stream().map(Minigame::getDisplayName).toList())));

                } else {
                    TreasureHuntModule thm = TreasureHuntModule.getMinigameModule(mgs.get(0));
                    if (thm != null && thm.hasTreasureLocation() && !thm.isTreasureFound()) {
                        thm.getHints(player);
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.MINIGAME_ERROR_NOTSTARTED,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), mgs.get(0).getName()));
                    }
                }
            } else {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_HINT_ERROR_NORUNNING);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> mgs = new ArrayList<>();
            for (Minigame mg : PLUGIN.getMinigameManager().getAllMinigames().values()) {
                if (mg.getType() == MinigameType.GLOBAL && mg.getMechanicName().equals("treasure_hunt"))
                    mgs.add(mg.getName());
            }
            return MinigameUtils.tabCompleteMatch(mgs, args[0]);
        }
        return null;
    }

}
