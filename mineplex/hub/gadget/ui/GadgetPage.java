package mineplex.hub.gadget.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.C;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.gadget.GadgetManager;
import mineplex.hub.gadget.types.Gadget;
import mineplex.hub.gadget.types.ItemGadget;
import mineplex.hub.mount.Mount;
import mineplex.hub.mount.MountManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class GadgetPage extends ShopPageBase<GadgetManager, GadgetShop>
{
  private MountManager _mountPlugin;
  
  public GadgetPage(GadgetManager plugin, GadgetShop shop, MountManager mountPlugin, CoreClientManager clientManager, DonationManager donationManager, String name, Player player)
  {
    super(plugin, shop, clientManager, donationManager, name, player, 54);
    
    this._mountPlugin = mountPlugin;
    
    BuildPage();
  }
  
  protected void BuildPage()
  {
    int activeSlot = 2;
    int headSlot = 11;
    int chestSlot = 20;
    int legSlot = 29;
    int bootSlot = 38;
    int mountSlot = 47;
    
    setItem(0, new ShopItem(Material.IRON_SWORD, (byte)0, "Active Gadget", new String[0], 1, false, false));
    
    setItem(9, new ShopItem(Material.LEATHER_HELMET, (byte)0, "Hub Helmet", new String[0], 1, false, false));
    LeatherArmorMeta meta = (LeatherArmorMeta)getItem(9).getItemMeta();
    meta.setColor(Color.BLACK);
    getItem(9).setItemMeta(meta);
    
    setItem(18, new ShopItem(Material.LEATHER_CHESTPLATE, (byte)0, "Hub Chestplate", new String[0], 1, false, false));
    meta = (LeatherArmorMeta)getItem(18).getItemMeta();
    meta.setColor(Color.BLACK);
    getItem(18).setItemMeta(meta);
    
    setItem(27, new ShopItem(Material.LEATHER_LEGGINGS, (byte)0, "Hub Leggings", new String[0], 1, false, false));
    meta = (LeatherArmorMeta)getItem(27).getItemMeta();
    meta.setColor(Color.BLACK);
    getItem(27).setItemMeta(meta);
    
    setItem(36, new ShopItem(Material.LEATHER_BOOTS, (byte)0, "Hub Boots", new String[0], 1, false, false));
    meta = (LeatherArmorMeta)getItem(36).getItemMeta();
    meta.setColor(Color.BLACK);
    getItem(36).setItemMeta(meta);
    
    setItem(45, new ShopItem(Material.SADDLE, (byte)0, "Hub Mount", new String[0], 1, false, false));
    
    for (Gadget gadget : ((GadgetManager)this.Plugin).getGadgets())
    {
      int slot = 0;
      
      if ((gadget instanceof mineplex.hub.gadget.types.ArmorGadget))
      {
        switch (((mineplex.hub.gadget.types.ArmorGadget)gadget).GetSlot())
        {
        case Legs: 
          slot = bootSlot;
          bootSlot++;
          break;
        case Chest: 
          slot = chestSlot;
          chestSlot++;
          break;
        case Boots: 
          slot = headSlot;
          headSlot++;
          break;
        case Helmet: 
          slot = legSlot;
          legSlot++;
          break;
        
        }
        
      }
      else if ((gadget instanceof ItemGadget))
      {
        slot = activeSlot;
        activeSlot++;
      }
      
      addGadget(gadget, slot);
    }
    
    for (Mount mount : this._mountPlugin.getMounts())
    {
      int slot = mountSlot;
      mountSlot++;
      
      addMount(mount, slot);
    }
  }
  
  protected void addGadget(Gadget gadget, int slot)
  {
    List<String> itemLore = new ArrayList();
    
    if (gadget.GetCost(CurrencyType.Gems) != -1)
    {
      itemLore.add(C.cYellow + gadget.GetCost(CurrencyType.Gems) + " Gems");
    }
    
    itemLore.add(C.cBlack);
    itemLore.addAll(Arrays.asList(gadget.GetDescription()));
    
    if (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage(gadget.GetName()))
    {
      if (gadget.GetActive().contains(this.Player))
      {
        AddButton(slot, new ShopItem(gadget.GetDisplayMaterial(), gadget.GetDisplayData(), "Deactivate " + gadget.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new DeactivateGadgetButton(gadget, this));
      }
      else
      {
        AddButton(slot, new ShopItem(gadget.GetDisplayMaterial(), gadget.GetDisplayData(), "Activate " + gadget.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new ActivateGadgetButton(gadget, this));
      }
      

    }
    else if ((gadget.GetCost(CurrencyType.Gems) != -1) && (this.DonationManager.Get(this.Player.getName()).GetBalance(CurrencyType.Gems) >= gadget.GetCost(CurrencyType.Gems))) {
      AddButton(slot, new ShopItem(gadget.GetDisplayMaterial(), gadget.GetDisplayData(), "Purchase " + gadget.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new GadgetButton(gadget, this));
    } else {
      setItem(slot, new ShopItem(gadget.GetDisplayMaterial(), gadget.GetDisplayData(), "Purchase " + gadget.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, true, false));
    }
  }
  
  protected void addMount(Mount mount, int slot)
  {
    List<String> itemLore = new ArrayList();
    
    if (mount.GetCost(CurrencyType.Gems) != -1)
    {
      itemLore.add(C.cYellow + mount.GetCost(CurrencyType.Gems) + " Gems");
    }
    
    itemLore.add(C.cBlack);
    itemLore.addAll(Arrays.asList(mount.GetDescription()));
    
    if (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage(mount.GetName()))
    {
      if (mount.GetActive().containsKey(this.Player))
      {
        AddButton(slot, new ShopItem(mount.GetDisplayMaterial(), mount.GetDisplayData(), "Deactivate " + mount.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new DeactivateMountButton(mount, this));
      }
      else
      {
        AddButton(slot, new ShopItem(mount.GetDisplayMaterial(), mount.GetDisplayData(), "Activate " + mount.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new ActivateMountButton(mount, this));
      }
      

    }
    else if ((mount.GetCost(CurrencyType.Gems) != -1) && (this.DonationManager.Get(this.Player.getName()).GetBalance(CurrencyType.Gems) >= mount.GetCost(CurrencyType.Gems))) {
      AddButton(slot, new ShopItem(mount.GetDisplayMaterial(), mount.GetDisplayData(), "Purchase " + mount.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new MountButton(mount, this));
    } else {
      setItem(slot, new ShopItem(mount.GetDisplayMaterial(), mount.GetDisplayData(), "Purchase " + mount.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, true, false));
    }
  }
  
  public void PurchaseGadget(Player player, Gadget gadget)
  {
    ((GadgetShop)this.Shop).OpenPageForPlayer(this.Player, new ConfirmationPage((GadgetManager)this.Plugin, (GadgetShop)this.Shop, this.ClientManager, this.DonationManager, new Runnable()
    {
      public void run()
      {
        GadgetPage.this.Player.closeInventory();
      }
    }, null, gadget, CurrencyType.Gems, this.Player));
  }
  
  public void ActivateGadget(Player player, Gadget gadget)
  {
    PlayAcceptSound(player);
    gadget.Enable(player);
    this.Player.closeInventory();
  }
  
  public void DeactivateGadget(Player player, Gadget gadget)
  {
    PlayAcceptSound(player);
    gadget.Disable(player);
    this.Player.closeInventory();
  }
  
  public void PurchaseMount(Player player, Mount _mount)
  {
    ((GadgetShop)this.Shop).OpenPageForPlayer(this.Player, new ConfirmationPage((GadgetManager)this.Plugin, (GadgetShop)this.Shop, this.ClientManager, this.DonationManager, new Runnable()
    {
      public void run()
      {
        GadgetPage.this.Player.closeInventory();
      }
    }, null, _mount, CurrencyType.Gems, this.Player));
  }
  
  public void ActivateMount(Player player, Mount _mount)
  {
    PlayAcceptSound(player);
    _mount.Enable(player);
    this.Player.closeInventory();
  }
  
  public void DeactivateMount(Player player, Mount _mount)
  {
    PlayAcceptSound(player);
    _mount.Disable(player);
    this.Player.closeInventory();
  }
}
