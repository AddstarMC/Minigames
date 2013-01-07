package com.pauldavdesign.mineauz.minigames.commands.set;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetCommand implements ICommand{
	private static Map<String, ICommand> parameterList = new HashMap<String, ICommand>();
	private static BufferedWriter cmdFile;
	
	static{
		if(plugin.getConfig().getBoolean("outputCMDToFile")){
			try {
				cmdFile = new BufferedWriter(new FileWriter(plugin.getDataFolder() + "/setcmds.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		registerSetCommand(new SetStartCommand());
		registerSetCommand(new SetEndCommand());
		registerSetCommand(new SetQuitCommand());
		registerSetCommand(new SetLobbyCommand());
		registerSetCommand(new SetRewardCommand());
		registerSetCommand(new SetSecondaryRewardCommand());
		registerSetCommand(new SetTypeCommand());
		registerSetCommand(new SetFloorDegeneratorCommand());
		registerSetCommand(new SetMaxPlayersCommand());
		registerSetCommand(new SetMinPlayersCommand());
		registerSetCommand(new SetLoadoutCommand());
		registerSetCommand(new SetEnabledCommand());
		registerSetCommand(new SetMaxRadiusCommand());
		registerSetCommand(new SetMinTreasureCommand());
		registerSetCommand(new SetMaxTreasureCommand());
		registerSetCommand(new SetFlagCommand());
		registerSetCommand(new SetBetsCommand());
		registerSetCommand(new SetLocationCommand());
		registerSetCommand(new SetRestoreBlockCommand());
		registerSetCommand(new SetUsePermissionsCommand());
		registerSetCommand(new SetMinScoreCommand());
		registerSetCommand(new SetMaxScoreCommand());
		registerSetCommand(new SetTimerCommand());
		registerSetCommand(new SetItemDropCommand());
		registerSetCommand(new SetItemPickupCommand());
		registerSetCommand(new SetBlockBreakCommand());
		registerSetCommand(new SetBlockPlaceCommand());
		registerSetCommand(new SetPlayersGamemodeCommand());
		registerSetCommand(new SetBlockWhitelistCommand());
		registerSetCommand(new SetBlocksDropCommand());
		
		if(plugin.getConfig().getBoolean("outputCMDToFile")){
			try {
				cmdFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void registerSetCommand(ICommand command){
		parameterList.put(command.getName(), command);
		
		if(plugin.getConfig().getBoolean("outputCMDToFile")){
			try {
				cmdFile.write("Command: " + command.getName());
				cmdFile.newLine();
				if(command.getDescription() != null){
					cmdFile.write("Description:");
					cmdFile.newLine();
					cmdFile.write(command.getDescription());
					cmdFile.newLine();
				}
				if(command.getParameters() != null){
					cmdFile.write("Parameters:");
					cmdFile.newLine();
					for(String par : command.getParameters()){
						cmdFile.write(par + ", ");
					}
					cmdFile.newLine();
				}
				if(command.getUsage() != null){
					cmdFile.write("Usage:");
					cmdFile.newLine();
					for(String use : command.getUsage()){
						cmdFile.write(use);
						cmdFile.newLine();
					}
				}
				if(command.getAliases() != null){
					cmdFile.write("Aliases:");
					cmdFile.newLine();
					for(String alias : command.getAliases()){
						cmdFile.write(alias + ", ");
					}
					cmdFile.newLine();
				}
				if(command.getPermission() != null){
					cmdFile.write("Permission:");
					cmdFile.newLine();
					cmdFile.write(command.getPermission());
					cmdFile.newLine();
				}
				cmdFile.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getName() {
		return "set";
	}
	
	@Override
	public String[] getAliases(){
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Modifies a Minigame using special parameters for each game type.";
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> <Parameters>..."};
	}

	@Override
	public String[] getParameters(){
		String[] parameters = new String[parameterList.size()];
		int inc = 0;
		for(String key : parameterList.keySet()){
			parameters[inc] = key;
			inc++;
		}
		return parameters;
	}
	
	@Override
	public String getPermissionMessage() {
		return null;
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
		Player ply = null;
		if(sender instanceof Player){
			ply = (Player)sender;
		}
		
		if(args != null){
			ICommand comd = null;
			Minigame mgm = null;
			String[] shortArgs = null;
			
			if(args.length >= 1){
				if(mdata.hasMinigame(args[0])){
					mgm = mdata.getMinigame(args[0]);
				}
				if(args.length >= 2){
					if(parameterList.containsKey(args[1].toLowerCase())){
						comd = parameterList.get(args[1].toLowerCase());
					}
					else{
AliasCheck:				for(ICommand com : parameterList.values()){
							if(com.getAliases() != null){
								for(String alias : com.getAliases()){
									if(args[1].equalsIgnoreCase(alias)){
										comd = com;
										break AliasCheck;
									}
								}
							}
						}
					}
				}
				
				if(args != null && args.length > 2){
					shortArgs = new String[args.length - 2];
					for(int i = 2; i < args.length; i++){
						shortArgs[i - 2] = args[i];
					}
				}
			}
			
			if(comd != null && mgm != null){
				if((ply == null && comd.canBeConsole()) || ply != null){
					if(ply == null || (comd.getPermission() == null || ply.hasPermission(comd.getPermission()))){
						boolean returnValue = comd.onCommand(sender, mgm, label, shortArgs);
						if(!returnValue){
							sender.sendMessage(ChatColor.GREEN + "------------------Command Info------------------");
							sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + comd.getDescription());
							if(comd.getParameters() != null){
								String parameters = "";
								boolean switchColour = false;
								for(String par : comd.getParameters()){
									if(switchColour){
										parameters += ChatColor.WHITE + par;
										if(!par.equalsIgnoreCase(comd.getParameters()[comd.getParameters().length - 1])){
											parameters += ChatColor.WHITE + ", ";
										}
										switchColour = false;
									}
									else{
										parameters += ChatColor.GRAY + par;
										if(!par.equalsIgnoreCase(comd.getParameters()[comd.getParameters().length - 1])){
											parameters += ChatColor.WHITE + ", ";
										}
										switchColour = true;
									}
								}
								sender.sendMessage(ChatColor.BLUE + "Parameters: " + parameters);
							}
							sender.sendMessage(ChatColor.BLUE + "Usage: ");
							sender.sendMessage(comd.getUsage());
							if(comd.getAliases() != null){
								String aliases = "";
								boolean switchColour = false;
								for(String alias : comd.getAliases()){
									if(switchColour){
										aliases += ChatColor.WHITE + alias;
										if(!alias.equalsIgnoreCase(comd.getAliases()[comd.getAliases().length - 1])){
											aliases += ChatColor.WHITE + ", ";
										}
										switchColour = false;
									}
									else{
										aliases += ChatColor.GRAY + alias;
										if(!alias.equalsIgnoreCase(comd.getAliases()[comd.getAliases().length - 1])){
											aliases += ChatColor.WHITE + ", ";
										}
										switchColour = true;
									}
								}
								sender.sendMessage(ChatColor.BLUE + "Aliases: " + aliases);
							}
						}
					}
					else{
						sender.sendMessage(ChatColor.RED + comd.getPermissionMessage());
						sender.sendMessage(ChatColor.RED + comd.getPermission());
					}
					return true;
				}
				else{
					sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
					return true;
				}
			}
			else if(mgm == null){
				sender.sendMessage(ChatColor.RED + "There is no Minigame by the name \"" + args[0] + "\"");
			}
		}
		return false;
	}
	
}
