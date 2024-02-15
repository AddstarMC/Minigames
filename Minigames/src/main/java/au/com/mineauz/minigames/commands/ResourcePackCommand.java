package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.managers.language.langkeys.MinigameLangKey;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ResourcePack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 15/02/2019.
 */
public class ResourcePackCommand extends ACommand {
    @Override
    public @NotNull String getName() {
        return "resourcepack";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_RESSOUCEPACK_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_RESSOUCEPACK_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.resourcepack.admin";
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        ResourcePack pack;
        switch (args[0]) {
            case "apply" -> {
                if (args.length < 2) {
                    return false;
                }
                pack = PLUGIN.getResourceManager().getResourcePack(args[1]);
                if (pack != null && pack.isValid()) {
                    MinigamePlayer mgPlayer;
                    if (args.length < 3) {
                        if (sender instanceof Player player) {
                            mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(player);
                        } else {
                            return false;
                        }
                    } else {
                        mgPlayer = PLUGIN.getPlayerManager().getMinigamePlayer(args[2]);

                        if (mgPlayer == null) {
                            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_ERROR_SENDERNOTAPLAYER,
                                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), args[2]));
                            return false;
                        }
                    }

                    mgPlayer.applyResourcePack(pack);
                    MinigameMessageManager.sendMgMessage(mgPlayer, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_RESSOURCEPACK_APPLY);
                }
                return true;
            }
            case "remove" -> { // note: there was no way to remove a ressource for a player, without overwriting it with an empty one. Maybe it will be possible when mutli- ressouce packs arrive
                if (args.length >= 2) {
                    pack = PLUGIN.getResourceManager().getResourcePack(args[1]);
                    PLUGIN.getResourceManager().removeResourcePack(pack);

                    MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MinigameLangKey.MINIGAME_RESSOURCEPACK_REMOVE);
                    sendList(sender);
                    return true;
                } else {
                    return false;
                }
            }
            case "addnew" -> {
                if (args.length >= 3) {
                    try {
                        URL url = new URL(args[2]);
                        final ResourcePack newPack = new ResourcePack(MiniMessage.miniMessage().deserialize(args[1]), url);
                        PLUGIN.getServer().getScheduler().runTaskLaterAsynchronously(PLUGIN, () -> {
                            if (newPack.isValid()) {
                                PLUGIN.getResourceManager().addResourcePack(newPack);
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_RESSOUCEPACK_ADDRESOURCE_SUCCESS);
                                sendList(sender);
                            } else {
                                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_RESSOUCEPACK_ADDRESOURCE_ERROR_INVALID);
                            }
                        }, 100);
                        return true;

                    } catch (MalformedURLException e) {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_RESSOUCEPACK_ADDRESOURCE_ERROR_BADURL);
                        return false;
                    }
                } else {
                    return false;
                }
            }
            case "list" -> {
                sendList(sender);
                return true;
            }
            case "clear" -> {
                for (MinigamePlayer mgPlayer : PLUGIN.getPlayerManager().getAllMinigamePlayers()) {
                    mgPlayer.applyResourcePack(PLUGIN.getResourceManager().getResourcePack("empty"));
                }
                return true;
            }
        }
        return false;
    }

    private void sendList(@NotNull CommandSender sender) {
        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_RESSOUCEPACK_LIST_HEADER);
        MinigameMessageManager.sendMessage(sender, MinigameMessageType.NONE,
                Component.join(JoinConfiguration.commas(true),
                        PLUGIN.getResourceManager().getResourcePacks().stream().map(ResourcePack::getDisplayName).toList()));
        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.NONE, MgCommandLangKey.COMMAND_DIVIDER_LARGE);
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                result.addAll(List.of("apply", "remove", "addnew", "list", "clear"));
            }
            case 2 -> {
                switch (args[0]) {
                    case "apply", "remove" -> result.addAll(PLUGIN.getResourceManager().getResourceNames());
                    case "addnew", "clear" -> {
                        return null;
                    }
                }
            }
            case 3 -> {
                if (args[0].equals("apply")) {
                    for (MinigamePlayer p : PLUGIN.getPlayerManager().getAllMinigamePlayers()) {
                        result.add(p.getName());
                    }
                }
            }
        }
        return result;
    }
}
