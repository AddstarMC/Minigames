package au.com.mineauz.minigamesregions.commands;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetRegionCommand implements ICommand {

    @Override
    public String getName() {
        return "region";
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
        return "Creates, edits and deletes Minigame regions";
    }

    @Override
    public String[] getParameters() {
        return new String[] {"select", "create", "delete", "modify"};
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "/minigame set <Minigame> region select <1/2>",
                "/minigame set <Minigame> region create <name>",
                "/minigame set <Minigame> region delete <name>",
                "/minigame set <Minigame> region modify"
        };
    }

    @Override
    public String getPermissionMessage() {
        return "You don't have permission to modify Minigame regions";
    }

    @Override
    public String getPermission() {
        return "minigame.set.region";
    }

    @Override
    public boolean onCommand(CommandSender sender, Minigame minigame,
            String label, String[] args) {
        if(args != null){
            MinigamePlayer ply = Minigames.getPlugin().getPlayerManager().getMinigamePlayer((Player) sender);
            RegionModule rmod = RegionModule.getMinigameModule(minigame);
            if(args.length == 2){
                if(args[0].equalsIgnoreCase("select")){
                    Location ploc = ply.getLocation();
                    ploc.setY(ploc.getY() - 1);
                    
                    if(args[1].equals("1")){
                        Location p2 = ply.getSelectionPoints()[1];
                        ply.clearSelection();
                        ply.setSelection(ploc, p2);

                        ply.sendInfoMessage(ChatColor.GRAY + "Point 1 selected");
                    }
                    else{
                        Location p2 = ply.getSelectionPoints()[0];
                        ply.clearSelection();
                        ply.setSelection(p2, ploc);

                        ply.sendInfoMessage(ChatColor.GRAY + "Point 2 selected");
                    }
                    return true;
                }
                else if(args[0].equalsIgnoreCase("create")){
                    if(ply.hasSelection()){
                        String name = args[1];
                        rmod.addRegion(name, new Region(name, ply.getSelectionPoints()[0], ply.getSelectionPoints()[1]));
                        ply.clearSelection();

                        ply.sendInfoMessage(ChatColor.GRAY + "Created new region for " + minigame.getName(false) + " named " + name);
                    }
                    else{
                        ply.sendInfoMessage(ChatColor.RED + "You have not made a selection!");
                    }
                    return true;
                }
                else if(args[0].equalsIgnoreCase("delete")){
                    if(rmod.hasRegion(args[1])){
                        rmod.removeRegion(args[1]);
                        ply.sendInfoMessage(ChatColor.GRAY + "Removed the region named " + args[1] + " from " + minigame.getName(false));
                    }
                    else{
                        ply.sendInfoMessage(ChatColor.GRAY + "No region by the name " + args[1] + " was found in " + minigame.getName(false));
                    }
                    return true;
                }
                
            }
            else if(args.length == 1){
                if(args[0].equalsIgnoreCase("modify")){
                    rmod.displayMenu(ply, null);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Minigame minigame,
            String alias, String[] args) {
        // TODO Tab Completion
        return null;
    }

}
