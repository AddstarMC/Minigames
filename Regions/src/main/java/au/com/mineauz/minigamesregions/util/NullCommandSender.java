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
    public void sendMessage(String message) {
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
    public void sendRawMessage(String message) {
        MinigameUtils.debugMessage("[Suppressed] " + message);
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return "Null";
    }

    @Override
    public @NotNull Spigot spigot() {
        return new CommandSender.Spigot();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
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
    public void acceptConversationInput(String input) {
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
    }

    @Override
    public @NotNull Component name() {
        return Component.text(this.getName());
    }

    @Override
    public void sendMessage(@Nullable UUID arg0, @NotNull String arg1) {
        MinigameUtils.debugMessage("[Suppressed] " + arg1);

    }

    @Override
    public void sendMessage(@Nullable UUID arg0, @NotNull String... arg1) {
        if (Minigames.getPlugin().isDebugging()) {
            for (String message : arg1) {
                MinigameUtils.debugMessage("[Suppressed] " + message);
            }
        }

    }

    @Override
    public void sendRawMessage(@Nullable UUID arg0, @NotNull String arg1) {
        MinigameUtils.debugMessage("[Suppressed] " + arg1);

    }
}
