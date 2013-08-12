package com.pauldavdesign.mineauz.minigames.commands.set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.Minigame;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.commands.ICommand;

public class SetSecondaryRewardCommand implements ICommand{
	
	@Override
	public String getName() {
		return "reward2";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"secondaryreward", "sreward"};
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public String getDescription() {
		return "Sets the players secondary reward for completing the Minigame after the first time.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame set <Minigame> reward2 <Item ID / Item Name> [Quantity]",
				"/minigame set <Minigame> reward2 $<Money Amount>"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to set a Minigames secondary reward!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.reward2";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		if(args != null){
			int quantity = 1;
			double money = -1;
			if(args.length >= 2 && args[1].matches("[0-9]+")){
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
				minigame.setSecondaryRewardItem(item);
				if(item.getAmount() == 1){
					sender.sendMessage(ChatColor.GRAY + "Secondary reward for \"" + minigame.getName() + "\" has been set to " + 
							MinigameUtils.getItemStackName(item));
				}
				else{
					sender.sendMessage(ChatColor.GRAY + "Secondary reward for \"" + minigame.getName() + "\" has been set to " + 
							MinigameUtils.getItemStackName(item) + " with a quantity of " + quantity);
				}
				return true;
			}
			else if(sender instanceof Player && args[0].equals("SLOT")){
				item = ((Player)sender).getItemInHand();
				minigame.setSecondaryRewardItem(item);
				sender.sendMessage(ChatColor.GRAY + "Primary reward for \"" + minigame.getName() + "\" has been set to " + 
						MinigameUtils.getItemStackName(item));
				return true;
			}
			else if(item != null && item.getTypeId() == 0){
				minigame.setSecondaryRewardItem(null);
				sender.sendMessage(ChatColor.GRAY + "Secondary reward for \"" + minigame.getName() + "\" has been removed.");
				return true;
			}
			else if(money != -1 && plugin.hasEconomy()){
				minigame.setSecondaryRewardPrice(money);
				sender.sendMessage(ChatColor.GRAY + "Secondary reward for \"" + minigame.getName() + "\" has been set to " + 
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
