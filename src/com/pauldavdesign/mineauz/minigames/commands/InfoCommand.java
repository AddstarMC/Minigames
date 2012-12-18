package com.pauldavdesign.mineauz.minigames.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class InfoCommand implements ICommand{

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Gets the information on a Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame info <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You don't have permission to review a Minigames information!";
	}

	@Override
	public String getPermission() {
		return "minigame.info";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			Minigame mgm = mdata.getMinigame(args[0]);
		
			if(mgm != null){
				sender.sendMessage(ChatColor.GRAY + "Checking " + mgm.getName() + " minigame...");
				if(!mgm.getType().equals("th")){
					if(!mgm.getType().equals("teamdm")){
						if(!mgm.getStartLocations().isEmpty()){
							sender.sendMessage(ChatColor.GREEN + "Starting position set (" + mgm.getStartLocations().size() + ")");
						}
						else {
							sender.sendMessage(ChatColor.RED + "Starting position is not set!");
						}
					}
					else{
						if(!mgm.getStartLocationsRed().isEmpty()){
							sender.sendMessage(ChatColor.GREEN + "Red starting positions set (" + mgm.getStartLocationsRed().size() + ")");
						}
						else {
							sender.sendMessage(ChatColor.RED + "Red starting positions are not set!");
						}
						
						if(!mgm.getStartLocationsBlue().isEmpty()){
							sender.sendMessage(ChatColor.GREEN + "Blue starting positions set (" + mgm.getStartLocationsBlue().size() + ")");
						}
						else {
							sender.sendMessage(ChatColor.RED + "Blue starting positions are not set!");
						}
					}
					
					if(mgm.getEndPosition() != null){
						sender.sendMessage(ChatColor.GREEN + "Ending position set");
					}
					else {
						sender.sendMessage(ChatColor.RED + "Ending position is not set!");
					}
					
					if(mgm.getQuitPosition() != null){
						sender.sendMessage(ChatColor.GREEN + "Quit position set");
					}
					else {
						sender.sendMessage(ChatColor.RED + "Quit position is not set!");
					}
					
					if(mgm.getRewardItem() != null){
						sender.sendMessage(ChatColor.GREEN + "Reward Item: " + mgm.getRewardItem().getType().toString().toLowerCase().replace("_", " "));
					}
					else {
						sender.sendMessage(ChatColor.RED + "Reward Item is not set!");
					}
					
					if(mgm.getRewardPrice() > 0 && plugin.getEconomy() != null){
						sender.sendMessage(ChatColor.GREEN + "Reward Money: $" + mgm.getRewardPrice());
					}
					else if(plugin.getEconomy() != null){
						sender.sendMessage(ChatColor.RED + "Reward money is not set!");
					}
					
					if(mgm.getSecondaryRewardItem() != null){
						sender.sendMessage(ChatColor.GREEN + "Secondary Reward Item: " + mgm.getSecondaryRewardItem().getType().toString().toLowerCase().replace("_", " "));
					}
					else {
						sender.sendMessage(ChatColor.RED + "Secondary reward is not set!");
					}
					
					if(mgm.getSecondaryRewardPrice() > 0 && plugin.getEconomy() != null){
						sender.sendMessage(ChatColor.GREEN + "Secondary Reward Money: $" + mgm.getSecondaryRewardPrice());
					}
					else if(plugin.getEconomy() != null){
						sender.sendMessage(ChatColor.RED + "Secondary Reward money is not set!");
					}
					
					if(mgm.isEnabled()){
						sender.sendMessage(ChatColor.GREEN + "Enabled: true");
					}
					else{
						sender.sendMessage(ChatColor.RED + "Enabled: false");
					}
					
					if(mgm.getType() != null){
						sender.sendMessage(ChatColor.GREEN + "Type: (" + mgm.getType() + ")");
						
						if(mgm.getType().equalsIgnoreCase("spleef")){
							if(mgm.getSpleefFloor1() != null){
								sender.sendMessage(ChatColor.GREEN + "Floor corner 1 set");
							}
							else {
								sender.sendMessage(ChatColor.RED + "Floor corner 1 is not set!");
							}
							
							if(mgm.getSpleefFloor2() != null){
								sender.sendMessage(ChatColor.GREEN + "Floor corner 2 set");
							}
							else {
								sender.sendMessage(ChatColor.RED + "Floor corner 2 is not set!");
							}
							
							sender.sendMessage(ChatColor.GREEN + "Floor material: " + mgm.getSpleefFloorMaterial().toString().toLowerCase().replace("_", " "));
						}
						
						if(!mgm.getType().equalsIgnoreCase("sp")){
							if(!mgm.getType().equals("teamdm")){
								if(mgm.bettingEnabled()){
									sender.sendMessage(ChatColor.GREEN + "Betting enabled: true");
								}
								else{
									sender.sendMessage(ChatColor.RED + "Betting enabled: false");
								}
							}
							
							if(mgm.getLobbyPosition() != null){
								sender.sendMessage(ChatColor.GREEN + "Lobby Set");
							}
							else{
								sender.sendMessage(ChatColor.RED + "Lobby is not set!");
							}
							
							
							sender.sendMessage(ChatColor.GREEN + "Maximum players: " + mgm.getMaxPlayers());
							
							sender.sendMessage(ChatColor.GREEN + "Minimum players: " + mgm.getMinPlayers());
						}
						
						if(mgm.getType().equals("teamdm") || mgm.getType().equals("dm")){
							sender.sendMessage(ChatColor.GREEN + "Max Score: " + mgm.getMaxScore());
							sender.sendMessage(ChatColor.GRAY + "Min Score: " + mgm.getMaxScorePerPlayer(mgm.getMinPlayers()));
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "Type is not set!");
					}
					
					if(mgm.getType().equalsIgnoreCase("sp")){
						if(!mgm.getFlags().isEmpty()){
							sender.sendMessage(ChatColor.GREEN + "Require flags: true");
							List<String> list = mgm.getFlags();
							String flags = "";
							for(String item : list){
								flags += item + ", ";
							}
							flags = flags.substring(0, flags.length() - 1);
							sender.sendMessage(ChatColor.GREEN + "Flags: " + ChatColor.GRAY + flags);
						}
						else {
							sender.sendMessage(ChatColor.RED + "Require flags: false");
						}
					}
					
					sender.sendMessage(ChatColor.GREEN + "Use permissions: " + mgm.getUsePermissions());
					if(mgm.getUsePermissions())
						sender.sendMessage(ChatColor.GRAY + "minigame.join." + mgm.getName().toLowerCase());
				}
				else{
					sender.sendMessage(ChatColor.GREEN + "Maximum radius: " + mgm.getMaxRadius());
					sender.sendMessage(ChatColor.GREEN + "Minimum treasure: " + mgm.getMinTreasure());
					sender.sendMessage(ChatColor.GREEN + "Maximum treasure: " + mgm.getMaxTreasure());
					if(mgm.getLocation() != null){
						sender.sendMessage(ChatColor.GREEN + "Location: " + mgm.getLocation());
					}
					else {
						sender.sendMessage(ChatColor.RED + "Location: Unset!");
					}
				}
				sender.sendMessage(ChatColor.GRAY + "The minigame " + mgm.getName() + "s check is complete");
			}
			else{
				sender.sendMessage(ChatColor.RED + "There is no Minigame by the name " + args[0]);
			}
			return true;
		}
		return false;
	}
}
