package mineplex.hub.gadget.types;

import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.hub.gadget.GadgetManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public abstract class ArmorGadget extends Gadget
{
  private ArmorSlot _slot;
  
  public static enum ArmorSlot
  {
    Helmet, 
    Chest, 
    Legs, 
    Boots;
  }
  


  public ArmorGadget(GadgetManager manager, String name, String[] desc, int cost, ArmorSlot slot, org.bukkit.Material mat, byte data)
  {
    super(manager, name, desc, cost, mat, data);
    
    this._slot = slot;
  }
  
  public ArmorSlot GetSlot()
  {
    return this._slot;
  }
  
  public void ApplyArmor(Player player)
  {
    this.Manager.RemoveArmor(player, this._slot);
    
    if (this._slot == ArmorSlot.Helmet) {
      player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(GetDisplayMaterial(), GetDisplayData(), 1, F.item(GetName())));
    } else if (this._slot == ArmorSlot.Chest) {
      player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(GetDisplayMaterial(), GetDisplayData(), 1, F.item(GetName())));
    } else if (this._slot == ArmorSlot.Legs) {
      player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(GetDisplayMaterial(), GetDisplayData(), 1, F.item(GetName())));
    } else if (this._slot == ArmorSlot.Boots) {
      player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(GetDisplayMaterial(), GetDisplayData(), 1, F.item(GetName())));
    }
    this._active.add(player);
    
    mineplex.core.common.util.UtilPlayer.message(player, F.main("Gadget", "You put on " + F.elem(GetName()) + "."));
  }
  
  public void RemoveArmor(Player player)
  {
    if (this._slot == ArmorSlot.Helmet) {
      player.getInventory().setHelmet(null);
    } else if (this._slot == ArmorSlot.Chest) {
      player.getInventory().setChestplate(null);
    } else if (this._slot == ArmorSlot.Legs) {
      player.getInventory().setLeggings(null);
    } else if (this._slot == ArmorSlot.Boots) {
      player.getInventory().setBoots(null);
    }
    this._active.remove(player);
    
    mineplex.core.common.util.UtilPlayer.message(player, F.main("Gadget", "You took off " + F.elem(GetName()) + "."));
  }
}
