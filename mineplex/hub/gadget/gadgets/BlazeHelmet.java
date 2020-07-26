package mineplex.hub.gadget.gadgets;

import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBlaze;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.gadget.GadgetManager;
import mineplex.hub.gadget.types.ArmorGadget;
import mineplex.hub.gadget.types.ArmorGadget.ArmorSlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;


public class BlazeHelmet
  extends ArmorGadget
{
  public BlazeHelmet(GadgetManager manager)
  {
    super(manager, "Blaze Helmet", new String[] {C.cWhite + "Transforms the wearer into a fiery Blaze!", " ", C.cYellow + "Crouch" + C.cGray + " to use " + C.cGreen + "Firefly", " ", C.cPurple + "Unlocked with Hero Rank" }, -1, ArmorGadget.ArmorSlot.Helmet, Material.FIRE, (byte)0);
  }
  


  public void Enable(Player player)
  {
    ApplyArmor(player);
    
    DisguiseBlaze disguise = new DisguiseBlaze(player);
    disguise.SetName(player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.Manager.GetDisguise().disguise(disguise);
  }
  

  public void Disable(Player player)
  {
    RemoveArmor(player);
    this.Manager.Manager.GetDisguise().undisguise(player);
  }
  
  @EventHandler
  public void Trail(UpdateEvent event)
  {
    if (event.getType() == UpdateType.TICK)
    {
      for (Player player : GetActive())
      {
        if (player.isSneaking())
        {
          player.leaveVehicle();
          player.eject();
          
          UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.25F, 0.25F, 0.0F, 3);
          UtilAction.velocity(player, 0.8D, 0.1D, 1.0D, true);
        }
      }
    }
  }
  
  @EventHandler
  public void HeroOwner(PlayerJoinEvent event)
  {
    event.getPlayer().getInventory().setItem(5, ItemStackFactory.Instance.CreateStack(Material.CHEST, (byte)0, 1, ChatColor.RESET + C.cGreen + "Gadget Menu"));
    
    if (this.Manager.Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.HERO))
    {
      this.Manager.Manager.GetDonation().Get(event.getPlayer().getName()).AddUnknownSalesPackagesOwned(GetName());
    }
  }
}
