package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.FloorDegenerator;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MgRegion;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetFloorDegeneratorCommand extends ASetCommand {

    @Override
    public @NotNull String getName() {
        return "floordegenerator";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"floord", "floordegen"};
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_FLOORDEGEN_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_SET_FLOORDEGEN_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.floordegenerator";
    }

    //todo this can easily expanded, so multiple degen regions are possible. Will implement, if needed.
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {

        if (args != null) {
            if (sender instanceof Player player) {
                MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
                Location placerLoc = mgPlayer.getLocation();

                switch (args[0].toLowerCase()) {
                    case "1" -> {
                        Location firstLoc = mgPlayer.getSelectionLocations()[1];
                        mgPlayer.clearSelection();
                        mgPlayer.setSelection(placerLoc, firstLoc);

                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.REGION_SELECT_POINT,
                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), "1"));
                    }
                    case "2" -> {
                        Location secondLoc = mgPlayer.getSelectionLocations()[0];
                        mgPlayer.clearSelection();
                        mgPlayer.setSelection(secondLoc, placerLoc);

                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.REGION_SELECT_POINT,
                                Placeholder.unparsed(MinigamePlaceHolderKey.NUMBER.getKey(), "2"));
                    }
                    case "create" -> {
                        if (mgPlayer.hasSelection()) {
                            minigame.setFloorDegen(new MgRegion("degen", mgPlayer.getSelectionLocations()[0], mgPlayer.getSelectionLocations()[1]));

                            mgPlayer.clearSelection();

                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLOORDEGEN_CREATE,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                        } else {
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MinigameLangKey.REGION_ERROR_NOSELECTION);
                        }
                    }
                    case "clear" -> {
                        minigame.removeFloorDegen();
                        MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLOORDEGEN_CLEAR,
                                Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                    }
                    case "type" -> {
                        if (args.length >= 2) {
                            FloorDegenerator.DegeneratorType type = FloorDegenerator.DegeneratorType.matchType(args[1]);

                            if (type != null) {
                                minigame.setDegenType(type);

                                if (args.length > 2 && args[2].matches("[0-9]+")) {
                                    minigame.setDegenRandomChance(Integer.parseInt(args[2]));
                                }

                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLOORDEGEN_TYPE,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), args[1]),
                                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName()));
                            } else {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_SET_FLOORDEGEN_ERROR_NOTYPE,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(),
                                                String.join(", ", Arrays.stream(FloorDegenerator.DegeneratorType.values()).map(Enum::name).toList())));
                            }
                        }
                    }
                    case "time" -> {
                        if (args.length >= 2) {
                            Long millis = MinigameUtils.parsePeriod(String.join(" ", args));

                            if (millis != null) {
                                minigame.setFloorDegenTime(TimeUnit.MILLISECONDS.toSeconds(millis));
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_SET_FLOORDEGEN_TIME,
                                        Placeholder.component(MinigamePlaceHolderKey.TIME.getKey(), MinigameUtils.convertTime(Duration.ofMillis(millis))));
                            } else {
                                MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_NOTTIME,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                            }
                        }
                    }
                    default ->
                            MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_UNKNOWN_PARAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        if (args != null) {
            if (args.length == 1) {
                return MinigameUtils.tabCompleteMatch(List.of("1", "2", "create", "clear", "type", "time"), args[0]);
            } else if (args[0].equalsIgnoreCase("type")) {
                return MinigameUtils.tabCompleteMatch(List.of("random", "inward", "circle"), args[1]);
            } else if (args[0].equalsIgnoreCase("time")) {
                return List.of("s", "m", "h");
            }
        }
        return null;
    }

}
