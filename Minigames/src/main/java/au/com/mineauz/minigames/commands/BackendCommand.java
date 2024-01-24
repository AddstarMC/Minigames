package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.backend.BackendManager;
import au.com.mineauz.minigames.backend.ExportNotifier;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import au.com.mineauz.minigames.minigame.Minigame;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

public class BackendCommand implements ICommand {
    @Override
    public @NotNull String getName() {
        return "backend";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_BACKEND_DESCRIPTION);
    }

    @Override
    public Component getUsage() {
        return new String[]{
                "/minigame backend export <type>",
                "/minigame backend switch <type>"
        };
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.backend";
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, @NotNull Minigame minigame,
                             @NotNull String @Nullable [] args) {
        if (args == null || args.length != 2) {
            return false;
        }

        BackendManager manager = Minigames.getPlugin().getBackend();

        if (args[0].equalsIgnoreCase("export")) {
            try {
                ListenableFuture<Void> future = manager.exportTo(args[1], Minigames.getPlugin().getConfig(), new Notifier(sender));
                sender.sendMessage(ChatColor.GOLD + "Exporting backend to " + args[1] + "...");

                Futures.addCallback(future, new FutureCallback<>() {
                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        sender.sendMessage(ChatColor.RED + "An internal error occurred while exporting." + t.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }
                }, directExecutor());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else if (args[0].equalsIgnoreCase("switch")) {
            try {
                ListenableFuture<Void> future = manager.switchBackend(args[1], Minigames.getPlugin().getConfig());
                sender.sendMessage(ChatColor.GOLD + "Switching minigames backend to " + args[1] + "...");

                Futures.addCallback(future, new FutureCallback<>() {
                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        sender.sendMessage(ChatColor.RED + "An internal error occurred while switching backend.");
                    }

                    @Override
                    public void onSuccess(Void result) {
                        sender.sendMessage(ChatColor.GOLD + "The backend has been successfully switched");
                        sender.sendMessage(ChatColor.GOLD + "!!! This change is " + ChatColor.BOLD + "temporary" + ChatColor.GOLD + ". Please update the config !!!");
                    }
                }, directExecutor());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        }

        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                                         @NotNull String @NotNull [] args) {
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
            Minigames.getCmpnntLogger().error("Exporting error at " + state + ": " + count, e);
        }
    }
}
