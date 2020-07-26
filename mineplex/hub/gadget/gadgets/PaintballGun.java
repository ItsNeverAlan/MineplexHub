package mineplex.hub.gadget.gadgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.gadget.GadgetManager;
import mineplex.hub.gadget.types.ItemGadget;
import mineplex.hub.modules.ParkourManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class PaintballGun extends ItemGadget
{
  private HashSet<Projectile> _balls = new HashSet();
  





  public PaintballGun(GadgetManager manager)
  {
    super(manager, "Paintball Gun", new String[] {C.cWhite + "PEW PEW PEW PEW!" }, 5000, Material.GOLD_BARDING, (byte)0);
  }
  

  public void Enable(Player player)
  {
    ApplyItem(player);
  }
  

  public void Disable(Player player)
  {
    RemoveItem(player);
  }
  
  @EventHandler
  public void Shoot(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.GOLD_BARDING) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!IsActive(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 400L, false, false)) {
      return;
    }
    event.setCancelled(true);
    
    Projectile proj = player.launchProjectile(org.bukkit.entity.EnderPearl.class);
    proj.setVelocity(proj.getVelocity().multiply(2));
    this._balls.add(proj);
    

    player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5F, 1.2F);
  }
  
  @EventHandler
  public void Paint(ProjectileHitEvent event)
  {
    if (this.Manager.Manager.GetParkour().InParkour(event.getEntity())) {
      return;
    }
    if (!this._balls.remove(event.getEntity())) {
      return;
    }
    Location loc = event.getEntity().getLocation().add(event.getEntity().getVelocity());
    loc.getWorld().playEffect(loc, org.bukkit.Effect.STEP_SOUND, 49);
    
    byte color = 2;
    double r = Math.random();
    if (r > 0.8D) { color = 4;
    } else if (r > 0.6D) { color = 5;
    } else if (r > 0.4D) { color = 9;
    } else if (r > 0.2D) { color = 14;
    }
    for (Block block : UtilBlock.getInRadius(loc, 3.0D).keySet())
    {
      if (block.getType() == Material.PORTAL) {
        return;
      }
      if (block.getType() == Material.CACTUS) {
        return;
      }
    }
    for (Block block : UtilBlock.getInRadius(loc, 1.5D).keySet())
    {
      if (UtilBlock.solid(block))
      {

        if (block.getType() == Material.CARPET) {
          this.Manager.Manager.GetBlockRestore().Add(block, 171, color, 4000L);
        } else
          this.Manager.Manager.GetBlockRestore().Add(block, 35, color, 4000L);
      }
    }
  }
  
  @EventHandler
  public void Teleport(PlayerTeleportEvent event) {
    if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void cleanupBalls(UpdateEvent event) {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    for (Iterator<Projectile> ballIterator = this._balls.iterator(); ballIterator.hasNext();)
    {
      Projectile ball = (Projectile)ballIterator.next();
      
      if ((ball.isDead()) || (!ball.isValid())) {
        ballIterator.remove();
      }
    }
  }
}
