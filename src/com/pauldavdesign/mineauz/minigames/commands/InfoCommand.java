package com.pauldavdesign.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;

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
		return new String[] {"/minigame info <Minigame> [Page]"};
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
			Minigame mgm = plugin.mdata.getMinigame(args[0]);
		
			if(mgm != null){
				//Ten lines per page
				List<String> lines = new ArrayList<String>();
				
				lines.add(ChatColor.GRAY + "Game Type: " + ChatColor.GREEN + mgm.getType());
				if(!mgm.getType().equals("th")){
					lines.add(ChatColor.GRAY + "Score Type: " + ChatColor.GREEN + mgm.getScoreType());
					
					if(mgm.isEnabled()){
						lines.add(ChatColor.GRAY + "Enabled: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Enabled: " + ChatColor.RED + "false");
					}
					
					if(mgm.getUsePermissions()){
						lines.add(ChatColor.GRAY + "Use Permissions: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Use Permissions: " + ChatColor.RED + "false");
					}
					
					if(mgm.getStartLocations().size() > 0){
						lines.add(ChatColor.GRAY + "Starting Positions: " + ChatColor.GREEN + mgm.getStartLocations().size());
					}
					else{
						lines.add(ChatColor.GRAY + "Starting Positions: " + ChatColor.RED + "0");
					}
					if(mgm.getType().equals("teamdm")){
						if(mgm.getStartLocationsRed().size() > 0){
							lines.add(ChatColor.RED + "Red Team " + ChatColor.GRAY + "Starting Positions: " + ChatColor.GREEN + mgm.getStartLocationsRed().size());
						}
						else{
							lines.add(ChatColor.GRAY + "Red Team Starting Positions: " + ChatColor.RED + "0");
						}
						if(mgm.getStartLocationsBlue().size() > 0){
							lines.add(ChatColor.BLUE + "Blue Team " + ChatColor.GRAY + "Starting Positions: " + ChatColor.GREEN + mgm.getStartLocationsBlue().size());
						}
						else{
							lines.add(ChatColor.RED + "Blue Team Starting Positions: 0");
						}
					}
					
					if(mgm.getEndPosition() != null){
						lines.add(ChatColor.GRAY + "End Position: " + ChatColor.GREEN + "Set");
					}
					else{
						lines.add(ChatColor.GRAY + "End Position: " + ChatColor.RED + "Not Set");
					}
					
					if(mgm.getQuitPosition() != null){
						lines.add(ChatColor.GRAY + "Quit Position: " + ChatColor.GREEN + "Set");
					}
					else{
						lines.add(ChatColor.GRAY + "Quit Position: " + ChatColor.RED + "Not Set");
					}
					
					if(!mgm.getType().equals("sp")){
						if(mgm.getLobbyPosition() != null){
							lines.add(ChatColor.GRAY + "Lobby Position: " + ChatColor.GREEN + "Set");
						}
						else{
							lines.add(ChatColor.GRAY + "Lobby Position: " + ChatColor.RED + "Not Set");
						}
						
						if(mgm.bettingEnabled()){
							lines.add(ChatColor.GRAY + "Bets: " + ChatColor.GREEN + "true");
						}
						else{
							lines.add(ChatColor.GRAY + "Bets: " + ChatColor.RED + "false");
						}
						
						if(mgm.getSpleefFloor1() != null){
							lines.add(ChatColor.GRAY + "Floor Degenerator Position 1: " + ChatColor.GREEN + "true");
						}
						else{
							lines.add(ChatColor.GRAY + "Floor Degenerator Position 1: " + ChatColor.RED + "false");
						}
						
						if(mgm.getSpleefFloor2() != null){
							lines.add(ChatColor.GRAY + "Floor Degenerator Position 2: " + ChatColor.GREEN + "true");
						}
						else{
							lines.add(ChatColor.GRAY + "Floor Degenerator Position 2: " + ChatColor.RED + "false");
						}
					}
					
					lines.add(ChatColor.GRAY + "Player Gamemode: " + ChatColor.GREEN + mgm.getDefaultGamemode().name().toLowerCase());
					
					if(mgm.hasDeathDrops()){
						lines.add(ChatColor.GRAY + "Drop Items on Death: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Drop Items on Death: " + ChatColor.RED + "false");
					}
					
					if(mgm.hasItemDrops()){
						lines.add(ChatColor.GRAY + "Player Drop Items: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Player Drop Items: " + ChatColor.RED + "false");
					}
					
					if(mgm.hasItemPickup()){
						lines.add(ChatColor.GRAY + "Player Pickup Items: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Player Pickup Items: " + ChatColor.RED + "false");
					}
					
					if(mgm.canBlockBreak()){
						lines.add(ChatColor.GRAY + "Block Break: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Block Break: " + ChatColor.RED + "false");
					}
					
					if(mgm.canBlockPlace()){
						lines.add(ChatColor.GRAY + "Block Place: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Block Place: " + ChatColor.RED + "false");
					}
					
					if(mgm.canBlocksdrop()){
						lines.add(ChatColor.GRAY + "Block Drop Items: " + ChatColor.GREEN + "true");
					}else{
						lines.add(ChatColor.GRAY + "Block Drop Items: " + ChatColor.RED + "false");
					}
					
					if(mgm.getBlockRecorder().getWhitelistMode()){
						lines.add(ChatColor.GRAY + "Block Placement/Breaking: " + ChatColor.GREEN + "Whitelist Mode");
					}
					else{
						lines.add(ChatColor.GRAY + "Block Placement/Breaking: " + ChatColor.RED + "Blacklist Mode");
					}
					
					if(mgm.hasFlags()){
						lines.add(ChatColor.GRAY + "Flags: " + ChatColor.GREEN + mgm.getFlags().size());
					}else{
						lines.add(ChatColor.GRAY + "Flags: " + ChatColor.RED + "0");
					}
					
					if(mgm.hasDefaultLoadout()){
						lines.add(ChatColor.GRAY + "Default Loadout: " + ChatColor.GREEN + mgm.getDefaultPlayerLoadout().getItems().size() + " items");
					}else{
						lines.add(ChatColor.GRAY + "Default Loadout: " + ChatColor.RED + "0 items");
					}
					
					if(!mgm.getLoadouts().isEmpty()){
						lines.add(ChatColor.GRAY + "Additional Loadouts: " + ChatColor.GREEN + mgm.getLoadouts().size());
					}else{
						lines.add(ChatColor.GRAY + "Additional Loadouts: " + ChatColor.RED + "0");
					}
					
					if(!mgm.getType().equals("sp")){
						lines.add(ChatColor.GRAY + "Maximum Players: " + ChatColor.GREEN + mgm.getMaxPlayers());
						lines.add(ChatColor.GRAY + "Minimum Players: " + ChatColor.GREEN + mgm.getMinPlayers());
					}
					
					if(mgm.getRewardItem() != null){
						lines.add(ChatColor.GRAY + "Reward Item: " + ChatColor.GREEN + MinigameUtils.getItemStackName(mgm.getRewardItem()) + "(x" + mgm.getRewardItem().getAmount() + ")");
					}else{
						lines.add(ChatColor.GRAY + "Reward Item: " + ChatColor.RED + "Not Set");
					}
					
					if(mgm.getRewardPrice() != 0){
						lines.add(ChatColor.GRAY + "Reward Money: " + ChatColor.GREEN + "$" + mgm.getRewardPrice());
					}
					else{
						lines.add(ChatColor.GRAY + "Reward Money: " + ChatColor.RED + "Not Set");
					}
					
					if(mgm.getSecondaryRewardItem() != null){
						lines.add(ChatColor.GRAY + "Secondary Reward Item: " + ChatColor.GREEN + MinigameUtils.getItemStackName(mgm.getSecondaryRewardItem()) + "(x" + mgm.getSecondaryRewardItem().getAmount() + ")");
					}else{
						lines.add(ChatColor.GRAY + "Secondary Reward Item: " + ChatColor.RED + "Not Set");
					}
					
					if(mgm.getSecondaryRewardPrice() != 0){
						lines.add(ChatColor.GRAY + "Secondary Reward Money: " + ChatColor.GREEN + "$" + mgm.getSecondaryRewardPrice());
					}
					else{
						lines.add(ChatColor.GRAY + "Secondary Reward Money: " + ChatColor.RED + "Not Set");
					}
					
					if(!mgm.getType().equals("sp")){
						if(mgm.getTimer() != 0){
							lines.add(ChatColor.GRAY + "Game Timer: " + ChatColor.GREEN + MinigameUtils.convertTime(mgm.getTimer()));
						}
						else{
							lines.add(ChatColor.GRAY + "Game Timer: " + ChatColor.RED + "Not Set");
						}
					}
					
					if(mgm.hasPaintBallMode()){
						lines.add(ChatColor.GRAY + "PaintBall Mode: " + ChatColor.GREEN + "true");
						lines.add(ChatColor.GRAY + "PaintBall Damage: " + ChatColor.GREEN + mgm.getPaintBallDamage());
					}
					else{
						lines.add(ChatColor.GRAY + "PaintBall Mode: " + ChatColor.RED + "false");
					}
				}
				else{
					if(mgm.getStartLocations().size() > 0){
						lines.add(ChatColor.GRAY + "Starting Position: " + ChatColor.GREEN + mgm.getStartLocations().get(0).getBlockX() + "x, " + 
								mgm.getStartLocations().get(0).getBlockY() + "y, " + 
								mgm.getStartLocations().get(0).getBlockZ() + "z");
					}
					else{
						lines.add(ChatColor.GRAY + "Starting Position: " + ChatColor.RED + "Not Set");
					}
					
					if(mgm.getLocation() != null){
						lines.add(ChatColor.GRAY + "Location Name: " + ChatColor.GREEN + mgm.getLocation());
					}
					else{
						lines.add(ChatColor.GRAY + "Location Name: " + ChatColor.RED + "Not Set");
					}
					
					lines.add(ChatColor.GRAY + "Maximum Radius: " + ChatColor.GREEN + mgm.getMaxRadius());
					lines.add(ChatColor.GRAY + "Minimum Treasure: " + ChatColor.GREEN + mgm.getMinTreasure());
					lines.add(ChatColor.GRAY + "Maximum Treasure: " + ChatColor.GREEN + mgm.getMaxTreasure());
					
					if(mgm.hasDefaultLoadout()){
						lines.add(ChatColor.GRAY + "Default Loadout: " + ChatColor.GREEN + mgm.getDefaultPlayerLoadout().getItems().size() + " items");
					}else{
						lines.add(ChatColor.GRAY + "Default Loadout: " + ChatColor.RED + "0 items");
					}
				}
				
				int page = 1;
				int pages = 1;
				
				if(lines.size() > 9){
					double pageamnt = Math.ceil(((double)lines.size()) / 9);
					pages = (int) pageamnt;
				}
				
				if(args.length >= 2 && args[1].matches("[0-9]+")){
					page = Integer.parseInt(args[1]);
					if(page > pages){
						page = pages;
					}
				}
				sender.sendMessage(ChatColor.GREEN + "-------------------Page " + page + "/" + pages + "-------------------");
				
				int offset = 0 + (page * 9 - 9);
				int offsetUpper = offset + 8;
				if(offsetUpper >= lines.size()){
					offsetUpper = lines.size() - 1;
				}
				
				for(int i = offset; i <= offsetUpper; i++){
					sender.sendMessage(lines.get(i));
				}
			}
			else{
				sender.sendMessage(ChatColor.RED + "There is no Minigame by the name " + args[0]);
			}
			return true;
		}
		return false;
	}
}
