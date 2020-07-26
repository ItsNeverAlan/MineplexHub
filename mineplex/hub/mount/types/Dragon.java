package mineplex.hub.mount.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilServer;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.mount.DragonData;
import mineplex.hub.mount.DragonMount;
import mineplex.hub.mount.MountManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;



public class Dragon
  extends DragonMount
{
  public Dragon(MountManager manager)
  {
    super(manager, "Ethereal Dragon", new String[] {C.cWhite + "From the distant ether realm,", C.cWhite + "this prized dragon is said to", C.cWhite + "obey only true Heroes!", " ", C.cPurple + "Unlocked with Hero Rank" }, Material.DRAGON_EGG, (byte)0, -1);
  }
  
  @EventHandler
  public void Trail(UpdateEvent event)
  {
    if (event.getType() == UpdateType.TICK) { int j;
      int i;
      for (Iterator localIterator = GetActive().values().iterator(); localIterator.hasNext(); 
          i < j)
      {
        DragonData data = (DragonData)localIterator.next();
        Player[] arrayOfPlayer; j = (arrayOfPlayer = UtilServer.getPlayers()).length;i = 0; continue;Player player = arrayOfPlayer[i];
        
        try
        {
          UtilParticle.PlayParticle(player, UtilParticle.ParticleType.WITCH_MAGIC, data.Dragon.getLocation().add(0.0D, 1.0D, 0.0D), 1.0F, 1.0F, 1.0F, 0.0F, 20);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        i++;
      }
    }
  }
  









  @EventHandler
  public void DragonLocation(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (DragonData data : GetActive().values()) {
      data.Move();
    }
    HashSet<Player> toRemove = new HashSet();
    
    for (Player player : GetActive().keySet())
    {
      DragonData data = (DragonData)GetActive().get(player);
      if (data == null)
      {
        toRemove.add(player);


      }
      else if ((!data.Dragon.isValid()) || (data.Dragon.getPassenger() == null))
      {
        data.Dragon.remove();
        toRemove.add(player);
      }
    }
    

    for (Player player : toRemove) {
      Disable(player);
    }
  }
  
  @EventHandler
  public void DragonTargetCancel(EntityTargetEvent event) {
    event.setCancelled(true);
  }
  
  public void SetName(String news)
  {
    for (DragonData dragon : GetActive().values()) {
      dragon.Dragon.setCustomName(news);
    }
  }
  
  @EventHandler
  public void HeroOwner(PlayerJoinEvent event) {
    event.getPlayer().getInventory().setItem(5, ItemStackFactory.Instance.CreateStack(Material.CHEST, (byte)0, 1, ChatColor.RESET + C.cGreen + "Gadget Menu"));
    
    if (this.Manager.Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.HERO))
    {
      this.Manager.Manager.GetDonation().Get(event.getPlayer().getName()).AddUnknownSalesPackagesOwned(GetName());
    }
  }
}
