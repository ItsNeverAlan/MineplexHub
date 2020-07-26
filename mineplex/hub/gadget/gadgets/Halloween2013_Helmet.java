package mineplex.hub.gadget.gadgets;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilServer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.hub.HubManager;
import mineplex.hub.gadget.GadgetManager;
import mineplex.hub.gadget.types.ArmorGadget;
import mineplex.hub.gadget.types.ArmorGadget.ArmorSlot;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;




public class Halloween2013_Helmet
  extends ArmorGadget
{
  public Halloween2013_Helmet(GadgetManager manager)
  {
    super(manager, "Pumpkin Kings Head", new String[] {C.cWhite + "Transforms the wearer into", C.cWhite + "the dreaded Pumpkin King!", "", C.cYellow + "Earned by defeating the Pumpkin King", C.cYellow + "in the 2013 Halloween Horror Event." }, -1, ArmorGadget.ArmorSlot.Helmet, Material.PUMPKIN, (byte)0);
  }
  


  public void Enable(final Player player)
  {
    ApplyArmor(player);
    
    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    disguise.showArmor();
    disguise.SetName(player.getName());
    disguise.SetCustomNameVisible(true);
    disguise.SetSkeletonType(Skeleton.SkeletonType.WITHER);
    this.Manager.Manager.GetDisguise().disguise(disguise);
    
    this.Manager.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        for (Player other : )
        {
          other.hidePlayer(player);
          other.showPlayer(player);
        }
      }
    }, 0L);
  }
  

  public void Disable(Player player)
  {
    RemoveArmor(player);
    this.Manager.Manager.GetDisguise().undisguise(player);
  }
}
