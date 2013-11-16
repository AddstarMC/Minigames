package com.pauldavdesign.mineauz.minigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameTool;
import com.pauldavdesign.mineauz.minigames.MinigameToolMode;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

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
		return null;
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
			"/minigame tool restoreblock",
			"/minigame tool regenarea",
			"/minigame tool select"
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
		else{
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
						tool.setTeam(null);
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
			else if(args[0].equalsIgnoreCase("restoreblock")){
				MinigameTool tool;
				if(!MinigameUtils.hasMinigameTool(player))
					tool = MinigameUtils.giveMinigameTool(player);
				else
					tool = MinigameUtils.getMinigameTool(player);
				
				if(tool.getMinigame() != null){
					tool.setMode(MinigameToolMode.RESTORE_BLOCK);
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
							tool.getMinigame().getSpleefFloor1() != null && tool.getMinigame().getSpleefFloor2() != null){
						player.setSelection(tool.getMinigame().getSpleefFloor1(), tool.getMinigame().getSpleefFloor2());
					}
					else if(tool.getMode() == MinigameToolMode.START){
						if(tool.getTeam() != null){
							if(tool.getTeam().equals("Red")){
								for(Location loc : tool.getMinigame().getStartLocationsRed()){
									Location nloc = loc.clone();
									player.getPlayer().sendBlockChange(nloc, Material.SKULL, (byte)1); //TODO: Use alternate Method!
								}
							}
							else{
								for(Location loc : tool.getMinigame().getStartLocationsBlue()){
									Location nloc = loc.clone();
									player.getPlayer().sendBlockChange(nloc, Material.SKULL, (byte)1); //TODO: Use alternate Method!
								}
							}
						}
						else{
							for(Location loc : tool.getMinigame().getStartLocations()){
								Location nloc = loc.clone();
								player.getPlayer().sendBlockChange(nloc, Material.SKULL, (byte)1); //TODO: Use alternate Method!
							}
						}
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && tool.getMinigame().getQuitPosition() != null){
						player.getPlayer().sendBlockChange(tool.getMinigame().getQuitPosition(), Material.SKULL, (byte)1); //TODO: Use alternate Method!
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && tool.getMinigame().getEndPosition() != null){
						player.getPlayer().sendBlockChange(tool.getMinigame().getEndPosition(), Material.SKULL, (byte)1); //TODO: Use alternate Method!
					}
					else if(tool.getMode() == MinigameToolMode.QUIT && tool.getMinigame().getLobbyPosition() != null){
						player.getPlayer().sendBlockChange(tool.getMinigame().getLobbyPosition(), Material.SKULL, (byte)1); //TODO: Use alternate Method!
					}
					else
						sender.sendMessage(ChatColor.RED + "Nothing to select.");
				}
				else
					sender.sendMessage(ChatColor.RED + "You must have a valid Minigame selected to use this tool!");
			}
			else{
				return false;
			}
		}
		return true;
	}
	
}
