package mineplex.hub.server.ui;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.ShopBase;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.party.Party;
import mineplex.hub.party.PartyManager;
import mineplex.hub.server.ServerManager;
import org.bukkit.entity.Player;

public class ServerNpcShop extends ShopBase<ServerManager>
{
  public ServerNpcShop(ServerManager plugin, CoreClientManager clientManager, DonationManager donationManager, String name)
  {
    super(plugin, clientManager, donationManager, name, new mineplex.core.common.CurrencyType[0]);
  }
  

  protected ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> BuildPagesFor(Player player)
  {
    return new ServerNpcPage((ServerManager)this.Plugin, this, this.ClientManager, this.DonationManager, this.Name, player, this.Name);
  }
  

  protected boolean CanOpenShop(Player player)
  {
    Party party = ((ServerManager)this.Plugin).getPartyManager().GetParty(player);
    
    if ((party != null) && (!player.getName().equalsIgnoreCase(party.GetLeader())))
    {
      player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_BREAK, 1.0F, 0.6F);
      player.sendMessage(F.main("Party", "Only Party Leaders can join games."));
      player.sendMessage(F.main("Party", "Type " + C.cGreen + "/party leave" + C.cGray + " if you wish to leave your party."));
      return false;
    }
    
    return true;
  }
  
  public void UpdatePages()
  {
    for (ShopPageBase<ServerManager, ? extends ShopBase<ServerManager>> page : this.PlayerPageMap.values())
    {
      if ((page instanceof ServerNpcPage))
      {
        ((ServerNpcPage)page).Update();
      }
      else if ((page instanceof ServerGameMenu))
      {
        ((ServerGameMenu)page).Update();
      }
    }
  }
}
