package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.objects.ResourcePack;
import org.bukkit.command.CommandSender;

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
    public String getName() {
        return "resourcepack";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return MinigameUtils.getLang("minigame.resource.command.description");
    }

    @Override
    public String[] getParameters() {
        List<String> result = new ArrayList<>();
        result.add("apply");
        result.add( "addnew");
        result.add( "clear");
        result.add( "remove");
        result.add( "list");
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
    public String getPermission() {
        return "minigame.resourcepack.admin";
    }

    @Override
    public boolean onCommand(final CommandSender sender, Minigame minigame, String label, String[] args) {
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
                    player.sendInfoMessage(MinigameUtils.getLang("minigame.resourcepack.apply"));
                    return true;
                }
            case "remove":
                if (args.length < 2) {
                    return false;
                }
                pack = plugin.getResourceManager().getResourcePack(args[1]);
                plugin.getResourceManager().removeResourcePack(pack);
                sender.sendMessage(MinigameUtils.getLang("minigame.resourcepack.command.remove"));
                return sendList(sender);
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
                            sender.sendMessage(MinigameUtils.getLang("minigame.resourcepack.command.addresource"));
                            sendList(sender);
                        } else {
                            sender.sendMessage(MinigameUtils.getLang("minigame.resourcepack.command.invalidpack"));
                        }
                    }, 100);
                    return true;

                } catch (MalformedURLException e) {
                    sender.sendMessage(MinigameUtils.getLang("minigame.resourcepack.command.badurl"));
                    return false;
                }
            case "list":
                return sendList(sender);
            case "clear":
                for (MinigamePlayer p : plugin.getPlayerManager().getAllMinigamePlayers()) {
                    p.applyResourcePack(plugin.getResourceManager().getResourcePack("empty"));
                }
                return true;
        }
        return false;
    }

    private boolean sendList(CommandSender sender) {
        sender.sendMessage("List of ResourcePacks");
        Set<String> arr = plugin.getResourceManager().getResourceNames();
        for (String s : arr) {
            sender.sendMessage(s);
        }
        sender.sendMessage("--------------------");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1:
                Collections.addAll(result, getParameters());
                break;
            case 2:
                switch (args[0]) {
                    case "apply":
                    case "remove":
                        result.addAll(plugin.getResourceManager().getResourceNames());
                        break;
                    case "addnew":
                    case "clear":
                        return null;
                }
            case 3:
                switch (args[0]) {
                    case "apply":
                        for (MinigamePlayer p : plugin.getPlayerManager().getAllMinigamePlayers()) {
                            result.add(p.getName());
                        }
                }
        }
        return result;
    }
}
