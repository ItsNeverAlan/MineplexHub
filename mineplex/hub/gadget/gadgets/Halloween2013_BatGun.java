package mineplex.hub.gadget.gadgets;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
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
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Halloween2013_BatGun extends ItemGadget
{
  private HashMap<Player, Long> _active = new HashMap();
  private HashMap<Player, Location> _velocity = new HashMap();
  private HashMap<Player, ArrayList<Bat>> _bats = new HashMap();
  






  public Halloween2013_BatGun(GadgetManager manager)
  {
    super(manager, "Bat Blaster", new String[] {C.cWhite + "Launch waves of annoying bats", C.cWhite + "at people you don't like!" }, 10000, Material.IRON_BARDING, (byte)0);
  }
  

  public void Enable(Player player)
  {
    ApplyItem(player);
  }
  

  public void Disable(Player player)
  {
    RemoveItem(player);
    
    Clear(player);
  }
  
  @EventHandler
  public void Activate(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!UtilGear.isMat(event.getPlayer().getItemInHand(), Material.IRON_BARDING)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!IsActive(player)) {
      return;
    }
    if (this.Manager.Manager.GetParkour().InParkour(player))
    {
      UtilPlayer.message(player, F.main("Parkour", "You cannot use Bat Blaster near Parkour Challenges."));
      return;
    }
    
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, false)) {
      return;
    }
    
    this._velocity.put(player, player.getEyeLocation());
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    
    this._bats.put(player, new ArrayList());
    
    for (int i = 0; i < 16; i++) {
      ((ArrayList)this._bats.get(player)).add((Bat)player.getWorld().spawn(player.getEyeLocation(), Bat.class));
    }
    
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this._active.containsKey(cur))
      {

        if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(cur)).longValue(), 3000L))
        {
          Clear(cur);
        }
        else
        {
          Location loc = (Location)this._velocity.get(cur);
          

          for (Bat bat : (ArrayList)this._bats.get(cur))
          {
            if (bat.isValid())
            {
              Vector rand = new Vector((Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D, (Math.random() - 0.5D) / 3.0D);
              bat.setVelocity(loc.getDirection().clone().multiply(0.5D).add(rand));
              
              for (Player other : UtilServer.getPlayers())
              {
                if (!other.equals(cur))
                {

                  if (Recharge.Instance.usable(other, "Hit by Bat"))
                  {

                    if (UtilEnt.hitBox(bat.getLocation(), other, 2.0D, null))
                    {

                      UtilAction.velocity(other, UtilAlg.getTrajectory(cur, other), 0.4D, false, 0.0D, 0.2D, 10.0D, true);
                      

                      bat.getWorld().playSound(bat.getLocation(), Sound.BAT_HURT, 1.0F, 1.0F);
                      UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, bat.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 3);
                      
                      bat.remove();
                      

                      Recharge.Instance.useForce(other, "Hit by Bat", 200L);
                      

                      this.Manager.Manager.SetPortalDelay(other);
                    } } } }
            } }
        }
      }
    }
  }
  
  public void Clear(Player player) {
    this._active.remove(player);
    this._velocity.remove(player);
    if (this._bats.containsKey(player))
    {
      for (Bat bat : (ArrayList)this._bats.get(player))
      {
        if (bat.isValid()) {
          UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, bat.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 3);
        }
        bat.remove();
      }
      
      this._bats.remove(player);
    }
  }
}
