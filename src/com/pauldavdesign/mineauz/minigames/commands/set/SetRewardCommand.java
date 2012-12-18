package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetRewardCommand implements ICommand{

	@Override
	public String getName() {
		return "reward";
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
		return "Sets the players reward for completing the Minigame for the first time.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> reward <Item ID / Item Name> [Quantity]"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set a Minigames reward!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.reward";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			int quantity = 1;
			double money = -1;
			if(args.length >= 2 && args[1].matches("[0-9]")){
				quantity = Integer.parseInt(args[1]);
			}
			
			ItemStack item = null;
			if(args[0].startsWith("$")){
				try{
					money = Double.parseDouble(args[0].replace("$", ""));
				}
				catch(NumberFormatException e){}
			}
			else{
				item = MinigameUtils.stringToItemStack(args[0], quantity);
			}
			
			if(item != null && item.getTypeId() != 0){
				minigame.setRewardItem(item);
				if(item.getAmount() == 1){
					sender.sendMessage(ChatColor.GRAY + "Primary reward for \"" + minigame.getName() + "\" has been set to " + 
							MinigameUtils.getItemStackName(item));
				}
				else{
					sender.sendMessage(ChatColor.GRAY + "Primary reward for \"" + minigame.getName() + "\" has been set to " + 
							MinigameUtils.getItemStackName(item) + " with a quantity of " + quantity);
				}
				return true;
			}
			else if(item != null && item.getTypeId() == 0){
				minigame.setRewardItem(null);
				sender.sendMessage(ChatColor.GRAY + "Primary reward for \"" + minigame.getName() + "\" has been removed.");
				return true;
			}
			else if(money != -1 && plugin.hasEconomy()){
				minigame.setRewardPrice(money);
				sender.sendMessage(ChatColor.GRAY + "Primary reward money for \"" + minigame.getName() + "\" has been set to " + 
						args[0]);
				return true;
			}
			else if(!plugin.hasEconomy()){
				sender.sendMessage(ChatColor.RED + "Vault required to set a money reward! Download from dev.bukkit.org");
				return true;
			}
		}
		return false;
	}

}
