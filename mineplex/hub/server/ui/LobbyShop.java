package mineplex.hub.server.ui;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.server.ServerManager;
import org.bukkit.entity.Player;

public class LobbyShop extends ShopBase<ServerManager>
{
  public LobbyShop(ServerManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
  {
    super(plugin, clientManager, donationManager, name, new mineplex.core.common.CurrencyType[0]);
    
    plugin.addServerGroup("Lobby", "Lobby");
  }
  

  protected ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> BuildPagesFor(Player player)
  {
    return new LobbyMenu((ServerManager)this.Plugin, this, this.ClientManager, this.DonationManager, "          " + org.bukkit.ChatColor.UNDERLINE + "Lobby Selector", player, "Lobby");
  }
  
  public void UpdatePages()
  {
    for (ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> page : this.PlayerPageMap.values())
    {
      if ((page instanceof LobbyMenu))
      {
        ((LobbyMenu)page).Update();
      }
    }
  }
}
