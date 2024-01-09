package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameLangKey;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LobbySettingsModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetLobbyCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "lobby";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_LOBBY_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MinigameLangKey.COMMAND_SET_LOBBY_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.lobby";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args == null) {
            if (sender instanceof Entity entity) {
                minigame.setLobbyLocation(entity.getLocation());
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_LOBBY_LOCATION,
                        Placeholder.unparsed(MinigamePlaceHolderKey.MINIGAME.getKey(), minigame.getName(false)));
            } else { // not a player
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTAPLAYER);
            }
        } else {
            LobbySettingsModule lobby = LobbySettingsModule.getMinigameModule(minigame);
            if (args.length == 3) {

                switch (args[0].toLowerCase()) {
                    case "canmove" -> {
                        Boolean canMove = BooleanUtils.toBooleanObject(args[2]);
                        if (canMove != null) {
                            if (args[1].equalsIgnoreCase("playerwait")) {
                                lobby.setCanMovePlayerWait(canMove);

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_LOBBY_CANMOVE_PLAYERWAIT,
                                        Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                                canMove ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                            } else if (args[1].equalsIgnoreCase("startwait")) {
                                lobby.setCanMoveStartWait(canMove);

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_LOBBY_CANMOVE_START,
                                        Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                                canMove ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_UNKNOWN_PARAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                                return false;
                            }
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTBOOL,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[2]));
                        }
                    }
                    case "caninteract" -> {
                        Boolean canInteract = BooleanUtils.toBooleanObject(args[2]);
                        if (canInteract != null) {
                            if (args[1].equalsIgnoreCase("playerwait")) {
                                lobby.setCanInteractPlayerWait(canInteract);

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_LOBBY_CANINTERACT_PLAYERWAIT,
                                        Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                                canInteract ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                            } else if (args[1].equalsIgnoreCase("startwait")) {
                                lobby.setCanInteractStartWait(canInteract);

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_LOBBY_CANINTERACT_STARTWAIT,
                                        Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                                canInteract ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_UNKNOWN_PARAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                                return false;
                            }
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTBOOL,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[2]));
                        }
                    }
                    case "teleport" -> {
                        Boolean teleport = BooleanUtils.toBooleanObject(args[2]);
                        if (teleport != null) {
                            if (args[1].equalsIgnoreCase("playerwait")) {
                                lobby.setTeleportOnPlayerWait(teleport);

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_LOBBY_TELEPORT_PLAYERWAIT,
                                        Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                                teleport ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                            } else if (args[1].equalsIgnoreCase("startwait")) {
                                lobby.setTeleportOnStart(teleport);

                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.COMMAND_SET_LOBBY_TELEPORT_STARTWAIT,
                                        Placeholder.component(MinigamePlaceHolderKey.STATE.getKey(), MinigameMessageManager.getMgMessage(
                                                teleport ? MinigameLangKey.COMMAND_STATE_ENABLED : MinigameLangKey.COMMAND_STATE_DISABLED)));
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_UNKNOWN_PARAM,
                                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                                return false;
                            }
                        } else {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTBOOL,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[2]));
                        }
                    }
                    default ->
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_UNKNOWN_PARAM,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[0]));
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("playerWait")) {
                    if (args[1].matches("[0-9]+")) {
                        int value = Integer.parseInt(args[1]);
                        if (value < 0) {
                            value = 0;
                        }

                        lobby.setPlayerWaitTime(value);

                        if (value == 0) {
                            sender.sendMessage(ChatColor.GRAY + "Using the default player wait time.");
                        } else {
                            sender.sendMessage(ChatColor.GRAY + "Using player wait time of " + value + " seconds.");
                        }
                    } else {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MinigameLangKey.COMMAND_ERROR_NOTNUMBER,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[1]));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull Minigame minigame,
                                                         @NotNull String @Nullable [] args) {
        if (args != null && args.length > 0) {
            return switch (args.length) {
                case 1 ->
                        MinigameUtils.tabCompleteMatch(List.of("canmove", "caninteract", "teleport", "playerWait"), args[args.length - 1]);
                case 2 -> MinigameUtils.tabCompleteMatch(List.of("playerwait", "startwait"), args[args.length - 1]);
                default -> MinigameUtils.tabCompleteMatch(List.of("true", "false"), args[args.length - 1]);
            };
        }
        return null;
    }
}
