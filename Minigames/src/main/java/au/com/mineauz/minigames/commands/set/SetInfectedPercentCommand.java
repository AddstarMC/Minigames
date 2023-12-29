package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.InfectionModule;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetInfectedPercentCommand implements ICommand {

    @Override
    public @NotNull String getName() {
        return "infectedpercent";
    }

    @Override
    public @NotNull String @Nullable [] getAliases() {
        return new String[]{"infperc"};
    }

    @Override
    public boolean canBeConsole() {
        return true;
    }

    @Override
    public @NotNull Component getDescription() {
        return "Sets the percentage of players that will be infected when an Infected Minigame starts. Value must be between 1 and 99.";
    }

    @Override
    public @NotNull String @Nullable [] getParameters() {
        return null;
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> infectedpercent <1-99>"};
    }

    @Override
    public @Nullable String getPermission() {
        return "minigame.set.infectedpercent";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Minigame minigame,
                             @NotNull String label, String @NotNull [] args) {
        if (args != null) {
            if (args[0].matches("[0-9]+")) {
                int val = Integer.parseInt(args[0]);
                if (val > 0 && val < 100) {
                    InfectionModule.getMinigameModule(minigame).setInfectedPercent(val);
                    sender.sendMessage(ChatColor.GRAY + "Infected percent has been set to " + val + "% for " + minigame);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid percentage! Value must be between 1 and 99");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a valid value! Make sure the value is between 1 and 99.");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Minigame minigame,
                                      String alias, @NotNull String @NotNull [] args) {
        return null;
    }

}
