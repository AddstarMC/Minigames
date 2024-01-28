package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kitteh.pastegg.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DebugCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "debug";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Debugs stuff.";
    }

    @Override
    public Component getUsage() {
        return new String[]{"/minigame debug"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.debug";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args != null && args.length > 0) {
            switch (args[0].toUpperCase()) {
                case "ON" -> {
                    if (Minigames.getPlugin().isDebugging()) {
                        sender.sendMessage(ChatColor.GRAY + "Debug mode already active.");
                    } else {
                        Minigames.getPlugin().toggleDebug();
                        sender.sendMessage(ChatColor.GRAY + "Debug mode active.");
                    }
                }
                case "OFF" -> {
                    if (!Minigames.getPlugin().isDebugging()) {
                        sender.sendMessage(ChatColor.GRAY + "Debug mode already inactive.");
                    } else {
                        Minigames.getPlugin().toggleDebug();
                        sender.sendMessage(ChatColor.GRAY + "Debug mode inactive.");
                    }
                }
                case "PASTE" -> {
                    sender.sendMessage(ChatColor.GRAY + "Generating a paste.....");
                    generatePaste(sender, minigame);
                }
                default -> {
                    return false;
                }
            }
        } else {
            Minigames.getPlugin().toggleDebug();

            if (Minigames.getPlugin().isDebugging())
                sender.sendMessage(ChatColor.GRAY + "Debug mode active.");
            else
                sender.sendMessage(ChatColor.GRAY + "Deactivated debug mode.");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 0) {
            out.add("NO");
            out.add("YES");
            out.add("PASTE");
        }
        return out;
    }

    private String getFile(Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return ExceptionUtils.getStackTrace(e);
        }
    }

    private void generatePaste(CommandSender sender, Minigame minigame) {
        StringBuilder mainInfo = new StringBuilder(); //todo
        mainInfo.append(Bukkit.getName()).append(" version: ").append(Bukkit.getServer().getVersion()).append('\n');
        mainInfo.append("Plugin version: ").append(Minigames.getPlugin().getDescription().getVersion()).append('\n');
        mainInfo.append("Java version: ").append(System.getProperty("java.version")).append('\n');
        mainInfo.append('\n');
        mainInfo.append("Plugins:\n");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            mainInfo.append(' ').append(plugin.getName()).append(" - ").append(plugin.getDescription().getVersion()).append('\n');
            mainInfo.append("  ").append(plugin.getDescription().getAuthors()).append('\n');
        }
        Bukkit.getScheduler().runTaskAsynchronously(Minigames.getPlugin(), () -> {
            Path dataPath = Minigames.getPlugin().getDataFolder().toPath();

            String apiKey = Minigames.getPlugin().getConfig().getString("pasteApiKey", null);
            PasteFile config = new PasteFile("config.yml",
                    new PasteContent(PasteContent.ContentType.TEXT,
                            getFile(dataPath.resolve("config.yml"))));
            PasteFile spigot = new PasteFile("spigot.yml",
                    new PasteContent(PasteContent.ContentType.TEXT,
                            getFile(Paths.get("spigot.yml"))));
            PasteFile startupLog = new PasteFile("startup.log", new PasteContent(PasteContent.ContentType.TEXT,
                    plugin.getStartupLog()));
            PasteFile startupExceptionsLog = new PasteFile("startupExceptions.log", new PasteContent(PasteContent.ContentType.TEXT,
                    plugin.getStartupExceptionLog()));
            PasteBuilder builder = new PasteBuilder();
            builder.addFile(startupLog);
            builder.addFile(startupExceptionsLog);
            try {
                PasteBuilder.PasteResult result = builder
                        .setApiKey(apiKey)
                        .name("Minigames Debug Outpout")
                        .visibility(Visibility.UNLISTED)
                        .addFile(spigot)
                        .addFile(config)
                        .debug(Minigames.getPlugin().isDebugging())
                        .build();
                if (result.getPaste().isPresent()) {
                    Paste paste = result.getPaste().get();
                    sender.sendMessage("Debug Paste: https://paste.gg/" + paste.getId());
                    sender.sendMessage("Deletion Key: " + paste.getDeletionKey());
                    Minigames.getCmpnntLogger().info("Paste:  https://paste.gg/" + paste.getId());
                    Minigames.getCmpnntLogger().info("Paste:  Deletion Key: " + paste.getDeletionKey());
                } else {
                    sender.sendMessage("Paste Failed.");
                }
            } catch (InvalidPasteException e) {
                sender.sendMessage("Paste Failed" + e.getMessage());
                Minigames.getCmpnntLogger().warn("", e);
            }
        });
    }

}
