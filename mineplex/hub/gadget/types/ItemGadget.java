package mineplex.hub.gadget.types;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.hub.gadget.GadgetManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class ItemGadget extends Gadget
{
  public ItemGadget(GadgetManager manager, String name, String[] desc, int cost, Material mat, byte data)
  {
    super(manager, name, desc, cost, mat, data);
  }
  
  public HashSet<Player> GetActive()
  {
    return this._active;
  }
  
  public boolean IsActive(Player player)
  {
    return this._active.contains(player);
  }
  
  public void ApplyItem(Player player)
  {
    this.Manager.RemoveItem(player);
    
    player.getInventory().setItem(4, ItemStackFactory.Instance.CreateStack(GetDisplayMaterial(), GetDisplayData(), 1, F.item(GetName())));
    
    this._active.add(player);
    
    UtilPlayer.message(player, F.main("Gadget", "You equipped " + F.elem(GetName()) + "."));
  }
  
  @EventHandler
  public void orderThatChest(final PlayerDropItemEvent event)
  {
    if (event.getItemDrop().getItemStack().getType() == GetDisplayMaterial())
    {
      org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          if (event.getPlayer().isOnline())
          {
            event.getPlayer().getInventory().remove(ItemGadget.this.GetDisplayMaterial());
            event.getPlayer().getInventory().setItem(4, ItemStackFactory.Instance.CreateStack(ItemGadget.this.GetDisplayMaterial(), ItemGadget.this.GetDisplayData(), 1, F.item(ItemGadget.this.GetName())));
            event.getPlayer().updateInventory();
          }
        }
      });
    }
  }
  
  public void RemoveItem(Player player)
  {
    if (this._active.remove(player))
    {
      player.getInventory().setItem(4, null);
      UtilPlayer.message(player, F.main("Gadget", "You unequipped " + F.elem(GetName()) + "."));
    }
  }
  
  public boolean IsItem(Player player)
  {
    return mineplex.core.common.util.UtilInv.IsItem(player.getItemInHand(), GetDisplayMaterial(), GetDisplayData());
  }
}
