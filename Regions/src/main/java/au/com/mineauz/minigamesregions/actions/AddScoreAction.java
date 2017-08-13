package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class AddScoreAction extends ActionInterface {
	
	private IntegerFlag amount = new IntegerFlag(1, "amount");

	@Override
	public String getName() {
		return "ADD_SCORE";
	}

	@Override
	public String getCategory() {
		return "Minigame Actions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
		out.put("Score", amount.getFlag());
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node base) {
		debug(player,base);
		if(player == null || !player.isInMinigame()) return;
		player.addScore(amount.getFlag());
		player.getMinigame().setScore(player, player.getScore());
		checkScore(player);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region base) {
		debug(player,base);
		if(player == null || !player.isInMinigame()) return;
		player.addScore(amount.getFlag());
		player.getMinigame().setScore(player, player.getScore());

		checkScore(player);
	}

	private void checkScore(MinigamePlayer player){
		if(player.getMinigame().getMaxScorePerPlayer() <= player.getScore() || player.getTeam().getScore() >= player.getMinigame().getMaxScore()){
			List<MinigamePlayer> w;
			List<MinigamePlayer> l;
			if(player.getMinigame().isTeamGame()){
				w = new ArrayList<>(player.getTeam().getPlayers());
				l = new ArrayList<>(player.getMinigame().getPlayers().size() - player.getTeam().getPlayers().size());
				for(Team t : TeamsModule.getMinigameModule(player.getMinigame()).getTeams()){
					if(t != player.getTeam())
						l.addAll(t.getPlayers());
				}
			}
			else{
				w = new ArrayList<>(1);
				l = new ArrayList<>(player.getMinigame().getPlayers().size());
				w.add(player);
				l.addAll(player.getMinigame().getPlayers());
				l.remove(player);
			}
			Minigames.plugin.pdata.endMinigame(player.getMinigame(), w, l);
		}
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		amount.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		amount.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Add Score", player);
		m.addItem(new MenuItemInteger("Add Score Amount", Material.ENDER_PEARL, new Callback<Integer>() {
			
			@Override
			public void setValue(Integer value) {
				amount.setFlag(value);
			}
			
			@Override
			public Integer getValue() {
				return amount.getFlag();
			}
		}, null, null));
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.displayMenu(player);
		return true;
	}

}
