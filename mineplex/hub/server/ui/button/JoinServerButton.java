package mineplex.hub.server.ui.button;

import mineplex.core.shop.item.IButton;
import mineplex.hub.server.ServerInfo;
import mineplex.hub.server.ui.IServerPage;
import org.bukkit.entity.Player;


public class JoinServerButton
  implements IButton
{
  private IServerPage _page;
  private ServerInfo _serverInfo;
  
  public JoinServerButton(IServerPage page, ServerInfo serverInfo)
  {
    this._page = page;
    this._serverInfo = serverInfo;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.SelectServer(player, this._serverInfo);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.SelectServer(player, this._serverInfo);
  }
}
