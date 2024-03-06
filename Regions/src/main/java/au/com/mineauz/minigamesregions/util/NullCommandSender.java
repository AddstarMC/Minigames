package au.com.mineauz.minigamesregions.util;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class NullCommandSender implements ConsoleCommandSender {
    @Override
    public void sendMessage(@NotNull String message) {
        MinigameUtils.debugMessage("[Suppressed] " + message);
    }

    @Override
    public void sendMessage(String[] messages) {
        if (Minigames.getPlugin().isDebugging()) {
            for (String message : messages) {
                MinigameUtils.debugMessage("[Suppressed] " + message);
            }
        }
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {

    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {

    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        MinigameUtils.debugMessage("[Suppressed] " + message);
    }

    @Override
    public void sendRawMessage(@Nullable UUID uuid, @NotNull String s) {

    }

    @Override
    public @NotNull Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public @NotNull String getName() {
        return "Null";
    }

    @Override
    public @NotNull Spigot spigot() {
        return new CommandSender.Spigot();
    }

    @Override
    public Component name() {
        return null;
    }

    @Override
    public boolean isPermissionSet(@Nullable String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(@Nullable Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(@Nullable String name) {
        return true;
    }

    @Override
    public boolean hasPermission(@Nullable Permission perm) {
        return true;
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Bukkit.getConsoleSender().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(@NotNull String input) {
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
    }
}
