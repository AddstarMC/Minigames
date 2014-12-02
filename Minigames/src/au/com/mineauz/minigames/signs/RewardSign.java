package au.com.mineauz.minigames.signs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.mineauz.minigames.MinigameData;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemRewardAdd;
import au.com.mineauz.minigames.menu.MenuItemRewardGroup;
import au.com.mineauz.minigames.menu.MenuItemRewardGroupAdd;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class RewardSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;
	private MinigameData mdata = plugin.mdata;

	@Override
	public String getName() {
		return "Reward";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.reward";
	}

	@Override
	public String getCreatePermissionMessage() {
		return MinigameUtils.getLang("sign.reward.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.reward";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.reward.usePermission");
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(!event.getLine(2).equals("")){
			event.setLine(1, ChatColor.GREEN + getName());
			return true;
		}
		plugin.pdata.getMinigamePlayer(event.getPlayer()).sendMessage(MinigameUtils.getLang("sign.reward.noName"), "error");
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		Location loc = sign.getLocation();
		if(!MinigameUtils.isMinigameTool(player.getPlayer().getItemInHand())){
			String label = sign.getLine(2).toLowerCase();
			if(player.isInMinigame()){
				if(!player.hasTempClaimedReward(label)){
					if(mdata.hasRewardSign(loc)){
						Rewards rew = mdata.getRewardSign(loc);
						for(RewardType r : rew.getReward()){
							r.giveReward(player);
						}
					}
					player.addTempClaimedReward(label);
				}
			}
			else{
				if(!player.hasClaimedReward(label)){
					if(mdata.hasRewardSign(loc)){
						Rewards rew = mdata.getRewardSign(loc);
						for(RewardType r : rew.getReward()){
							r.giveReward(player);
						}
						
						player.getPlayer().updateInventory();
					}
					player.addClaimedReward(label);
				}
			}
		}
		else if(player.getPlayer().hasPermission("minigame.tool")){
			Rewards rew = null;
			if(!mdata.hasRewardSign(loc)){
				mdata.addRewardSign(loc);
			}
			rew = mdata.getRewardSign(loc);
			
			Menu rewardMenu = new Menu(5, getName(), player);
			
			List<String> des = new ArrayList<String>();
			des.add("Click this with an item");
			des.add("to add it to rewards.");
			des.add("Click without an item");
			des.add("to add a money reward.");
			rewardMenu.addItem(new MenuItemRewardGroupAdd("Add Group", Material.ITEM_FRAME, rew), 42);
			rewardMenu.addItem(new MenuItemRewardAdd("Add Item", des, Material.ITEM_FRAME, rew), 43);
			final MenuItemCustom mic = new MenuItemCustom("Save Rewards", Material.REDSTONE_TORCH_ON);
			final Location floc = loc;
			mic.setClick(new InteractionInterface() {
				
				@Override
				public Object interact(Object object) {
					mdata.saveRewardSign(MinigameUtils.createLocationID(floc), true);
					mic.getContainer().getViewer().sendMessage("Saved rewards for this sign.", null);
					mic.getContainer().getViewer().getPlayer().closeInventory();
					return null;
				}
			});
			rewardMenu.addItem(mic, 44);
			List<String> list = new ArrayList<String>();
			for(RewardRarity r : RewardRarity.values()){
				list.add(r.toString());
			}
			
			List<MenuItem> mi = new ArrayList<MenuItem>();
			for(RewardType item : rew.getRewards()){
				mi.add(item.getMenuItem());
//				if(item.getRewardItem() != null){
//					MenuItemReward mrew = new MenuItemReward(MinigameUtils.getItemStackName(item.getRewardItem()), item.getRewardItem().getType(), item, rew, list);
//					mrew.setRewardItem(item.getRewardItem());
//					mrew.updateDescription();
//					mi.add(mrew);
//				}
//				else{
//					MenuItemReward mrew = new MenuItemReward("$" + item.getMoney(), Material.PAPER, item, rew, list);
//					mi.add(mrew);
//				}
			}
			des = new ArrayList<String>();
			des.add("Double Click to edit");
			for(RewardGroup group : rew.getGroups()){
				MenuItemRewardGroup rwg = new MenuItemRewardGroup(group.getName() + " Group", des, Material.CHEST, group, rew);
				mi.add(rwg);
			}
			rewardMenu.addItems(mi);
			rewardMenu.displayMenu(player);
		}
		return true;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		if(plugin.mdata.hasRewardSign(sign.getLocation())){
			plugin.mdata.removeRewardSign(sign.getLocation());
		}
	}

}
