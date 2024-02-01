package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.backend.BackendManager;
import au.com.mineauz.minigames.backend.Notifier;
import au.com.mineauz.minigames.managers.MinigameMessageManager;
import au.com.mineauz.minigames.managers.language.MinigameMessageType;
import au.com.mineauz.minigames.managers.language.MinigamePlaceHolderKey;
import au.com.mineauz.minigames.managers.language.langkeys.MgCommandLangKey;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

public class BackendCommand extends ACommand {

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
        return MinigameMessageManager.getMgMessage(MgCommandLangKey.COMMAND_BACKEND_USAGE);
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.backend";
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        if (args.length != 2) {
            return false;
        }

        BackendManager manager = Minigames.getPlugin().getBackend();

        if (args[0].equalsIgnoreCase("export")) {
            try {
                ListenableFuture<Void> future = manager.exportTo(args[1], Minigames.getPlugin().getConfig(), new ExportNotifier(sender));
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_BACKEND_EXPORT_START,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), args[1]));
                Minigames.getCmpnntLogger().warn("Started exporting backend. Started by " + sender.getName());

                Futures.addCallback(future, new FutureCallback<>() {
                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKEND_ERROR_INTERNAL,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), t.getMessage()));
                        Minigames.getCmpnntLogger().error("An internal error occurred while exporting.", t);
                    }

                    @Override
                    public void onSuccess(Void result) { // success gets handled by notifier
                    }
                }, directExecutor());
            } catch (IllegalArgumentException e) {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKEND_ERROR_INTERNAL,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), e.getMessage()));
                Minigames.getCmpnntLogger().error("An internal error occurred while exporting.", e);
            }
        } else if (args[0].equalsIgnoreCase("switch")) {
            try { // todo why only temporary?
                ListenableFuture<Void> future = manager.switchBackend(args[1], Minigames.getPlugin().getConfig());
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.INFO, MgCommandLangKey.COMMAND_BACKEND_SWITCH_START,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TYPE.getKey(), args[1]));

                Futures.addCallback(future, new FutureCallback<>() {
                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKEND_ERROR_INTERNAL,
                                Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), t.getMessage()));
                        Minigames.getCmpnntLogger().error("An internal error occurred while exporting.", t);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_BACKEND_SWITCH_SUCCESS);
                        MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.WARNING, MgCommandLangKey.COMMAND_BACKEND_SWITCH_WARNING_TEMP);
                    }
                }, directExecutor());
            } catch (IllegalArgumentException e) {
                MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKEND_ERROR_INTERNAL,
                        Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), e.getMessage()));
                Minigames.getCmpnntLogger().error("An internal error occurred while exporting.", e);
            }
        }

        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

    private static class ExportNotifier implements Notifier {
        private final CommandSender sender;

        public ExportNotifier(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public void onProgress(String state, int count) {
            Minigames.getCmpnntLogger().info("Exporting backend... " + state + ": " + count);
        }

        @Override
        public void onComplete() {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.SUCCESS, MgCommandLangKey.COMMAND_BACKEND_EXPORT_SUCCES);
            Minigames.getCmpnntLogger().info("Exporting complete");
        }

        @Override
        public void onError(Exception e, String state, int count) {
            MinigameMessageManager.sendMgMessage(sender, MinigameMessageType.ERROR, MgCommandLangKey.COMMAND_BACKEND_ERROR_INTERNAL,
                    Placeholder.unparsed(MinigamePlaceHolderKey.TEXT.getKey(), e.getMessage()));
            Minigames.getCmpnntLogger().error("Exporting error at " + state + ": " + count, e);
        }
    }
}
