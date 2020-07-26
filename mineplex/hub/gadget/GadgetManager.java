package mineplex.hub.gadget;

import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.hub.HubManager;
import mineplex.hub.gadget.gadgets.Halloween2013_Helmet;
import mineplex.hub.gadget.types.ArmorGadget;
import mineplex.hub.gadget.types.ArmorGadget.ArmorSlot;
import mineplex.hub.gadget.types.Gadget;
import mineplex.hub.gadget.types.ItemGadget;
import mineplex.hub.gadget.ui.GadgetShop;
import mineplex.hub.mount.MountManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GadgetManager extends MiniPlugin
{
  public HubManager Manager;
  private GadgetShop _gadgetShop;
  private HashSet<Gadget> _gadgets;
  
  public GadgetManager(HubManager manager, MountManager mountManager)
  {
    super("Gadget Manager", manager.GetPlugin());
    
    this.Manager = manager;
    
    CreateGadgets();
    
    this._gadgetShop = new GadgetShop(this, mountManager, this.Manager.GetClients(), this.Manager.GetDonation());
  }
  
  private void CreateGadgets()
  {
    this._gadgets = new HashSet();
    

    this._gadgets.add(new mineplex.hub.gadget.gadgets.PaintballGun(this));
    

    this._gadgets.add(new mineplex.hub.gadget.gadgets.BlazeHelmet(this));
    

    this._gadgets.add(new mineplex.hub.gadget.gadgets.Halloween2013_BatGun(this));
    this._gadgets.add(new Halloween2013_Helmet(this));
  }
  



  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    event.getPlayer().getInventory().setItem(5, ItemStackFactory.Instance.CreateStack(Material.CHEST, (byte)0, 1, ChatColor.RESET + C.cGreen + "Gadget Menu"));
    
    if (this.Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.MODERATOR))
    {
      for (Gadget gadget : this._gadgets)
      {
        this.Manager.GetDonation().Get(event.getPlayer().getName()).AddUnknownSalesPackagesOwned(gadget.GetName());
      }
    }
  }
  
  @EventHandler
  public void orderThatChest(final PlayerDropItemEvent event)
  {
    if (event.getItemDrop().getItemStack().getType() == Material.CHEST)
    {
      org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
      {
        public void run()
        {
          if (event.getPlayer().isOnline())
          {
            event.getPlayer().getInventory().remove(Material.CHEST);
            event.getPlayer().getInventory().setItem(5, ItemStackFactory.Instance.CreateStack(Material.CHEST, (byte)0, 1, ChatColor.RESET + C.cGreen + "Gadget Menu"));
            event.getPlayer().updateInventory();
          }
        }
      });
    }
  }
  
  @EventHandler
  public void openShop(PlayerInteractEvent event)
  {
    if ((event.hasItem()) && (event.getItem().getType() == Material.CHEST))
    {
      this._gadgetShop.attemptShopOpen(event.getPlayer());
      event.setCancelled(true);
    }
  }
  
  public HashSet<Gadget> getGadgets()
  {
    return this._gadgets;
  }
  

  public void RemoveArmor(Player player, ArmorGadget.ArmorSlot slot)
  {
    for (Gadget gadget : this._gadgets)
    {
      if ((gadget instanceof ArmorGadget))
      {
        ArmorGadget armor = (ArmorGadget)gadget;
        
        if (armor.GetSlot() == slot)
        {
          armor.RemoveArmor(player);
        }
      }
    }
  }
  
  public void RemoveItem(Player player)
  {
    for (Gadget gadget : this._gadgets)
    {
      if ((gadget instanceof ItemGadget))
      {
        ItemGadget item = (ItemGadget)gadget;
        
        item.RemoveItem(player);
      }
    }
  }
  
  @EventHandler
  public void SnowballPickup(BlockDamageEvent event)
  {
    if (!this.Manager.Mode.equals("Christmas")) {
      return;
    }
    if (event.getBlock().getType() != Material.SNOW) {
      return;
    }
    Player player = event.getPlayer();
    
    RemoveItem(player);
    
    player.getInventory().setItem(4, new ItemStack(Material.SNOW_BALL, 16));
  }
  
  @EventHandler
  public void SnowballHit(CustomDamageEvent event)
  {
    if (!this.Manager.Mode.equals("Christmas")) {
      return;
    }
    Projectile proj = event.GetProjectile();
    if (proj == null) { return;
    }
    if (!(proj instanceof Snowball)) {
      return;
    }
    event.SetCancelled("Snowball Cancel");
    
    if (this.Manager.BumpDisabled(event.GetDamageeEntity())) {
      return;
    }
    if (this.Manager.BumpDisabled(event.GetDamagerEntity(true))) {
      return;
    }
    UtilAction.velocity(event.GetDamageeEntity(), mineplex.core.common.util.UtilAlg.getTrajectory2d(event.GetDamagerEntity(true), event.GetDamageeEntity()), 
      0.4D, false, 0.0D, 0.2D, 1.0D, false);
    

    this.Manager.SetPortalDelay(event.GetDamageeEntity());
  }
}
