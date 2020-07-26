package mineplex.hub.modules;

import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.hub.HubManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;





public class StackerManager
  extends MiniPlugin
  implements IThrown
{
  public HubManager Manager;
  private ProjectileManager _projectileManager;
  private HashSet<Entity> _tempStackShift = new HashSet();
  


  public StackerManager(HubManager manager)
  {
    super("Stacker", manager.GetPlugin());
    
    this.Manager = manager;
    
    this._projectileManager = new ProjectileManager(manager.GetPlugin());
  }
  






  public boolean CanStack(LivingEntity ent)
  {
    if (!(ent instanceof Player)) {
      return true;
    }
    if (this.Manager.BumpDisabled(ent)) {
      return false;
    }
    if (this.Manager.GetVisibility().IsHiding(ent)) {
      return false;
    }
    return true;
  }
  
  @EventHandler
  public void GrabEntity(PlayerInteractEntityEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player stacker = event.getPlayer();
    
    if (stacker.getGameMode() != GameMode.SURVIVAL) {
      return;
    }
    if (UtilGear.isMat(stacker.getItemInHand(), Material.SNOW_BALL)) {
      return;
    }
    
    if (this.Manager.GetParkour().InParkour(stacker))
    {
      UtilPlayer.message(stacker, F.main("Parkour", "You cannot Stack/Throw near Parkour Challenges."));
      return;
    }
    
    if (!CanStack(stacker))
    {
      UtilPlayer.message(stacker, F.main("Stacker", "You are not playing stacker."));
      return;
    }
    
    if ((stacker.getVehicle() != null) || (this._tempStackShift.contains(stacker)))
    {
      UtilPlayer.message(stacker, F.main("Stacker", "You cannot stack while stacked..."));
      return;
    }
    
    Entity stackee = event.getRightClicked();
    if (stackee == null) {
      return;
    }
    if (!(stackee instanceof LivingEntity)) {
      return;
    }
    if ((stackee instanceof Horse)) {
      return;
    }
    if ((stackee instanceof EnderDragon)) {
      return;
    }
    if (((stackee instanceof Player)) && (((Player)stackee).getGameMode() != GameMode.SURVIVAL)) {
      return;
    }
    if (((stackee instanceof Player)) && (!CanStack((Player)stackee)))
    {
      UtilPlayer.message(stacker, F.main("Stacker", F.name(UtilEnt.getName(stackee)) + " is not playing stacker."));
      return;
    }
    

    if (((stackee instanceof Player)) && (this.Manager.GetClients().Get((Player)stackee).GetRank() == Rank.YOUTUBE))
    {
      if (!this.Manager.GetClients().Get(stacker).GetRank().Has(Rank.YOUTUBE))
      {
        UtilPlayer.message(stacker, F.main("Stacker", F.name(UtilEnt.getName(stackee)) + " cannot be stacked! Leave him/her alone!"));
        return;
      }
    }
    
    if ((stackee instanceof LivingEntity))
    {
      if (((LivingEntity)stackee).isCustomNameVisible())
      {
        UtilPlayer.message(stacker, F.main("Stacker", "You cannot stack this entity."));
        return;
      }
    }
    
    while (stackee.getVehicle() != null) {
      stackee = stackee.getVehicle();
    }
    if (stackee.equals(stacker)) {
      return;
    }
    Entity top = stacker;
    while (top.getPassenger() != null) {
      top = top.getPassenger();
    }
    if (!Recharge.Instance.use(stacker, "Stacker", 500L, true, false)) {
      return;
    }
    top.setPassenger(stackee);
    
    UtilPlayer.message(stacker, F.main("Stacker", "You stacked " + F.name(new StringBuilder(String.valueOf(UtilEnt.getName(stackee))).append(".").toString())));
    UtilPlayer.message(stackee, F.main("Stacker", "You were stacked by " + F.name(new StringBuilder(String.valueOf(stacker.getName())).append(".").toString())));
    UtilPlayer.message(stackee, F.main("Stacker", "Push " + F.skill("Crouch") + " to escape!"));
    

    this.Manager.SetPortalDelay(stacker);
    this.Manager.SetPortalDelay(stackee);
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void ThrowEntity(PlayerInteractEvent event)
  {
    if (!UtilEvent.isAction(event, UtilEvent.ActionType.L)) {
      return;
    }
    Player thrower = event.getPlayer();
    
    if (thrower.getVehicle() != null) {
      return;
    }
    Entity throwee = thrower.getPassenger();
    if (throwee == null) {
      return;
    }
    thrower.eject();
    
    Entity throweeStack = throwee.getPassenger();
    if (throweeStack != null)
    {
      throwee.eject();
      throweeStack.leaveVehicle();
      
      final Entity fThrower = thrower;
      final Entity fThroweeStack = throweeStack;
      
      this._tempStackShift.add(throweeStack);
      
      GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
      {
        public void run()
        {
          fThrower.setPassenger(fThroweeStack);
          StackerManager.this._tempStackShift.remove(fThroweeStack);
        }
      }, 2L);
    }
    

    if (this.Manager.GetParkour().InParkour(thrower))
    {
      UtilPlayer.message(thrower, F.main("Parkour", "You cannot Stack/Throw near Parkour Challenges."));
      UtilPlayer.message(throwee, F.main("Parkour", "You cannot Stack/Throw near Parkour Challenges."));
      return;
    }
    
    UtilPlayer.message(thrower, F.main("Stacker", "You threw " + F.name(UtilEnt.getName(throwee))));
    UtilPlayer.message(throwee, F.main("Stacker", "You were thrown by " + F.name(thrower.getName())));
    
    UtilAction.velocity(throwee, thrower.getLocation().getDirection(), 1.8D, false, 0.0D, 0.3D, 2.0D, false);
    
    this._projectileManager.AddThrow(throwee, thrower, this, -1L, true, false, true, false, 2.4D);
    

    this.Manager.SetPortalDelay(thrower);
    this.Manager.SetPortalDelay(throwee);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    if ((target.getCustomName() != null) || ((target.getPassenger() != null) && ((target.getPassenger() instanceof LivingEntity)) && (((LivingEntity)target.getPassenger()).getCustomName() != null))) {
      return;
    }
    
    UtilAction.velocity(target, UtilAlg.getTrajectory2d(data.GetThrown(), target), 1.0D, true, 0.8D, 0.0D, 10.0D, true);
    
    Entity rider = target.getPassenger();
    while (rider != null)
    {

      this.Manager.SetPortalDelay(rider);
      
      rider.leaveVehicle();
      rider.setVelocity(new Vector(0.25D - Math.random() / 2.0D, Math.random() / 2.0D, 0.25D - Math.random() / 2.0D));
      rider = rider.getPassenger();
    }
    
    UtilPlayer.message(target, F.main("Stacker", F.name(UtilEnt.getName(data.GetThrower())) + " hit you with " + F.name(UtilEnt.getName(data.GetThrown()))));
    

    data.GetThrown().getWorld().playSound(data.GetThrown().getLocation(), Sound.HURT_FLESH, 1.0F, 1.0F);
    

    this.Manager.SetPortalDelay(target);
  }
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
