package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import com.google.common.base.Charsets;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.kitteh.pastegg.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DebugCommand implements ICommand {

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Debugs stuff.";
    }

    @Override
    public String[] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame debug"};
    }

    @Override
    public String getPermissionMessage() {
        return "You may not debug!";
    }

    @Override
    public String getPermission() {
        return "minigame.debug";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if(args != null && args.length > 0) {
            switch (args[0].toUpperCase()){
                case "ON":
                    if(Minigames.getPlugin().isDebugging()){
                        sender.sendMessage(ChatColor.GRAY + "Debug mode already active.");
                    } else {
                        Minigames.getPlugin().toggleDebug();
                        sender.sendMessage(ChatColor.GRAY + "Debug mode active.");
                    }
                    break;
                case "OFF":
                    if(!Minigames.getPlugin().isDebugging()){
                        sender.sendMessage(ChatColor.GRAY + "Debug mode already inactive.");
                    } else {
                        Minigames.getPlugin().toggleDebug();
                        sender.sendMessage(ChatColor.GRAY + "Debug mode inactive.");
                    }
                    break;
                case "PASTE":
                    sender.sendMessage(ChatColor.GRAY + "Generating a paste.....");
                    generatePaste(sender,minigame);
                    break;
                default:
                    return false;
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
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if(args.length==0){
            out.add("NO");
            out.add("YES");
            out.add("PASTE");
        }
        return out;
    }

    private String getFile(Path file) {
        try {
            return new String(Files.readAllBytes(file), Charsets.UTF_8);
        } catch (IOException e) {
            return ExceptionUtils.getFullStackTrace(e);
        }
    }

    private void generatePaste(CommandSender sender, Minigame minigame){
        StringBuilder mainInfo = new StringBuilder();
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

            String apiKey = Minigames.getPlugin().getConfig().getString("pasteApiKey",null);
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
 /*           List<PasteFile> gamesConfigs =  new ArrayList<>();
            Minigames.getPlugin().getMinigameManager().getAllMinigames().forEach((s, minigame1) -> {
                PasteContent content = new PasteContent(PasteContent.ContentType.TEXT,
                      getFile(dataPath.resolve("/minigames/" + s + "/config.yml")));
                PasteFile file = new PasteFile(s+"-config.yml",content);
                gamesConfigs.add(file);
            });*/
            PasteBuilder builder = new PasteBuilder();
            //gamesConfigs.forEach(builder::addFile);
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
                    plugin.getLogger().log(new LogRecord(Level.INFO,"Paste:  https://paste.gg/" + paste.getId()));
                    plugin.getLogger().log(new LogRecord(Level.INFO,"Paste:  Deletion Key: " + paste.getDeletionKey()));
                } else {
                    sender.sendMessage("Paste Failed.");
                }
            }catch (InvalidPasteException e) {
                sender.sendMessage("Paste Failed" + e.getMessage());
                Minigames.log().warning(e.getMessage());
            }
        });
    }

}
