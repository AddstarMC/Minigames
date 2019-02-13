package au.com.mineauz.minigamesregions;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import au.com.mineauz.minigames.managers.MinigamePlayerManager;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.events.*;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigamesregions.events.EnterRegionEvent;
import au.com.mineauz.minigamesregions.events.LeaveRegionEvent;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import au.com.mineauz.minigamesregions.triggers.Triggers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class RegionEvents implements Listener{
    
    private Minigames plugin = Minigames.getPlugin();
    private MinigamePlayerManager pdata = plugin.getPlayerManager();
    
    private void executeRegionChanges(Minigame mg, MinigamePlayer ply){
        for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
            if(r.playerInRegion(ply)){
                if(!r.hasPlayer(ply)){
                    r.addPlayer(ply);
                    r.execute(Triggers.getTrigger("ENTER"), ply);
                    EnterRegionEvent ev = new EnterRegionEvent(ply, r);
                    Bukkit.getPluginManager().callEvent(ev);
                }
            }
            else{
                if(r.hasPlayer(ply)){
                    r.removePlayer(ply);
                    r.execute(Triggers.getTrigger("LEAVE"), ply);
                    LeaveRegionEvent ev = new LeaveRegionEvent(ply, r);
                    Bukkit.getPluginManager().callEvent(ev);
                }
            }
        }
    }
    
    private RegionModule getRegionModule(Minigame minigame){
        return RegionModule.getMinigameModule(minigame);
    }
    
    @EventHandler(ignoreCancelled = true)
    private void playerMove(PlayerMoveEvent event){
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if(ply == null) return;
        if(ply.isInMinigame()){
            Minigame mg = ply.getMinigame();
            executeRegionChanges(mg, ply);
        }
    }
    
    @EventHandler
    private void playerSpawn(PlayerRespawnEvent event){
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if(ply == null) return;
        if(ply.isInMinigame()){
            final Minigame mg = ply.getMinigame();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (!ply.isInMinigame()) {
                    return;
                }

                executeRegionChanges(mg, ply);

                for(Node node : RegionModule.getMinigameModule(ply.getMinigame()).getNodes()){
                    node.execute(Triggers.getTrigger("RESPAWN"), ply);
                }
                for(Region region : RegionModule.getMinigameModule(ply.getMinigame()).getRegions()){
                    if(region.hasPlayer(ply))
                        region.execute(Triggers.getTrigger("RESPAWN"), ply);
                }
            });
        }
    }
    
    @EventHandler
    private void playerDeath(PlayerDeathEvent event){
        MinigamePlayer ply = pdata.getMinigamePlayer(event.getEntity());
        boolean pvp = false;
        MinigamePlayer killer = null;
        if(ply == null) return;
        if(ply.isInMinigame()){
            if (event.getEntity().getKiller() != null) {
                killer = pdata.getMinigamePlayer(event.getEntity().getKiller());
                if (killer != null && killer.isInMinigame()) {
                    pvp = true;
                }
            }
            for(Node node : RegionModule.getMinigameModule(ply.getMinigame()).getNodes()){
                node.execute(Triggers.getTrigger("DEATH"), ply);
                if(pvp){
                    node.execute(Triggers.getTrigger("PLAYER_KILL"),killer);
                    node.execute(Triggers.getTrigger("PLAYER_KILLED"),ply);
                }
            }
            for(Region region : RegionModule.getMinigameModule(ply.getMinigame()).getRegions()){
                if(region.hasPlayer(ply))
                    region.execute(Triggers.getTrigger("DEATH"), ply);
                if(pvp){
                    region.execute(Triggers.getTrigger("PLAYER_KILL"),killer);
                    region.execute(Triggers.getTrigger("PLAYER_KILLED"),ply);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private void playerJoin(JoinMinigameEvent event){
        final MinigamePlayer ply = event.getMinigamePlayer();
        if(ply == null) return;
        final Minigame mg = event.getMinigame();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            executeRegionChanges(mg, ply);

            for(Node node : RegionModule.getMinigameModule(mg).getNodes()){
                node.execute(Triggers.getTrigger("GAME_JOIN"), ply);
            }
            for(Region region : RegionModule.getMinigameModule(mg).getRegions()){
                if(region.hasPlayer(ply))
                    region.execute(Triggers.getTrigger("GAME_JOIN"), ply);
            }
        });
        if(event.getMinigame().getPlayers().size() == 0){
            for(Region region : RegionModule.getMinigameModule(event.getMinigame()).getRegions()){
                for(RegionExecutor ex : region.getExecutors()){
                    if(ex.getTrigger().getName().equalsIgnoreCase("TICK")){
                        region.startTickTask();
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    private void minigameStart(StartMinigameEvent event){
        for(Node node : RegionModule.getMinigameModule(event.getMinigame()).getNodes()){
            for (MinigamePlayer player:event.getPlayers()) {
                node.execute(Triggers.getTrigger("GAME_START"), player);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private void playerQuit(QuitMinigameEvent event){
        if(RegionModule.getMinigameModule(event.getMinigame()) == null) {
            Minigames.debugMessage(event.getMinigame() + " called region event with no RegionModule loaded... was this intended?");
            return;
        }
        MinigamePlayer ply = event.getMinigamePlayer();
        if(ply == null) return;
        Minigame mg = ply.getMinigame();
        for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
            if(r.hasPlayer(ply))
                r.removePlayer(ply);
        }
        for(Node node : RegionModule.getMinigameModule(event.getMinigame()).getNodes()){
            node.execute(Triggers.getTrigger("GAME_QUIT"), event.getMinigamePlayer());
            if(event.getMinigame().getPlayers().size() > 1){
                for(NodeExecutor exec : node.getExecutors())
                    exec.removeTrigger(event.getMinigamePlayer());
            }
            else{
                for(NodeExecutor exec : node.getExecutors())
                    exec.clearTriggers();
                node.setEnabled(true);
            }
        }
        for(Region region : RegionModule.getMinigameModule(event.getMinigame()).getRegions()){
            if(region.playerInRegion(ply))
                region.execute(Triggers.getTrigger("GAME_QUIT"), event.getMinigamePlayer());
            if(event.getMinigame().getPlayers().size() > 1){
                for(RegionExecutor exec : region.getExecutors())
                    exec.removeTrigger(event.getMinigamePlayer());
            }
            else{
                for(RegionExecutor exec : region.getExecutors()){
                    exec.clearTriggers();
                }
                region.removeTickTask();
                region.setEnabled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private void playersEnd(EndMinigameEvent event){
        if(RegionModule.getMinigameModule(event.getMinigame()) == null) {
            Minigames.debugMessage(event.getMinigame() + " called region event with no RegionModule loaded... was this intended?");
            return;
        }
        for(MinigamePlayer ply : event.getWinners()){
            Minigame mg = ply.getMinigame();
            for(Region r : RegionModule.getMinigameModule(mg).getRegions()){
                if(r.hasPlayer(ply))
                    r.removePlayer(ply);
            }
        }
        for(Node node : RegionModule.getMinigameModule(event.getMinigame()).getNodes()){
            node.execute(Triggers.getTrigger("GAME_END"), null);
            for(NodeExecutor exec : node.getExecutors())
                exec.clearTriggers();
        }
        for(Region region : RegionModule.getMinigameModule(event.getMinigame()).getRegions()){
            for(RegionExecutor exec : region.getExecutors())
                exec.clearTriggers();
        }
    }
    
    @EventHandler()
    private void interactNode(PlayerInteractEvent event){
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply == null || !ply.isInMinigame()) {
            return;
        }
        
        if (!event.isCancelled()) {
            if (event.getAction() == Action.PHYSICAL) {
                switch (event.getClickedBlock().getType()) {
                    case STONE_PRESSURE_PLATE:
                    case ACACIA_PRESSURE_PLATE:
                    case LIGHT_WEIGHTED_PRESSURE_PLATE:
                    case HEAVY_WEIGHTED_PRESSURE_PLATE:
                    case BIRCH_PRESSURE_PLATE:
                    case DARK_OAK_PRESSURE_PLATE:
                    case JUNGLE_PRESSURE_PLATE:
                    case OAK_PRESSURE_PLATE:
                    case SPRUCE_PRESSURE_PLATE:
                    trigger(ply, event.getClickedBlock(), Triggers.getTrigger("INTERACT"));
                    break;
                default:
                    break;
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                switch (event.getClickedBlock().getType()) {
                    case ACACIA_BUTTON:
                    case BIRCH_BUTTON:
                    case DARK_OAK_BUTTON:
                    case JUNGLE_BUTTON:
                    case OAK_BUTTON:
                    case SPRUCE_BUTTON:
                    case STONE_BUTTON:
                        trigger(ply, event.getClickedBlock(), Triggers.getTrigger("INTERACT"));
                        break;
                    default:
                        break;
                }
            }
        }
        
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            trigger(ply, event.getClickedBlock(), Triggers.getTrigger("LEFT_CLICK_BLOCK"));
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            trigger(ply, event.getClickedBlock(), Triggers.getTrigger("RIGHT_CLICK_BLOCK"));
        }
    }
    
    private void trigger(final MinigamePlayer player, final Block block, final Trigger trigger) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!player.isInMinigame()) {
                return;
            }

            for (Node node : RegionModule.getMinigameModule(player.getMinigame()).getNodes()) {
                if (node.getLocation().getBlock().equals(block)) {
                    node.execute(trigger, player);
                }
            }
        });
    }
    
    @EventHandler(ignoreCancelled = true)
    private void blockBreak(BlockBreakEvent event){
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if(ply == null)return;
        
        if(ply.isInMinigame()){
            final Location loc2 = event.getBlock().getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (!ply.isInMinigame()) {
                    return;
                }

                for(Node node : RegionModule.getMinigameModule(ply.getMinigame()).getNodes()){
                    if(node.getLocation().getWorld() == loc2.getWorld()){
                        Location loc1 = node.getLocation();
                        if(loc1.getBlockX() == loc2.getBlockX() &&
                                loc1.getBlockY() == loc2.getBlockY() &&
                                loc1.getBlockZ() == loc2.getBlockZ()){
                            node.execute(Triggers.getTrigger("BLOCK_BREAK"), ply);
                        }
                    }
                }

                for(Region region : RegionModule.getMinigameModule(ply.getMinigame()).getRegions()){
                    if (region.locationInRegion(loc2)) {
                        region.execute(Triggers.getTrigger("BLOCK_BREAK"), ply);
                    }
                }
            });
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private void blockPlace(BlockPlaceEvent event){
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if(ply == null)return;
        
        if(ply.isInMinigame()){
            final Location loc2 = event.getBlock().getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (!ply.isInMinigame()) {
                    return;
                }

                for(Node node : RegionModule.getMinigameModule(ply.getMinigame()).getNodes()){
                    if(node.getLocation().getWorld() == loc2.getWorld()){
                        Location loc1 = node.getLocation();
                        if(loc1.getBlockX() == loc2.getBlockX() &&
                                loc1.getBlockY() == loc2.getBlockY() &&
                                loc1.getBlockZ() == loc2.getBlockZ()){
                            node.execute(Triggers.getTrigger("BLOCK_PLACE"), ply);
                        }
                    }
                }

                for(Region region : RegionModule.getMinigameModule(ply.getMinigame()).getRegions()){
                    if (region.locationInRegion(loc2)) {
                        region.execute(Triggers.getTrigger("BLOCK_PLACE"), ply);
                    }
                }
            });
        }
    }
    
    @EventHandler
    private void minigameTimerTick(MinigameTimerTickEvent event){
        for(Node node : getRegionModule(event.getMinigame()).getNodes()){
            for(MinigamePlayer player : event.getMinigame().getPlayers()) {
                node.execute(Triggers.getTrigger("MINIGAME_TIMER"), player);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private void itemPickupEvent(EntityPickupItemEvent event){
        if(!(event.getEntity() instanceof Player))return;
        final MinigamePlayer ply = pdata.getMinigamePlayer(((Player)event.getEntity()));
        if(ply.isInMinigame()){
            final Trigger trig = Triggers.getTrigger("ITEM_PICKUP");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, executeScriptObjects(ply,trig));
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    private void itemPickupEvent(PlayerDropItemEvent event) {
        final MinigamePlayer ply = pdata.getMinigamePlayer(event.getPlayer());
        if (ply.isInMinigame()) {
            final Trigger trig = Triggers.getTrigger("ITEM_DROP");
            
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, executeScriptObjects(ply, trig));
        }
    }
    
    private Runnable executeScriptObjects(MinigamePlayer ply, Trigger trig){
        return () -> {
            if (!ply.isInMinigame()) {
                return;
            }
            
            for(Node node : getRegionModule(ply.getMinigame()).getNodes()){
                node.execute(trig, ply);
            }
            
            for(Region region : getRegionModule(ply.getMinigame()).getRegions()){
                if(region.hasPlayer(ply)){
                    region.execute(trig, ply);
                }
            }
        };
    }
    
    
    @EventHandler(priority=EventPriority.LOWEST)
    private void playerDisconnect(PlayerQuitEvent event) {
        Main.getPlugin().getDisplayManager().hideAll(event.getPlayer());
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void playerXpChange(PlayerExpChangeEvent event) {
        final MinigamePlayer player = pdata.getMinigamePlayer(event.getPlayer());
        if (player == null || !player.isInMinigame()) {
            return;
        }
        
        executeTrigger(Triggers.getTrigger("XP_CHANGE"), player);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void playerFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        final MinigamePlayer player = pdata.getMinigamePlayer((Player)event.getEntity());
        if (player == null || !player.isInMinigame()) {
            return;
        }
        
        executeTrigger(Triggers.getTrigger("FOOD_CHANGE"), player);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void playerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        final MinigamePlayer player = pdata.getMinigamePlayer((Player)event.getEntity());
        if (player == null || !player.isInMinigame()) {
            return;
        }
        
        executeTrigger(Triggers.getTrigger("PLAYER_DAMAGE"), player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerDropFlag(DropFlagEvent ev){
        executeTrigger(Triggers.getTrigger("PLAYER_DROP_FLAG"),ev.getPlayer());
    }

    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void playerGetFlag(TakeFlagEvent event) {
        executeTrigger(Triggers.getTrigger("PLAYER_TAKE_FLAG"), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    private void entityGlide(EntityToggleGlideEvent event){
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final MinigamePlayer player = pdata.getMinigamePlayer((Player)event.getEntity());
        if (player == null || !player.isInMinigame()) {
            return;
        }
        if(event.isGliding()){
            executeTrigger(Triggers.getTrigger("START_GLIDE"),player);
        }else{
            executeTrigger(Triggers.getTrigger("STOP_GLIDE"), player);
        }
    }

    
    private void executeTrigger(final Trigger trigger, final MinigamePlayer player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!player.isInMinigame()) {
                return;
            }

            RegionModule module = getRegionModule(player.getMinigame());

            if (trigger.useInNodes()) {
                for (Node node : module.getNodes()) {
                    node.execute(trigger, player);
                }
            }

            if (trigger.useInRegions()) {
                for (Region region : module.getRegions()) {
                    if (region.hasPlayer(player)) {
                        region.execute(trigger, player);
                    }
                }
            }
        });
    }
}
