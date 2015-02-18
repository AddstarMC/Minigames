package au.com.mineauz.minigames.minigame.reward;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemString;

public class CommandReward extends RewardType{
	
	private String command = "say Hello World!";

	public CommandReward(Rewards rewards) {
		super(rewards);
	}

	@Override
	public String getName() {
		return "COMMAND";
	}

	@Override
	public boolean isUsable() {
		return true;
	}

	@Override
	public void giveReward(MinigamePlayer player) {
		String finalCommand = command.replace("%player%", player.getName());
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
	}

	@Override
	public MenuItem getMenuItem() {
		return new CommandRewardItem(this);
	}

	@Override
	public void saveReward(String path, FileConfiguration config) {
		config.set(path, command);
	}

	@Override
	public void loadReward(String path, FileConfiguration config) {
		command = config.getString(path);
	}
	
	private class CommandRewardItem extends MenuItemString{

		private List<String> options = new ArrayList<String>();
		private CommandReward reward;

		public CommandRewardItem(CommandReward reward) {
			super("/" + command, Material.COMMAND, new Callback<String>() {

				@Override
				public void setValue(String value) {
					if(value.startsWith("./"))
						value = value.replace("./", "/");
					command = value;
				}

				@Override
				public String getValue() {
					return command;
				}
			});
			for(RewardRarity rarity : RewardRarity.values()){
				options.add(rarity.toString());
			}
			this.reward = reward;
			updateDescription();
		}
		
		public void updateName(String newName){
			ItemMeta meta = getItem().getItemMeta();
			if(newName.length() > 16){
				newName = newName.substring(0, 15);
				newName += "...";
			}
			meta.setDisplayName(ChatColor.RESET + newName);
			getItem().setItemMeta(meta);
			getContainer().refresh();
		}
		
		@Override
		public void updateDescription(){
			List<String> description = null;
			if(options == null){
				options = new ArrayList<String>();
				for(RewardRarity rarity : RewardRarity.values()){
					options.add(rarity.toString());
				}
			}
			int pos = options.indexOf(getRarity().toString());
			int before = pos - 1;
			int after = pos + 1;
			if(before == -1)
				before = options.size() - 1;
			if(after == options.size())
				after = 0;
			
			if(getDescription() != null){
				description = getDescription();
				if(getDescription().size() >= 3){
					String desc = ChatColor.stripColor(getDescription().get(1));
					
					if(options.contains(desc)){
						description.set(0, ChatColor.GRAY.toString() + options.get(before));
						description.set(1, ChatColor.GREEN.toString() + getRarity().toString());
						description.set(2, ChatColor.GRAY.toString() + options.get(after));
					}
					else{
						description.add(0, ChatColor.GRAY.toString() + options.get(before));
						description.add(1, ChatColor.GREEN.toString() + getRarity().toString());
						description.add(2, ChatColor.GRAY.toString() + options.get(after));
						description.add(3, ChatColor.DARK_PURPLE.toString() + "Shift + Left Click to change");
						description.add(4, ChatColor.DARK_PURPLE.toString() + "Shift + Right Click to remove");
					}
				}
				else{
					description.add(0, ChatColor.GRAY.toString() + options.get(before));
					description.add(1, ChatColor.GREEN.toString() + getRarity().toString());
					description.add(2, ChatColor.GRAY.toString() + options.get(after));
					description.add(3, ChatColor.DARK_PURPLE.toString() + "Shift + Left Click to change");
					description.add(4, ChatColor.DARK_PURPLE.toString() + "Shift + Right Click to remove");
				}
			}
			else{
				description = new ArrayList<String>();
				description.add(ChatColor.GRAY.toString() + options.get(before));
				description.add(ChatColor.GREEN.toString() + getRarity().toString());
				description.add(ChatColor.GRAY.toString() + options.get(after));
				description.add(3, ChatColor.DARK_PURPLE.toString() + "Shift + Left Click to change");
				description.add(4, ChatColor.DARK_PURPLE.toString() + "Shift + Right Click to remove");
			}
			
			setDescription(description);
		}
		
		@Override
		public ItemStack onDoubleClick(MinigamePlayer player){
			return getItem();
		}
		
		@Override
		public ItemStack onClick(MinigamePlayer player){
			int ind = options.lastIndexOf(getRarity().toString());
			ind++;
			if(ind == options.size())
				ind = 0;
			
			setRarity(RewardRarity.valueOf(options.get(ind)));
			updateDescription();
			
			return getItem();
		}
		
		@Override
		public ItemStack onRightClick(MinigamePlayer player){
			int ind = options.lastIndexOf(getRarity().toString());
			ind--;
			if(ind == -1)
				ind = options.size() - 1;
			
			setRarity(RewardRarity.valueOf(options.get(ind)));
			updateDescription();
			
			return getItem();
		}
		
		@Override
		public ItemStack onShiftRightClick(MinigamePlayer player){
			getRewards().removeReward(reward);
			remove();
			return null;
		}
		
		@Override
		public ItemStack onShiftClick(MinigamePlayer player){
			super.onDoubleClick(player);
			return null;
		}
		
		@Override
		public void checkValidEntry(MinigamePlayer player, String entry){
			super.checkValidEntry(player, entry);
			updateName(entry);
		}
	}

}
