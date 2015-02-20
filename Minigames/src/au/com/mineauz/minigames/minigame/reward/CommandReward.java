package au.com.mineauz.minigames.minigame.reward;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemReward;

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
		return new MenuItemRewardCommand(this);
	}

	@Override
	public void saveReward(String path, FileConfiguration config) {
		config.set(path, command);
	}

	@Override
	public void loadReward(String path, FileConfiguration config) {
		command = config.getString(path);
	}
	
	private static class MenuItemRewardCommand extends MenuItemReward {
		private CommandReward reward;

		public MenuItemRewardCommand(CommandReward reward) {
			super("/" + reward.command, "Double Click to change;Shift + Right Click to remove", Material.COMMAND, reward);
			
			this.reward = reward;
		}
		
		public void updateName(String newName) {
			setName(StringUtils.abbreviate(newName, 16));
		}
		
		@Override
		public void onDoubleClick(MinigamePlayer player) {
			beginManualEntry(player, "Enter command into chat, the menu will automatically reopen in 40s if nothing is entered." , 40);
			player.sendMessage("Dont start the command with '/'. If the command requires a '/' to start, use './'. Note that placing a './' means the command would have 2 slashes", null);
		}
		
		@Override
		public void checkValidEntry(MinigamePlayer player, String entry) {
			if (entry.startsWith("./")) {
				entry = entry.substring(1);
			}
			
			updateName("/" + entry);
			reward.command = entry;
		}
	}

}
