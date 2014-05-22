package com.pauldavdesign.mineauz.minigames.signs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.SignChangeEvent;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.Minigames;
import com.pauldavdesign.mineauz.minigames.gametypes.MinigameType;
import com.pauldavdesign.mineauz.minigames.minigame.Minigame;

public class FinishSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;

	@Override
	public String getName() {
		return "Finish";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.finish";
	}

	@Override
	public String getCreatePermissionMessage() {
		return MinigameUtils.getLang("sign.finish.createPermission");
	}

	@Override
	public String getUsePermission() {
		return null;
	}

	@Override
	public String getUsePermissionMessage() {
		return null;
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		event.setLine(1, ChatColor.GREEN + "Finish");
		if(!event.getLine(2).isEmpty() && plugin.mdata.hasMinigame(event.getLine(2))){
			event.setLine(2, plugin.mdata.getMinigame(event.getLine(2)).getName(false));
		}
		else if(!event.getLine(2).isEmpty()){
			event.getPlayer().sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("minigame.error.noMinigame"));
			return false;
		}
		return true;
	}

	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		if(player.isInMinigame() && player.getPlayer().getItemInHand().getType() == Material.AIR){
			Minigame minigame = player.getMinigame();

			if(minigame.isSpectator(player)){
				return false;
			}
			
			if(!minigame.getFlags().isEmpty()){
				if(((LivingEntity)player.getPlayer()).isOnGround()){
					if(plugin.pdata.checkRequiredFlags(player, minigame.getName(false)).isEmpty()){
						if(sign.getLine(2).isEmpty() || sign.getLine(2).equals(player.getMinigame().getName(false))){
							if(player.getMinigame().getType() == MinigameType.TEAMS){
								if(player.getMinigame().getRedTeam().contains(player.getPlayer().getPlayer())){
									List<MinigamePlayer> w;
									List<MinigamePlayer> l;
									w = new ArrayList<MinigamePlayer>(minigame.getRedTeam().size());
									l = new ArrayList<MinigamePlayer>(minigame.getBlueTeam().size());
									for(OfflinePlayer pl : minigame.getRedTeam()){
										w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
									}
									for(OfflinePlayer pl : minigame.getBlueTeam()){
										l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
									}
									plugin.pdata.endMinigame(minigame, w, l);
								}
								else{
									List<MinigamePlayer> w;
									List<MinigamePlayer> l;
									l = new ArrayList<MinigamePlayer>(minigame.getRedTeam().size());
									w = new ArrayList<MinigamePlayer>(minigame.getBlueTeam().size());
									for(OfflinePlayer pl : minigame.getRedTeam()){
										l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
									}
									for(OfflinePlayer pl : minigame.getBlueTeam()){
										w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
									}
									plugin.pdata.endMinigame(minigame, w, l);
								}
							}
							else{
								if(minigame.getType() == MinigameType.FREE_FOR_ALL){
									List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(1);
									w.add(player);
									List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(minigame.getPlayers().size());
									l.addAll(minigame.getPlayers());
									l.remove(player);
									plugin.pdata.endMinigame(minigame, w, l);
								}
								else
									plugin.pdata.endMinigame(player);
							}
							
							plugin.pdata.partyMode(player, 3, 10L);
						}
					}
					else{
						List<String> requiredFlags = plugin.pdata.checkRequiredFlags(player, minigame.getName(false));
						String flags = "";
						int num = requiredFlags.size();
						
						for(int i = 0; i < num; i++){
							flags += requiredFlags.get(i);
							if(i != num - 1){
								flags += ", ";
							}
						}
						player.sendMessage(ChatColor.AQUA + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.finish.requireFlags"));
						player.sendMessage(ChatColor.GRAY + flags);
					}
				}
				return true;
			}
			else{
				if(((LivingEntity)player.getPlayer()).isOnGround()){
					if(player.getMinigame().getType() == MinigameType.TEAMS){
						if(player.getMinigame().getRedTeam().contains(player.getPlayer().getPlayer())){
							List<MinigamePlayer> w;
							List<MinigamePlayer> l;
							w = new ArrayList<MinigamePlayer>(minigame.getRedTeam().size());
							l = new ArrayList<MinigamePlayer>(minigame.getBlueTeam().size());
							for(OfflinePlayer pl : minigame.getRedTeam()){
								w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							for(OfflinePlayer pl : minigame.getBlueTeam()){
								l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							plugin.pdata.endMinigame(minigame, w, l);
						}
						else{
							List<MinigamePlayer> w;
							List<MinigamePlayer> l;
							l = new ArrayList<MinigamePlayer>(minigame.getRedTeam().size());
							w = new ArrayList<MinigamePlayer>(minigame.getBlueTeam().size());
							for(OfflinePlayer pl : minigame.getRedTeam()){
								l.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							for(OfflinePlayer pl : minigame.getBlueTeam()){
								w.add(plugin.pdata.getMinigamePlayer(pl.getName()));
							}
							plugin.pdata.endMinigame(minigame, w, l);
						}
					}
					else{
						if(minigame.getType() == MinigameType.FREE_FOR_ALL){
							List<MinigamePlayer> w = new ArrayList<MinigamePlayer>(1);
							w.add(player);
							List<MinigamePlayer> l = new ArrayList<MinigamePlayer>(minigame.getPlayers().size());
							l.addAll(minigame.getPlayers());
							l.remove(player);
							plugin.pdata.endMinigame(minigame, w, l);
						}
						else
							plugin.pdata.endMinigame(player);
					}
					plugin.pdata.partyMode(player);
					return true;
				}
			}
		}
		else if(player.getPlayer().getItemInHand().getType() != Material.AIR){
			player.sendMessage(ChatColor.RED + "[Minigames] " + ChatColor.WHITE + MinigameUtils.getLang("sign.emptyHand"));
		}
		return false;
	}

}
