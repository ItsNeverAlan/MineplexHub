package mineplex.hub.server.ui;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.server.ServerManager;
import org.bukkit.entity.Player;

public class QuickShop extends ShopBase<ServerManager>
{
  public QuickShop(ServerManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
  {
    super(plugin, clientManager, donationManager, name, new mineplex.core.common.CurrencyType[0]);
  }
  

  protected ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> BuildPagesFor(Player player)
  {
    return new ServerGameMenu((ServerManager)this.Plugin, this, this.ClientManager, this.DonationManager, "          " + org.bukkit.ChatColor.UNDERLINE + "Quick Game Menu", player);
  }
  
  public void UpdatePages()
  {
    for (ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> page : this.PlayerPageMap.values())
    {
      if ((page instanceof ServerGameMenu))
      {
        ((ServerGameMenu)page).Update();
      }
    }
  }
}
