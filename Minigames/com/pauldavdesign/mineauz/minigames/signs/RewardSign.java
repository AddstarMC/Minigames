package com.pauldavdesign.mineauz.minigames.signs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigameData;
import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemReward;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemRewardAdd;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemRewardGroup;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemRewardGroupAdd;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardGroup;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardItem;
import com.pauldavdesign.mineauz.minigames.minigame.reward.RewardRarity;
import com.pauldavdesign.mineauz.minigames.minigame.reward.Rewards;

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
						for(RewardItem r : rew.getReward()){
							if(r.getMoney() != 0){
								plugin.getEconomy().depositPlayer(player.getName(), r.getMoney());
								player.sendMessage(MinigameUtils.formStr("sign.reward.rewardedMoney", r.getMoney()), null);
							}
							else{
								player.addTempRewardItem(r.getItem().clone());
								player.sendMessage(MinigameUtils.formStr("sign.reward.preRewarded", r.getItem().getAmount(), MinigameUtils.getItemStackName(r.getItem())), null);
							}
						}
					}
					player.addTempClaimedReward(label);
				}
			}
			else{
				if(!player.hasClaimedReward(label)){
					if(mdata.hasRewardSign(loc)){
						Rewards rew = mdata.getRewardSign(loc);
						for(RewardItem r : rew.getReward()){
							if(r.getMoney() != 0){
								plugin.getEconomy().depositPlayer(player.getName(), r.getMoney());
								player.sendMessage(MinigameUtils.formStr("sign.reward.rewardedMoney", r.getMoney()), null);
							}
							else{
								Map<Integer, ItemStack> m = player.getPlayer().getInventory().addItem(r.getItem());
								player.sendMessage(MinigameUtils.formStr("sign.reward.rewarded", r.getItem().getAmount(), MinigameUtils.getItemStackName(r.getItem())), null);
								if(!m.isEmpty()){
									for(ItemStack i : m.values()){
										player.getPlayer().getWorld().dropItemNaturally(sign.getLocation(), i);
									}
								}
							}
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
				public Object interact() {
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
			for(RewardItem item : rew.getRewards()){
				if(item.getItem() != null){
					MenuItemReward mrew = new MenuItemReward(MinigameUtils.getItemStackName(item.getItem()), item.getItem().getType(), item, rew, list);
					mrew.setItem(item.getItem());
					mrew.updateDescription();
					mi.add(mrew);
				}
				else{
					MenuItemReward mrew = new MenuItemReward("$" + item.getMoney(), Material.PAPER, item, rew, list);
					mi.add(mrew);
				}
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
