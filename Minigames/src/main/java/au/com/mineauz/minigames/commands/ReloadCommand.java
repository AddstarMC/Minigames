package au.com.mineauz.minigames.commands;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends ACommand {

    @Override
    public @NotNull String getName() {
        return "reload";
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Reloads the Minigames config files.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame reload"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.reload";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull String @NotNull [] args) {
        for (Player p : PLUGIN.getServer().getOnlinePlayers()) {
            if (PLUGIN.getPlayerManager().getMinigamePlayer(p).isInMinigame()) {
                PLUGIN.getPlayerManager().quitMinigame(PLUGIN.getPlayerManager().getMinigamePlayer(p), true);
            }
        }

        Minigames.getPlugin().getMinigameManager().getAllMinigames().clear();

        try {
            PLUGIN.getConfig().load(PLUGIN.getDataFolder() + "/config.yml");
        } catch (FileNotFoundException ex) {
            PLUGIN.getLogger().info("Failed to load config, creating one.");
            try {
                PLUGIN.getConfig().save(PLUGIN.getDataFolder() + "/config.yml");
            } catch (IOException e) {
                Minigames.getCmpnntLogger().error("Could not save config.yml!", e);
            }
        } catch (Exception e) {
            Minigames.getCmpnntLogger().error("Failed to load config!", e);
        }

        List<String> mgs = new ArrayList<>();
        if (Minigames.getPlugin().getConfig().contains("minigames")) {
            mgs = Minigames.getPlugin().getConfig().getStringList("minigames");
        }
        final List<String> allMGS = new ArrayList<>(mgs);

        if (!mgs.isEmpty()) {
            for (String mgm : allMGS) {
                Minigame game = new Minigame(mgm);
                game.loadMinigame();
                Minigames.getPlugin().getMinigameManager().addMinigame(game);
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Reloaded Minigame configs");
        return true;
    }

    @Override
    public @Nullable List<@NotNull String> onTabComplete(@NotNull CommandSender sender,
                                                         @NotNull String @NotNull [] args) {
        return null;
    }

}
