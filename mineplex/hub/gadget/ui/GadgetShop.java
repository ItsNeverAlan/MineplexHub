package mineplex.hub.gadget.ui;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.gadget.GadgetManager;
import mineplex.hub.mount.MountManager;
import org.bukkit.entity.Player;

public class GadgetShop
  extends ShopBase<GadgetManager>
{
  private MountManager _mountManager;
  
  public GadgetShop(GadgetManager plugin, MountManager mountManager, CoreClientManager manager, DonationManager donationManager)
  {
    super(plugin, manager, donationManager, "Gadget Shop", new CurrencyType[] { CurrencyType.Gems });
    
    this._mountManager = mountManager;
  }
  

  protected ShopPageBase<GadgetManager, ? extends ShopBase<GadgetManager>> BuildPagesFor(Player player)
  {
    return new GadgetPage((GadgetManager)this.Plugin, this, this._mountManager, this.ClientManager, this.DonationManager, "     Gadgets", player);
  }
}
