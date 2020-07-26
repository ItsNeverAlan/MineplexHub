package mineplex.hub.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.WeakHashMap;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.donation.DonationManager;
import mineplex.core.recharge.Recharge;
import mineplex.core.task.TaskManager;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;


public class ParkourManager
  extends MiniPlugin
{
  public HubManager Manager;
  private HashSet<ParkourData> _parkour = new HashSet();
  
  private Location _lavaParkourReturn;
  private WeakHashMap<Player, Location> _lavaLocation = new WeakHashMap();
  private WeakHashMap<Player, Long> _lavaTimer = new WeakHashMap();
  
  protected DonationManager _donationManager;
  
  protected TaskManager _taskManager;
  
  public ParkourManager(HubManager manager, DonationManager donation, TaskManager task)
  {
    super("Parkour", manager.GetPlugin());
    
    this.Manager = manager;
    
    this._taskManager = task;
    this._donationManager = donation;
    
    this._parkour.add(new ParkourData("Ruins Parkour", 
      new String[] {
      "This is an extremely difficult parkour.", 
      "You will need to find the correct way through", 
      "the ruins, overcoming many challenging jumps." }, 
      
      4000, new Location(this.Manager.GetSpawn().getWorld(), 115.0D, 70.0D, -10.0D), 60.0D));
    

    this._parkour.add(new ParkourData("Lava Parkour", 
      new String[] {
      "This parkour is HOT! It's so hot that you", 
      "must keep sprinting for the entire course,", 
      "or you will die in flames!" }, 
      1000, new Location(this.Manager.GetSpawn().getWorld(), -100.0D, 60.0D, 0.0D), 60.0D));
    
    this._lavaParkourReturn = new Location(this.Manager.GetSpawn().getWorld(), -89.5D, 68.0D, 36.5D);
    this._lavaParkourReturn.setYaw(90.0F);
  }
  
  public boolean InParkour(Entity ent)
  {
    for (ParkourData data : this._parkour)
    {
      if (UtilMath.offset(ent.getLocation(), data.Location) < data.Distance)
      {
        return true;
      }
    }
    
    return false;
  }
  
  @EventHandler
  public void BlockBreak(BlockBreakEvent event)
  {
    if (InParkour(event.getPlayer()))
    {
      event.getPlayer().teleport(this.Manager.GetSpawn());
      UtilPlayer.message(event.getPlayer(), F.main("Parkour", "You cannot break blocks near parkour!"));
    }
  }
  
  @EventHandler
  public void combustPrevent(EntityCombustEvent event)
  {
    if ((event.getEntity() instanceof Player))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void LavaReturn(EntityDamageEvent event)
  {
    if (event.getCause() == EntityDamageEvent.DamageCause.LAVA)
      if ((event.getEntity() instanceof Player))
      {
        event.getEntity().eject();
        event.getEntity().leaveVehicle();
        event.getEntity().teleport(this._lavaParkourReturn);
      }
      else
      {
        event.getEntity().remove();
      }
    event.setCancelled(true);
  }
  
  @EventHandler
  public void preventCarriers(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (InParkour(player))
      {
        if (((player.getVehicle() != null) && (!(player.getVehicle() instanceof EnderDragon))) || (player.getPassenger() != null))
        {
          player.eject();
          player.leaveVehicle();
          
          UtilPlayer.message(player, F.main("Parkour", "You can't be a passenger near Parkours!"));
        }
      }
    }
  }
  
  @EventHandler
  public void LavaBlockReturn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (UtilEnt.isGrounded(player))
      {

        int id = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getTypeId();
        int data = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getData();
        if ((id == 0) || (id == 112) || (id == 114) || ((id == 43) && (data == 6)) || ((id == 44) && (data == 6)))
        {

          if ((!this._lavaLocation.containsKey(player)) || (UtilMath.offset(player.getLocation(), (Location)this._lavaLocation.get(player)) > 1.5D))
          {
            this._lavaLocation.put(player, player.getLocation());
            this._lavaTimer.put(player, Long.valueOf(System.currentTimeMillis()));


          }
          else if (UtilTime.elapsed(((Long)this._lavaTimer.get(player)).longValue(), 500L))
          {
            boolean inCourse = false;
            for (Block block : UtilBlock.getInRadius(player.getLocation(), 1.5D).keySet())
            {
              if ((block.getType() == Material.NETHER_BRICK) || (block.getType() == Material.NETHER_BRICK_STAIRS))
              {
                inCourse = true;
                break;
              }
            }
            
            if (inCourse)
            {

              this._lavaLocation.remove(player);
              this._lavaTimer.remove(player);
              
              player.eject();
              player.leaveVehicle();
              player.teleport(this._lavaParkourReturn);
              player.setFireTicks(0);
              
              UtilPlayer.message(player, F.main("Parkour", "You cannot stop running during Lava Parkour!"));
            }
          } }
      } }
  }
  
  @EventHandler
  public void Finish(PlayerInteractEntityEvent event) {
    if (event.getRightClicked() == null) {
      return;
    }
    if (!(event.getRightClicked() instanceof LivingEntity)) {
      return;
    }
    LivingEntity ent = (LivingEntity)event.getRightClicked();
    
    if (ent.getCustomName() == null) {
      return;
    }
    
    if (ent.getCustomName().contains("Start"))
    {
      Player player = event.getPlayer();
      
      for (ParkourData data : this._parkour)
      {
        if (ent.getCustomName().contains(data.Name))
        {

          data.Inform(player);
        }
      }
    }
    
    if (ent.getCustomName().contains("Finish"))
    {
      final Player player = event.getPlayer();
      
      if (!Recharge.Instance.use(player, "Finish Parkour", 30000L, false, false)) {
        return;
      }
      for (ParkourData data : this._parkour)
      {
        if (ent.getCustomName().contains(data.Name))
        {


          UtilPlayer.message(player, F.main("Parkour", "You completed " + F.elem(data.Name) + "."));
          

          if (!this._taskManager.hasCompletedTask(player, data.Name))
          {
            final ParkourData fData = data;
            
            this._donationManager.RewardGems(new Callback()
            {
              public void run(Boolean completed)
              {
                UtilPlayer.message(player, F.main("Parkour", "You received " + F.elem(new StringBuilder(String.valueOf(C.cGreen)).append(fData.Gems).append(" Gems").toString()) + "."));
                
                ParkourManager.this._taskManager.completedTask(player, fData.Name);
                

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.5F);
              }
            }, "Parkour " + data.Name, player.getName(), data.Gems);
          }
        }
      }
    }
  }
}
