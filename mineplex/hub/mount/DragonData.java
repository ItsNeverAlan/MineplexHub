package mineplex.hub.mount;

import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import net.minecraft.server.v1_7_R3.EntityEnderDragon;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class DragonData
{
  DragonMount Host;
  public EnderDragon Dragon;
  public Player Rider;
  public Entity TargetEntity = null;
  
  public Location Location = null;
  
  public float Pitch = 0.0F;
  public Vector Velocity = new Vector(0, 0, 0);
  
  public DragonData(DragonMount dragonMount, Player rider)
  {
    this.Host = dragonMount;
    
    this.Rider = rider;
    
    this.Velocity = rider.getLocation().getDirection().setY(0).normalize();
    this.Pitch = UtilAlg.GetPitch(rider.getLocation().getDirection());
    
    this.Location = rider.getLocation();
    

    this.Dragon = ((EnderDragon)rider.getWorld().spawn(rider.getLocation(), EnderDragon.class));
    UtilEnt.Vegetate(this.Dragon);
    UtilEnt.ghost(this.Dragon, true, false);
    
    rider.getWorld().playSound(rider.getLocation(), Sound.ENDERDRAGON_GROWL, 20.0F, 1.0F);
    
    this.Dragon.setPassenger(this.Rider);
  }
  
  public void Move()
  {
    this.Rider.eject();
    ((CraftEnderDragon)this.Dragon).getHandle().setTargetBlock(GetTarget().getBlockX(), GetTarget().getBlockY(), GetTarget().getBlockZ());
  }
  
  public Location GetTarget()
  {
    return this.Rider.getLocation().add(this.Rider.getLocation().getDirection().multiply(40));
  }
}
