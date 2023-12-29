package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 15/02/2019.
 */
public class ResourcePackCommand implements ICommand {
    @Override
    public @NotNull String getName() {
        return "resourcepack";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[0];
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMessage(null, "minigame.resource.command.description");
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        List<String> result = new ArrayList<>();
        result.add("apply");
        result.add("addnew");
        result.add("clear");
        result.add("remove");
        result.add("list");
        String[] res = new String[result.size()];
        result.toArray(res);
        return res;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "/minigame resourcepack <options>"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You cannot use this command";
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.resourcepack.admin";
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, Minigame minigame, @NotNull String label, @NotNull String @Nullable [] args) {
        ResourcePack pack;
        switch (args[0]) {
            case "apply":
                if (args.length < 3) {
                    sender.sendMessage(getUsage());
                    return false;
                }
                pack = plugin.getResourceManager().getResourcePack(args[1]);
                if (pack != null && pack.isValid()) {
                    MinigamePlayer player = plugin.getPlayerManager().getMinigamePlayer(args[2]);
                    player.applyResourcePack(pack);
                    player.sendInfoMessage(MinigameMessageManager.getMessage(null, "minigame.resourcepack.apply"));
                    return true;
                }
            case "remove":
                if (args.length < 2) {
                    return false;
                }
                pack = plugin.getResourceManager().getResourcePack(args[1]);
                plugin.getResourceManager().removeResourcePack(pack);
                sender.sendMessage(MinigameMessageManager.getMessage(null, "minigame.resourcepack.command.remove"));
                sendList(sender);
                return true;
            case "addnew":
                if (args.length < 3) {
                    sender.sendMessage(getUsage());
                    return false;
                }
                String name = args[1];
                String u = args[2];
                try {
                    URL url = new URL(u);
                    final ResourcePack newPack = new ResourcePack(name, url);
                    plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                        if (newPack.isValid()) {
                            plugin.getResourceManager().addResourcePack(newPack);
                            sender.sendMessage(MinigameMessageManager.getMessage(null, "minigame.resourcepack.command.addresource"));
                            sendList(sender);
                        } else {
                            sender.sendMessage(MinigameMessageManager.getMessage(null, "minigame.resourcepack.command.invalidpack"));
                        }
                    }, 100);
                    return true;

                } catch (MalformedURLException e) {
                    sender.sendMessage(MinigameMessageManager.getMessage(null, "minigame.resourcepack.command.badurl"));
                    return false;
                }
            case "list":
                sendList(sender);
                return true;
            case "clear":
                for (MinigamePlayer p : plugin.getPlayerManager().getAllMinigamePlayers()) {
                    p.applyResourcePack(plugin.getResourceManager().getResourcePack("empty"));
                }
                return true;
        }
        return false;
    }

    private void sendList(CommandSender sender) {
        sender.sendMessage("List of ResourcePacks");
        Set<String> arr = plugin.getResourceManager().getResourceNames();
        for (String s : arr) {
            sender.sendMessage(s);
        }
        sender.sendMessage("--------------------");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame, String alias, @NotNull String @NotNull [] args) {
        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1:
                Collections.addAll(result, getParameters());
                break;
            case 2:
                switch (args[0]) {
                    case "apply", "remove" -> result.addAll(plugin.getResourceManager().getResourceNames());
                    case "addnew", "clear" -> {
                        return null;
                    }
                }
            case 3:
                if (args[0].equals("apply")) {
                    for (MinigamePlayer p : plugin.getPlayerManager().getAllMinigamePlayers()) {
                        result.add(p.getName());
                    }
                }
        }
        return result;
    }
}
