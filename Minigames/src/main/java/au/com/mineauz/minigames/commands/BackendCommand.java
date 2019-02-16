package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.backend.BackendManager;
import au.com.mineauz.minigames.backend.ExportNotifier;
import au.com.mineauz.minigames.minigame.Minigame;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class BackendCommand implements ICommand {
    @Override
    public String getName() {
        return "backend";
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
        return "Allows access to export and change the backend used by minigames.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"export", "switch"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame backend export <type>", "/minigame backend switch <type>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to control the backend.";
    }

    @Override
    public String getPermission() {
        return "minigame.backend";
    }

    @Override
    public boolean onCommand(final CommandSender sender, Minigame minigame, String label, String[] args) {
        if (args == null || args.length != 2) {
            return false;
        }

        BackendManager manager = Minigames.getPlugin().getBackend();

        if (args[0].equalsIgnoreCase("export")) {
            try {
                ListenableFuture<Void> future = manager.exportTo(args[1], Minigames.getPlugin().getConfig(), new Notifier(sender));
                sender.sendMessage(ChatColor.GOLD + "Exporting backend to " + args[1] + "...");

                Futures.addCallback(future, new FutureCallback<Void>() {
                    @Override
                    public void onFailure(Throwable t) {
                        sender.sendMessage(ChatColor.RED + "An internal error occured while exporting.");
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }
                });
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else if (args[0].equalsIgnoreCase("switch")) {
            try {
                ListenableFuture<Void> future = manager.switchBackend(args[1], Minigames.getPlugin().getConfig());
                sender.sendMessage(ChatColor.GOLD + "Switching minigames backend to " + args[1] + "...");

                Futures.addCallback(future, new FutureCallback<Void>() {
                    @Override
                    public void onFailure(Throwable t) {
                        sender.sendMessage(ChatColor.RED + "An internal error occured while switching backend.");
                    }

                    @Override
                    public void onSuccess(Void result) {
                        sender.sendMessage(ChatColor.GOLD + "The backend has been successfully switched");
                        sender.sendMessage(ChatColor.GOLD + "!!! This change is " + ChatColor.BOLD + "temporary" + ChatColor.GOLD + ". Please update the config !!!");
                    }
                });
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown option " + args[0]);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        return null;
    }

    private static class Notifier implements ExportNotifier {
        private final CommandSender sender;
        private boolean begun;

        public Notifier(CommandSender sender) {
            this.sender = sender;
            begun = false;
        }

        @Override
        public void onProgress(String state, int count) {
            if (!begun) {
                begun = true;
                if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.GREEN + "[Minigames] Export started...");
                }

                Minigames.getPlugin().getLogger().warning("Started exporting backend. Started by " + sender.getName());
            }

            Minigames.getPlugin().getLogger().info("Exporting backend... " + state + ": " + count);
        }

        @Override
        public void onComplete() {
            sender.sendMessage(ChatColor.GREEN + "[Minigames] Export complete!");
            Minigames.getPlugin().getLogger().info("Exporting complete");
        }

        @Override
        public void onError(Throwable e, String state, int count) {
            sender.sendMessage(ChatColor.RED + "[Minigames] Export error. See console for details.");
            Minigames.getPlugin().getLogger().log(Level.SEVERE, "Exporting error at " + state + ": " + count, e);
        }
    }
}
