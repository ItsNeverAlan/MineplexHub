package mineplex.hub.modules;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EntityEquipment;

public class WorldManager extends MiniPlugin
{
  public HubManager Manager;
  private HashSet<LivingEntity> _mobs = new HashSet();
  
  private boolean _christmasSnow = false;
  private long _christSnowTime = 0L;
  
  public WorldManager(HubManager manager)
  {
    super("World Manager", manager.GetPlugin());
    
    this.Manager = manager;
  }
  
  @EventHandler
  public void SpawnAnimals(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    Iterator<LivingEntity> entIterator = this._mobs.iterator();
    
    while (entIterator.hasNext())
    {
      LivingEntity ent = (LivingEntity)entIterator.next();
      
      if (!ent.isValid())
      {
        ent.remove();
        entIterator.remove();
      }
    }
    
    if (this._mobs.size() > 16) {
      return;
    }
    
    double r = Math.random();
    
    Location loc = this.Manager.GetSpawn();
    
    if (r > 0.75D) { loc.add(32.0D, 0.5D, 0.0D);
    } else if (r > 0.5D) { loc.add(0.0D, 0.5D, 32.0D);
    } else if (r > 0.25D) loc.add(-32.0D, 0.5D, 0.0D); else {
      loc.add(0.0D, 0.5D, -32.0D);
    }
    
    if (this.Manager.Mode.equals("Halloween"))
    {
      Skeleton ent = (Skeleton)loc.getWorld().spawn(loc, Skeleton.class);
      
      if (Math.random() > 0.5D) {
        ent.setSkeletonType(Skeleton.SkeletonType.WITHER);
      }
      ent.getEquipment().setHelmet(ItemStackFactory.Instance.CreateStack(Material.PUMPKIN));
      
      ent.setCustomName(C.cYellow + "Pumpkin Minion");
      
      this._mobs.add(ent);
      
      this.Manager.GetCondition().Factory().Invisible("Perm", ent, ent, 999999999.0D, 0, false, false, true);
      this.Manager.GetCondition().Factory().Slow("Perm", ent, ent, 999999999.0D, 1, false, false, false, true);
    }
    else if (this.Manager.Mode.equals("Christmas"))
    {
      this._mobs.add((LivingEntity)loc.getWorld().spawn(loc, org.bukkit.entity.Snowman.class));
    }
    else
    {
      r = Math.random();
      
      if (r > 0.66D) { this._mobs.add((LivingEntity)loc.getWorld().spawn(loc, Cow.class));
      } else if (r > 0.33D) this._mobs.add((LivingEntity)loc.getWorld().spawn(loc, Pig.class)); else {
        this._mobs.add((LivingEntity)loc.getWorld().spawn(loc, Chicken.class));
      }
    }
  }
  
  @EventHandler
  public void BlockBreak(BlockBreakEvent event) {
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void Explosion(EntityExplodeEvent event)
  {
    event.blockList().clear();
  }
  
  @EventHandler
  public void VineGrow(BlockSpreadEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void LeaveDecay(LeavesDecayEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void BlockPlace(BlockPlaceEvent event)
  {
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler
  public void BorderUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (UtilMath.offset(player.getLocation(), this.Manager.GetSpawn()) > 200.0D)
      {
        player.eject();
        player.leaveVehicle();
        player.teleport(this.Manager.GetSpawn());
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void ItemPickup(PlayerPickupItemEvent event)
  {
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void ItemDrop(PlayerDropItemEvent event)
  {
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler
  public void UpdateWeather(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    World world = UtilWorld.getWorld("world");
    
    if (this.Manager.Mode.equals("Halloween")) {
      world.setTime(16000L);
    } else {
      world.setTime(6000L);
    }
    world.setStorm(false);
  }
  
  @EventHandler
  public void HalloweenUpdates(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!this.Manager.Mode.equals("Halloween")) {
      return;
    }
    
    for (Player player : UtilServer.getPlayers())
    {
      for (Block block : UtilBlock.getInRadius(player.getLocation(), 3.0D).keySet())
      {
        if (block.getType() == Material.PUMPKIN) {
          this.Manager.GetBlockRestore().Add(block, 91, block.getData(), 2000L);
        }
      }
    }
    
    for (LivingEntity ent : this._mobs)
    {
      if ((ent instanceof Creature))
      {

        Creature skel = (Creature)ent;
        
        if ((skel.getTarget() != null) && ((skel.getTarget() instanceof Player)) && (UtilMath.offset(skel, skel.getTarget()) < 6.0D))
        {
          skel.getEquipment().setHelmet(ItemStackFactory.Instance.CreateStack(Material.JACK_O_LANTERN));
        }
        else
        {
          skel.getEquipment().setHelmet(ItemStackFactory.Instance.CreateStack(Material.PUMPKIN));
        }
      }
    }
  }
  
  @EventHandler
  public void BlockForm(BlockFormEvent event) {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void CreatureTarget(EntityTargetEvent event)
  {
    if (this.Manager.Mode.equals("Christmas"))
    {
      event.setCancelled(true);
    }
  }
}
