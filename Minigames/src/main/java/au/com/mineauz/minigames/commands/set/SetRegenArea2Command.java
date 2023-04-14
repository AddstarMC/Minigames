package au.com.mineauz.minigames.commands.set;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.blockRecorder.Position;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.MgRegion;
import au.com.mineauz.minigames.minigame.Minigame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetRegenArea2Command implements ICommand {

    @Override
    public String getName() {
        return "regenarea";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public boolean canBeConsole() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Sets a regeneration area for a Minigame. This only needs to be used for Minigames that have things like leaf decay, fire, tnt etc." +
                " If the Minigame has anything that the player doesn't directly interract with that breaks, this should be used.";
    }

    @Override
    public String[] getParameters() {
        return new String[]{"clear"};
    }

    @Override
    public String[] getUsage() {
        return new String[]{"/minigame set <Minigame> regenarea1 <parameters>"};
    }

    @Override
    public String getPermissionMessage() {
        return "You do not have permission to set the regen area of a Minigame!";
    }

    @Override
    public String getPermission() {
        return "minigame.set.regenarea";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
                             String label, String[] args) {
        if (args != null) {
            if (sender instanceof Player player){
                if (args[0].equalsIgnoreCase("clear")){
                    minigame.getRegenRegions().removeIf(region -> region.pos1() == null || region.pos2() == null);

                    player.sendMessage(ChatColor.GRAY + "All incomplete regeneration areas have been cleared for " + minigame);
                } else {
                    try{
                        int i = Math.max(Math.min(minigame.getRegenRegions().size(), Integer.parseInt(args[0])), 0);

                        if (i == minigame.getRegenRegions().size()){
                            minigame.getRegenRegions().add(new MgRegion(player.getWorld(), null, Position.block(player.getLocation())));
                        } else {
                            MgRegion region = minigame.getRegenRegions().get(i);

                            minigame.getRegenRegions().set(i, new MgRegion(player.getWorld(), region.pos1(), Position.block(player.getLocation())));
                        }

                    } catch (NumberFormatException ignored){
                        player.sendMessage(Component.text("This was no valid input.", NamedTextColor.RED));
                        return false;
                    }
                }
            } else {
                sender.sendMessage(Component.text("&2You have to be a player.", NamedTextColor.RED));
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
                                      String alias, String[] args) {
        if (args.length == 1){
            ArrayList<String> numofRegegions = new ArrayList<>();
            for (int i = 1; i <= minigame.getRegenRegions().size(); i++){
                numofRegegions.add(String.valueOf(i));
            }

            numofRegegions.add("clear");
            return MinigameUtils.tabCompleteMatch(numofRegegions, args[0]);
        }
        return null;
    }
}
