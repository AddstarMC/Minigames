package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameTool;
import au.com.mineauz.minigames.MinigameToolMode;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;

public class ToolCommand implements ICommand {

	@Override
	public String getName() {
		return "tool";
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
		return "Spawns the Minigame tool for use in setting locations in a Minigame.";
	}

	@Override
	public String[] getParameters() {
		return new String[] {"minigame", "start", "quit", "end", "lobby", 
				"degenarea", "regenarea", "select", "deselect"};
	}

	@Override
	public String[] getUsage() {
		return new String[] {
			"/minigame tool minigame <Minigame>",
			"/minigame tool start [team]",
			"/minigame tool quit",
			"/minigame tool end",
			"/minigame tool lobby",
			"/minigame tool degenarea",
			"/minigame tool regenarea",
			"/minigame tool select",
			"/minigame tool deselect"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the Minigame Tool!";
	}

	@Override
	public String getPermission() {
		return "minigame.tool";
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		MinigamePlayer player = Minigames.plugin.pdata.getMinigamePlayer((Player)sender);
		if(args == null){
			MinigameUtils.giveMinigameTool(player);
		}
		else if(MinigameUtils.hasMinigameTool(player)){
			if(args[0].equalsIgnoreCase("minigame") && args.length == 2){
				if(Minigames.plugin.mdata.hasMinigame(args[1])){
					MinigameTool tool;
					Minigame mg = Minigames.plugin.mdata.getMinigame(args[1]);
					if(!MinigameUtils.hasMinigameTool(player))
						tool = MinigameUtils.giveMinigameTool(player);
					else
						tool = MinigameUtils.getMinigameTool(player);
					
					tool.setMinigame(mg);
				}
				else{
					sender.sendMessage(ChatColor.RED + "No Minigame found by the name \"" + args[1] + "\"");
				}
			}
			else if(args[0].equalsIgnoreCase("start")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					tool.setMode(MinigameToolMode.START);
					if(args.length == 2 && args[1].matches("r(ed)?|b(lue)?")){
						tool.setTeam(args[1]);
					}
					else{
						tool.setTeam("none");
					}
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else if(args[0].equalsIgnoreCase("quit")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					tool.setMode(MinigameToolMode.QUIT);
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else if(args[0].equalsIgnoreCase("end")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					tool.setMode(MinigameToolMode.END);
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else if(args[0].equalsIgnoreCase("lobby")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					tool.setMode(MinigameToolMode.LOBBY);
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else if(args[0].equalsIgnoreCase("degenarea")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					tool.setMode(MinigameToolMode.DEGEN_AREA);
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else if(args[0].equalsIgnoreCase("regenarea")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					tool.setMode(MinigameToolMode.REGEN_AREA);
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else if(args[0].equalsIgnoreCase("select")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					if(tool.getMode() == MinigameToolMode.REGEN_AREA && 
							tool.getMinigame().getRegenArea1() != null && tool.getMinigame().getRegenArea2() != null){
						player.setSelection(tool.getMinigame().getRegenArea1(), tool.getMinigame().getRegenArea2());
					}
					else if(tool.getMode() == MinigameToolMode.DEGEN_AREA && 
							tool.getMinigame().getFloorDegen1() != null && tool.getMinigame().getFloorDegen2() != null){
						player.setSelection(tool.getMinigame().getFloorDegen1(), tool.getMinigame().getFloorDegen2());
					}
					else if(tool.getMode() == MinigameToolMode.START){
						if(!tool.getTeam().equals("none")){
							if(TeamsModule.getMinigameModule(tool.getMinigame()).hasTeam(TeamColor.matchColor(tool.getTeam()))){
								Team team = TeamsModule.getMinigameModule(tool.getMinigame()).getTeam(TeamColor.matchColor(tool.getTeam()));
								Location nloc = null;
								for(Location loc : team.getStartLocations()){
									nloc = loc.clone();
									player.getPlayer().sendBlockChange(nloc, Material.SKULL, (byte)1); //TODO: Use alternate Method!
								}
							}
							else{
								player.sendMessage(ChatColor.RED + tool.getMinigame().getName(false) + " has no " + tool.getTeam() + " team");
							}
						}
						else{
							Location nloc = null;
							for(Location loc : tool.getMinigame().getStartLocations()){
								nloc = loc.clone();
								player.getPlayer().sendBlockChange(nloc, Material.SKULL, (byte)1); //TODO: Use alternate Method!
							}
						}
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && tool.getMinigame().getQuitPosition() != null){
						player.getPlayer().sendBlockChange(tool.getMinigame().getQuitPosition(), Material.SKULL, (byte)1); //TODO: Use alternate Method!
					}
					else if(tool.getMode() == MinigameToolMode.END && tool.getMinigame().getEndPosition() != null){
						player.getPlayer().sendBlockChange(tool.getMinigame().getEndPosition(), Material.SKULL, (byte)1); //TODO: Use alternate Method!
					}
					else if(tool.getMode() == MinigameToolMode.LOBBY && tool.getMinigame().getLobbyPosition() != null){
						player.getPlayer().sendBlockChange(tool.getMinigame().getLobbyPosition(), Material.SKULL, (byte)1); //TODO: Use alternate Method!
					}
					else
						sender.sendMessage(ChatColor.RED + "Nothing to select.");
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else if(args[0].equalsIgnoreCase("deselect")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					if((tool.getMode() == MinigameToolMode.REGEN_AREA || tool.getMode() == MinigameToolMode.DEGEN_AREA) && player.hasSelection()){
						player.showSelection(true);
					}
					else if(tool.getMode() == MinigameToolMode.START){
						if(!tool.getTeam().equals("none")){
							if(TeamsModule.getMinigameModule(tool.getMinigame()).hasTeam(TeamColor.matchColor(tool.getTeam()))){
								Team team = TeamsModule.getMinigameModule(tool.getMinigame()).getTeam(TeamColor.matchColor(tool.getTeam()));
								Location nloc = null;
								for(Location loc : team.getStartLocations()){
									nloc = loc.clone();
									player.getPlayer().sendBlockChange(nloc, nloc.getBlock().getType(), nloc.getBlock().getData()); //TODO: Use alternate Method!
								}
							}
							else{
								player.sendMessage(ChatColor.RED + tool.getMinigame().getName(false) + " has no " + tool.getTeam() + " team");
							}
						}
						else{
							Location nloc = null;
							for(Location loc : tool.getMinigame().getStartLocations()){
								nloc = loc.clone();
								player.getPlayer().sendBlockChange(nloc, nloc.getBlock().getType(), nloc.getBlock().getData()); //TODO: Use alternate Method!
							}
						}
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && tool.getMinigame().getQuitPosition() != null){
						Block bl = tool.getMinigame().getQuitPosition().getBlock();
						player.getPlayer().sendBlockChange(bl.getLocation(), bl.getType(), bl.getData()); //TODO: Use alternate Method!
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && tool.getMinigame().getEndPosition() != null){
						Block bl = tool.getMinigame().getEndPosition().getBlock();
						player.getPlayer().sendBlockChange(bl.getLocation(), bl.getType(), bl.getData()); //TODO: Use alternate Method!
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && tool.getMinigame().getLobbyPosition() != null){
						Block bl = tool.getMinigame().getLobbyPosition().getBlock();
						player.getPlayer().sendBlockChange(bl.getLocation(), bl.getType(), bl.getData()); //TODO: Use alternate Method!
					}
					else
						sender.sendMessage(ChatColor.RED + "Nothing to deselect.");
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else{
				return false;
			}
		}
		else{
			sender.sendMessage(ChatColor.RED + "You must have a Minigame Tool! Type \"/minigame tool\" to recieve one.");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 1){
			List<String> par = new ArrayList<String>();
			for(String p : getParameters()){
				par.add(p);
			}
			return MinigameUtils.tabCompleteMatch(par, args[0]);
		}
		else if(args.length == 2 && args[0].equalsIgnoreCase("start")){
			return MinigameUtils.tabCompleteMatch(MinigameUtils.stringToList("red;blue"), args[1]);
		}
		List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
		return MinigameUtils.tabCompleteMatch(mgs, args[args.length - 1]);
	}
	
}
